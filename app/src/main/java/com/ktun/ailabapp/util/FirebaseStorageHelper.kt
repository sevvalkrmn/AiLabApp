package com.ktun.ailabapp.util

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.UUID

object FirebaseStorageHelper {

    private val storage = FirebaseStorage.getInstance()
    private val profileImagesRef = storage.reference.child("profile_images")

    /**
     * Kullanıcının seçtiği fotoğrafı Firebase Storage'a yükler
     * @param userId Kullanıcı ID'si (dosya adı için)
     * @param imageUri Yüklenecek resmin URI'si
     * @return Firebase Storage'daki download URL'i
     */
    suspend fun uploadProfileImage(userId: String, imageUri: Uri): Result<String> {
        return try {
            // Benzersiz dosya adı oluştur
            val fileName = "${userId}_${UUID.randomUUID()}.jpg"
            val fileRef = profileImagesRef.child(fileName)

            // Dosyayı yükle
            fileRef.putFile(imageUri).await()

            // Download URL'i al
            val downloadUrl = fileRef.downloadUrl.await().toString()

            Result.success(downloadUrl)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Eski profil fotoğrafını Firebase Storage'dan siler (opsiyonel)
     * Sadece Firebase Storage URL'lerini siler, hazır avatarları silmez
     */
    suspend fun deleteProfileImage(imageUrl: String): Result<Unit> {
        return try {
            // Sadece Firebase Storage URL'lerini sil
            if (imageUrl.contains("firebasestorage.googleapis.com") &&
                imageUrl.contains("profile_images")) {
                val fileRef = storage.getReferenceFromUrl(imageUrl)
                fileRef.delete().await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            // Silme hatası kritik değil, log'layıp devam et
            Result.failure(e)
        }
    }
}