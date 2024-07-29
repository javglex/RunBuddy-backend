package skymonkey.com.auth.data

import com.mongodb.client.MongoDatabase
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import org.litote.kmongo.getCollection
import org.mindrot.jbcrypt.BCrypt
import skymonkey.com.auth.domain.AuthRepository
import skymonkey.com.auth.domain.User

class AuthMongoRepository(
    authDb: MongoDatabase
): AuthRepository {
    private val userCollection = authDb.getCollection<User>()

    override suspend fun checkUserExists(email: String): Boolean {
        val existingUser = userCollection.findOne(User::email eq email)
        return existingUser != null
    }

    override suspend fun registerNewUser(email: String, pass: String) {
        val hashedPassword = BCrypt.hashpw(pass, BCrypt.gensalt())
        userCollection.insertOne(User(email = email, passwordHash = hashedPassword))
    }

    override suspend fun authenticateUser(email: String, pass: String): User? {
        val user = userCollection.findOne(User::email eq email)
        return if (user == null || !BCrypt.checkpw(pass, user.passwordHash))
            null
        else user
    }
}