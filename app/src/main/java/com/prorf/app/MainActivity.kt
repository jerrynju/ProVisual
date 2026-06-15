package com.prorf.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.prorf.app.ui.ProRFApp
import com.prorf.app.ui.theme.ProRFTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            ProRFTheme {
                ProRFApp()
            }
        }
    }
}
