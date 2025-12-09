package com.ktunailab.ailabapp.data.repository

import android.net.Uri
import com.google.gson.Gson
import com.ktun.ailabapp.data.remote.dto.request.UpdateProfileImageRequest
import com.ktun.ailabapp.data.remote.dto.response.LeaderboardUserResponse
import com.ktun.ailabapp.util.FirebaseStorageHelper
import com.ktunailab.ailabapp.data.local.datastore.PreferencesManager
import com.ktunailab.ailabapp.data.remote.api.AuthApi
import com.ktunailab.ailabapp.data.remote.dto.request.LoginRequest
import com.ktunailab.ailabapp.data.remote.dto.request.RefreshTokenRequest
import com.ktunailab.ailabapp.data.remote.dto.request.RegisterRequest
import com.ktunailab.ailabapp.data.remote.dto.response.AuthResponse
import com.ktunailab.ailabapp.data.remote.dto.response.ErrorResponse
import com.ktunailab.ailabapp.data.remote.dto.response.ProfileResponse
import com.ktunailab.ailabapp.util.NetworkResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val authApi: AuthApi,
    private val preferencesManager: PreferencesManager
) {

    /**
     * Kullanıcı Kayıt
     */
    suspend fun register(
        fullName: String,
        username: String,
        email: String,
        schoolNumber: String,
        phone: String,
        password: String
    ): NetworkResult<AuthResponse> = withContext(Dispatchers.IO) {
        try {
            val request = RegisterRequest(
                fullName = fullName,
                username = username,
                email = email,
                schoolNumber = schoolNumber,
                phoneNumber = phone,
                password = password
            )

            val response = authApi.register(request)

            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()!!

                authResponse.token?.let { token ->
                    preferencesManager.saveToken(token)
                }

                authResponse.refreshToken?.let { refreshToken ->
                    preferencesManager.saveRefreshToken(refreshToken)
                }

                preferencesManager.saveUserData(
                    userId = authResponse.user.id,
                    email = authResponse.user.email,
                    firstName = authResponse.user.fullName.split(" ").firstOrNull() ?: "",
                    lastName = authResponse.user.fullName.split(" ").drop(1).joinToString(" "),
                    phone = authResponse.user.phoneNumber ?: ""
                )

                NetworkResult.Success(authResponse)
            } else {
                val errorMessage = when (response.code()) {
                    400 -> "Geçersiz bilgiler"
                    409 -> "Bu email adresi zaten kayıtlı"
                    else -> "Kayıt başarısız"
                }
                NetworkResult.Error(errorMessage)
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Bilinmeyen hata")
        }
    }

    /**
     * Kullanıcı Girişi
     */
    suspend fun login(
        email: String,
        password: String
    ): NetworkResult<AuthResponse> = withContext(Dispatchers.IO) {
        try {
            val request = LoginRequest(
                emailOrUsername = email,
                password = password
            )

            val response = authApi.login(request)

            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()!!

                authResponse.token?.let { token ->
                    preferencesManager.saveToken(token)
                }

                authResponse.refreshToken?.let { refreshToken ->
                    preferencesManager.saveRefreshToken(refreshToken)
                }

                preferencesManager.saveUserData(
                    userId = authResponse.user.id,
                    email = authResponse.user.email,
                    firstName = authResponse.user.fullName.split(" ").firstOrNull() ?: "",
                    lastName = authResponse.user.fullName.split(" ").drop(1).joinToString(" "),
                    phone = authResponse.user.phoneNumber ?: ""
                )

                NetworkResult.Success(authResponse)
            } else {
                val errorMessage = when (response.code()) {
                    401 -> "Email veya şifre hatalı"
                    404 -> "Kullanıcı bulunamadı"
                    else -> "Giriş başarısız"
                }
                NetworkResult.Error(errorMessage)
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Bilinmeyen hata")
        }
    }

    /**
     * Kullanıcı Profil Bilgilerini Getir
     */
    suspend fun getProfile(): NetworkResult<ProfileResponse> = withContext(Dispatchers.IO) {
        try {
            val response = authApi.getProfile()

            if (response.isSuccessful && response.body() != null) {
                NetworkResult.Success(response.body()!!)
            } else {
                val errorMessage = when (response.code()) {
                    401 -> "Oturum süresi dolmuş"
                    404 -> "Profil bulunamadı"
                    else -> "Profil bilgileri alınamadı"
                }
                NetworkResult.Error(errorMessage)
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Bilinmeyen hata")
        }
    }

    /**
     * Çıkış Yap
     */
    suspend fun logout() {
        try {
            authApi.logout()
        } catch (e: Exception) {
            // Hata olsa bile local verileri temizle
        } finally {
            preferencesManager.clearAllData()
        }
    }

    /**
     * Token Yenile
     */
    suspend fun refreshToken(): NetworkResult<AuthResponse> = withContext(Dispatchers.IO) {
        try {
            val refreshToken = preferencesManager.getRefreshToken().first()

            if (refreshToken.isNullOrEmpty()) {
                return@withContext NetworkResult.Error("Oturum süresi dolmuş")
            }

            val request = RefreshTokenRequest(refreshToken)
            val response = authApi.refreshToken(request)

            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()!!

                authResponse.token?.let { token ->
                    preferencesManager.saveToken(token)
                }
                authResponse.refreshToken?.let { newRefreshToken ->
                    preferencesManager.saveRefreshToken(newRefreshToken)
                }

                NetworkResult.Success(authResponse)
            } else {
                preferencesManager.clearAllData()
                NetworkResult.Error("Oturum süresi dolmuş")
            }
        } catch (e: Exception) {
            NetworkResult.Error("Token yenilenemedi")
        }
    }

    /**
     * ✅ YENİ: Profil Fotoğrafını Güncelle (Firebase + Backend)
     */
    suspend fun uploadAndUpdateProfileImage(
        userId: String,
        imageUri: Uri
    ): NetworkResult<ProfileResponse> = withContext(Dispatchers.IO) {
        try {
            android.util.Log.d("AuthRepository", "Profil fotoğrafı yükleniyor...")

            val uploadResult = FirebaseStorageHelper.uploadProfileImage(userId, imageUri)

            if (uploadResult.isFailure) {
                val error = uploadResult.exceptionOrNull()
                android.util.Log.e("AuthRepository", "Firebase yükleme hatası", error)
                return@withContext NetworkResult.Error(
                    error?.message ?: "Fotoğraf yüklenemedi"
                )
            }

            val downloadUrl = uploadResult.getOrNull()!!
            android.util.Log.d("AuthRepository", "Firebase yükleme başarılı: $downloadUrl")

            val request = UpdateProfileImageRequest(profileImageUrl = downloadUrl)
            val response = authApi.updateProfileImage(request)

            if (response.isSuccessful && response.body() != null) {
                val profileResponse = response.body()!!

                android.util.Log.d("AuthRepository", """
                    Profile Image Update Success:
                    New Image URL: ${profileResponse.profileImageUrl}
                """.trimIndent())

                NetworkResult.Success(profileResponse)
            } else {
                val errorBody = response.errorBody()?.string()

                android.util.Log.e("AuthRepository", """
                    Profile Image Update Error:
                    Code: ${response.code()}
                    Error Body: $errorBody
                """.trimIndent())

                val errorMessage = when (response.code()) {
                    400 -> "Geçersiz fotoğraf URL'i"
                    401 -> "Oturum süresi dolmuş. Lütfen tekrar giriş yapın."
                    else -> "Profil fotoğrafı güncellenemedi"
                }
                NetworkResult.Error(errorMessage)
            }
        } catch (e: java.net.UnknownHostException) {
            android.util.Log.e("AuthRepository", "UpdateProfileImage: İnternet bağlantısı yok", e)
            NetworkResult.Error("İnternet bağlantısı yok")
        } catch (e: java.net.SocketTimeoutException) {
            android.util.Log.e("AuthRepository", "UpdateProfileImage: Bağlantı zaman aşımı", e)
            NetworkResult.Error("Bağlantı zaman aşımına uğradı")
        } catch (e: Exception) {
            android.util.Log.e("AuthRepository", "UpdateProfileImage: Bilinmeyen hata", e)
            NetworkResult.Error(e.message ?: "Bilinmeyen bir hata oluştu")
        }
    }

    /**
     * ✅ YENİ: Hazır Avatar Seç
     */
    suspend fun selectDefaultAvatar(
        avatarUrl: String
    ): NetworkResult<ProfileResponse> = withContext(Dispatchers.IO) {
        try {
            android.util.Log.d("AuthRepository", "Hazır avatar seçiliyor: $avatarUrl")

            val request = UpdateProfileImageRequest(profileImageUrl = avatarUrl)
            val response = authApi.updateProfileImage(request)

            if (response.isSuccessful && response.body() != null) {
                val profileResponse = response.body()!!

                android.util.Log.d("AuthRepository", """
                    Default Avatar Selection Success:
                    New Avatar URL: ${profileResponse.profileImageUrl}
                """.trimIndent())

                NetworkResult.Success(profileResponse)
            } else {
                val errorBody = response.errorBody()?.string()

                android.util.Log.e("AuthRepository", """
                    Default Avatar Selection Error:
                    Code: ${response.code()}
                    Error Body: $errorBody
                """.trimIndent())

                val errorMessage = when (response.code()) {
                    400 -> "Geçersiz avatar URL'i"
                    401 -> "Oturum süresi dolmuş. Lütfen tekrar giriş yapın."
                    else -> "Avatar seçimi başarısız"
                }
                NetworkResult.Error(errorMessage)
            }
        } catch (e: java.net.UnknownHostException) {
            android.util.Log.e("AuthRepository", "SelectDefaultAvatar: İnternet bağlantısı yok", e)
            NetworkResult.Error("İnternet bağlantısı yok")
        } catch (e: java.net.SocketTimeoutException) {
            android.util.Log.e("AuthRepository", "SelectDefaultAvatar: Bağlantı zaman aşımı", e)
            NetworkResult.Error("Bağlantı zaman aşımına uğradı")
        } catch (e: Exception) {
            android.util.Log.e("AuthRepository", "SelectDefaultAvatar: Bilinmeyen hata", e)
            NetworkResult.Error(e.message ?: "Bilinmeyen bir hata oluştu")
        }
    }

    /**
     * ✅ YENİ: Varsayılan Avatarları Getir
     */
    suspend fun getDefaultAvatars(): NetworkResult<List<String>> = withContext(Dispatchers.IO) {
        try {
            android.util.Log.d("AuthRepository", "Varsayılan avatarlar çekiliyor...")

            val response = authApi.getDefaultAvatars()

            if (response.isSuccessful && response.body() != null) {
                val avatarUrls = response.body()!!.avatarUrls

                android.util.Log.d("AuthRepository", """
                    Default Avatars Success:
                    Count: ${avatarUrls.size}
                """.trimIndent())

                NetworkResult.Success(avatarUrls)
            } else {
                val errorBody = response.errorBody()?.string()

                android.util.Log.e("AuthRepository", """
                    Get Default Avatars Error:
                    Code: ${response.code()}
                    Error Body: $errorBody
                """.trimIndent())

                val errorMessage = when (response.code()) {
                    401 -> "Oturum süresi dolmuş. Lütfen tekrar giriş yapın."
                    else -> "Avatarlar yüklenemedi"
                }
                NetworkResult.Error(errorMessage)
            }
        } catch (e: java.net.UnknownHostException) {
            android.util.Log.e("AuthRepository", "GetDefaultAvatars: İnternet bağlantısı yok", e)
            NetworkResult.Error("İnternet bağlantısı yok")
        } catch (e: java.net.SocketTimeoutException) {
            android.util.Log.e("AuthRepository", "GetDefaultAvatars: Bağlantı zaman aşımı", e)
            NetworkResult.Error("Bağlantı zaman aşımına uğradı")
        } catch (e: Exception) {
            android.util.Log.e("AuthRepository", "GetDefaultAvatars: Bilinmeyen hata", e)
            NetworkResult.Error(e.message ?: "Bilinmeyen bir hata oluştu")
        }
    }

    suspend fun getLeaderboard(): NetworkResult<List<LeaderboardUserResponse>> {
        return try {
            val response = authApi.getLeaderboard()
            if (response.isSuccessful && response.body() != null) {
                NetworkResult.Success(response.body()!!)
            } else {
                NetworkResult.Error(message = response.message() ?: "Bilinmeyen hata")
            }
        } catch (e: Exception) {
            NetworkResult.Error(message = e.localizedMessage ?: "Bağlantı hatası")
        }
    }

}