package skymonkey.com.auth.data

import com.mongodb.client.MongoClient
import com.mongodb.client.MongoDatabase
import org.litote.kmongo.KMongo
import skymonkey.com.EnvConfig

class AuthDatabaseFactory {
    private var mongoClient: MongoClient
    private var database: MongoDatabase

    init {
        val connectionString = EnvConfig.mongoConnectionString
        mongoClient = KMongo.createClient(connectionString)
        database = mongoClient.getDatabase("runbuddy_auth")
    }

    fun getDatabase(): MongoDatabase = database
}