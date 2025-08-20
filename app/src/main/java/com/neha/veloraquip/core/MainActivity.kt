package com.neha.veloraquip.core

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.app.ActivityCompat
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.navigation.compose.rememberNavController
import com.neha.veloraquip.core.navigation.AppNavGraph
import com.neha.veloraquip.data.FirebaseRepository
import com.neha.veloraquip.fcm.NotificationHandler
import com.neha.veloraquip.ui.theme.VeloraQuipTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Request notification permission for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                101
            )
        }

        // Start listening to notifications
        val repo = FirebaseRepository()
        val notifHandler = NotificationHandler(this, repo)
        notifHandler.startListening() // listens to /notify/{uid}

        setContent {
            VeloraQuipTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()
                    AppNavGraph(navController)
                }
            }
        }
    }
}
