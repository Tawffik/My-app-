package com.cyberos.app.ui.bugbounty

import com.cyberos.app.data.FindingNextAction

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.cyberos.app.data.BugBountyFilters
import com.cyberos.app.data.BugBountyFinding
import com.cyberos.app.data.BugBountyProgram
import com.cyberos.app.data.BugBountyState
import com.cyberos.app.ui.EmptyState
import com.cyberos.app.ui.lang.Lang

@Composable
fun BugBountyScreen(
    state: BugBountyState,
    onBack: () -> Unit,
    onOpenFinding: (Long) -> Unit,
    onNewFinding: () -> Unit,
    onOpenProgram: (Long) -> Unit,
    onNewProgram: () -> Unit,
    onOpenChecklists: () -> Unit = {},
    onOpenAssets: (Long) -> Unit = {}
) {
    var section by remember { mutableStateOf(0) } // 0 findings, 1 programs

    Column(Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Spacer(Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = null) }
            Text(
                Lang.t("Bug Bounty", "Bug Bounty"),
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = { if (section == 0) onNewFinding() else onNewProgram() }) {
                Icon(Icons.Filled.Add, contentDescription = null)
            }
        }
        Spacer(Modifier.height(4.dp))
        Card(
            Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
        ) {
            Text(
                Lang.t(
                    "Authorized scope only. AI suggestions = hypotheses until you verify.",
                    "نطاق مصرّح فقط. اقتراحات AI فرضيات حتى تتحقق بنفسك."
                ),
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(10.dp)
            )
        }
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = section == 0,
                onClick = { section = 0 },
                label = { Text(Lang.t("Findings", "Findings")) }
            )
            FilterChip(
                selected = section == 1,
                onClick = { section = 1 },
                label = { Text(Lang.t("Programs", "Programs")) }
            )
            FilterChip(
                selected = false,
                onClick = onOpenChecklists,
                label = { Text(Lang.t("Checklists", "Checklists")) }
            )
        }
        Spacer(Modifier.height(8.dp))

        if (section == 0) {
            FindingsList(state, onOpenFinding)
        } else {
            ProgramsList(state, onOpenProgram, onOpenAssets)
        }
    }
}

@Composable
private fun FindingsList(state: BugBountyState, onOpen: (Long) -> Unit) {
    Row(
        Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        BugBountyState.STATUS_FILTERS.forEach { s ->
            FilterChip(
                selected = state.statusFilter == s,
                onClick = { state.statusFilter = s },
                label = { Text(s) }
            )
        }
    }
    Spacer(Modifier.height(6.dp))
    Row(
        Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        BugBountyFilters.VULN_TYPES.forEach { v ->
            FilterChip(
                selected = state.vulnFilter == v,
                onClick = { state.vulnFilter = v },
                label = { Text(v) }
            )
        }
    }
    Spacer(Modifier.height(8.dp))

    val list = state.filteredFindings()
    if (list.isEmpty()) {
        EmptyState(
            Lang.t(
                "No findings yet. Add a program, then log your first idea.",
                "No findings yet. Add a program, then log your first idea."
            )
        )
    } else {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            items(list, key = { it.id }) { f ->
                FindingCard(f, state.programName(f.programId), onClick = { onOpen(f.id) })
            }
        }
    }
}

@Composable
private fun FindingCard(f: BugBountyFinding, programName: String, onClick: () -> Unit) {
    Card(Modifier.fillMaxWidth().clickable(onClick = onClick)) {
        Column(Modifier.padding(14.dp)) {
            Text(f.title, style = MaterialTheme.typography.titleMedium, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                AssistChip(onClick = {}, enabled = false, label = { Text(f.status, style = MaterialTheme.typography.labelSmall) })
                if (f.vulnerabilityType.isNotBlank()) {
                    AssistChip(onClick = {}, enabled = false, label = { Text(f.vulnerabilityType, style = MaterialTheme.typography.labelSmall) })
                }
                if (f.severity != "None") {
                    AssistChip(onClick = {}, enabled = false, label = { Text(f.severity, style = MaterialTheme.typography.labelSmall) })
                }
            }
            if (FindingNextAction.needsActionToday(f.status)) {
                Text(
                    "→ " + FindingNextAction.forStatus(f.status),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 2
                )
            }
            if (programName.isNotBlank() || f.asset.isNotBlank()) {
                Spacer(Modifier.height(4.dp))
                Text(
                    listOfNotNull(programName.ifBlank { null }, f.asset.ifBlank { null }).joinToString(" · "),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun ProgramsList(state: BugBountyState, onOpen: (Long) -> Unit, onOpenAssets: (Long) -> Unit) {
    if (state.programs.isEmpty()) {
        EmptyState(
            Lang.t(
                "No programs yet. Add the bug bounty program you are testing.",
                "No programs yet. Add the bug bounty program you are testing."
            )
        )
        return
    }
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        items(state.programs, key = { it.id }) { p ->
            ProgramCard(
                p,
                findingCount = state.findings.count { it.programId == p.id },
                assetCount = state.assets.count { it.programId == p.id },
                onClick = { onOpen(p.id) },
                onAssets = { onOpenAssets(p.id) }
            )
        }
    }
}

@Composable
private fun ProgramCard(
    p: BugBountyProgram,
    findingCount: Int,
    assetCount: Int,
    onClick: () -> Unit,
    onAssets: () -> Unit
) {
    Card(Modifier.fillMaxWidth().clickable(onClick = onClick)) {
        Column(Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(p.name, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                if (!p.active) {
                    Text("Paused", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.error)
                }
            }
            Spacer(Modifier.height(4.dp))
            Text(
                listOfNotNull(
                    p.platform.ifBlank { null },
                    "$findingCount findings",
                    "$assetCount assets"
                ).joinToString(" · "),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (p.scopeSummary.isNotBlank()) {
                Spacer(Modifier.height(4.dp))
                Text(p.scopeSummary, style = MaterialTheme.typography.labelSmall, maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
            Spacer(Modifier.height(6.dp))
            TextButton(onClick = onAssets) { Text(Lang.t("Assets", "Assets")) }
        }
    }
}
