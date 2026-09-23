package id.bubakangreen.app.data.location

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import id.bubakangreen.app.core.result.Result
import id.bubakangreen.app.domain.model.Coordinates
import kotlinx.coroutines.tasks.await

class AndroidLocationClient(
    private val context: Context
) : LocationClient {

    private val fusedClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }

    @SuppressLint("MissingPermission")
    override suspend fun getCurrentLocation(): Result<Coordinates> {
        return try {
            val cancellationTokenSource = CancellationTokenSource()
            val location = fusedClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                cancellationTokenSource.token
            ).await()

            if (location != null) {
                Result.Success(
                    Coordinates(
                        latitude = location.latitude,
                        longitude = location.longitude,
                        accuracyMeters = location.accuracy
                    )
                )
            } else {
                Result.Error(IllegalStateException("GPS fix timed out or unavailable"))
            }
        } catch (e: Exception) {
            Result.Error(e, e.localizedMessage)
        }
    }
}
