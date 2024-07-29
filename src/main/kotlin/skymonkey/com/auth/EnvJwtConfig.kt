package skymonkey.com.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.*
import skymonkey.com.EnvConfig
import skymonkey.com.auth.domain.JwtConfig
import java.util.*

data class AccessTokenResult(
    val accessToken: String,
    val accessTokenExpirationTimestamp: Long
)

class EnvJwtConfig(environment: ApplicationEnvironment) : JwtConfig {
    private val accessTokenValidity = 5 * 60 * 1000 // 5 minutes
    private val refreshTokenValidity = 7 * 24 * 60 * 60 * 1000 // 7 days
    private val secret = EnvConfig.jwtSecret
    private val issuer = environment.config.property("jwt.issuer").getString()
    private val audience = environment.config.property("jwt.audience").getString()
    private val algorithm = Algorithm.HMAC256(secret)
    override val myRealm = environment.config.property("jwt.realm").getString()

    override val verifier: JWTVerifier = JWT
        .require(algorithm)
        .withAudience(audience)
        .withIssuer(issuer)
        .build()

    override fun generateAccessToken(userId: String): AccessTokenResult {
        val expiration = Date(System.currentTimeMillis() + accessTokenValidity)
        val token = JWT.create()
            .withSubject("Authentication")
            .withAudience(audience)
            .withIssuer(issuer)
            .withClaim("userId", userId)
            .withExpiresAt(expiration)
            .sign(algorithm)

        return AccessTokenResult(token, expiration.time)
    }

    override fun generateRefreshToken(userId: String): String {
        val expiration = Date(System.currentTimeMillis() + refreshTokenValidity)
        return JWT.create()
            .withSubject("Refresh")
            .withAudience(audience)
            .withIssuer(issuer)
            .withClaim("userId", userId)
            .withExpiresAt(expiration)
            .sign(algorithm)
    }
}