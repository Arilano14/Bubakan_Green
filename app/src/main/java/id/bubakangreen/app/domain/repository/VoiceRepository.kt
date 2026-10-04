package id.bubakangreen.app.domain.repository

import id.bubakangreen.app.core.result.Result
import id.bubakangreen.app.domain.model.PlantVoice
import kotlinx.coroutines.flow.Flow

interface VoiceRepository {
    /**
     * Retrieves optional Mandarin botanical audio metadata for a plant.
     */
    fun getVoiceByPlant(plantId: String): Flow<Result<PlantVoice?>>

    /**
     * Sets or updates voice metadata for a plant (admin only).
     */
    suspend fun setPlantVoice(voice: PlantVoice): Result<Unit>
}
