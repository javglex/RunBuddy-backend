package skymonkey.com.run.domain

import kotlinx.serialization.Serializable

@Serializable
data class UploadRunRequest(
    val runEntity: RunEntity,
    val mapPicture: ByteArray
)