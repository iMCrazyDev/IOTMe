package com.jetbrains.handson.httpapi.routes

import com.jetbrains.handson.httpapi.models.*
import io.ktor.application.*
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.*
import io.ktor.routing.*
import java.lang.Exception


fun Route.moduleMessage() {
    route("/message/send") {
        post {
            try {
                val message = call.receive<ModuleMessage>()
                val endPoint = mutableListOf<String>()
                when (message.type) {
                    "UserMessage" -> {
                        endPoint.addAll(userStorage[message.from]!!)
                    }
                    "AdminMessage" -> {
                        systemAdminsStorage.filter { it.modules.contains(message.from) } .forEach { endPoint.add(it.guid) }
                    }
                    "ConfigMessage" -> {
                        endPoint.add(message.to)
                    }
                    "UserInfo" -> {
                        endPoint.add(message.to)
                    }
                }
                endPoint.forEach {
                    if (!messages.containsKey(it))
                        messages[it] = mutableListOf<ModuleMessage>()
                    messages[it]!!.add(message);
                }
                call.respond(HttpAnswer("ok"))
            }
            catch (e : Exception) {
                call.respondText("Bad Request")
            }
        }
    }
}

fun Route.getUpdates()
{
    //type other, admin,
    route("/messages/get/{type}/{guid}/{secret}") {
        get {
            var id = call.parameters["guid"] ?: return@get call.respondText("Bad Request", status = HttpStatusCode.BadRequest)
            val type = call.parameters["type"] ?: return@get call.respondText("Bad Request", status = HttpStatusCode.BadRequest)
            var secret = ""
            if(type == "admin") {
                secret = call.parameters["secret"] ?: return@get call.respondText("Bad Request", status = HttpStatusCode.BadRequest)
                val searchRes = systemAdminsStorage.find { x -> (x.guid == id && x.secret == secret) } ?: return@get call.respond(HttpAnswer("Bad Request"))
                id = searchRes.guid
            }
            if(!messages.containsKey(id))
                return@get call.respondText("[]")

            call.respond(messages[id]!!)
            messages[id]!!.clear()
        }
    }
}


fun Application.registerMessageRoutes() {
    routing {
        moduleMessage()
        getUpdates()
    }
}


