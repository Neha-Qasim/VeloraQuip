package com.neha.veloraquip.core.chatlist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.neha.veloraquip.core.navigation.Routes
import com.neha.veloraquip.data.models.User
import kotlinx.coroutines.launch
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun ChatListScreen(navController: NavHostController, viewModel: ChatListViewModel = viewModel()) {
    val chatUsers by viewModel.users.collectAsState()
    val scope = rememberCoroutineScope()

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Chats", style = MaterialTheme.typography.headlineMedium)

            // LOGOUT BUTTON
            Text(
                text = "Logout",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable {
                    scope.launch {
                        viewModel.logout()
                        navController.navigate(Routes.Login) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                }
            )
        }

        Spacer(Modifier.height(16.dp))

        chatUsers.forEach { user ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clickable {
                        val encodedName = URLEncoder.encode(user.username, StandardCharsets.UTF_8.toString())
                        var route = Routes.ChatDetail
                            .replace("{uid}", user.uid)
                            .replace("{name}", encodedName)

                        if (!user.profilePic.isNullOrBlank()) {
                            val encodedPhoto = URLEncoder.encode(user.profilePic, StandardCharsets.UTF_8.toString())
                            route = route.replace("{photo}", encodedPhoto)
                        } else {
                            route = route.replace("?photo={photo}", "")
                        }

                        navController.navigate(route)
                    }
            ) {
                Row(Modifier.padding(16.dp)) {
                    Text(user.username, style = MaterialTheme.typography.bodyLarge)
                    // TODO: Add profile picture & online/offline status
                }
            }
        }
    }
}
