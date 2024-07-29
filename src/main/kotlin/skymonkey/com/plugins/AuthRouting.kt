package skymonkey.com.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import skymonkey.com.auth.domain.*
import skymonkey.com.run.domain.RefreshTokenRequest
import skymonkey.com.run.domain.RefreshTokenResponse

/**
 * Contains auth related route handling e.g login, tokenRefresh
 */

fun Application.configureAuthRouting(
    authRepository: AuthRepository,
    jwtConfig: JwtConfig
) {

    configureJwtInstall(jwtConfig)

    routing {
        staticResources("static", "static")

        post("/register") {
            val user = call.receive<RegisterRequest>()
            // check if the user already exists
            val userExists = authRepository.checkUserExists(email = user.email)
            if (userExists) {
                call.respond(HttpStatusCode.BadRequest, "User already exists")
                return@post
            }

            // register new user in db
            authRepository.registerNewUser(user.email, user.password)
            call.respond(HttpStatusCode.Created)
        }

        post("/login") {
            val loginRequest = call.receive<LoginRequest>()
            val user = authRepository.authenticateUser(loginRequest.email, loginRequest.password)
            if (user == null) {
                call.respond(HttpStatusCode.Unauthorized, "Invalid credentials")
            } else {
                val accessTokenBundle = jwtConfig.generateAccessToken(user.id.toString())
                val refreshToken = jwtConfig.generateRefreshToken(user.id.toString())
                call.respond(
                    LoginResponse(
                        accessToken = accessTokenBundle.accessToken,
                        refreshToken = refreshToken,
                        accessTokenExpirationTimestamp = accessTokenBundle.accessTokenExpirationTimestamp,
                        userId = user.id.toString()
                    )
                )
            }
        }

        post("/refreshToken") {
            val refreshTokenRequest = call.receive<RefreshTokenRequest>()
            val refreshToken = refreshTokenRequest.refreshToken
            val userId = refreshTokenRequest.userId

            if (refreshToken.isEmpty()) {
                call.respond(HttpStatusCode.BadRequest, "Missing or invalid refresh token")
                return@post
            }

            try {
                val jwtUserId = jwtConfig.verifier.verify(refreshToken).getClaim("userId").asString()

                if (jwtUserId == userId) {
                    val newAccessTokenBundle = jwtConfig.generateAccessToken(userId)
                    call.respond(HttpStatusCode.OK,
                        RefreshTokenResponse(
                            newAccessTokenBundle.accessToken,
                            newAccessTokenBundle.accessTokenExpirationTimestamp
                        )
                    )
                } else {
                    call.respond(HttpStatusCode.Unauthorized, "Invalid refresh token")
                }
            } catch (e: Exception) {
                println(e.localizedMessage)
                call.respond(HttpStatusCode.Unauthorized, "Invalid refresh token")
            }
        }
    }
}