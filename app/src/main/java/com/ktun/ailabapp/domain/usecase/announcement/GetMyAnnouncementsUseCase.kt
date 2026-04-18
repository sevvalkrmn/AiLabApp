package com.ktun.ailabapp.domain.usecase.announcement

import com.ktun.ailabapp.data.model.Announcement
import com.ktun.ailabapp.domain.repository.IAnnouncementRepository
import javax.inject.Inject

class GetMyAnnouncementsUseCase @Inject constructor(
    private val repository: IAnnouncementRepository
) {
    suspend operator fun invoke(pageNumber: Int = 1, pageSize: Int = 20, isRead: Boolean? = null): Result<List<Announcement>> =
        repository.getMyAnnouncements(pageNumber, pageSize, isRead)
}
