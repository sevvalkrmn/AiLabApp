package com.ktunailab.ailabapp.data.repository

import com.google.gson.Gson
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

            android.util.Log.d("AuthRepository", """
                Register Request:
                FullName: $fullName
                Username: $username
                Email: $email
                SchoolNumber: $schoolNumber
                Phone: $phone
            """.trimIndent())

            val response = authApi.register(request)

            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()!!

                android.util.Log.d("AuthRepository", "Register Success!")

                // Token'ı kaydet
                authResponse.token?.let { token ->
                    android.util.Log.d("AuthRepository", "Token kaydediliyor: ${token.take(20)}...")
                    preferencesManager.saveToken(token)
                    android.util.Log.d("AuthRepository", "Token kaydedildi!")
                }

                // RefreshToken'ı kaydet
                authResponse.refreshToken?.let { refreshToken ->
                    android.util.Log.d("AuthRepository", "RefreshToken kaydediliyor...")
                    preferencesManager.saveRefreshToken(refreshToken)
                }

                // Kullanıcı bilgilerini kaydet
                preferencesManager.saveUserData(
                    userId = authResponse.user.id,
                    email = authResponse.user.email,
                    firstName = authResponse.user.fullName.split(" ").firstOrNull() ?: "",
                    lastName = authResponse.user.fullName.split(" ").drop(1).joinToString(" "),
                    phone = authResponse.user.phoneNumber ?: ""
                )
                android.util.Log.d("AuthRepository", "Kullanıcı bilgileri kaydedildi!")

                NetworkResult.Success(authResponse)
            } else {
                // Hata durumu
                val errorBody = response.errorBody()?.string()

                android.util.Log.e("AuthRepository", """
                    Register Error:
                    Code: ${response.code()}
                    Error Body: $errorBody
                """.trimIndent())

                val errorResponse = try {
                    Gson().fromJson(errorBody, ErrorResponse::class.java)
                } catch (e: Exception) {
                    null
                }

                val errorMessage = errorResponse?.message ?: when (response.code()) {
                    400 -> "Geçersiz bilgiler. Lütfen kontrol edin."
                    409 -> "Bu email adresi veya kullanıcı adı zaten kayıtlı."
                    422 -> "Girilen bilgiler hatalı."
                    500 -> "Sunucu hatası. Lütfen daha sonra tekrar deneyin."
                    else -> "Kayıt başarısız (${response.code()})"
                }

                NetworkResult.Error(errorMessage)
            }
        } catch (e: java.net.UnknownHostException) {
            android.util.Log.e("AuthRepository", "Register: İnternet bağlantısı yok", e)
            NetworkResult.Error("İnternet bağlantısı yok")
        } catch (e: java.net.SocketTimeoutException) {
            android.util.Log.e("AuthRepository", "Register: Bağlantı zaman aşımı", e)
            NetworkResult.Error("Bağlantı zaman aşımına uğradı")
        } catch (e: Exception) {
            android.util.Log.e("AuthRepository", "Register: Bilinmeyen hata", e)
            NetworkResult.Error(e.message ?: "Bilinmeyen bir hata oluştu")
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

            android.util.Log.d("AuthRepository", "Login Request: EmailOrUsername=$email")

            val response = authApi.login(request)

            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()!!

                android.util.Log.d("AuthRepository", "Login Success!")

                // Token'ı kaydet
                authResponse.token?.let { token ->
                    android.util.Log.d("AuthRepository", "Token kaydediliyor: ${token.take(20)}...")
                    preferencesManager.saveToken(token)
                    android.util.Log.d("AuthRepository", "Token kaydedildi!")
                }

                // RefreshToken'ı kaydet
                authResponse.refreshToken?.let { refreshToken ->
                    android.util.Log.d("AuthRepository", "RefreshToken kaydediliyor...")
                    preferencesManager.saveRefreshToken(refreshToken)
                }

                // Kullanıcı bilgilerini kaydet
                preferencesManager.saveUserData(
                    userId = authResponse.user.id,
                    email = authResponse.user.email,
                    firstName = authResponse.user.fullName.split(" ").firstOrNull() ?: "",
                    lastName = authResponse.user.fullName.split(" ").drop(1).joinToString(" "),
                    phone = authResponse.user.phoneNumber ?: ""
                )
                android.util.Log.d("AuthRepository", "Kullanıcı bilgileri kaydedildi!")

                NetworkResult.Success(authResponse)
            } else {
                val errorBody = response.errorBody()?.string()

                android.util.Log.e("AuthRepository", """
                Login Error:
                Code: ${response.code()}
                Error Body: $errorBody
            """.trimIndent())

                val errorMessage = when (response.code()) {
                    401 -> "Email veya şifre hatalı"
                    404 -> "Kullanıcı bulunamadı"
                    else -> "Giriş başarısız"
                }
                NetworkResult.Error(errorMessage)
            }
        } catch (e: java.net.UnknownHostException) {
            android.util.Log.e("AuthRepository", "Login: İnternet bağlantısı yok", e)
            NetworkResult.Error("İnternet bağlantısı yok")
        } catch (e: java.net.SocketTimeoutException) {
            android.util.Log.e("AuthRepository", "Login: Bağlantı zaman aşımı", e)
            NetworkResult.Error("Bağlantı zaman aşımına uğradı")
        } catch (e: Exception) {
            android.util.Log.e("AuthRepository", "Login: Bilinmeyen hata", e)
            NetworkResult.Error(e.message ?: "Bilinmeyen bir hata oluştu")
        }
    }

    /**
     * Kullanıcı Profil Bilgilerini Getir
     */
    suspend fun getProfile(): NetworkResult<ProfileResponse> = withContext(Dispatchers.IO) {
        try {
            android.util.Log.d("AuthRepository", "Profile bilgileri çekiliyor...")

            val response = authApi.getProfile()

            if (response.isSuccessful && response.body() != null) {
                val profileResponse = response.body()!!

                android.util.Log.d("AuthRepository", """
                    Profile Success:
                    Name: ${profileResponse.fullName}
                    Email: ${profileResponse.email}
                    Score: ${profileResponse.totalScore}
                """.trimIndent())

                NetworkResult.Success(profileResponse)
            } else {
                val errorBody = response.errorBody()?.string()

                android.util.Log.e("AuthRepository", """
                    Profile Error:
                    Code: ${response.code()}
                    Error Body: $errorBody
                """.trimIndent())

                val errorMessage = when (response.code()) {
                    401 -> "Oturum süresi dolmuş. Lütfen tekrar giriş yapın."
                    404 -> "Profil bulunamadı"
                    else -> "Profil bilgileri alınamadı"
                }
                NetworkResult.Error(errorMessage)
            }
        } catch (e: java.net.UnknownHostException) {
            android.util.Log.e("AuthRepository", "Profile: İnternet bağlantısı yok", e)
            NetworkResult.Error("İnternet bağlantısı yok")
        } catch (e: java.net.SocketTimeoutException) {
            android.util.Log.e("AuthRepository", "Profile: Bağlantı zaman aşımı", e)
            NetworkResult.Error("Bağlantı zaman aşımına uğradı")
        } catch (e: Exception) {
            android.util.Log.e("AuthRepository", "Profile: Bilinmeyen hata", e)
            NetworkResult.Error(e.message ?: "Bilinmeyen bir hata oluştu")
        }
    }

    /**
     * Çıkış Yap
     */
    suspend fun logout() {
        try {
            android.util.Log.d("AuthRepository", "Logout initiated")
            authApi.logout()
        } catch (e: Exception) {
            android.util.Log.e("AuthRepository", "Logout error", e)
            // Hata olsa bile local verileri temizle
        } finally {
            preferencesManager.clearAllData()
            android.util.Log.d("AuthRepository", "Local data cleared")
        }
    }

    /**
     * Token Yenile
     */
    suspend fun refreshToken(): NetworkResult<AuthResponse> = withContext(Dispatchers.IO) {
        try {
            val refreshToken = preferencesManager.getRefreshToken().first()

            if (refreshToken.isNullOrEmpty()) {
                android.util.Log.e("AuthRepository", "Refresh token bulunamadı")
                return@withContext NetworkResult.Error("Oturum süresi dolmuş. Lütfen tekrar giriş yapın.")
            }

            android.util.Log.d("AuthRepository", "Token yenileniyor...")

            val request = RefreshTokenRequest(refreshToken)
            val response = authApi.refreshToken(request)

            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()!!

                android.util.Log.d("AuthRepository", "Token yenileme başarılı!")

                // Yeni token'ları kaydet
                authResponse.token?.let { token ->
                    android.util.Log.d("AuthRepository", "Yeni token kaydediliyor...")
                    preferencesManager.saveToken(token)
                }
                authResponse.refreshToken?.let { newRefreshToken ->
                    android.util.Log.d("AuthRepository", "Yeni refresh token kaydediliyor...")
                    preferencesManager.saveRefreshToken(newRefreshToken)
                }

                NetworkResult.Success(authResponse)
            } else {
                val errorBody = response.errorBody()?.string()
                android.util.Log.e("AuthRepository", "Token yenileme hatası: ${response.code()} - $errorBody")

                // Refresh token da geçersizse logout yap
                preferencesManager.clearAllData()
                NetworkResult.Error("Oturum süresi dolmuş. Lütfen tekrar giriş yapın.")
            }
        } catch (e: Exception) {
            android.util.Log.e("AuthRepository", "Token refresh error", e)
            NetworkResult.Error("Token yenilenemedi")
        }
    }

    /**
     * Avatar Güncelle
     */
    suspend fun updateAvatar(avatarId: String): NetworkResult<ProfileResponse> = withContext(Dispatchers.IO) {
        try {
            android.util.Log.d("AuthRepository", "Avatar güncelleniyor: $avatarId")

            // ✅ Request body: avatarFileName ile
            val requestBody = mapOf("avatarFileName" to "$avatarId.png")  // "man01.png"

            val response = authApi.updateAvatar(requestBody)

            if (response.isSuccessful && response.body() != null) {
                val profileResponse = response.body()!!

                android.util.Log.d("AuthRepository", """
                Avatar Update Success:
                New Avatar URL: ${profileResponse.avatarUrl}
            """.trimIndent())

                NetworkResult.Success(profileResponse)
            } else {
                val errorBody = response.errorBody()?.string()

                android.util.Log.e("AuthRepository", """
                Avatar Update Error:
                Code: ${response.code()}
                Error Body: $errorBody
            """.trimIndent())

                val errorMessage = when (response.code()) {
                    400 -> "Geçersiz avatar dosya adı"
                    401 -> "Oturum süresi dolmuş. Lütfen tekrar giriş yapın."
                    404 -> "Avatar bulunamadı"
                    else -> "Avatar güncellenemedi"
                }
                NetworkResult.Error(errorMessage)
            }
        } catch (e: java.net.UnknownHostException) {
            android.util.Log.e("AuthRepository", "UpdateAvatar: İnternet bağlantısı yok", e)
            NetworkResult.Error("İnternet bağlantısı yok")
        } catch (e: java.net.SocketTimeoutException) {
            android.util.Log.e("AuthRepository", "UpdateAvatar: Bağlantı zaman aşımı", e)
            NetworkResult.Error("Bağlantı zaman aşımına uğradı")
        } catch (e: Exception) {
            android.util.Log.e("AuthRepository", "UpdateAvatar: Bilinmeyen hata", e)
            NetworkResult.Error(e.message ?: "Bilinmeyen bir hata oluştu")
        }
    }
}