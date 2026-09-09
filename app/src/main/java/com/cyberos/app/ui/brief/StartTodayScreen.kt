package com.cyberos.app.ui.brief

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cyberos.app.data.BugBountyFinding
import com.cyberos.app.data.FindingNextAction
import com.cyberos.app.data.ResearchItem
import com.cyberos.app.learning.TopicData
import com.cyberos.app.ui.lang.Lang

/**
 * Closed daily loop: Review → Learn → Note → Apply (optional finding/writeup).
 */
@Composable
fun StartTodayScreen(
    cardsDue: Int,
    nextTopic: TopicData?,
    openFindings: List<BugBountyFinding>,
    recentWriteup: ResearchItem?,
    onBack: () -> Unit,
    onReview: () -> Unit,
    onOpenTopic: (String) -> Unit,
    onOpenNotes: () -> Unit,
    onOpenFinding: (Long) -> Unit,
    onOpenResearch: () -> Unit,
    onDone: () -> Unit
) {
    var stepDone by remember { mutableStateOf(setOf<Int>()) }

    fun mark(i: Int) {
        stepDone = stepDone + i
    }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.ArrowBack, contentDescription = null)
            }
            Column(Modifier.weight(1f)) {
                Text(Lang.t("Start today", "ابدأ اليوم"), style = MaterialTheme.typography.titleLarge)
                Text(
                    Lang.t(
                        "One focused loop — finish what you start.",
                        "حلقة واحدة مركّزة — خلّص اللي بدأتة."
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Spacer(Modifier.height(12.dp))

        StepCard(
            index = 1,
            title = Lang.t("Review flashcards", "راجع الكروت"),
            detail = if (cardsDue > 0) "$cardsDue due" else Lang.t("None due — quick pass still helps", "مفيش مستحق — مراجعة سريعة مفيدة"),
            done = 1 in stepDone,
            actionLabel = Lang.t("Go", "اذهب"),
            onAction = {
                mark(1)
                onReview()
            }
        )
        Spacer(Modifier.height(8.dp))
        StepCard(
            index = 2,
            title = Lang.t("One learning topic", "موضوع تعلّم واحد"),
            detail = nextTopic?.title ?: Lang.t("Pick any path in Learn", "اختار أي مسار من تعلّم"),
            done = 2 in stepDone,
            actionLabel = Lang.t("Open topic", "افتح الموضوع"),
            onAction = {
                mark(2)
                if (nextTopic != null) onOpenTopic(nextTopic.id) else onBack()
            },
            enabled = nextTopic != null
        )
        Spacer(Modifier.height(8.dp))
        StepCard(
            index = 3,
            title = Lang.t("Capture a study note", "سجّل نوت دراسة"),
            detail = Lang.t("Use a template: Observation → Hypothesis → Validation", "استخدم قالب: ملاحظة → فرضية → تحقق"),
            done = 3 in stepDone,
            actionLabel = Lang.t("Open Notes", "افتح النوتس"),
            onAction = {
                mark(3)
                onOpenNotes()
            }
        )
        Spacer(Modifier.height(8.dp))
        val focusFinding = openFindings.firstOrNull { FindingNextAction.needsActionToday(it.status) }
        StepCard(
            index = 4,
            title = Lang.t("Apply (optional)", "طبّق (اختياري)"),
            detail = when {
                focusFinding != null ->
                    "${focusFinding.title}\n→ ${FindingNextAction.forStatus(focusFinding.status)}"
                recentWriteup != null ->
                    Lang.t("Read one writeup: ", "اقرأ رايت أب: ") + recentWriteup.title
                else ->
                    Lang.t("No open finding — skim Research writeups", "مفيش finding — تصفّح الأبحاث")
            },
            done = 4 in stepDone,
            actionLabel = when {
                focusFinding != null -> Lang.t("Open finding", "افتح الـ finding")
                else -> Lang.t("Open Research", "افتح الأبحاث")
            },
            onAction = {
                mark(4)
                if (focusFinding != null) onOpenFinding(focusFinding.id) else onOpenResearch()
            }
        )

        Spacer(Modifier.height(16.dp))
        val n = stepDone.size
        Text(
            Lang.t("Progress: $n / 4 steps touched", "التقدم: $n / 4 خطوات"),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(8.dp))
        Button(
            onClick = onDone,
            modifier = Modifier.fillMaxWidth(),
            enabled = n >= 2
        ) {
            Text(Lang.t("Mark session complete", "إنهاء الجلسة"))
        }
        Text(
            Lang.t(
                "Tip: authorized targets only. AI output = hypothesis until you verify.",
                "تذكير: أهداف مصرّح بها فقط. مخرجات AI فرضية حتى تتحقق."
            ),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 12.dp)
        )
    }
}

@Composable
private fun StepCard(
    index: Int,
    title: String,
    detail: String,
    done: Boolean,
    actionLabel: String,
    onAction: () -> Unit,
    enabled: Boolean = true
) {
    Card(
        Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (done) MaterialTheme.colorScheme.secondaryContainer
            else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(Modifier.padding(14.dp)) {
            Text(
                "${if (done) "✓" else index}. $title",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(4.dp))
            Text(
                detail,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(8.dp))
            OutlinedButton(onClick = onAction, enabled = enabled) {
                Text(actionLabel)
            }
        }
    }
}
