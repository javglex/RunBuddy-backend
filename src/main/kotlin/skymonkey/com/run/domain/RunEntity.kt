package skymonkey.com.run.domain

import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId

/**
 * Run data for database.
 */
@Serializable
data class RunEntity(
    @BsonId val id: String,
    val userId: String,
    val dateTimeUtc: String,
    val durationMillis: Long,
    val distanceMeters: Int,
    val lat: Double,
    val long: Double,
    val avgSpeedKmh: Double,
    val maxSpeedKmh: Double,
    val totalElevationMeters: Int,
    val mapPictureUrl: String?,
    val avgHeartRate: Int?,
    val maxHeartRate: Int?
)