package com.joist.assestment.navigation

sealed class Routes(val route: String) {
    data object Echo : Routes("echo")
}
