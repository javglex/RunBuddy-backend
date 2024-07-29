package skymonkey.com.run.data

import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId

/**
 * Run object sent to client.
 * Compared to RunEntity, we don't send userId in this object.
 */
@Serializable
data class RunDto(
    @BsonId val id: String,
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