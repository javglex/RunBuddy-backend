package skymonkey.com.auth.domain

import com.auth0.jwt.JWTVerifier
import skymonkey.com.auth.AccessTokenResult
import software.amazon.awssdk.services.s3.endpoints.internal.Value.Str

interface JwtConfig {
    val verifier: JWTVerifier
    val myRealm: String
    fun generateAccessToken(userId: String): AccessTokenResult
    fun generateRefreshToken(userId: String): String
}