package com.afidalgo.polaredgeservice.user

data class User(
    val userName: String,
    val firstName: String,
    val lastName: String,
    val roles: MutableList<String>
)
