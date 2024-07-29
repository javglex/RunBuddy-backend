package skymonkey.com.auth.di

import com.mongodb.client.MongoDatabase
import io.ktor.server.application.*
import org.koin.core.module.Module
import org.koin.dsl.module
import skymonkey.com.auth.EnvJwtConfig
import skymonkey.com.auth.data.AuthDatabaseFactory
import skymonkey.com.auth.data.AuthMongoRepository
import skymonkey.com.auth.domain.AuthRepository
import skymonkey.com.auth.domain.JwtConfig

fun koinAuthModule(environment: ApplicationEnvironment): org.koin.core.module.Module =
    module {
        single {  }
        single<MongoDatabase> { AuthDatabaseFactory().getDatabase() }
        single<AuthRepository> { AuthMongoRepository(get()) }
        single<JwtConfig> { EnvJwtConfig(environment) }
    }