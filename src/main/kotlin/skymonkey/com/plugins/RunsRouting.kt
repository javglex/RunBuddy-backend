package skymonkey.com.plugins

import io.ktor.http.HttpStatusCode
import io.ktor.http.content.*
import io.ktor.serialization.JsonConvertException
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.http.content.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import skymonkey.com.auth.UserPrincipal
import skymonkey.com.run.data.RunListResult
import skymonkey.com.run.data.RunResult
import skymonkey.com.run.domain.CreateRunRequest
import skymonkey.com.run.domain.RunRepository

fun Application.configureRunsRouting(repository: RunRepository) {

    routing {
        staticResources("static", "static")

        authenticate("auth-jwt") {
            get("/secure") {
                val userId = call.principal<UserPrincipal>()?.userId
                call.respondText("Hello, $userId!")
            }
        }

        route("/runs") {
            authenticate("auth-jwt") {
                get {
                    val userId = call.principal<UserPrincipal>()?.userId
                    if (userId == null) {
                        call.respond(HttpStatusCode.Unauthorized, "User not authenticated")
                        return@get
                    }
                    when(val result = repository.allRuns(userId)) {
                        is RunListResult.Success -> call.respond(result.runList)
                        is RunListResult.Failure ->  call.respond(HttpStatusCode.BadRequest, "Could not find run data")
                    }
                }

                post {
                    try {
                        val multipart = call.receiveMultipart()
                        var runRequestData: CreateRunRequest? = null
                        var mapPicture: ByteArray? = null

                        // extract multipart form data into run data & image
                        multipart.forEachPart { part ->
                            when(part) {
                                is PartData.FormItem -> {
                                    if (part.name == "RUN_DATA") {
                                        runRequestData = Json.decodeFromString(CreateRunRequest.serializer(), part.value)
                                    }
                                }
                                is PartData.FileItem -> {
                                    if(part.name == "MAP_PICTURE") {
                                        mapPicture = part.streamProvider().readBytes()
                                    }
                                }
                                else -> {}
                            }
                            part.dispose
                        }

                        if (runRequestData == null || mapPicture == null) {
                            call.respond(HttpStatusCode.BadRequest, "Missing run data or map picture")
                            return@post
                        }

                        val userId = call.principal<UserPrincipal>()?.userId
                        if (userId == null) {
                            call.respond(HttpStatusCode.Unauthorized, "User not authenticated")
                            return@post
                        }

                        val immutableMapPicture = mapPicture ?: throw IllegalStateException("missing map image")

                        runRequestData?.let {
                            when (val result = repository.upsertRun(it, immutableMapPicture, userId)) {
                                is RunResult.Success -> {
                                    call.respond(HttpStatusCode.Created, result.run)
                                }
                                is RunResult.Failure -> {
                                    call.respond(HttpStatusCode.InternalServerError, result.message)
                                }
                            }
                        } ?: run {
                            call.respond(HttpStatusCode.BadRequest, "Missing run data")
                        }
                    } catch (ex: IllegalStateException) {
                        print(ex.localizedMessage)
                        call.respond(HttpStatusCode.BadRequest)
                    } catch (ex: JsonConvertException) {
                        call.respond(HttpStatusCode.BadRequest)
                    }
                }

                delete {
                    val id = call.request.queryParameters["id"] ?: run {
                        call.respond(HttpStatusCode.BadRequest, "Missing query parameter: id")
                        return@delete
                    }

                    val userId = call.principal<UserPrincipal>()?.userId
                    if (userId == null) {
                        call.respond(HttpStatusCode.Unauthorized, "User not authenticated")
                        return@delete
                    }

                    val removed = repository.removeRun(id, userId)
                    if (removed) {
                        call.respond(HttpStatusCode.OK)
                    } else {
                        call.respond(HttpStatusCode.NotFound)
                    }

                }
            }

            get("/byId/{id}") {
                val id = call.parameters["id"]
                if (id == null || id == "0") {
                    call.respond(HttpStatusCode.BadRequest)
                    return@get
                }

                val run = repository.runById(id, "user1") //TODO use actual userId, or remove the route.
                if (run == null) {
                    call.respond(HttpStatusCode.NotFound)
                    return@get
                }
                call.respond(run)
            }
        }
    }
}