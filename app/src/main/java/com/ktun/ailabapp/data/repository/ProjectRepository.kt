// data/repository/ProjectRepository.kt

package com.ktun.ailabapp.data.repository

import com.ktun.ailabapp.data.model.UserProject
import com.ktun.ailabapp.data.remote.api.ProjectApi
import com.ktun.ailabapp.data.remote.dto.response.MyProjectsResponse
import com.ktun.ailabapp.data.remote.dto.response.ProjectDetailResponse
import com.ktun.ailabapp.data.remote.dto.response.ProjectMember
import com.ktun.ailabapp.data.remote.dto.response.toUserProject
import com.ktun.ailabapp.util.NetworkResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProjectRepository @Inject constructor(
    private val projectApi: ProjectApi
) {
    /**
     * Kullanıcının kendi projelerini çeker
     * GET /api/projects/my-projects
     */
    suspend fun getMyProjects(roleFilter: String? = null): NetworkResult<List<MyProjectsResponse>> = withContext(Dispatchers.IO) {
        try {
            android.util.Log.d("ProjectRepository", "🔍 Fetching my projects with role filter: $roleFilter")

            val response = projectApi.getMyProjects(roleFilter)

            when {
                response.code() == 401 -> {
                    NetworkResult.Error("Oturum süresi doldu")
                }
                response.isSuccessful && response.body() != null -> {
                    val projects = response.body()!!

                    android.util.Log.d("ProjectRepository", "✅ Loaded ${projects.size} projects")

                    NetworkResult.Success(projects)
                }
                response.isSuccessful && response.body() == null -> {
                    android.util.Log.d("ProjectRepository", "⚠️ No projects found")
                    NetworkResult.Success(emptyList())
                }
                else -> {
                    android.util.Log.e("ProjectRepository", "❌ Error: ${response.code()}")
                    NetworkResult.Error("Projeler yüklenemedi: ${response.code()}")
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("ProjectRepository", "❌ Exception: ${e.message}", e)
            NetworkResult.Error(e.message ?: "Bilinmeyen hata")
        }
    }

    /**
     * Belirli bir kullanıcının projelerini çeker
     * GET /api/projects/user/{userId}
     */
    suspend fun getUserProjects(userId: String): NetworkResult<List<UserProject>> = withContext(Dispatchers.IO) {
        try {
            android.util.Log.d("ProjectRepository", "🔍 Fetching projects for userId: $userId")

            val response = projectApi.getUserProjects(userId)

            when {
                response.code() == 401 -> {
                    NetworkResult.Error("Oturum süresi doldu")
                }
                response.code() == 404 -> {
                    android.util.Log.d("ProjectRepository", "⚠️ User has no projects (404)")
                    NetworkResult.Success(emptyList())
                }
                response.isSuccessful && response.body() != null -> {
                    val projects = response.body()!!.map { it.toUserProject(userId) }

                    android.util.Log.d("ProjectRepository", "✅ Loaded ${projects.size} projects")

                    NetworkResult.Success(projects)
                }
                response.isSuccessful && response.body() == null -> {
                    android.util.Log.d("ProjectRepository", "⚠️ User has no projects (empty body)")
                    NetworkResult.Success(emptyList())
                }
                else -> {
                    android.util.Log.e("ProjectRepository", "❌ Error: ${response.code()}")
                    NetworkResult.Error("Projeler yüklenemedi: ${response.code()}")
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("ProjectRepository", "❌ Exception: ${e.message}", e)
            NetworkResult.Error(e.message ?: "Bilinmeyen hata")
        }
    }

    /**
     * Belirli bir projenin detaylarını getirir
     * GET /api/projects/{id}
     */
    suspend fun getProjectDetail(projectId: String): NetworkResult<ProjectDetailResponse> = withContext(Dispatchers.IO) {
        try {
            android.util.Log.d("ProjectRepository", "🔍 Fetching project detail: $projectId")

            val response = projectApi.getProjectDetail(projectId)

            when {
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

                    android.util.Log.d("ProjectRepository", "✅ Loaded project: ${project.name}")

                    NetworkResult.Success(project)
                }
                else -> {
                    android.util.Log.e("ProjectRepository", "❌ Error: ${response.code()}")
                    NetworkResult.Error("Proje yüklenemedi: ${response.code()}")
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("ProjectRepository", "❌ Exception: ${e.message}", e)
            NetworkResult.Error(e.message ?: "Bilinmeyen hata")
        }
    }

    /**
     * Proje üyelerini listele
     * GET /api/projects/{id}/members
     */
    suspend fun getProjectMembers(projectId: String): NetworkResult<List<ProjectMember>> = withContext(Dispatchers.IO) {
        try {
            android.util.Log.d("ProjectRepository", "🔍 Fetching members for project: $projectId")

            val response = projectApi.getProjectMembers(projectId)

            when {
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

                    android.util.Log.d("ProjectRepository", "✅ Loaded ${members.size} members")

                    NetworkResult.Success(members)
                }
                response.isSuccessful && response.body() == null -> {
                    android.util.Log.d("ProjectRepository", "⚠️ No members found")
                    NetworkResult.Success(emptyList())
                }
                else -> {
                    android.util.Log.e("ProjectRepository", "❌ Error: ${response.code()}")
                    NetworkResult.Error("Üyeler yüklenemedi: ${response.code()}")
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("ProjectRepository", "❌ Exception: ${e.message}", e)
            NetworkResult.Error(e.message ?: "Bilinmeyen hata")
        }
    }
}