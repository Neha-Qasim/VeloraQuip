package com.neha.veloraquip.data.models

data class Conversation(
    val chatId: String = "",            // uidA_uidB (sorted)
    val otherUser: User = User(),
    val lastMessage: String = "",
    val lastTimestamp: Long = 0L,
    val otherOnline: Boolean = false
)
