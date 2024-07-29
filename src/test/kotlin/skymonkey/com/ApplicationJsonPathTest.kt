package skymonkey.com

import com.jayway.jsonpath.DocumentContext
import com.jayway.jsonpath.JsonPath
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.config.*
import io.ktor.server.testing.*
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationJsonPathTest {

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
    fun runsCanBeFound() = testApplication {
        environment {
            config = ApplicationConfig("application-custom.conf")
        }

        val accessTokenResult = FakeJwtConfig().generateAccessToken("user123@email.com")

        val jsonDoc = client.getAsJsonPath("/runs") {
            header(HttpHeaders.Authorization, "Bearer ${accessTokenResult.accessToken}")
        }

        val result: List<String> = jsonDoc.read("$[*].id") // return the value of the id property of each entry, as a list.
        assertEquals("123", result[0])
        assertEquals("456", result[1])
    }

    @Test
    fun runsCanBeFoundById() = testApplication {
        environment {
            config = ApplicationConfig("application-custom.conf")
        }

        val id = "123"
        val jsonDoc = client.getAsJsonPath("/runs/byId/$id")

        val result: List<String> =
            jsonDoc.read("$[?(@.id == '$id')].id") //return the value of the id property of every entry in the array with an id equal to the supplied value
        assertEquals(1, result.size)

        assertEquals("123", result[0])
    }

    private suspend fun HttpClient.getAsJsonPath(url: String, block: HttpRequestBuilder.() -> Unit = {}): DocumentContext {
        val response = get(url, block)
        val responseBody = response.bodyAsText()
        return JsonPath.parse(responseBody)
    }
}