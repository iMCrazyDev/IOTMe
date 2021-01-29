package com.jetbrains.handson.httpapi.models

import kotlinx.serialization.Serializable


@Serializable
data class SystemAdmins(val guid: String, val secret : String , val modules : MutableList<String>)

@Serializable
data class User(val guid: String)

val userStorage = mutableMapOf<String,MutableList<String>>() //user-module
val userAdminModel = mutableMapOf<String,String>()
val messages = mutableMapOf<String,MutableList<ModuleMessage>>()
val systemAdminsStorage = mutableListOf<SystemAdmins>()

