package com.cyberos.app.ui.focus

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.cyberos.app.learning.ProgressState
import com.cyberos.app.ui.lang.Lang
import kotlinx.coroutines.delay

@Composable
fun FocusScreen(progress: ProgressState, onBack: () -> Unit) {
    var minutes by remember { mutableStateOf(25) }
    var remaining by remember { mutableStateOf(25 * 60) }
    var running by remember { mutableStateOf(false) }
    var finished by remember { mutableStateOf(false) }

    LaunchedEffect(running, remaining) { if (running && remaining > 0) { delay(1000); remaining -= 1 } }
    LaunchedEffect(remaining) { if (running && remaining == 0) { running = false; finished = true; progress.addFocusSession(minutes) } }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = null) }
            Text(Lang.t("Focus Session", "جلسة تركيز"), style = MaterialTheme.typography.titleLarge)
        }
        Spacer(Modifier.height(20.dp))
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.fillMaxWidth().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("%02d:%02d".format(remaining / 60, remaining % 60), style = MaterialTheme.typography.displayLarge, color = if (finished) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface)
                Spacer(Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(5, 15, 25, 45).forEach { m ->
                        FilterChip(selected = minutes == m, onClick = { minutes = m; remaining = m * 60; running = false; finished = false }, label = { Text("${m}m") })
                    }
                }
                Spacer(Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    Button(onClick = { running = !running }, enabled = remaining > 0, modifier = Modifier.weight(1f)) { Text(if (running) Lang.t("Pause", "إيقاف") else Lang.t("Start", "ابدأ")) }
                    OutlinedButton(onClick = { remaining = minutes * 60; running = false; finished = false }, modifier = Modifier.weight(1f)) { Text(Lang.t("Reset", "إعادة")) }
                }
                if (finished) {
                    Spacer(Modifier.height(12.dp))
                    Text(Lang.t("Complete! (+10 XP)", "خلصت! (+10 XP)"), color = MaterialTheme.colorScheme.primary, textAlign = TextAlign.Center)
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        Text("${progress.focusSessions} (${progress.focusMinutes} min)", style = MaterialTheme.typography.bodyMedium)
    }
}
