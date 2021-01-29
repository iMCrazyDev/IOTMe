package com.jetbrains.handson.httpapi.routes

import com.jetbrains.handson.httpapi.models.*
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.routing
import kotlinx.serialization.Serializable

@Serializable
data class HttpAnswer(val status: String)

fun Route.addAdmin() {
    get("/admin/add/{guid}/{secret}") {
        val id = call.parameters["guid"] ?: return@get call.respondText("Bad Request", status = HttpStatusCode.BadRequest)
        val secret = call.parameters["secret"] ?: return@get call.respondText("Bad Request", status = HttpStatusCode.BadRequest)

        if(systemAdminsStorage.any { x -> x.guid == id }) return@get call.respondText("Bad Request", status = HttpStatusCode.BadRequest)
        systemAdminsStorage.add(SystemAdmins(id, secret, mutableListOf<String>()))

        call.respond(HttpAnswer("ok"));

    }
}

fun Route.setAdminModules() {
    get("/admin/set/{guid}/{secret}/{module}") {
        val id = call.parameters["guid"] ?: return@get call.respondText("Bad Request", status = HttpStatusCode.BadRequest)
        val secret = call.parameters["secret"] ?: return@get call.respondText("Bad Request", status = HttpStatusCode.BadRequest)
        val module = call.parameters["module"] ?: return@get call.respondText("Bad Request", status = HttpStatusCode.BadRequest)

        val searchRes = systemAdminsStorage.findLast { x -> (x.guid == id && x.secret == secret) } ?: return@get call.respondText("Bad Request", status = HttpStatusCode.BadRequest)
        searchRes.modules.add(module);

        call.respond(HttpAnswer("ok"));
    }
}

fun Route.getAdminModules() {
    get("/admin/get/{guid}/{secret}/") {
        val id = call.parameters["guid"] ?: return@get call.respondText("Bad Request", status = HttpStatusCode.BadRequest)
        val secret = call.parameters["secret"] ?: return@get call.respondText("Bad Request", status = HttpStatusCode.BadRequest)

        val searchRes = systemAdminsStorage.find { x -> (x.guid == id && x.secret == secret) } ?: return@get call.respond(HttpAnswer("null"))

        call.respond(searchRes!!.modules);
    }
}

fun Route.addUser() {
    get("/user/add/{guid}/{admin}/{secret}") {
        val id = call.parameters["admin"] ?: return@get call.respondText("Bad Request", status = HttpStatusCode.BadRequest)
        val secret = call.parameters["secret"] ?: return@get call.respondText("Bad Request", status = HttpStatusCode.BadRequest)
        val user = call.parameters["guid"] ?: return@get call.respondText("Bad Request", status = HttpStatusCode.BadRequest)

        val searchRes = systemAdminsStorage.find { x -> (x.guid == id && x.secret == secret) } ?: return@get call.respond(HttpAnswer("Bad Request"))

        if(userStorage.containsKey(user))
            return@get call.respond(HttpAnswer("Bad Request"))

        userAdminModel[user] = searchRes.guid;
        userStorage[user] = mutableListOf<String>()

        call.respond(HttpAnswer("ok"));
    }
}

fun Route.setUserModules() {
    get("/user/set/{guid}/{module}/{admin}/{secret}") {
        val id = call.parameters["admin"] ?: return@get call.respondText("Bad Request", status = HttpStatusCode.BadRequest)
        val secret = call.parameters["secret"] ?: return@get call.respondText("Bad Request", status = HttpStatusCode.BadRequest)
        val user = call.parameters["guid"] ?: return@get call.respondText("Bad Request", status = HttpStatusCode.BadRequest)
        val module = call.parameters["module"] ?: return@get call.respondText("Bad Request", status = HttpStatusCode.BadRequest)

        val searchRes = systemAdminsStorage.find { x -> (x.guid == id && x.secret == secret) } ?: return@get call.respond(HttpAnswer("Bad Request"))

        if(!userStorage.containsKey(user) || userAdminModel[user] != searchRes.guid)
            return@get call.respond(HttpAnswer("Bad Request"))

        userStorage[user]!!.add(module)
        if(!moduleStorage.containsKey(module))
            moduleStorage[module] = mutableListOf<String>()

        moduleStorage[module]!!.add(user)

        call.respond(HttpAnswer("ok"));
    }
}

fun Application.registerUserRoutes() {
    routing {
        addAdmin();
        getAdminModules();
        setAdminModules();
        addUser();
        setUserModules();
    }
}