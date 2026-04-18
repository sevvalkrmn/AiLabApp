package com.ktun.ailabapp.domain.usecase.announcement

import com.ktun.ailabapp.domain.repository.IAnnouncementRepository
import javax.inject.Inject

class MarkAnnouncementAsReadUseCase @Inject constructor(
    private val repository: IAnnouncementRepository
) {
    suspend operator fun invoke(id: String): Result<Unit> =
        repository.markAsRead(id)
}
