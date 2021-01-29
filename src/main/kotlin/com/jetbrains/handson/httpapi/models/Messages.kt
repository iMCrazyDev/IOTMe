package com.jetbrains.handson.httpapi.models

import kotlinx.serialization.Serializable

/*enum class TypesOfMessage {
    UserMessage, AdminMessage, ConfigMessage, UserInfo
} */

@Serializable
data class ModuleMessage(val from: String, val to : String, val message: String, val unixTimes : Long, val type : String)
