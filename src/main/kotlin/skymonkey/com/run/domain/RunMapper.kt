package skymonkey.com.run.domain

import skymonkey.com.run.data.RunDto
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

fun CreateRunRequest.toRunEntity(userId: String, mapPictureUrl: String): RunEntity {
    val dateTimeUtc = Instant.ofEpochMilli(epochMillis)
        .atZone(ZoneOffset.UTC)
        .format(DateTimeFormatter.ISO_INSTANT)
    return RunEntity(
        id = id,
        userId = userId,
        dateTimeUtc = dateTimeUtc,
        durationMillis = durationMillis,
        distanceMeters = distanceMeters,
        lat = lat,
        long = long,
        avgSpeedKmh = avgSpeedKmh,
        maxSpeedKmh = maxSpeedKmh,
        totalElevationMeters = totalElevationMeters,
        mapPictureUrl = mapPictureUrl,
        avgHeartRate = avgHeartRate,
        maxHeartRate = maxHeartRate
    )
}

fun RunEntity.toRunDto() : RunDto {
    return RunDto(
        id = id,
        dateTimeUtc = dateTimeUtc,
        durationMillis = durationMillis,
        distanceMeters = distanceMeters,
        lat = lat,
        long = long,
        avgSpeedKmh = avgSpeedKmh,
        maxSpeedKmh = maxSpeedKmh,
        totalElevationMeters = totalElevationMeters,
        mapPictureUrl = mapPictureUrl,
        avgHeartRate = avgHeartRate,
        maxHeartRate = maxHeartRate
    )
}