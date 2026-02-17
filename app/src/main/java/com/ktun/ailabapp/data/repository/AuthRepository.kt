package com.ktun.ailabapp.data.repository

import android.net.Uri
import com.ktun.ailabapp.data.local.datastore.PreferencesManager
import com.ktun.ailabapp.data.remote.api.AuthApi
import com.ktun.ailabapp.data.remote.dto.request.FirebaseLoginRequest
import com.ktun.ailabapp.data.remote.dto.request.UpdateEmailRequest
import com.ktun.ailabapp.data.remote.dto.request.UpdatePhoneRequest
import com.ktun.ailabapp.data.remote.dto.request.UpdateProfileImageRequest
import com.ktun.ailabapp.data.remote.dto.response.AuthResponse
import com.ktun.ailabapp.data.remote.dto.response.LeaderboardUserResponse
import com.ktun.ailabapp.data.remote.dto.response.ProfileResponse
import com.ktun.ailabapp.util.FirebaseAuthManager
import com.ktun.ailabapp.util.FirebaseStorageHelper
import com.ktun.ailabapp.util.Logger
import com.ktun.ailabapp.util.NetworkResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val authApi: AuthApi,
    private val preferencesManager: PreferencesManager,
    private val authManager: FirebaseAuthManager
) {

    suspend fun completeRegistration(
        idToken: String,
        fullName: String,
        username: String,
        email: String,
        schoolNumber: String,
        phone: String
    ): NetworkResult<AuthResponse> = withContext(Dispatchers.IO) {
        try {
            val request = FirebaseLoginRequest(
                idToken = idToken,
                fullName = fullName,
                userName = username,
                schoolNumber = schoolNumber,
                phoneNumber = phone
            )

            val response = authApi.loginFirebase(request)

            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()!!
                
                val userId = authResponse.actualUserId
                if (userId.isBlank()) {
                    return@withContext NetworkResult.Error("Backend hatası: Kullanıcı ID bulunamadı")
                }

                val respFullName = authResponse.fullName ?: fullName
                val splitName = respFullName.split(" ")

                preferencesManager.saveUserData(
                    userId = userId,
                    email = authResponse.email ?: email,
                    firstName = splitName.firstOrNull() ?: "",
                    lastName = splitName.drop(1).joinToString(" "),
                    phone = authResponse.phoneNumber ?: phone
                )

                NetworkResult.Success(authResponse)
            } else {
                val errorMessage = when (response.code()) {
                    400 -> "Geçersiz bilgiler"
                    409 -> "Bu email adresi zaten kayıtlı"
                    else -> "Backend Kayıt başarısız: ${response.code()}"
                }
                NetworkResult.Error(errorMessage)
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Bilinmeyen hata")
        }
    }

    suspend fun register(
        fullName: String,
        username: String,
        email: String,
        schoolNumber: String,
        phone: String,
        password: String
    ): NetworkResult<AuthResponse> = withContext(Dispatchers.IO) {
        try {
            val firebaseResult = authManager.signUp(email, password)
            val idToken = firebaseResult.getOrElse {
                return@withContext NetworkResult.Error("Firebase Kayıt Hatası: ${it.message}")
            }

            completeRegistration(idToken, fullName, username, email, schoolNumber, phone)
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Bilinmeyen hata")
        }
    }

    suspend fun login(
        email: String,
        password: String,
        rememberMe: Boolean
    ): NetworkResult<AuthResponse> = withContext(Dispatchers.IO) {
        try {
            val firebaseResult = authManager.signIn(email, password)
            val idToken = firebaseResult.getOrElse {
                return@withContext NetworkResult.Error("Firebase Giriş Hatası: ${it.message}")
            }

            val request = FirebaseLoginRequest(idToken = idToken)
            val response = authApi.loginFirebase(request)

            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()!!
                
                val userId = authResponse.actualUserId
                if (userId.isBlank()) {
                    return@withContext NetworkResult.Error("Backend hatası: Kullanıcı ID eksik")
                }

                preferencesManager.saveRememberMe(rememberMe)

                val fullName = authResponse.fullName ?: ""
                val splitName = fullName.split(" ")

                preferencesManager.saveUserData(
                    userId = userId,
                    email = authResponse.email ?: email,
                    firstName = splitName.firstOrNull() ?: "",
                    lastName = splitName.drop(1).joinToString(" "),
                    phone = authResponse.phoneNumber ?: ""
                )

                NetworkResult.Success(authResponse)
            } else {
                NetworkResult.Error("Giriş başarısız: ${response.code()}")
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Bilinmeyen hata")
        }
    }

    suspend fun logout() {
        try {
            authManager.signOut()
            authApi.logout()
        } catch (e: Exception) {
            // Ignore
        } finally {
            preferencesManager.clearAllData()
        }
    }

    suspend fun updateEmail(password: String, newEmail: String): NetworkResult<Unit> = withContext(Dispatchers.IO) {
        try {
            // 1. Re-authenticate
            val reauthResult = authManager.reauthenticate(password)
            if (reauthResult.isFailure) {
                return@withContext NetworkResult.Error(reauthResult.exceptionOrNull()?.message ?: "Kimlik doğrulama başarısız")
            }

            // 2. Firebase Update
            val firebaseUpdateResult = authManager.updateEmail(newEmail)
            if (firebaseUpdateResult.isFailure) {
                return@withContext NetworkResult.Error(firebaseUpdateResult.exceptionOrNull()?.message ?: "Firebase güncelleme hatası")
            }

            // 3. Backend Sync
            val request = UpdateEmailRequest(newEmail = newEmail)
            val response = authApi.updateEmail(request)

            if (response.isSuccessful) {
                NetworkResult.Success(Unit)
            } else {
                NetworkResult.Error("Backend senkronizasyon hatası: ${response.code()}")
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Bilinmeyen hata")
        }
    }

    suspend fun changePassword(oldPassword: String, newPassword: String): NetworkResult<Unit> = withContext(Dispatchers.IO) {
        try {
            // 1. Eski şifre ile doğrulama (Re-auth)
            val reauthResult = authManager.reauthenticate(oldPassword)
            if (reauthResult.isFailure) {
                return@withContext NetworkResult.Error(reauthResult.exceptionOrNull()?.message ?: "Kimlik doğrulama başarısız")
            }

            // 2. Yeni şifreyi Firebase'de güncelle
            val updateResult = authManager.updatePassword(newPassword)
            if (updateResult.isFailure) {
                return@withContext NetworkResult.Error(updateResult.exceptionOrNull()?.message ?: "Şifre güncelleme hatası")
            }

            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Bilinmeyen bir hata oluştu")
        }
    }

    suspend fun updatePhone(newPhone: String): NetworkResult<Unit> = withContext(Dispatchers.IO) {
        try {
            val request = UpdatePhoneRequest(phoneNumber = newPhone)
            val response = authApi.updatePhone(request)

            if (response.isSuccessful) {
                NetworkResult.Success(Unit)
            } else {
                NetworkResult.Error("Telefon güncelleme hatası: ${response.code()}")
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Bilinmeyen hata")
        }
    }

    suspend fun getProfile(): NetworkResult<ProfileResponse> = withContext(Dispatchers.IO) {
        try {
            val response = authApi.getProfile()

            when {
                response.code() == 401 -> {
                    NetworkResult.Error("Oturum süresi doldu")
                }
                response.isSuccessful && response.body() != null -> {
                    NetworkResult.Success(response.body()!!)
                }
                else -> {
                    NetworkResult.Error("Profil bilgileri alınamadı")
                }
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Bilinmeyen hata")
        }
    }

    suspend fun uploadAndUpdateProfileImage(
        userId: String,
        imageUri: Uri
    ): NetworkResult<ProfileResponse> = withContext(Dispatchers.IO) {
        try {
            val uploadResult = FirebaseStorageHelper.uploadProfileImage(userId, imageUri)

            if (uploadResult.isFailure) {
                return@withContext NetworkResult.Error("Fotoğraf yüklenemedi")
            }

            val downloadUrl = uploadResult.getOrNull()!!
            val request = UpdateProfileImageRequest(profileImageUrl = downloadUrl)
            val response = authApi.updateProfileImage(request)

            if (response.isSuccessful && response.body() != null) {
                NetworkResult.Success(response.body()!!)
            } else {
                NetworkResult.Error("Profil fotoğrafı güncellenemedi")
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Bilinmeyen hata")
        }
    }

    suspend fun selectDefaultAvatar(avatarUrl: String): NetworkResult<ProfileResponse> = withContext(Dispatchers.IO) {
        try {
            val request = UpdateProfileImageRequest(profileImageUrl = avatarUrl)
            val response = authApi.updateProfileImage(request)

            if (response.isSuccessful && response.body() != null) {
                NetworkResult.Success(response.body()!!)
            } else {
                NetworkResult.Error("Avatar seçimi başarısız")
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Bilinmeyen hata")
        }
    }

    suspend fun getDefaultAvatars(): NetworkResult<List<String>> = withContext(Dispatchers.IO) {
        try {
            val response = authApi.getDefaultAvatars()
            if (response.isSuccessful && response.body() != null) {
                NetworkResult.Success(response.body()!!.avatarUrls)
            } else {
                NetworkResult.Error("Avatarlar yüklenemedi")
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Bilinmeyen hata")
        }
    }

    suspend fun getLeaderboard(): NetworkResult<List<LeaderboardUserResponse>> = withContext(Dispatchers.IO) {
        try {
            val response = authApi.getLeaderboard()
            if (response.isSuccessful && response.body() != null) {
                NetworkResult.Success(response.body()!!)
            } else {
                NetworkResult.Error("Leaderboard yüklenemedi")
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Bilinmeyen hata")
        }
    }
}