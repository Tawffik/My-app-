package com.cyberos.app.ui.ai

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cyberos.app.data.*
import com.cyberos.app.learning.ProgressState
import com.cyberos.app.ui.lang.Lang
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CouncilScreen(state: CouncilState, progress: ProgressState, onOpenSettings: () -> Unit) {
    var input by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    Column(Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Text(Lang.t("Agent Council", "مجلس الوكلاء"), style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f))
            IconButton(onClick = onOpenSettings) { Icon(Icons.Filled.Settings, contentDescription = null) }
        }
        Text(
            Lang.t("3 stages: analyst → critic → synthesizer. Same model, different roles. Untrusted injection.",
                "٣ مراحل: محلل ← ناقد ← مُخلِّص. نفس الموديل بأدوار مختلفة."),
            style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(12.dp))

        state.notice?.let { n ->
            Surface(color = MaterialTheme.colorScheme.primaryContainer, shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth()) {
                Text(n, Modifier.padding(10.dp), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimaryContainer)
            }
            Spacer(Modifier.height(6.dp))
        }

        OutlinedTextField(value = input, onValueChange = { input = it }, modifier = Modifier.fillMaxWidth(), placeholder = { Text(Lang.t("Ask a serious question...", "اسأل سؤال جدّي...")) }, maxLines = 4)
        Spacer(Modifier.height(8.dp))
        Button(
            onClick = { val q = input; input = ""; scope.launch { if (state.run(q)) progress.addXp(15) } },
            enabled = !state.running && input.isNotBlank() && state.configured,
            modifier = Modifier.fillMaxWidth()
        ) { Text(Lang.t("Run (3 calls)", "شغّل (٣ استدعاءات)")) }

        if (!state.configured) {
            Spacer(Modifier.height(8.dp))
            Text(Lang.t("Needs an API key.", "محتاج مفتاح."), color = MaterialTheme.colorScheme.error)
        }
        state.error?.let { msg -> Spacer(Modifier.height(8.dp)); Text(msg, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }

        if (state.running) {
            Spacer(Modifier.height(12.dp))
            listOf("1) Analyst", "2) Critic", "3) Synthesizer").forEachIndexed { i, label ->
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
                    if (state.phase == i + 1) CircularProgressIndicator(Modifier.size(16.dp), strokeWidth = 2.dp)
                    else Text(if (state.phase > i + 1) "✓" else "•", color = if (state.phase > i + 1) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.width(8.dp))
                    Text(label, style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        Spacer(Modifier.height(12.dp))
        Text(Lang.t("Previous sessions", "الجلسات"), style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        if (state.sessions.isEmpty()) {
            Text(Lang.t("No sessions yet.", "مفيش."), color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), contentPadding = PaddingValues(bottom = 16.dp)) {
                items(state.sessions, key = { it.id }) { s -> CouncilSessionCard(s) }
            }
        }
    }
}

@Composable
private fun CouncilSessionCard(s: CouncilSession) {
    var expanded by remember(s.id) { mutableStateOf(false) }
    val dateText = remember(s.createdAt) { SimpleDateFormat("d MMM", Locale.getDefault()).format(Date(s.createdAt)) }
    Card(Modifier.fillMaxWidth().clickable { expanded = !expanded }) {
        Column(Modifier.padding(14.dp)) {
            Text(s.question, style = MaterialTheme.typography.titleMedium)
            Text(dateText, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(8.dp))
            Text(Lang.t("Verdict:", "الحكم:"), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
            Text(s.synthesis, style = MaterialTheme.typography.bodyMedium)
            if (expanded) {
                Spacer(Modifier.height(10.dp))
                Text("Analyst:", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(s.analyst, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(8.dp))
                Text("Critic:", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(s.critic, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
