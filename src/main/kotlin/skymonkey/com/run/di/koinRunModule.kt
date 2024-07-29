package skymonkey.com.run.di

import com.mongodb.client.MongoDatabase
import org.koin.dsl.module
import skymonkey.com.run.data.RunDatabaseFactory
import skymonkey.com.run.data.RunMongoRepository
import skymonkey.com.run.domain.RunRepository

val koinRunModule = module {
    single<MongoDatabase> { RunDatabaseFactory().getDatabase() }
    single<RunRepository> { RunMongoRepository(get()) }
}
