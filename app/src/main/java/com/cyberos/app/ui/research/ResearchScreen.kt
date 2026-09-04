package com.cyberos.app.ui.research

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.cyberos.app.data.ResearchItem
import com.cyberos.app.data.ResearchState
import com.cyberos.app.ui.EmptyState
import com.cyberos.app.ui.lang.Lang
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ResearchScreen(state: ResearchState, onOpenItem: (Long) -> Unit) {
    val scope = rememberCoroutineScope()
    LaunchedEffect(Unit) { if (state.items.isEmpty()) state.fetchLatest() }

    Column(Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Spacer(Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(Lang.t("Research", "الأبحاث"), style = MaterialTheme.typography.headlineSmall, modifier = Modifier.weight(1f))
            IconButton(onClick = { scope.launch { state.fetchLatest() } }, enabled = !state.refreshing) {
                if (state.refreshing) CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp)
                else Icon(Icons.Filled.Refresh, contentDescription = null)
            }
        }
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            ResearchState.CATEGORIES.forEach { c ->
                FilterChip(selected = state.category == c, onClick = { state.category = c }, label = { Text(c) })
            }
        }
        Spacer(Modifier.height(8.dp))

        state.lastError?.let { err ->
            Text(err, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.height(8.dp))
        }

        val list = state.filtered()
        if (list.isEmpty() && !state.refreshing) {
            EmptyState(Lang.t("No research items yet — tap refresh.", "مفيش عناصر بحث لسه — اضغط تحديث."))
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), contentPadding = PaddingValues(bottom = 16.dp)) {
                items(list, key = { it.id }) { item ->
                    ResearchCard(item = item, onOpen = { onOpenItem(item.id) }, onBookmark = { state.toggleBookmark(item.id) })
                }
            }
        }
    }
}

@Composable
private fun ResearchCard(item: ResearchItem, onOpen: () -> Unit, onBookmark: () -> Unit) {
    val dateText = remember(item.publishedAt) {
        if (item.publishedAt <= 0L) "" else SimpleDateFormat("d MMM", Locale.getDefault()).format(Date(item.publishedAt))
    }
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.clickable(onClick = onOpen).padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(item.title.ifBlank { "Untitled" }, style = MaterialTheme.typography.titleMedium, maxLines = 2, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                IconButton(onClick = onBookmark) {
                    Icon(
                        Icons.Filled.Star,
                        contentDescription = null,
                        tint = if (item.bookmarked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            if (item.summary.isNotBlank()) {
                Spacer(Modifier.height(4.dp))
                Text(item.summary, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(item.category, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.weight(1f))
                if (dateText.isNotBlank()) Text(dateText, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
