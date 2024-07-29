package skymonkey.com

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import org.koin.ktor.ext.inject
import skymonkey.com.auth.EnvJwtConfig
import skymonkey.com.auth.di.koinAuthModule
import skymonkey.com.auth.domain.AuthRepository
import skymonkey.com.plugins.configureAuthRouting
import skymonkey.com.plugins.configureRunsRouting
import skymonkey.com.plugins.configureSerialization
import org.koin.ktor.plugin.Koin
import skymonkey.com.auth.domain.JwtConfig
import skymonkey.com.run.di.koinRunModule
import skymonkey.com.run.domain.RunRepository

fun main(args: Array<String>) {
    println("starting main engine..")
    io.ktor.server.netty.EngineMain.main(args)
    println("end of main")
}

fun Application.module() {
    println("installing Koin modules..")
    install(Koin) {
        modules(
            koinRunModule,
            koinAuthModule(environment)
        )
    }

    val jwtConfig by inject<JwtConfig>()
    val runRepository by inject<RunRepository>()
    val authRepository by inject<AuthRepository>()

    configureApplication(runRepository, authRepository, jwtConfig)
}

fun Application.testModule() {

    val envJwtConfig by inject<JwtConfig>()
    val runRepository by inject<RunRepository>()
    val authRepository by inject<AuthRepository>()

    configureApplication(runRepository, authRepository, envJwtConfig)
}


private fun Application.configureApplication(
    runRepository: RunRepository,
    authRepository: AuthRepository,
    jwtConfig: JwtConfig
) {
    install(ContentNegotiation) {
        json()
    }
    configureSerialization()
    println("configuring auth routing..")
    configureAuthRouting(authRepository, jwtConfig)
    println("configuring runs routing..")
    configureRunsRouting(runRepository)

}