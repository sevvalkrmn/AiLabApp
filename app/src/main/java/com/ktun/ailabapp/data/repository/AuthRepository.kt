package com.ktun.ailabapp.data.repository

import android.content.Context
import com.google.gson.Gson
import com.ktun.ailabapp.data.local.datastore.PreferencesManager
import com.ktun.ailabapp.data.remote.dto.request.LoginRequest
import com.ktun.ailabapp.data.remote.dto.request.RegisterRequest
import com.ktun.ailabapp.data.remote.dto.response.AuthResponse
import com.ktun.ailabapp.data.remote.dto.response.ErrorResponse
import com.ktun.ailabapp.data.remote.network.RetrofitClient
import com.ktun.ailabapp.util.NetworkResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository(private val context: Context) {

    private val authApi = RetrofitClient.getAuthApi(context)
    private val preferencesManager = PreferencesManager(context)

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

                android.util.Log.d("AuthRepository", "Login Success!")  // ← message kaldırıldı

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
}