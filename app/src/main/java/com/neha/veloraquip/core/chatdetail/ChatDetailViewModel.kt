package com.neha.veloraquip.core.chatdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neha.veloraquip.data.FirebaseRepository
import com.neha.veloraquip.data.models.Message
import com.neha.veloraquip.auth.AuthManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ChatDetailViewModel(
    private val repo: FirebaseRepository = FirebaseRepository()
) : ViewModel() {

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages

    private val currentUserName: String
        get() = AuthManager.currentUser?.displayName ?: "User"

    fun load(otherUid: String) {
        viewModelScope.launch {
            repo.messagesFlow(otherUid).collectLatest { _messages.value = it }
        }
    }

    fun send(toUid: String, text: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            repo.sendMessage(toUid, text.trim(), currentUserName)
        }
    }
}
