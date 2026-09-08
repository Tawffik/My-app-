package com.cyberos.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.cyberos.app.data.ResearchSyncWorker
import com.cyberos.app.ui.CyberOSApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Schedule periodic research / writeup sync (WorkManager survives reboots).
        ResearchSyncWorker.schedule(applicationContext)
        enableEdgeToEdge()
        setContent {
            CyberOSApp()
        }
    }
}
