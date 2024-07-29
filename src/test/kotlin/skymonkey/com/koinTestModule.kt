package skymonkey.com

import org.koin.dsl.module
import skymonkey.com.auth.domain.AuthRepository
import skymonkey.com.auth.domain.JwtConfig
import skymonkey.com.run.data.RunMongoRepository
import skymonkey.com.run.domain.RunRepository

val koinTestModule = module {
    single<RunRepository> { FakeRunsRepository() }
    single<AuthRepository> { FakeAuthRepository() }
    single<JwtConfig> { FakeJwtConfig() }
}