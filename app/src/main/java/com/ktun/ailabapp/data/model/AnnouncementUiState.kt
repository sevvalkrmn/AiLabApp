package com.ktunailab.ailabapp.data.model

/**
 * Duyuru modeli
 * @param id Duyuru benzersiz kimliği
 * @param type Duyuru tipi (Genel/Takım/Kişisel)
 * @param title Duyuru başlığı
 * @param content Duyuru içeriği
 * @param senderName Gönderen kişinin adı
 * @param senderImage Gönderen kişinin profil resmi URL'i (opsiyonel)
 * @param timestamp Duyuru tarihi ve saati (format: "dd.MM.yyyy HH:mm")
 * @param isRead Duyurunun okunup okunmadığı durumu
 */
data class Announcement(
    val id: String,
    val type: AnnouncementType,
    val title: String,
    val content: String,
    val senderName: String,
    val senderImage: String? = null,
    val timestamp: String,
    val isRead: Boolean = false
)

/**
 * Duyuru tiplerini tanımlar
 * ALL: Laboratuvar genelindeki duyurular
 * TEAM: Takım içi duyurular
 * PERSONAL: Kişiye özel mesajlar
 */
enum class AnnouncementType {
    ALL,      // Tümü - Laboratuvar genel duyuruları
    TEAM,     // Takım - Takım içi duyurular
    PERSONAL  // Kişisel - Kişiye özel duyurular
}

/**
 * Duyuruları filtrelemek için kullanılan filtre tipleri
 * UI'da filtre butonlarında kullanılır
 */
enum class AnnouncementFilter {
    ALL,      // Tüm duyuruları göster
    GENERAL,  // Sadece genel (lab) duyurularını göster
    TEAM,     // Sadece takım duyurularını göster
    PERSONAL  // Sadece kişisel mesajları göster
}