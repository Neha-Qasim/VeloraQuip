package com.neha.veloraquip.data.models

data class Message(
    val id: String = "",
    val senderId: String = "",
    val receiverId: String = "",
    val text: String = "",
    val timestamp: Long = 0L,
    val seen: Boolean = false
)
