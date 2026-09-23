package id.bubakangreen.app.domain.model

data class Coordinates(
    val latitude: Double,
    val longitude: Double,
    val accuracyMeters: Float? = null
)
