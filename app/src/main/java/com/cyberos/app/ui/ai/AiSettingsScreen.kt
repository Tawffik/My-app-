package com.cyberos.app.ui.ai

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.cyberos.app.data.*
import com.cyberos.app.ui.lang.Lang

@Composable
fun AiSettingsScreen(state: AiState, onBack: () -> Unit) {
    val current = state.settings()
    var apiKey by remember { mutableStateOf("") }
    var baseUrl by remember { mutableStateOf(current.baseUrl) }
    var model by remember { mutableStateOf(current.model) }
    var confirmWipe by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = null) }
            Spacer(Modifier.width(8.dp))
            Text(Lang.t("AI Settings", "إعدادات المساعد"), style = MaterialTheme.typography.titleLarge)
        }
        Spacer(Modifier.height(16.dp))

        if (state.configured) {
            Text(Lang.t("Key stored, encrypted. Leave empty to keep.", "المفتاح مخزّن مشفّرًا."), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(8.dp))
        }

        OutlinedTextField(value = apiKey, onValueChange = { apiKey = it }, modifier = Modifier.fillMaxWidth(), label = { Text(Lang.t("API Key", "مفتاح API")) }, placeholder = { Text("sk-...") }, visualTransformation = PasswordVisualTransformation(), singleLine = true)
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(value = baseUrl, onValueChange = { baseUrl = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Base URL") }, singleLine = true)
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(value = model, onValueChange = { model = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Model") }, singleLine = true)

        Spacer(Modifier.height(16.dp))
        Button(onClick = { state.saveSettings(apiKey.ifBlank { null }, AiSettings(baseUrl, model)); onBack() }, modifier = Modifier.fillMaxWidth()) { Text(Lang.t("Save", "حفظ")) }
        Spacer(Modifier.height(8.dp))
        if (state.configured) {
            OutlinedButton(onClick = { confirmWipe = true }, modifier = Modifier.fillMaxWidth()) {
                Text(Lang.t("Delete key", "حذف المفتاح"), color = MaterialTheme.colorScheme.error)
            }
        }
        Spacer(Modifier.height(16.dp))
        Text("🔒 " + Lang.t("Keystore-encrypted. Secrets auto-hidden.", "مشفّر بـ Keystore. الأسرار بتتخفى تلقائيًا."), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }

    if (confirmWipe) {
        AlertDialog(
            onDismissRequest = { confirmWipe = false },
            title = { Text(Lang.t("Delete key?", "حذف؟")) },
            text = { Text(Lang.t("Assistant won't work until new key.", "مش هيشتغل لحد مفتاح جديد.")) },
            confirmButton = {
                TextButton(onClick = { state.removeKey(); confirmWipe = false }) {
                    Text(Lang.t("Delete", "حذف"), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmWipe = false }) { Text(Lang.t("Cancel", "إلغاء")) }
            }
        )
    }
}
