package skymonkey.com

import skymonkey.com.run.domain.RunEntity
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.config.*
import io.ktor.server.testing.*
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.litote.kmongo.json
import skymonkey.com.auth.domain.LoginRequest
import skymonkey.com.auth.domain.LoginResponse
import skymonkey.com.auth.domain.RegisterRequest
import skymonkey.com.run.data.RunDto
import skymonkey.com.run.domain.CreateRunRequest
import skymonkey.com.run.domain.RefreshTokenRequest
import skymonkey.com.run.domain.RefreshTokenResponse
import kotlin.test.*

class ApplicationTest {

    @BeforeTest
    fun setup() {
        startKoin {
            modules(
                koinTestModule
            )
        }
    }

    @AfterTest
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun runsCanBeFoundById() = testApplication {
        environment {
            config = ApplicationConfig("application-custom.conf")
        }

        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        val response = client.get("/runs/byId/123")
        val result = response.body<RunDto>()

        assertEquals(HttpStatusCode.OK, response.status)

        val expectedRunsId = "123"
        val actualRunsId = result.id
        assertEquals(expectedRunsId, actualRunsId)
    }

    @Test
    fun invalidIdProduces400() = testApplication {
        environment {
            config = ApplicationConfig("application-custom.conf")
        }


        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        val response = client.get("/runs/byId/0")
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @Test
    fun unusedIdProduces404() = testApplication {
        environment {
            config = ApplicationConfig("application-custom.conf")
        }

        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        val response = client.get("/runs/byId/1232")
        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun newRunsCanBeAdded() = testApplication {
        environment {
            config = ApplicationConfig("application-custom.conf")
        }

        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }
        val newRunId = "12345"
        val accessTokenResult = FakeJwtConfig().generateAccessToken("user123@email.com")
        val runRequest = CreateRunRequest(
            durationMillis = 1000,
            avgHeartRate = 90,
            avgSpeedKmh = 20.0,
            distanceMeters = 20,
            maxHeartRate = 120,
            epochMillis = 1000,
            maxSpeedKmh = 90.0,
            totalElevationMeters = 10,
            id = newRunId,
            long = 123.0,
            lat = 32.0
        )

        val response1 = client.submitFormWithBinaryData(
            url = "/runs",
            formData = formData {
                append("RUN_DATA", runRequest.json, Headers.build {
                    append(HttpHeaders.ContentType, ContentType.Application.Json)
                })
                append("MAP_PICTURE", "test-image".toByteArray(), Headers.build {
                    append(HttpHeaders.ContentType, ContentType.Image.JPEG)
                    append(HttpHeaders.ContentDisposition, "filename=test-image.jpg")
                })
            }
        ) {
            header(HttpHeaders.Authorization, "Bearer ${accessTokenResult.accessToken}")
        }
        assertEquals(HttpStatusCode.Created, response1.status)

        // fetch the runs to confirm it's been added to repository.
        val response2 = client.get("/runs"){
            header(HttpHeaders.Authorization, "Bearer ${accessTokenResult.accessToken}")
        }
        assertEquals(HttpStatusCode.OK, response2.status)

        val runIds = response2
            .body<List<RunDto>>()
            .map { it.id }

        assertContains(runIds, newRunId)
    }

    @Test
    fun fetchRuns() = testApplication {
        environment {
            config = ApplicationConfig("application-custom.conf")
        }

        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }
        val accessTokenResult = FakeJwtConfig().generateAccessToken("user123@email.com")

        val response = client.get("/runs") {
            header(HttpHeaders.Authorization, "Bearer ${accessTokenResult.accessToken}")
        }

        assertEquals(HttpStatusCode.OK, response.status)
    }


    @Test
    fun userCanRegister() = testApplication {
        environment {
            config = ApplicationConfig("application-custom.conf")
        }

        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        val registerRequest = RegisterRequest("test@example.com", "password")

        val response = client.post("/register") {
            header(HttpHeaders.ContentType, ContentType.Application.Json)
            setBody(registerRequest)
        }
        assertEquals(HttpStatusCode.Created, response.status)
    }

    @Test
    fun userCanLogin() = testApplication {
        environment {
            config = ApplicationConfig("application-custom.conf")
        }

        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        val loginRequest = LoginRequest("user123@email.com", "123")

        val response = client.post("/login") {
            header(HttpHeaders.ContentType, ContentType.Application.Json)
            setBody(loginRequest)
        }
        assertEquals(HttpStatusCode.OK, response.status)

        val loginResponse = response.body<LoginResponse>()
        assertNotNull(loginResponse.accessToken)
        assertNotNull(loginResponse.refreshToken)
        assertNotNull(loginResponse.accessTokenExpirationTimestamp)
        assertNotNull(loginResponse.userId)
    }

    @Test
    fun tokenCanBeRefreshed() = testApplication {
        environment {
            config = ApplicationConfig("application-custom.conf")
        }

        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        val refreshToken = FakeJwtConfig().generateRefreshToken("user123")
        val refreshTokenRequest = RefreshTokenRequest(refreshToken, "user123")

        val response = client.post("/refreshToken") {
            header(HttpHeaders.ContentType, ContentType.Application.Json)
            setBody(refreshTokenRequest)
        }
        assertEquals(HttpStatusCode.OK, response.status)

        val refreshTokenResponse = response.body<RefreshTokenResponse>()
        assertNotNull(refreshTokenResponse.accessToken)
        assertNotNull(refreshTokenResponse.expirationTimeStamp)
    }

}
