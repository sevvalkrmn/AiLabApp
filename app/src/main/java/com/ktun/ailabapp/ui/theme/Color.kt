package com.ktun.ailabapp.ui.theme

import androidx.compose.ui.graphics.Color

/*
 * ============================================================================
 *  MATERIAL 3 ÇEKİRDEK RENK ŞEMASI (Color Roles)
 * ============================================================================
 *  Seed (marka) rengi: #07137F — Material 3 HCT renk uzayından türetilmiştir
 *  (bkz. Material Theme Builder / material-color-utilities).
 *
 *  "primary" rolü, standart "TonalSpot" varyantının uyguladığı ton/doygunluk
 *  sıkıştırması (tone 40, chroma 36) YERİNE marka renginin kendi tonu ve
 *  doygunluğu ("brand fidelity", chroma 54.6 tone 16) ile üretilmiştir; aksi
 *  halde koyu lacivert marka rengi splash/topbar/bottomnav gibi büyük renk
 *  alanlarında soluk/gri bir maviye dönüşüyordu. primaryContainer/
 *  onPrimaryContainer ve dark şema tonları da aynı marka paletinden (aynı
 *  hue+chroma, standart ton dilimleri: 90/10 açık, 80/20/30/90 koyu) alınır.
 *  Diğer roller (secondary/tertiary/nötr/hata) standart TonalSpot türetimidir.
 *
 *  Bu sabitler SADECE Theme.kt içindeki lightColorScheme()/darkColorScheme()
 *  tanımları için kullanılır.
 *
 *  Ekranlarda ve bileşenlerde renklere BURADAN değil,
 *  MaterialTheme.colorScheme.<rol> üzerinden erişilmelidir
 *  (ör. MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.onSurface).
 * ============================================================================
 */

// --- Light color scheme ---
val Md3LightPrimary = Color(0xFF07137F)
val Md3LightOnPrimary = Color(0xFFFFFFFF)
val Md3LightPrimaryContainer = Color(0xFFDFE0FF)
val Md3LightOnPrimaryContainer = Color(0xFF000766)
val Md3LightSecondary = Color(0xFF5C5D72)
val Md3LightOnSecondary = Color(0xFFFFFFFF)
val Md3LightSecondaryContainer = Color(0xFFE1E0F9)
val Md3LightOnSecondaryContainer = Color(0xFF444559)
val Md3LightTertiary = Color(0xFF78536B)
val Md3LightOnTertiary = Color(0xFFFFFFFF)
val Md3LightTertiaryContainer = Color(0xFFFFD7EF)
val Md3LightOnTertiaryContainer = Color(0xFF5E3C53)
val Md3LightError = Color(0xFFBA1A1A)
val Md3LightOnError = Color(0xFFFFFFFF)
val Md3LightErrorContainer = Color(0xFFFFDAD6)
val Md3LightOnErrorContainer = Color(0xFF93000A)
val Md3LightBackground = Color(0xFFFBF8FF)
val Md3LightOnBackground = Color(0xFF1B1B21)
val Md3LightSurface = Color(0xFFFBF8FF)
val Md3LightOnSurface = Color(0xFF1B1B21)
val Md3LightSurfaceVariant = Color(0xFFE3E1EC)
val Md3LightOnSurfaceVariant = Color(0xFF46464F)
val Md3LightOutline = Color(0xFF777680)
val Md3LightOutlineVariant = Color(0xFFC7C5D0)
val Md3LightScrim = Color(0xFF000000)
val Md3LightInverseSurface = Color(0xFF303036)
val Md3LightInverseOnSurface = Color(0xFFF2EFF7)
val Md3LightInversePrimary = Color(0xFF2536D8) // koyu temanın primary'si
val Md3LightSurfaceDim = Color(0xFFDBD9E0)
val Md3LightSurfaceBright = Color(0xFFFBF8FF)
val Md3LightSurfaceContainerLowest = Color(0xFFFFFFFF)
val Md3LightSurfaceContainerLow = Color(0xFFF5F2FA)
val Md3LightSurfaceContainer = Color(0xFFEFEDF4)
val Md3LightSurfaceContainerHigh = Color(0xFFEAE7EF)
val Md3LightSurfaceContainerHighest = Color(0xFFE4E1E9)

