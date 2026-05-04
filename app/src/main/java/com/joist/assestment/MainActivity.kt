package com.joist.assestment

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.joist.assestment.navigation.AppNavGraph
import com.joist.assestment.ui.theme.EchoAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EchoAppTheme {
                AppNavGraph()
            }
        }
    }
}
