package com.ktun.ailabapp.util

import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseAuthManager @Inject constructor() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // Cached token — IdTokenListener tarafından güncellenir, interceptor senkron okur
    @Volatile
    private var cachedToken: String? = null

    init {
        auth.addIdTokenListener(FirebaseAuth.IdTokenListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                user.getIdToken(false).addOnSuccessListener { result ->
                    cachedToken = result.token
                }
            } else {
                cachedToken = null
            }
        })
    }

    /** OkHttp interceptor için non-blocking token erişimi */
    fun getTokenSync(): String? = cachedToken

    /** Firebase Auth hazır olana kadar bekler, ardından mevcut kullanıcıyı döner */
    suspend fun awaitCurrentUser(): FirebaseUser? = suspendCancellableCoroutine { cont ->
        val listener = object : FirebaseAuth.AuthStateListener {
            override fun onAuthStateChanged(firebaseAuth: FirebaseAuth) {
                firebaseAuth.removeAuthStateListener(this)
                cont.resume(firebaseAuth.currentUser)
            }
        }
        auth.addAuthStateListener(listener)
        cont.invokeOnCancellation {
            auth.removeAuthStateListener(listener)
        }
    }

    // Şu anki kullanıcı
    val currentUser: FirebaseUser?
        get() = auth.currentUser

    // Token değişikliklerini dinleyen Flow (Refresh Token için)
    val idTokenFlow: Flow<String?> = callbackFlow {
        val listener = FirebaseAuth.IdTokenListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                user.getIdToken(false).addOnSuccessListener { result ->
                    trySend(result.token)
                }
            } else {
                trySend(null)
            }
        }
        auth.addIdTokenListener(listener)
        awaitClose { auth.removeIdTokenListener(listener) }
    }

    // Geçerli Token'ı al (Suspend)
    suspend fun getIdToken(): String? {
        return try {
            auth.currentUser?.getIdToken(false)?.await()?.token
        } catch (e: Exception) {
            null
        }
    }

    // Login
    suspend fun signIn(email: String, pass: String): Result<String> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, pass).await()
            val token = result.user?.getIdToken(false)?.await()?.token
            if (token != null) Result.success(token)
            else Result.failure(Exception("Token alınamadı"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Register
    suspend fun signUp(email: String, pass: String): Result<String> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, pass).await()
            val token = result.user?.getIdToken(false)?.await()?.token
            if (token != null) Result.success(token)
            else Result.failure(Exception("Token alınamadı"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Logout
    fun signOut() {
        auth.signOut()
    }

    // Re-authenticate
    suspend fun reauthenticate(password: String): Result<Unit> {
        return try {
            val user = auth.currentUser ?: return Result.failure(Exception("Kullanıcı oturumu yok"))
            
            user.reload().await() // Kullanıcıyı tazele

            val credential = EmailAuthProvider.getCredential(user.email!!, password)
            user.reauthenticate(credential).await()
            Result.success(Unit)
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            Result.failure(Exception("Re-auth: Mevcut şifre hatalı"))
        } catch (e: FirebaseAuthException) {
            if (e.errorCode == "ERROR_OPERATION_NOT_ALLOWED" || e.errorCode == "operation-not-allowed") {
                Result.failure(Exception("Re-auth: Firebase konsolunda işlem kısıtlı"))
            } else {
                Result.failure(Exception("Re-auth Firebase: ${e.errorCode}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Re-auth Hata: ${e.message}"))
        }
    }

    // Update Email
    suspend fun updateEmail(newEmail: String): Result<Unit> {
        return try {
            val user = auth.currentUser ?: return Result.failure(Exception("Kullanıcı oturumu yok"))
            user.updateEmail(newEmail).await()
            Result.success(Unit)
        } catch (e: FirebaseAuthUserCollisionException) {
            Result.failure(Exception("Update: Bu e-posta adresi zaten kullanımda"))
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            Result.failure(Exception("Update: Geçersiz e-posta formatı"))
        } catch (e: FirebaseAuthRecentLoginRequiredException) {
            Result.failure(Exception("Update: Güvenlik gereği tekrar giriş yapmanız gerekiyor"))
        } catch (e: FirebaseAuthException) {
             if (e.errorCode == "ERROR_OPERATION_NOT_ALLOWED" || e.errorCode == "operation-not-allowed") {
                Result.failure(Exception("Update: E-posta güncelleme izni yok. (Firebase Konsolu -> Authentication -> Settings -> User actions -> 'Email enumeration protection' kapatmayı deneyin)"))
            } else {
                Result.failure(Exception("Update Firebase: ${e.errorCode}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Update Hata: ${e.message}"))
        }
    }

    // ✅ YENİ: Şifre Güncelleme
    suspend fun updatePassword(newPassword: String): Result<Unit> {
        return try {
            val user = auth.currentUser ?: return Result.failure(Exception("Kullanıcı oturumu yok"))
            user.updatePassword(newPassword).await()
            Result.success(Unit)
        } catch (e: FirebaseAuthWeakPasswordException) {
            Result.failure(Exception("Şifre çok zayıf. Lütfen en az 6 karakterli daha güçlü bir şifre seçin."))
        } catch (e: FirebaseAuthRecentLoginRequiredException) {
            Result.failure(Exception("Güvenlik gereği tekrar giriş yapmanız gerekiyor."))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
