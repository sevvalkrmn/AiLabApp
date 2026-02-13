# ============================================================
# AiLabApp ProGuard / R8 Kuralları
# ============================================================

# ---- Genel Ayarlar ----
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes Exceptions

# ---- Gson (JSON serialization) ----
-keepclassmembers,allowobfuscation class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# ---- DTO Sınıfları (Gson reflection ile erişir - DOKUNMA) ----
-keep class com.ktun.ailabapp.data.remote.dto.request.** { *; }
-keep class com.ktun.ailabapp.data.remote.dto.response.** { *; }
-keep class com.ktun.ailabapp.data.model.** { *; }

# ---- Retrofit ----
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# Retrofit API interface'leri
-keep interface com.ktun.ailabapp.data.remote.api.** { *; }

# ---- OkHttp ----
-dontwarn okhttp3.**
-dontwarn okio.**
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }

# ---- Firebase ----
-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**

# ---- Hilt / Dagger ----
-dontwarn dagger.**
-keep class dagger.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper { *; }

# ---- Kotlin Coroutines ----
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}

# ---- Jetpack Compose ----
-dontwarn androidx.compose.**

# ---- Coil (Image Loading) ----
-dontwarn coil.**

# ---- Kotlin Serialization (gelecekte kullanılabilir) ----
-keepclassmembers class * {
    @kotlinx.serialization.SerialName <fields>;
}

# ---- Enum sınıflarını koru ----
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# ---- Parcelable ----
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# ---- Release build'de TÜM Log çağrılarını sil ----
-assumenosideeffects class android.util.Log {
    public static int v(...);
    public static int d(...);
    public static int i(...);
    public static int w(...);
    public static int e(...);
    public static int wtf(...);
}

# ---- R8 full mode uyumluluk ----
-dontwarn java.lang.invoke.StringConcatFactory
