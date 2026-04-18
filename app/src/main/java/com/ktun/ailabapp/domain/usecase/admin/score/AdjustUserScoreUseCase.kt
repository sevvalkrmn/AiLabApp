package com.ktun.ailabapp.domain.usecase.admin.score

import com.ktun.ailabapp.domain.repository.IAdminScoreRepository
import com.ktun.ailabapp.util.NetworkResult
import javax.inject.Inject

class AdjustUserScoreUseCase @Inject constructor(
    private val repository: IAdminScoreRepository
) {
    suspend operator fun invoke(userId: String, amount: Double, reason: String): NetworkResult<Unit> {
        if (reason.isBlank()) return NetworkResult.Error("Açıklama giriniz")
        if (amount == 0.0) return NetworkResult.Error("Geçerli bir puan giriniz")
        return repository.adjustUserScore(userId, amount, reason)
    }
}
