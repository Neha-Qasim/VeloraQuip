package com.neha.veloraquip.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.neha.veloraquip.auth.AuthScreen
import com.neha.veloraquip.core.chatdetail.ChatDetailScreen
import com.neha.veloraquip.core.chatlist.ChatListScreen
import java.net.URLDecoder
import java.nio.charset.StandardCharsets


@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Routes.Auth) {
        composable(Routes.Auth) {
            AuthScreen(navController)
        }
        composable(Routes.ChatList) {
            ChatListScreen(navController)
        }
        composable(Routes.ChatDetail) { backStackEntry ->
            val uid = backStackEntry.arguments?.getString("uid") ?: ""

            // Decode the username safely
            val nameEncoded = backStackEntry.arguments?.getString("name") ?: ""
            val name = URLDecoder.decode(nameEncoded, StandardCharsets.UTF_8.toString())

            // Optional photo (decoded if needed)
            // val photoEncoded = backStackEntry.arguments?.getString("photo")
            // val photo = photoEncoded?.let { URLDecoder.decode(it, StandardCharsets.UTF_8.toString()) }

            ChatDetailScreen(
                navController = navController,
                uid = uid,
                name = name
            )
        }
    }
}
