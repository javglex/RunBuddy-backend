package skymonkey.com.run.domain

import kotlinx.serialization.Serializable

@Serializable
data class RefreshTokenResponse(
    val accessToken: String,
    val expirationTimeStamp: Long? = null
)
