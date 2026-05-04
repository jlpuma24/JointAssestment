package com.joist.assestment.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.joist.assestment.ui.EchoScreen

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.Echo.route
    ) {
        composable(route = Routes.Echo.route) {
            EchoScreen()
        }
    }
}
