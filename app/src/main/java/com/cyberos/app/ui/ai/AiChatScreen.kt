package com.cyberos.app.ui.ai

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.cyberos.app.data.*
import com.cyberos.app.ui.lang.Lang
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AiChatScreen(state: AiState, archiveState: ChatArchiveState, onOpenSettings: () -> Unit) {
    var input by remember { mutableStateOf("") }
    var showArchive by remember { mutableStateOf(false) }
    var deleteArchiveTarget by remember { mutableStateOf<ChatArchive?>(null) }
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    LaunchedEffect(state.messages.size) {
        if (state.messages.isNotEmpty()) listState.animateScrollToItem(state.messages.size - 1)
    }
    LaunchedEffect(state.pendingQuestion) {
        val q = state.pendingQuestion
        if (q != null && !state.busy) { state.pendingQuestion = null; state.send(q) }
    }

    Column(Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Text(Lang.t("AI Assistant", "المساعد"), style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f))
            IconButton(onClick = { showArchive = !showArchive }, enabled = archiveState.list.isNotEmpty() || state.messages.isNotEmpty()) { Icon(Icons.Filled.Star, contentDescription = null) }
            IconButton(onClick = { state.clearChat() }, enabled = state.messages.isNotEmpty()) { Icon(Icons.Filled.Clear, contentDescription = null) }
            IconButton(onClick = onOpenSettings) { Icon(Icons.Filled.Settings, contentDescription = null) }
        }

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            ChatMode.values().forEach { m ->
                FilterChip(selected = state.mode == m, onClick = { state.mode = m }, label = { Text(Lang.t(m.labelEn, m.labelAr), style = MaterialTheme.typography.labelSmall) })
            }
        }
        Spacer(Modifier.height(6.dp))

        state.ragNotice?.let { n -> NoticeBar(n); Spacer(Modifier.height(6.dp)) }
        state.redactionNotice?.let { n -> NoticeBar(n); Spacer(Modifier.height(6.dp)) }

        if (showArchive && archiveState.list.isNotEmpty()) {
            LazyColumn(Modifier.height(180.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                items(archiveState.list.size) { i ->
                    val a = archiveState.list[i]
                    Row(Modifier.fillMaxWidth().clickable { state.loadArchive(a); showArchive = false }, verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(a.title.ifBlank { "Chat" }, style = MaterialTheme.typography.bodyMedium, maxLines = 1)
                            Text("${a.messages.size} msgs", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        IconButton(onClick = { deleteArchiveTarget = a }) { Icon(Icons.Filled.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error) }
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
        }

        if (!state.configured) {
            Column(Modifier.weight(1f).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                Text(Lang.t("No API key configured", "مفيش مفتاح"), style = MaterialTheme.typography.titleMedium)
                Text(Lang.t("Open Settings (key is encrypted on device).", "افتح الإعدادات."), color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
                Spacer(Modifier.height(16.dp))
                Button(onClick = onOpenSettings) { Text(Lang.t("Open Settings", "فتح الإعدادات")) }
            }
        } else {
            state.error?.let { msg ->
                Surface(color = MaterialTheme.colorScheme.errorContainer, shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth()) {
                    Text(msg, color = MaterialTheme.colorScheme.onErrorContainer, modifier = Modifier.padding(10.dp), style = MaterialTheme.typography.bodySmall)
                }
                Spacer(Modifier.height(6.dp))
            }

            LazyColumn(state = listState, modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp), contentPadding = PaddingValues(bottom = 8.dp)) {
                items(state.messages.size) { index ->
                    val m = state.messages[index]
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = if (m.role == "user") Arrangement.End else Arrangement.Start) {
                        Surface(
                            color = if (m.role == "user") MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(12.dp)
                        ) { Text(m.content, Modifier.padding(10.dp), style = MaterialTheme.typography.bodyMedium) }
                    }
                }
                if (state.busy) {
                    item { CircularProgressIndicator(Modifier.size(20.dp).padding(start = 10.dp), strokeWidth = 2.dp) }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 8.dp)) {
                OutlinedTextField(value = input, onValueChange = { input = it }, modifier = Modifier.weight(1f), placeholder = { Text(Lang.t("Ask...", "اسأل...")) }, maxLines = 4)
                Spacer(Modifier.width(8.dp))
                IconButton(onClick = { val text = input; input = ""; scope.launch { state.send(text) } }, enabled = !state.busy && input.isNotBlank()) {
                    Icon(Icons.Filled.Send, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }

    if (deleteArchiveTarget != null) {
        AlertDialog(
            onDismissRequest = { deleteArchiveTarget = null },
            title = { Text(Lang.t("Delete chat?", "حذف؟")) },
            text = { Text("...") },
            confirmButton = {
                TextButton(onClick = {
                    deleteArchiveTarget?.let { archiveState.delete(it.id) }
                    deleteArchiveTarget = null
                }) { Text(Lang.t("Delete", "حذف"), color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { deleteArchiveTarget = null }) { Text(Lang.t("Cancel", "إلغاء")) }
            }
        )
    }
}

@Composable
private fun NoticeBar(text: String) {
    Surface(color = MaterialTheme.colorScheme.primaryContainer, shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth()) {
        Text(text, color = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.padding(10.dp), style = MaterialTheme.typography.bodySmall)
    }
}
