package com.ktun.ailabapp.domain.usecase.announcement

import com.ktun.ailabapp.data.model.Announcement
import com.ktun.ailabapp.domain.repository.IAnnouncementRepository
import javax.inject.Inject

class GetAnnouncementDetailUseCase @Inject constructor(
    private val repository: IAnnouncementRepository
) {
    suspend operator fun invoke(id: String): Result<Announcement> =
        repository.getAnnouncementDetail(id)
}
