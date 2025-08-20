package com.neha.veloraquip.core.chatdetail

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.neha.veloraquip.auth.AuthManager
import com.neha.veloraquip.util.formatTimeShort
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDetailScreen(
    navController: NavHostController,
    uid: String,
    name: String,
    viewModel: ChatDetailViewModel = viewModel()
) {
    val currentUser = AuthManager.currentUser
    var input by remember { mutableStateOf("") }
    val messages by viewModel.messages.collectAsState()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    // Load messages
    LaunchedEffect(uid) { viewModel.load(uid) }

    // Handle hardware back button
    BackHandler {
        focusManager.clearFocus(force = true)
        navController.popBackStack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chat with $name") },
                navigationIcon = {
                    IconButton(onClick = {
                        focusManager.clearFocus(force = true)
                        navController.popBackStack()
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(messages) { msg ->
                    val isMe = msg.senderId == currentUser?.uid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
                    ) {
                        Surface(
                            color = if (isMe) Color(0xFF2196F3) else Color(0xFFFFEB3B),
                            shape = RoundedCornerShape(
                                topStart = 16.dp,
                                topEnd = 16.dp,
                                bottomEnd = if (isMe) 0.dp else 16.dp,
                                bottomStart = if (isMe) 16.dp else 0.dp
                            ),
                            modifier = Modifier.padding(4.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                                    .widthIn(max = 250.dp)
                            ) {
                                Text(
                                    text = msg.text,
                                    color = if (isMe) Color.White else Color.Black,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = formatTimeShort(msg.timestamp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isMe) Color.White.copy(alpha = 0.7f) else Color.Black.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BasicTextField(
                    value = input,
                    onValueChange = { input = it },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )

                Button(
                    onClick = {
                        if (input.isNotBlank() && currentUser != null) {
                            coroutineScope.launch {
                                viewModel.send(uid, input)
                                input = ""
                                focusManager.clearFocus(force = true)
                            }
                        }
                    },
                    enabled = input.isNotBlank()
                ) { Text("Send") }
            }
        }
    }
}
