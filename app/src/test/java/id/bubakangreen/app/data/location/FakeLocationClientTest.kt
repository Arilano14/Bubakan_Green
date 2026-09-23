package id.bubakangreen.app.data.location

import com.google.common.truth.Truth.assertThat
import id.bubakangreen.app.core.result.Result
import id.bubakangreen.app.domain.model.Coordinates
import kotlinx.coroutines.runBlocking
import org.junit.Test

class FakeLocationClient(
    var simulatedResult: Result<Coordinates>
) : LocationClient {
    override suspend fun getCurrentLocation(): Result<Coordinates> = simulatedResult
}

class FakeLocationClientTest {

    @Test
    fun fakeLocationClient_returnsSimulatedSuccessCoordinates() = runBlocking {
        val expectedCoords = Coordinates(latitude = -7.0500, longitude = 110.3200, accuracyMeters = 5.0f)
        val client = FakeLocationClient(Result.Success(expectedCoords))

        val result = client.getCurrentLocation()

        assertThat(result).isInstanceOf(Result.Success::class.java)
        val success = result as Result.Success
        assertThat(success.data.latitude).isEqualTo(-7.0500)
        assertThat(success.data.longitude).isEqualTo(110.3200)
        assertThat(success.data.accuracyMeters).isEqualTo(5.0f)
    }

    @Test
    fun fakeLocationClient_simulatesGpsError() = runBlocking {
        val client = FakeLocationClient(Result.Error(IllegalStateException("GPS fix unavailable")))

        val result = client.getCurrentLocation()

        assertThat(result).isInstanceOf(Result.Error::class.java)
        val error = result as Result.Error
        assertThat(error.exception).hasMessageThat().contains("GPS fix unavailable")
    }
}
