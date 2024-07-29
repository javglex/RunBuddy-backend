package skymonkey.com.run.data

import com.mongodb.client.MongoClient
import org.litote.kmongo.KMongo

class RunDatabaseFactory {
    private var mongoClient: MongoClient
    private var database: com.mongodb.client.MongoDatabase

    init {
        val connectionString = System.getenv("MONGODB_CONNECTION_STRING") ?: error("Missing MongoDB Atlas connection string")
        mongoClient = KMongo.createClient(connectionString)
        database = mongoClient.getDatabase("runbuddy_runs")
    }

    fun getDatabase() = database
}