package skymonkey.com

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import skymonkey.com.auth.AccessTokenResult
import skymonkey.com.auth.domain.JwtConfig
import java.util.*

class FakeJwtConfig : JwtConfig {

    private val accessTokenValidity = 5 * 60 * 1000 // 5 minutes
    private val refreshTokenValidity = 7 * 24 * 60 * 60 * 1000 // 7 days
    private val secret = "secret"
    private val issuer = "iss"
    private val audience = "aud"
    private val algorithm = Algorithm.HMAC256(secret)

    override val myRealm = "myRealm"
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
        return JWT.create()
            .withSubject("Refresh")
            .withAudience(audience)
            .withIssuer(issuer)
            .withClaim("userId", userId)
            .withExpiresAt(Date(System.currentTimeMillis() + refreshTokenValidity))
            .sign(algorithm)
    }
}