/*
 * --- Dark color scheme ---
 * Bu bölümdeki değerler, projeye verilen özel dark mode renk paletinden
 * gelir (marka ekibinin belirlediği sabit hex'ler — bir HCT/TonalSpot
 * türetimi DEĞİLDİR, çünkü koyu temada "primary" bilerek açık temayla aynı
 * doygun mavi ailesinde tutulmuştur):
 *
 *   DarkBackground=#090D24  DarkSurface=#101637      DarkSurfaceVariant=#171E45
 *   DarkPrimary=#2536D8     DarkPrimaryPressed=#19269F  DarkPrimaryLight=#7180FF
 *   DarkTextPrimary=#F5F6FF DarkTextSecondary=#B8BED8   DarkTextDisabled=#858CA8
 *   DarkOutline=#535B7D     DarkError=#FF6B73           DarkSuccess=#50D890
 *
 * primaryContainer/errorContainer/successContainer ve surface-container
 * kademeleri spec'te verilmediği için, aynı ailenin HCT tonlarından
 * (aynı hue/chroma, farklı tone) tutarlı biçimde türetildi.
 */
val Md3DarkPrimary = Color(0xFF360185)
val Md3DarkOnPrimary = Color(0xFFFFFFFF)
val Md3DarkPrimaryContainer = Color(0xFF19269F) // = DarkPrimaryPressed (aynı mavi ailesinden koyu ton)
val Md3DarkOnPrimaryContainer = Color(0xFFF5F6FF) // = DarkTextPrimary
val Md3DarkSecondary = Color(0xFFC4C4DD)
val Md3DarkOnSecondary = Color(0xFF2D2F42)
val Md3DarkSecondaryContainer = Color(0xFF444559)
val Md3DarkOnSecondaryContainer = Color(0xFFE1E0F9)
val Md3DarkTertiary = Color(0xFFE7B9D6)
val Md3DarkOnTertiary = Color(0xFF45263C)
val Md3DarkTertiaryContainer = Color(0xFF5E3C53)
val Md3DarkOnTertiaryContainer = Color(0xFFFFD7EF)
val Md3DarkError = Color(0xFFFF6B73)
val Md3DarkOnError = Color(0xFF090D24) // = DarkBackground (beyaz metin yetersiz kontrast veriyordu)
val Md3DarkErrorContainer = Color(0xFF7C031C)
val Md3DarkOnErrorContainer = Color(0xFFF5F6FF)
val Md3DarkBackground = Color(0xFF090D24)
val Md3DarkOnBackground = Color(0xFFF5F6FF)
val Md3DarkSurface = Color(0xFF101637)
val Md3DarkOnSurface = Color(0xFFF5F6FF)
val Md3DarkSurfaceVariant = Color(0xFF171E45)
val Md3DarkOnSurfaceVariant = Color(0xFFB8BED8) // = DarkTextSecondary
val Md3DarkOutline = Color(0xFF535B7D)
val Md3DarkOutlineVariant = Color(0xFF323A5A)
val Md3DarkScrim = Color(0xFF000000)
val Md3DarkInverseSurface = Color(0xFFE4E1E9)
val Md3DarkInverseOnSurface = Color(0xFF303036)
val Md3DarkInversePrimary = Color(0xFF07137F) // açık temanın primary'si (marka rengi)
val Md3DarkSurfaceDim = Color(0xFF090D24) // = DarkBackground
val Md3DarkSurfaceBright = Color(0xFF222849)
val Md3DarkSurfaceContainerLowest = Color(0xFF050B2C)
val Md3DarkSurfaceContainerLow = Color(0xFF13193A)
val Md3DarkSurfaceContainer = Color(0xFF161C3D)
val Md3DarkSurfaceContainerHigh = Color(0xFF171E45) // = DarkSurfaceVariant
val Md3DarkSurfaceContainerHighest = Color(0xFF1D2345)

// Koyu temaya özel ek marka tonları — MD3 çekirdek rol setinde karşılığı
// olmayan, buton/textfield/link etkileşim durumları için (bkz. Theme.kt >
// AiLabExtendedColors: primaryPressed / focusAccent / textDisabled).
val Md3DarkPrimaryPressed = Color(0xFF19269F)
val Md3DarkFocusAccent = Color(0xFF7180FF) // = DarkPrimaryLight
val Md3DarkTextDisabled = Color(0xFF858CA8)

