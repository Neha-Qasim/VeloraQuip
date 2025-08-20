package com.neha.veloraquip.data.models

data class User(
    val uid: String = "",
    val username: String = "",
    val profilePic: String = "",
    val online: Boolean = false,
    val fcmToken: String = ""
)
