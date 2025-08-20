// com.neha.veloraquip.core.navigation.Routes.kt
package com.neha.veloraquip.core.navigation

object Routes {
    const val Auth = "auth"
    const val ChatList = "chat_list"
    // photo is now an optional query parameter
    const val ChatDetail = "chat_detail/{uid}/{name}?photo={photo}"
    const val Login = "login" // Added Login route for navigation
}
