package com.neha.veloraquip.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.neha.veloraquip.auth.AuthManager
import com.neha.veloraquip.core.navigation.Routes
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(navController: NavHostController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Welcome to VeloraQuip", style = MaterialTheme.typography.headlineLarge)
            Spacer(Modifier.height(32.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    scope.launch {
                        val result = AuthManager.login(email, password)
                        if (result.isSuccess) {
                            navController.navigate(Routes.ChatList) {
                                popUpTo(Routes.Auth) { inclusive = true }
                            }
                        } else {
                            snackbarHostState.showSnackbar("Login failed: ${result.exceptionOrNull()?.message}")
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Login")
            }
            Spacer(Modifier.height(8.dp))

            OutlinedButton(
                onClick = {
                    scope.launch {
                        val result = AuthManager.signUp(email, password)
                        if (result.isSuccess) {
                            snackbarHostState.showSnackbar("Sign up successful! Please login.")
                        } else {
                            snackbarHostState.showSnackbar("Sign up failed: ${result.exceptionOrNull()?.message}")
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Sign Up")
            }
        }
    }
}