/*
 * ============================================================================
 *  GENİŞLETİLMİŞ (MARKA-ÖZEL) SEMANTİK RENKLER
 * ============================================================================
 *  MD3'ün çekirdek rol setinde karşılığı olmayan durum renkleri
 *  (başarı / uyarı / bilgi) ve rütbe rozeti renkleri. Her biri, çekirdek
 *  şemayla aynı HCT tonal mantığıyla üretilmiştir (base=tone40/80,
 *  onBase=tone100/20, container=tone90/30, onContainer=tone10/90).
 *
 *  Erişim: MaterialTheme değil, AiLabTheme.extendedColors üzerinden yapılır
 *  (bkz. Theme.kt > AiLabExtendedColors). Ör: AiLabTheme.extendedColors.success
 * ============================================================================
 */

// Success — light
val Md3LightSuccess = Color(0xFF1B6D24)
val Md3LightOnSuccess = Color(0xFFFFFFFF)
val Md3LightSuccessContainer = Color(0xFFA3F69C)
val Md3LightOnSuccessContainer = Color(0xFF002204)
// Success — dark (proje paletindeki DarkSuccess=#50D890)
val Md3DarkSuccess = Color(0xFF50D890)
val Md3DarkOnSuccess = Color(0xFF090D24) // = DarkBackground (beyaz metin yetersiz kontrast veriyordu)
val Md3DarkSuccessContainer = Color(0xFF004527)
val Md3DarkOnSuccessContainer = Color(0xFFF5F6FF)

// Warning — light
val Md3LightWarning = Color(0xFF9C4400)
val Md3LightOnWarning = Color(0xFFFFFFFF)
val Md3LightWarningContainer = Color(0xFFFFDBCA)
val Md3LightOnWarningContainer = Color(0xFF331200)
// Warning — dark
val Md3DarkWarning = Color(0xFFFFB68E)
val Md3DarkOnWarning = Color(0xFF542200)
val Md3DarkWarningContainer = Color(0xFF773300)
val Md3DarkOnWarningContainer = Color(0xFFFFDBCA)

// Info — light
val Md3LightInfo = Color(0xFF00639A)
val Md3LightOnInfo = Color(0xFFFFFFFF)
val Md3LightInfoContainer = Color(0xFFCEE5FF)
val Md3LightOnInfoContainer = Color(0xFF001D32)
// Info — dark
val Md3DarkInfo = Color(0xFF96CCFF)
val Md3DarkOnInfo = Color(0xFF003353)
val Md3DarkInfoContainer = Color(0xFF004A75)
val Md3DarkOnInfoContainer = Color(0xFFCEE5FF)

// Rütbe / sıralama rozetleri — temadan bağımsız sabit vurgu renkleri
val Md3RankGold = Color(0xFFFFD700)
val Md3RankSilver = Color(0xFFC0C0C0)
val Md3RankBronze = Color(0xFFB8860B)

/*
 * ============================================================================
 *  LEGACY (KALDIRILACAK) RENKLER
 * ============================================================================
 *  Bu bölüm, MD3 geçişi tamamlanana kadar geriye dönük uyumluluk için
 *  tutuluyor. Sayfa sayfa geçiş yapılırken buradaki sabitlerin kullanımı
 *  MaterialTheme.colorScheme veya AiLabTheme.extendedColors ile
 *  değiştirilip, artık hiçbir yerde referans kalmayınca buradan silinecek.
 *  YENİ KOD BU BÖLÜMDEKİ SABİTLERİ KULLANMAMALIDIR.
 * ============================================================================
 */

// Uygulama ana renkleri
@Deprecated("MD3 geçişi: MaterialTheme.colorScheme.primary kullanın")
val PrimaryBlue = Color(0xFF07137F)
@Deprecated("MD3 geçişi: MaterialTheme.colorScheme.secondary kullanın")
val SecondaryBlue = Color(0xFF0D24D8)
@Deprecated("MD3 geçişi: MaterialTheme.colorScheme.background kullanın")
val BackgroundLight = Color(0xFFF1F1FC)
@Deprecated("MD3 geçişi: MaterialTheme.colorScheme.onSurfaceVariant kullanın")
val TextGray = Color(0xFF757575)
@Deprecated("MD3 geçişi: MaterialTheme.colorScheme.outline kullanın")
val BorderGray = Color(0xFFE0E0E0)
@Deprecated("MD3 geçişi: MaterialTheme.colorScheme.onSurfaceVariant kullanın")
val LabelGray = Color(0xFF9E9E9E)
@Deprecated("MD3 geçişi: bağlama göre MaterialTheme.colorScheme.onPrimary / surface kullanın")
val White = Color(0xFFFFFFFF)
@Deprecated("MD3 geçişi: bağlama göre MaterialTheme.colorScheme.onSurface kullanın")
val Black = Color(0xFF000000)

