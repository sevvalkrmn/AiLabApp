package com.ktun.ailabapp.domain.usecase.announcement

import com.ktun.ailabapp.domain.repository.IAnnouncementRepository
import javax.inject.Inject

class CreateAnnouncementUseCase @Inject constructor(
    private val repository: IAnnouncementRepository
) {
    suspend operator fun invoke(
        title: String,
        content: String,
        scope: Int,
        userId: String? = null,
        projectId: String? = null
    ): Result<Unit> = repository.createAnnouncement(title, content, scope, userId, projectId)
}
