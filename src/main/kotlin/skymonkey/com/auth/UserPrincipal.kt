package skymonkey.com.auth

import io.ktor.server.auth.Principal

data class UserPrincipal(val userId: String) : Principal
