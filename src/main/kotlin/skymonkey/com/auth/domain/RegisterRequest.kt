package skymonkey.com.auth.domain

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(val email: String, val password: String)
