package com.neha.veloraquip.core.chatlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neha.veloraquip.data.FirebaseRepository
import com.neha.veloraquip.data.models.Conversation
import com.neha.veloraquip.data.models.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ChatListViewModel(
    private val repo: FirebaseRepository = FirebaseRepository()
) : ViewModel() {

    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users

    private val _conversations = MutableStateFlow<List<Conversation>>(emptyList())
    val conversations: StateFlow<List<Conversation>> = _conversations

    init {
        viewModelScope.launch {
            repo.signInIfNeeded() // Sign in and get token
            repo.usersFlow().collectLatest { _users.value = it }
        }
        viewModelScope.launch {
            repo.conversationsFlow().collectLatest { _conversations.value = it }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repo.signOut()
        }
    }
}
