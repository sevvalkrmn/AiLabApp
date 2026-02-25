// data/repository/ProjectRepository.kt

package com.ktun.ailabapp.data.repository

import com.ktun.ailabapp.data.model.UserProject
import com.ktun.ailabapp.data.remote.api.ProjectApi
import com.ktun.ailabapp.data.remote.dto.request.AddMemberRequest
import com.ktun.ailabapp.data.remote.dto.request.CreateProjectRequest
import com.ktun.ailabapp.data.remote.dto.response.MyProjectsResponse
import com.ktun.ailabapp.data.remote.dto.response.ProjectDetailResponse
import com.ktun.ailabapp.data.remote.dto.response.ProjectMember
import com.ktun.ailabapp.data.remote.dto.response.toUserProject
import com.ktun.ailabapp.util.CacheEntry
import com.ktun.ailabapp.util.Logger
import com.ktun.ailabapp.util.NetworkResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProjectRepository @Inject constructor(
    private val projectApi: ProjectApi
) {

    private companion object {
        const val PROJECTS_TTL_MS = 5 * 60 * 1000L
        const val MEMBERS_TTL_MS = 5 * 60 * 1000L
        const val PROJECT_DETAIL_TTL_MS = 5 * 60 * 1000L
    }

    private val myProjectsCache = ConcurrentHashMap<String, CacheEntry<List<MyProjectsResponse>>>()
    private val membersCacheMap = ConcurrentHashMap<String, CacheEntry<List<ProjectMember>>>()
    private val projectDetailCache = ConcurrentHashMap<String, CacheEntry<ProjectDetailResponse>>()

    fun clearCache() {
        myProjectsCache.clear()
        membersCacheMap.clear()
        projectDetailCache.clear()
    }

    fun invalidateProjectDetail(projectId: String) {
        projectDetailCache.remove(projectId)
    }
    /**
     * Kullanıcının kendi projelerini çeker
     * GET /api/projects/my-projects
     */
    suspend fun getMyProjects(roleFilter: String? = null): NetworkResult<List<MyProjectsResponse>> = withContext(Dispatchers.IO) {
        val cacheKey = roleFilter ?: "all"
        myProjectsCache[cacheKey]?.let { if (it.isValid(PROJECTS_TTL_MS)) return@withContext NetworkResult.Success(it.data) }

        try {
            Logger.d( "🔍 Fetching my projects with role filter: $roleFilter")

            val response = projectApi.getMyProjects(roleFilter)

            return@withContext when {
                response.code() == 401 -> {
                    NetworkResult.Error("Oturum süresi doldu")
                }
                response.isSuccessful && response.body() != null -> {
                    val projects = response.body()!!
                    myProjectsCache[cacheKey] = CacheEntry(projects)
                    Logger.d( "✅ Loaded ${projects.size} projects")
                    NetworkResult.Success(projects)
                }
                response.isSuccessful && response.body() == null -> {
                    myProjectsCache[cacheKey] = CacheEntry(emptyList())
                    Logger.d( "⚠️ No projects found")
                    NetworkResult.Success(emptyList())
                }
                else -> {
                    Logger.e( "❌ Error: ${response.code()}")
                    NetworkResult.Error("Projeler yüklenemedi: ${response.code()}")
                }
            }
        } catch (e: Exception) {
            Logger.e( "❌ Exception: ${e.message}", e)
            return@withContext NetworkResult.Error(e.message ?: "Bilinmeyen hata")
        }
    }

    /**
     * Sistemdeki TÜM projeleri çeker (Admin)
     * GET /api/Projects
     */
    suspend fun getAllProjects(): NetworkResult<List<MyProjectsResponse>> = withContext(Dispatchers.IO) {
        try {
            Logger.d( "🔍 Fetching ALL projects (Admin)")

            val response = projectApi.getAllProjects()

            return@withContext when {
                response.code() == 401 -> NetworkResult.Error("Oturum süresi doldu")
                response.code() == 403 -> NetworkResult.Error("Yetkisiz erişim (Sadece Admin)")
                response.isSuccessful && response.body() != null -> {
                    // ✅ PaginatedResponse içinden items listesini al
                    val projects = response.body()!!.items 
                    Logger.d( "✅ Loaded ${projects.size} total projects")
                    NetworkResult.Success(projects)
                }
                else -> NetworkResult.Error("Projeler yüklenemedi: ${response.code()}")
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Bilinmeyen hata")
        }
    }

    /**
     * Belirli bir kullanıcının projelerini çeker
     * GET /api/projects/user/{userId}
     */
    suspend fun getUserProjects(userId: String): NetworkResult<List<UserProject>> = withContext(Dispatchers.IO) {
        try {
            Logger.d( "🔍 Fetching projects for userId: $userId")

            val response = projectApi.getUserProjects(userId)

            return@withContext when {
                response.code() == 401 -> {
                    NetworkResult.Error("Oturum süresi doldu")
                }
                response.code() == 404 -> {
                    Logger.d( "⚠️ User has no projects (404)")
                    NetworkResult.Success(emptyList())
                }
                response.isSuccessful && response.body() != null -> {
                    val projects = response.body()!!.map { it.toUserProject(userId) }
                    Logger.d( "✅ Loaded ${projects.size} projects")
                    NetworkResult.Success(projects)
                }
                response.isSuccessful && response.body() == null -> {
                    Logger.d( "⚠️ User has no projects (empty body)")
                    NetworkResult.Success(emptyList())
                }
                else -> {
                    Logger.e( "❌ Error: ${response.code()}")
                    NetworkResult.Error("Projeler yüklenemedi: ${response.code()}")
                }
            }
        } catch (e: Exception) {
            Logger.e( "❌ Exception: ${e.message}", e)
            return@withContext NetworkResult.Error(e.message ?: "Bilinmeyen hata")
        }
    }

    /**
     * Belirli bir projenin detaylarını getirir
     * GET /api/projects/{id}
     */
    suspend fun getProjectDetail(projectId: String): NetworkResult<ProjectDetailResponse> = withContext(Dispatchers.IO) {
        projectDetailCache[projectId]?.let { if (it.isValid(PROJECT_DETAIL_TTL_MS)) return@withContext NetworkResult.Success(it.data) }

        try {
            Logger.d( "🔍 Fetching project detail: $projectId")

            val response = projectApi.getProjectDetail(projectId)

            return@withContext when {
                response.code() == 401 -> {
                    NetworkResult.Error("Oturum süresi doldu")
                }
                response.code() == 403 -> {
                    NetworkResult.Error("Bu projeyi görme yetkiniz yok")
                }
                response.code() == 404 -> {
                    NetworkResult.Error("Proje bulunamadı")
                }
                response.isSuccessful && response.body() != null -> {
                    val project = response.body()!!
                    projectDetailCache[projectId] = CacheEntry(project)
                    Logger.d( "✅ Loaded project: ${project.name}")
                    NetworkResult.Success(project)
                }
                else -> {
                    Logger.e( "❌ Error: ${response.code()}")
                    NetworkResult.Error("Proje yüklenemedi: ${response.code()}")
                }
            }
        } catch (e: Exception) {
            Logger.e( "❌ Exception: ${e.message}", e)
            return@withContext NetworkResult.Error(e.message ?: "Bilinmeyen hata")
        }
    }

    /**
     * Proje üyelerini listele
     * GET /api/projects/{id}/members
     */
    suspend fun getProjectMembers(projectId: String): NetworkResult<List<ProjectMember>> = withContext(Dispatchers.IO) {
        membersCacheMap[projectId]?.let { if (it.isValid(MEMBERS_TTL_MS)) return@withContext NetworkResult.Success(it.data) }

        try {
            Logger.d( "🔍 Fetching members for project: $projectId")

            val response = projectApi.getProjectMembers(projectId)

            return@withContext when {
                response.code() == 401 -> {
                    NetworkResult.Error("Oturum süresi doldu")
                }
                response.code() == 403 -> {
                    NetworkResult.Error("Bu projenin üyelerini görme yetkiniz yok")
                }
                response.code() == 404 -> {
                    NetworkResult.Error("Proje bulunamadı")
                }
                response.isSuccessful && response.body() != null -> {
                    val members = response.body()!!
                    membersCacheMap[projectId] = CacheEntry(members)
                    Logger.d( "✅ Loaded ${members.size} members")
                    NetworkResult.Success(members)
                }
                response.isSuccessful && response.body() == null -> {
                    membersCacheMap[projectId] = CacheEntry(emptyList())
                    Logger.d( "⚠️ No members found")
                    NetworkResult.Success(emptyList())
                }
                else -> {
                    Logger.e( "❌ Error: ${response.code()}")
                    NetworkResult.Error("Üyeler yüklenemedi: ${response.code()}")
                }
            }
        } catch (e: Exception) {
            Logger.e( "❌ Exception: ${e.message}", e)
            return@withContext NetworkResult.Error(e.message ?: "Bilinmeyen hata")
        }
    }

    /**
     * ✅ YENİ - Proje oluştur
     * POST /api/projects
     */
    suspend fun createProject(request: CreateProjectRequest): NetworkResult<ProjectDetailResponse> = withContext(Dispatchers.IO) {
        try {
            Logger.d( "🔍 Creating project: ${request.name}")

            // ProjectApi'deki createProject muhtemelen Response<ProjectDetailResponse> dönüyor
            val response = projectApi.createProject(request)

            return@withContext when {
                response.code() == 401 -> {
                    NetworkResult.Error("Oturum süresi doldu")
                }
                response.code() == 400 -> {
                    NetworkResult.Error("Geçersiz bilgiler girildi")
                }
                response.code() == 404 -> {
                    NetworkResult.Error("Seçilen kullanıcı bulunamadı")
                }
                response.isSuccessful && response.body() != null -> {
                    val project = response.body()!!
                    myProjectsCache.clear()
                    Logger.d( "✅ Project created: ${project.name}")
                    NetworkResult.Success(project)
                }
                else -> {
                    Logger.e( "❌ Error: ${response.code()}")
                    NetworkResult.Error("Proje oluşturulamadı: ${response.code()}")
                }
            }
        } catch (e: HttpException) {
            Logger.e( "❌ HTTP Exception: ${e.code()}", e)
            return@withContext NetworkResult.Error("HTTP ${e.code()}: ${e.message()}")
        } catch (e: Exception) {
            Logger.e( "❌ Exception: ${e.message}", e)
            return@withContext NetworkResult.Error(e.message ?: "Bilinmeyen hata")
        }
    }

    suspend fun addMember(
        projectId: String,
        request: AddMemberRequest
    ): NetworkResult<ProjectMember> = withContext(Dispatchers.IO) {
        try {
            Logger.d( "🔍 Adding member to project: $projectId")

            val response = projectApi.addMember(projectId, request)

            return@withContext when {  // ✅ return@withContext EKLE
                response.code() == 401 -> NetworkResult.Error("Oturum süresi doldu")
                response.code() == 403 -> NetworkResult.Error("Üye ekleme yetkiniz yok")
                response.code() == 409 -> NetworkResult.Error("Kullanıcı zaten bu projede")
                response.code() == 400 -> {
                    // ✅ Backend'den gelen gerçek hatayı okumaya çalış
                    val errorBody = response.errorBody()?.string()
                    Logger.e( "Add Member 400 Error Body: $errorBody")
                    
                    if (!errorBody.isNullOrEmpty()) {
                        NetworkResult.Error("Ekleme başarısız (400): $errorBody")
                    } else {
                        NetworkResult.Error("İşlem başarısız veya geçersiz istek (400)")
                    }
                }
                response.isSuccessful && response.body() != null -> {
                    val member = response.body()!!
                    membersCacheMap.remove(projectId)
                    myProjectsCache.clear()
                    Logger.d( "✅ Member added: ${member.fullName}")
                    NetworkResult.Success(member)
                }
                else -> {
                    Logger.e( "❌ Error: ${response.code()}")
                    NetworkResult.Error("Üye eklenemedi: ${response.code()}")
                }
            }
        } catch (e: HttpException) {
            Logger.e( "❌ HTTP Exception: ${e.code()}", e)
            return@withContext NetworkResult.Error("HTTP ${e.code()}: ${e.message()}")
        } catch (e: Exception) {
            Logger.e( "❌ Exception: ${e.message}", e)
            return@withContext NetworkResult.Error(e.message ?: "Bilinmeyen hata")
        }
    }

    /**
     * Projeden üye çıkar
     * DELETE /api/projects/{projectId}/members/{userId}
     */
    suspend fun removeMember(
        projectId: String,
        userId: String
    ): NetworkResult<Unit> = withContext(Dispatchers.IO) {
        try {
            Logger.d( "🔍 Removing member from project: $projectId")

            val response = projectApi.removeMember(projectId, userId)

            return@withContext when {
                response.code() == 401 -> NetworkResult.Error("Oturum süresi doldu")
                response.code() == 403 -> NetworkResult.Error("Üye çıkarma yetkiniz yok")
                response.code() == 400 -> {
                    // ✅ Backend'den gelen gerçek hatayı okumaya çalış
                    val errorBody = response.errorBody()?.string()
                    Logger.e( "Remove Member 400 Error Body: $errorBody")
                    
                    if (!errorBody.isNullOrEmpty()) {
                        // Eğer backend düz metin veya JSON içinde mesaj dönüyorsa onu kullanabiliriz.
                        // Basitçe errorBody'i döndürelim (veya JSON parse edilebilir)
                        NetworkResult.Error("İşlem başarısız: $errorBody") 
                    } else {
                        NetworkResult.Error("Kullanıcı projeden çıkarılamadı (400)")
                    }
                }
                response.isSuccessful -> {
                    membersCacheMap.remove(projectId)
                    myProjectsCache.clear()
                    Logger.d( "✅ Member removed")
                    NetworkResult.Success(Unit)
                }
                else -> {
                    Logger.e( "❌ Error: ${response.code()}")
                    NetworkResult.Error("Üye çıkarılamadı: ${response.code()}")
                }
            }
        } catch (e: HttpException) {
            Logger.e( "❌ HTTP Exception: ${e.code()}", e)
            return@withContext NetworkResult.Error("HTTP ${e.code()}: ${e.message()}")
        } catch (e: Exception) {
            Logger.e( "❌ Exception: ${e.message}", e)
            return@withContext NetworkResult.Error(e.message ?: "Bilinmeyen hata")
        }
    }

    /**
     * Projeyi sil
     * DELETE /api/projects/{projectId}
     */
    suspend fun deleteProject(
        projectId: String
    ): NetworkResult<Unit> = withContext(Dispatchers.IO) {
        try {
            Logger.d( "🔍 Deleting project: $projectId")

            val response = projectApi.deleteProject(projectId)

            return@withContext when {
                response.code() == 401 -> NetworkResult.Error("Oturum süresi doldu")
                response.code() == 403 -> NetworkResult.Error("Proje silme yetkiniz yok")
                response.code() == 400 -> NetworkResult.Error("Aktif görevler var, önce bunları tamamlayın")
                response.isSuccessful -> {
                    myProjectsCache.clear()
                    Logger.d( "✅ Project deleted")
                    NetworkResult.Success(Unit)
                }
                else -> {
                    Logger.e( "❌ Error: ${response.code()}")
                    NetworkResult.Error("Proje silinemedi: ${response.code()}")
                }
            }
        } catch (e: HttpException) {
            Logger.e( "❌ HTTP Exception: ${e.code()}", e)
            return@withContext NetworkResult.Error("HTTP ${e.code()}: ${e.message()}")
        } catch (e: Exception) {
            Logger.e( "❌ Exception: ${e.message}", e)
            return@withContext NetworkResult.Error(e.message ?: "Bilinmeyen hata")
        }
    }
}