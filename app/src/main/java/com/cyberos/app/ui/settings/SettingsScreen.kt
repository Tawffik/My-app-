package com.cyberos.app.ui.settings

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.cyberos.app.data.*
import com.cyberos.app.flashcards.FlashcardStore
import com.cyberos.app.learning.ProgressStore
import com.cyberos.app.methodology.MethodologyStore
import com.cyberos.app.ui.lang.Lang

@Composable
fun SettingsScreen(
    langStore: LangStore, noteStore: NoteStore, cardStore: FlashcardStore,
    methStore: MethodologyStore, progressStore: ProgressStore,
    taskStore: TaskStore, projectStore: ProjectStore,
    onRestored: () -> Unit, onOpenAiSettings: () -> Unit, onBack: () -> Unit
) {
    val context = LocalContext.current
    var backupStatus by remember { mutableStateOf<String?>(null) }

    val exportLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri ->
        if (uri != null) {
            val ok = Backup.export(context, uri, noteStore, cardStore, methStore, progressStore, taskStore, projectStore)
            backupStatus = if (ok) Lang.t("Export OK", "تم التصدير") else Lang.t("Export failed", "فشل")
        }
    }
    val importLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            val ok = Backup.import(context, uri, noteStore, cardStore, methStore, progressStore, taskStore, projectStore)
            backupStatus = if (ok) { onRestored(); Lang.t("Imported", "تم الاستيراد") } else Lang.t("Invalid file", "ملف غير صالح")
        }
    }

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = null) }
            Text(Lang.t("Settings", "الإعدادات"), style = MaterialTheme.typography.titleLarge)
        }
        Spacer(Modifier.height(16.dp))

        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text(Lang.t("Language", "اللغة"), style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(selected = Lang.current == "en", onClick = { langStore.save("en") }, label = { Text("English") })
                    FilterChip(selected = Lang.current == "ar", onClick = { langStore.save("ar") }, label = { Text("عربي") })
                }
            }
        }
        Spacer(Modifier.height(12.dp))

        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text(Lang.t("AI Provider", "مزوّد AI"), style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(10.dp))
                Button(onOpenAiSettings, Modifier.fillMaxWidth()) { Text(Lang.t("Configure", "إعداد")) }
            }
        }
        Spacer(Modifier.height(12.dp))

        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text(Lang.t("Data & Backup", "البيانات"), style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(10.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { exportLauncher.launch("CyberOS-backup.json") }, modifier = Modifier.weight(1f)) { Text(Lang.t("Export", "تصدير")) }
                    OutlinedButton(onClick = { importLauncher.launch(arrayOf("application/json")) }, modifier = Modifier.weight(1f)) { Text(Lang.t("Import", "استيراد")) }
                }
                backupStatus?.let { Spacer(Modifier.height(8.dp)); Text(it) }
            }
        }
        Spacer(Modifier.height(12.dp))
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text("About", style = MaterialTheme.typography.titleMedium)
                Text("CyberOS v1.0.0")
            }
        }
    }
}
