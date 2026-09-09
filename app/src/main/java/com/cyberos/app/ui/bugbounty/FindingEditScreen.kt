package com.cyberos.app.ui.bugbounty

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import android.widget.Toast
import com.cyberos.app.data.BugBountyFilters
import com.cyberos.app.data.BugBountyFinding
import com.cyberos.app.data.BugBountyState
import com.cyberos.app.data.FindingSeverities
import com.cyberos.app.data.FindingStatuses
import com.cyberos.app.data.FindingNextAction
import com.cyberos.app.ui.lang.Lang

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FindingEditScreen(
    state: BugBountyState,
    findingId: Long,
    prefillTitle: String = "",
    prefillVuln: String = "",
    linkedResearchId: Long = 0L,
    onBack: () -> Unit,
    onAiReview: ((title: String, reportMarkdown: String) -> Unit)? = null
) {
    val existing = if (findingId > 0) state.getFinding(findingId) else null
    var title by remember { mutableStateOf(existing?.title ?: prefillTitle) }
    var programId by remember { mutableStateOf(existing?.programId ?: 0L) }
    var vuln by remember { mutableStateOf(existing?.vulnerabilityType ?: prefillVuln.ifBlank { "Other" }) }
    var severity by remember { mutableStateOf(existing?.severity ?: "None") }
    var status by remember { mutableStateOf(existing?.status ?: "Idea") }
    var asset by remember { mutableStateOf(existing?.asset ?: "") }
    var endpoint by remember { mutableStateOf(existing?.endpoint ?: "") }
    var description by remember { mutableStateOf(existing?.description ?: "") }
    var steps by remember { mutableStateOf(existing?.stepsToReproduce ?: "") }
    var impact by remember { mutableStateOf(existing?.impact ?: "") }
    var rootCause by remember { mutableStateOf(existing?.rootCause ?: "") }
    var remediation by remember { mutableStateOf(existing?.remediation ?: "") }
    var evidence by remember { mutableStateOf(existing?.evidenceNotes ?: "") }
    var showDelete by remember { mutableStateOf(false) }

    var programMenu by remember { mutableStateOf(false) }
    var statusMenu by remember { mutableStateOf(false) }
    var severityMenu by remember { mutableStateOf(false) }
    var vulnMenu by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = null) }
            Text(
                if (findingId > 0) Lang.t("Edit Finding", "Edit Finding")
                else Lang.t("New Finding", "New Finding"),
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.weight(1f)
            )
            if (findingId > 0) {
                IconButton(onClick = { showDelete = true }) {
                    Icon(Icons.Filled.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                }
            }
        }
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text(Lang.t("Title", "Title")) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(Modifier.height(8.dp))

        ExposedDropdownMenuBox(expanded = programMenu, onExpandedChange = { programMenu = it }) {
            OutlinedTextField(
                value = state.programName(programId).ifBlank { Lang.t("No program", "No program") },
                onValueChange = {},
                readOnly = true,
                label = { Text(Lang.t("Program", "Program")) },
                modifier = Modifier.menuAnchor().fillMaxWidth(),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(programMenu) }
            )
            ExposedDropdownMenu(expanded = programMenu, onDismissRequest = { programMenu = false }) {
                DropdownMenuItem(
                    text = { Text(Lang.t("No program", "No program")) },
                    onClick = { programId = 0L; programMenu = false }
                )
                state.programs.forEach { p ->
                    DropdownMenuItem(
                        text = { Text(p.name) },
                        onClick = { programId = p.id; programMenu = false }
                    )
                }
            }
        }
        Spacer(Modifier.height(8.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ExposedDropdownMenuBox(expanded = statusMenu, onExpandedChange = { statusMenu = it }, modifier = Modifier.weight(1f)) {
                OutlinedTextField(
                    value = status, onValueChange = {}, readOnly = true,
                    label = { Text("Status") },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(statusMenu) }
                )
                ExposedDropdownMenu(expanded = statusMenu, onDismissRequest = { statusMenu = false }) {
                    FindingStatuses.ALL.forEach { s ->
                        DropdownMenuItem(text = { Text(s) }, onClick = { status = s; statusMenu = false })
                    }
                }
            }
            ExposedDropdownMenuBox(expanded = severityMenu, onExpandedChange = { severityMenu = it }, modifier = Modifier.weight(1f)) {
                OutlinedTextField(
                    value = severity, onValueChange = {}, readOnly = true,
                    label = { Text("Severity") },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(severityMenu) }
                )
                ExposedDropdownMenu(expanded = severityMenu, onDismissRequest = { severityMenu = false }) {
                    FindingSeverities.ALL.forEach { s ->
                        DropdownMenuItem(text = { Text(s) }, onClick = { severity = s; severityMenu = false })
                    }
                }
            }
        }
        Spacer(Modifier.height(8.dp))

        ExposedDropdownMenuBox(expanded = vulnMenu, onExpandedChange = { vulnMenu = it }) {
            OutlinedTextField(
                value = vuln, onValueChange = {}, readOnly = true,
                label = { Text(Lang.t("Vulnerability type", "Vulnerability type")) },
                modifier = Modifier.menuAnchor().fillMaxWidth(),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(vulnMenu) }
            )
            ExposedDropdownMenu(expanded = vulnMenu, onDismissRequest = { vulnMenu = false }) {
                BugBountyFilters.VULN_TYPES.filter { it != "All" }.forEach { v ->
                    DropdownMenuItem(text = { Text(v) }, onClick = { vuln = v; vulnMenu = false })
                }
            }
        }
        Spacer(Modifier.height(8.dp))

        Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
            Column(Modifier.padding(12.dp)) {
                Text(Lang.t("Next action", "الخطوة الجاية"), style = MaterialTheme.typography.titleSmall)
                Text(
                    FindingNextAction.forStatus(status),
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    Lang.t(
                        "Only log findings on programs you are authorized to test.",
                        "سجّل findings فقط على برامج مصرّح باختبارها."
                    ),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = asset, onValueChange = { asset = it }, label = { Text("Asset") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = endpoint, onValueChange = { endpoint = it }, label = { Text("Endpoint") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = steps, onValueChange = { steps = it }, label = { Text("Steps to reproduce") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = impact, onValueChange = { impact = it }, label = { Text("Impact") }, modifier = Modifier.fillMaxWidth(), minLines = 2)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = rootCause, onValueChange = { rootCause = it }, label = { Text("Root cause") }, modifier = Modifier.fillMaxWidth(), minLines = 2)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = remediation, onValueChange = { remediation = it }, label = { Text("Remediation") }, modifier = Modifier.fillMaxWidth(), minLines = 2)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = evidence, onValueChange = { evidence = it }, label = { Text("Evidence notes") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
        Spacer(Modifier.height(16.dp))

        val clipboard = LocalClipboardManager.current
        val ctx = LocalContext.current
        Button(
            onClick = {
                if (title.isBlank()) return@Button
                val linked = (existing?.linkedResearchIds ?: emptyList()).toMutableList()
                if (linkedResearchId > 0L && linkedResearchId !in linked) linked.add(linkedResearchId)
                state.upsertFinding(
                    BugBountyFinding(
                        id = if (findingId > 0) findingId else 0L,
                        programId = programId,
                        title = title.trim(),
                        vulnerabilityType = vuln,
                        severity = severity,
                        status = status,
                        asset = asset.trim(),
                        endpoint = endpoint.trim(),
                        description = description.trim(),
                        stepsToReproduce = steps.trim(),
                        impact = impact.trim(),
                        rootCause = rootCause.trim(),
                        remediation = remediation.trim(),
                        evidenceNotes = evidence.trim(),
                        linkedResearchIds = linked
                    )
                )
                onBack()
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = title.isNotBlank()
        ) {
            Text(Lang.t("Save Finding", "Save Finding"))
        }
        if (findingId > 0) {
            Spacer(Modifier.height(8.dp))
            OutlinedButton(
                onClick = {
                    val md = state.reportMarkdown(findingId)
                    if (md.isNotBlank()) {
                        clipboard.setText(AnnotatedString(md))
                        Toast.makeText(ctx, Lang.t("Report copied as Markdown", "Report copied as Markdown"), Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(Lang.t("Copy Markdown Report", "Copy Markdown Report"))
            }
            if (onAiReview != null) {
                Spacer(Modifier.height(8.dp))
                OutlinedButton(
                    onClick = {
                        val md = state.reportMarkdown(findingId)
                        onAiReview(title.ifBlank { "Finding" }, md)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = title.isNotBlank()
                ) {
                    Text(Lang.t("AI Report Review", "AI Report Review"))
                }
            }
        }
        Spacer(Modifier.height(24.dp))
    }

    if (showDelete) {
        AlertDialog(
            onDismissRequest = { showDelete = false },
            title = { Text(Lang.t("Delete finding?", "Delete finding?")) },
            text = { Text(Lang.t("This cannot be undone.", "This cannot be undone.")) },
            confirmButton = {
                TextButton(onClick = {
                    state.deleteFinding(findingId)
                    showDelete = false
                    onBack()
                }) { Text(Lang.t("Delete", "Delete")) }
            },
            dismissButton = {
                TextButton(onClick = { showDelete = false }) { Text(Lang.t("Cancel", "Cancel")) }
            }
        )
    }
}
