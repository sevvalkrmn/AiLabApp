package com.ktunailab.ailabapp.data.repository

import com.ktunailab.ailabapp.data.remote.api.ProjectApi
import com.ktunailab.ailabapp.data.remote.dto.request.CreateProjectRequest
import com.ktunailab.ailabapp.data.remote.dto.request.UpdateProjectRequest
import com.ktunailab.ailabapp.data.remote.dto.response.MyProjectsResponse
import com.ktunailab.ailabapp.data.remote.dto.response.ProjectDetailResponse
import com.ktunailab.ailabapp.data.remote.dto.response.ProjectMember
import com.ktunailab.ailabapp.util.NetworkResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProjectRepository @Inject constructor(
    private val projectApi: ProjectApi
) {

    /**
     * Kullanıcının projelerini getir
     */
    suspend fun getMyProjects(role: String? = null): NetworkResult<List<MyProjectsResponse>> =
        withContext(Dispatchers.IO) {
            try {
                android.util.Log.d("ProjectRepository", "Projeler çekiliyor... Role: $role")

                val response = projectApi.getMyProjects(role)

                if (response.isSuccessful && response.body() != null) {
                    val projects = response.body()!!

                    android.util.Log.d("ProjectRepository", "Proje sayısı: ${projects.size}")

                    // HER PROJENİN ROLÜNÜ LOGLA
                    projects.forEachIndexed { index, project ->
                        android.util.Log.d("ProjectRepository", "Proje $index: ${project.name} - Role: '${project.userRole}'")
                    }

                    NetworkResult.Success(projects)
                } else {
                    val errorBody = response.errorBody()?.string()

                    android.util.Log.e("ProjectRepository", """
                        Projeler Error:
                        Code: ${response.code()}
                        Error: $errorBody
                    """.trimIndent())

                    val errorMessage = when (response.code()) {
                        401 -> "Oturum süresi dolmuş. Lütfen tekrar giriş yapın."
                        403 -> "Bu işlem için yetkiniz yok."
                        404 -> "Proje bulunamadı."
                        else -> "Projeler yüklenemedi."
                    }
                    NetworkResult.Error(errorMessage)
                }
            } catch (e: Exception) {
                android.util.Log.e("ProjectRepository", "Projeler Exception", e)
                NetworkResult.Error(e.message ?: "Bilinmeyen hata")
            }
        }

    /**
     * Proje detayını getir
     */
    suspend fun getProjectDetail(projectId: String): NetworkResult<ProjectDetailResponse> =
        withContext(Dispatchers.IO) {
            try {
                android.util.Log.d("ProjectRepository", "Proje detayı çekiliyor: $projectId")

                val response = projectApi.getProjectDetail(projectId)

                if (response.isSuccessful && response.body() != null) {
                    val project = response.body()!!

                    android.util.Log.d("ProjectRepository", "Proje: ${project.name}")

                    NetworkResult.Success(project)
                } else {
                    val errorBody = response.errorBody()?.string()

                    android.util.Log.e("ProjectRepository", """
                        Proje Detay Error:
                        Code: ${response.code()}
                        Error: $errorBody
                    """.trimIndent())

                    val errorMessage = when (response.code()) {
                        401 -> "Oturum süresi dolmuş."
                        403 -> "Bu projeyi görüntüleme yetkiniz yok."
                        404 -> "Proje bulunamadı."
                        else -> "Proje detayı yüklenemedi."
                    }
                    NetworkResult.Error(errorMessage)
                }
            } catch (e: Exception) {
                android.util.Log.e("ProjectRepository", "Proje Detay Exception", e)
                NetworkResult.Error(e.message ?: "Bilinmeyen hata")
            }
        }

    /**
     * Yeni proje oluştur
     */
    suspend fun createProject(
        name: String,
        description: String?
    ): NetworkResult<ProjectDetailResponse> = withContext(Dispatchers.IO) {
        try {
            android.util.Log.d("ProjectRepository", "Proje oluşturuluyor: $name")

            val request = CreateProjectRequest(name, description)
            val response = projectApi.createProject(request)

            if (response.isSuccessful && response.body() != null) {
                val project = response.body()!!

                android.util.Log.d("ProjectRepository", "Proje oluşturuldu: ${project.id}")

                NetworkResult.Success(project)
            } else {
                val errorBody = response.errorBody()?.string()

                android.util.Log.e("ProjectRepository", "Proje oluşturma hatası: $errorBody")

                val errorMessage = when (response.code()) {
                    400 -> "Geçersiz proje bilgileri."
                    403 -> "Proje oluşturma yetkiniz yok."
                    else -> "Proje oluşturulamadı."
                }
                NetworkResult.Error(errorMessage)
            }
        } catch (e: Exception) {
            android.util.Log.e("ProjectRepository", "Proje oluşturma exception", e)
            NetworkResult.Error(e.message ?: "Bilinmeyen hata")
        }
    }

    /**
     * Projeyi güncelle
     */
    suspend fun updateProject(
        projectId: String,
        name: String?,
        description: String?
    ): NetworkResult<ProjectDetailResponse> = withContext(Dispatchers.IO) {
        try {
            android.util.Log.d("ProjectRepository", "Proje güncelleniyor: $projectId")

            val request = UpdateProjectRequest(name, description)
            val response = projectApi.updateProject(projectId, request)

            if (response.isSuccessful && response.body() != null) {
                val project = response.body()!!

                android.util.Log.d("ProjectRepository", "Proje güncellendi")

                NetworkResult.Success(project)
            } else {
                val errorBody = response.errorBody()?.string()

                android.util.Log.e("ProjectRepository", "Proje güncelleme hatası: $errorBody")

                val errorMessage = when (response.code()) {
                    400 -> "En az bir alan güncellenmelidir."
                    403 -> "Bu projeyi güncelleme yetkiniz yok."
                    404 -> "Proje bulunamadı."
                    else -> "Proje güncellenemedi."
                }
                NetworkResult.Error(errorMessage)
            }
        } catch (e: Exception) {
            android.util.Log.e("ProjectRepository", "Proje güncelleme exception", e)
            NetworkResult.Error(e.message ?: "Bilinmeyen hata")
        }
    }

    /**
     * Projeyi sil
     */
    suspend fun deleteProject(projectId: String): NetworkResult<Unit> =
        withContext(Dispatchers.IO) {
            try {
                android.util.Log.d("ProjectRepository", "Proje siliniyor: $projectId")

                val response = projectApi.deleteProject(projectId)

                if (response.isSuccessful) {
                    android.util.Log.d("ProjectRepository", "Proje silindi")
                    NetworkResult.Success(Unit)
                } else {
                    val errorBody = response.errorBody()?.string()

                    android.util.Log.e("ProjectRepository", "Proje silme hatası: $errorBody")

                    val errorMessage = when (response.code()) {
                        400 -> "Aktif görevleri olan proje silinemez."
                        403 -> "Proje silme yetkiniz yok."
                        404 -> "Proje bulunamadı."
                        else -> "Proje silinemedi."
                    }
                    NetworkResult.Error(errorMessage)
                }
            } catch (e: Exception) {
                android.util.Log.e("ProjectRepository", "Proje silme exception", e)
                NetworkResult.Error(e.message ?: "Bilinmeyen hata")
            }
        }

    /**
     * Proje üyelerini getir
     */
    suspend fun getProjectMembers(projectId: String): NetworkResult<List<ProjectMember>> =
        withContext(Dispatchers.IO) {
            try {
                android.util.Log.d("ProjectRepository", "Proje üyeleri çekiliyor: $projectId")

                val response = projectApi.getProjectMembers(projectId)

                if (response.isSuccessful && response.body() != null) {
                    val members = response.body()!!

                    android.util.Log.d("ProjectRepository", "Üye sayısı: ${members.size}")

                    NetworkResult.Success(members)
                } else {
                    val errorBody = response.errorBody()?.string()

                    android.util.Log.e("ProjectRepository", "Üyeler error: $errorBody")

                    NetworkResult.Error("Üyeler yüklenemedi.")
                }
            } catch (e: Exception) {
                android.util.Log.e("ProjectRepository", "Üyeler exception", e)
                NetworkResult.Error(e.message ?: "Bilinmeyen hata")
            }
        }
}