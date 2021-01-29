package com.jetbrains.handson.httpapi.models

import kotlinx.serialization.Serializable

@Serializable
val moduleStorage = mutableMapOf<String,MutableList<String>>() //module-users