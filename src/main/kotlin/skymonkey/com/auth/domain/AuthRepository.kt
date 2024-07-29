package skymonkey.com.auth.domain

interface AuthRepository {
    suspend fun checkUserExists(email: String): Boolean
    suspend fun registerNewUser(email: String, pass: String)
    suspend fun authenticateUser(email: String, pass: String): User?

}