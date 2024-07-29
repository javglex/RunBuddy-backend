package skymonkey.com.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import skymonkey.com.auth.EnvJwtConfig
import skymonkey.com.auth.UserPrincipal
import skymonkey.com.auth.domain.JwtConfig

fun Application.configureJwtInstall(jwtConfig: JwtConfig) {
    install(Authentication) {
        jwt("auth-jwt") {
            realm = jwtConfig.myRealm
            verifier(jwtConfig.verifier)

            // validating our JWT payload. In this case simply checking username is not empty.
            validate { credential ->
                val userId = credential.payload.getClaim("userId").asString()
                if (!userId.isNullOrBlank()) {
                    UserPrincipal(credential.payload.getClaim("userId").asString())
                } else {
                    null
                }
            }

            // challenge function allows to configure a response if authentication fails.
            challenge { defaultScheme, realm ->
                call.respond(HttpStatusCode.Unauthorized, "Token is not valid or has expired")
            }
        }
    }
}