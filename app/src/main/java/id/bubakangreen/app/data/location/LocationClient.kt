package id.bubakangreen.app.data.location

import id.bubakangreen.app.core.result.Result
import id.bubakangreen.app.domain.model.Coordinates

/**
 * Contract for contextual single-point GPS capture.
 * Strictly acquires current coordinates once and terminates.
 */
interface LocationClient {
    suspend fun getCurrentLocation(): Result<Coordinates>
}