// Durum Renkleri
@Deprecated("MD3 geçişi: MaterialTheme.colorScheme.error kullanın")
val ErrorRed = Color(0xFFD32F2F)
@Deprecated("MD3 geçişi: AiLabTheme.extendedColors.success kullanın")
val SuccessGreen = Color(0xFF4CAF50)
@Deprecated("MD3 geçişi: AiLabTheme.extendedColors.info kullanın")
val InfoBlue = Color(0xFF2196F3)
@Deprecated("MD3 geçişi: AiLabTheme.extendedColors.warning kullanın")
val WarningOrange = Color(0xFFFF9800)
@Deprecated("MD3 geçişi: AiLabTheme.extendedColors.gold kullanın")
val Gold = Color(0xFFFFD700)
@Deprecated("MD3 geçişi: AiLabTheme.extendedColors.silver kullanın")
val Silver = Color(0xFFC0C0C0)
@Deprecated("MD3 geçişi: AiLabTheme.extendedColors.bronze kullanın")
val CaptainGold = Color(0xFFB8860B)

// Menu kartları için renkler
@Deprecated("MD3 geçişi: MaterialTheme.colorScheme.primaryContainer / tertiaryContainer kullanın")
val LightBlue = Color(0xFF42A5F5)      // Projelerim
@Deprecated("MD3 geçişi: AiLabTheme.extendedColors.successContainer kullanın")
val LightGreen = Color(0xFF66BB6A)     // AI Asistan
@Deprecated("MD3 geçişi: MaterialTheme.colorScheme.tertiaryContainer kullanın")
val LightPurple = Color(0xFF9575CD)    // Profilim
@Deprecated("MD3 geçişi: AiLabTheme.extendedColors.warningContainer kullanın")
val LightOrange = Color(0xFFFF9800)    // Duyurular

// Özel UI Renkleri
@Deprecated("MD3 geçişi: MaterialTheme.colorScheme.secondaryContainer kullanın")
val LabBarBackground = Color(0xFFB8C5D6)
@Deprecated("MD3 geçişi: MaterialTheme.colorScheme.surfaceVariant kullanın")
val FilterChipUnselected = Color(0xFFB0B8D4)
@Deprecated("MD3 geçişi: MaterialTheme.colorScheme.secondaryContainer kullanın")
val AnnouncementBadgeBg = Color(0xFF9FA8DA)
@Deprecated("MD3 geçişi: MaterialTheme.colorScheme.onSecondaryContainer kullanın")
val AnnouncementBadgeText = Color(0xFF5C6BC0)
@Deprecated("MD3 geçişi: MaterialTheme.colorScheme.surfaceContainerHigh kullanın")
val TaskHistoryBg = Color(0xFFE8EAF6)

// Shimmer renkleri
@Deprecated("MD3 geçişi: MaterialTheme.colorScheme.surfaceVariant kullanın")
val ShimmerBase      = Color(0xFFE0E0E0)
@Deprecated("MD3 geçişi: MaterialTheme.colorScheme.surfaceContainerHighest kullanın")
val ShimmerHighlight = Color(0xFFF5F5F5)

// Login / Register arka plan gradient
@Deprecated("MD3 geçişi: MaterialTheme.colorScheme.surfaceContainerLow kullanın")
val GradientStart = Color(0xFFE8E8EC)
@Deprecated("MD3 geçişi: MaterialTheme.colorScheme.surfaceContainer kullanın")
val GradientMid   = Color(0xFFD4D4D8)
@Deprecated("MD3 geçişi: MaterialTheme.colorScheme.surfaceContainerHigh kullanın")
val GradientEnd   = Color(0xFFC0C0C4)
