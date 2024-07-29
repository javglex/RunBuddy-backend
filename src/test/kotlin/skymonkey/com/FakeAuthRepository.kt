package skymonkey.com

import skymonkey.com.auth.domain.AuthRepository
import skymonkey.com.auth.domain.User

class FakeAuthRepository : AuthRepository {

    val emails = listOf(
        "user123@email.com",
        "user222@email.com",
        "user432@email.com"
    )

    val users = listOf(
        User(email = "user123@email.com", passwordHash =  "123"),
        User(email = "user222@email.com", passwordHash =  "222"),
        User(email = "user432@email.com", passwordHash =  "432")
    )

    override suspend fun checkUserExists(email: String): Boolean {
        return emails.indexOf(email) != -1
    }

    override suspend fun registerNewUser(email: String, pass: String) {
        return
    }

    override suspend fun authenticateUser(email: String, pass: String): User? {
        val user = users.firstOrNull() { it.email == email }
        val passwordValid = user?.passwordHash == pass
        return if (user != null && passwordValid) user else null
    }
}