package com.ktun.ailabapp.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import androidx.core.net.toFile
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

object ImageCompressor {

    /**
     * Galeriden seçilen fotoğrafı WebP formatına çevirir ve %75 kalitede sıkıştırır
     * ✅ EXIF rotation bilgisini okuyup fotoğrafı doğru yönde gösterir
     * @param context Context
     * @param imageUri Galeriden seçilen fotoğrafın URI'si
     * @return Optimize edilmiş fotoğrafın URI'si
     */
    suspend fun compressToWebP(context: Context, imageUri: Uri): Result<Uri> {
        return try {
            // 1. URI'den Bitmap oluştur
            val inputStream = context.contentResolver.openInputStream(imageUri)
            val originalBitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            if (originalBitmap == null) {
                return Result.failure(Exception("Fotoğraf okunamadı"))
            }

            // ✅ 2. EXIF rotation bilgisini oku ve fotoğrafı döndür
            val rotatedBitmap = rotateImageIfRequired(context, imageUri, originalBitmap)

            // 3. Bitmap'i ölçeklendir (max 1024x1024)
            val scaledBitmap = scaleBitmap(rotatedBitmap, 1024)

            // 4. WebP formatına çevir ve sıkıştır (%75 kalite)
            val outputStream = ByteArrayOutputStream()
            scaledBitmap.compress(Bitmap.CompressFormat.WEBP, 75, outputStream)
            val webpBytes = outputStream.toByteArray()

            // 5. Geçici dosyaya kaydet
            val tempFile = File(context.cacheDir, "profile_${System.currentTimeMillis()}.webp")
            FileOutputStream(tempFile).use { fileOut ->
                fileOut.write(webpBytes)
            }

            // 6. Bitmap'leri temizle (memory leak önleme)
            originalBitmap.recycle()
            if (rotatedBitmap != originalBitmap) {
                rotatedBitmap.recycle()
            }
            scaledBitmap.recycle()

            android.util.Log.d("ImageCompressor", "Image compressed and rotated successfully")
            Result.success(Uri.fromFile(tempFile))

        } catch (e: Exception) {
            android.util.Log.e("ImageCompressor", "Compression error", e)
            Result.failure(e)
        }
    }

    /**
     * ✅ YENİ: EXIF orientation bilgisini okuyup fotoğrafı doğru yönde döndürür
     */
    private fun rotateImageIfRequired(context: Context, imageUri: Uri, bitmap: Bitmap): Bitmap {
        return try {
            val inputStream = context.contentResolver.openInputStream(imageUri)
            val exif = ExifInterface(inputStream!!)
            inputStream.close()

            val orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )

            val rotationDegrees = when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> 90
                ExifInterface.ORIENTATION_ROTATE_180 -> 180
                ExifInterface.ORIENTATION_ROTATE_270 -> 270
                else -> 0
            }

            android.util.Log.d("ImageCompressor", "EXIF Orientation: $orientation, Rotation: $rotationDegrees°")

            if (rotationDegrees != 0) {
                val matrix = Matrix().apply {
                    postRotate(rotationDegrees.toFloat())
                }
                Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
            } else {
                bitmap
            }

        } catch (e: Exception) {
            android.util.Log.e("ImageCompressor", "Error reading EXIF data, using original bitmap", e)
            bitmap // Hata olursa orijinal bitmap'i döndür
        }
    }

    /**
     * Bitmap'i belirtilen maksimum boyuta ölçeklendirir (aspect ratio korunur)
     */
    private fun scaleBitmap(bitmap: Bitmap, maxSize: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        // Zaten küçükse olduğu gibi döndür
        if (width <= maxSize && height <= maxSize) {
            return bitmap
        }

        // Aspect ratio'yu koru
        val scale = if (width > height) {
            maxSize.toFloat() / width
        } else {
            maxSize.toFloat() / height
        }

        val newWidth = (width * scale).toInt()
        val newHeight = (height * scale).toInt()

        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }
}