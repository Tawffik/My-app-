package com.cyberos.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.cyberos.app.data.*
import com.cyberos.app.flashcards.*
import com.cyberos.app.learning.*
import com.cyberos.app.methodology.*
import com.cyberos.app.ui.ai.*
import com.cyberos.app.ui.challenge.ChallengeScreen
import com.cyberos.app.ui.focus.FocusScreen
import com.cyberos.app.ui.home.HomeScreen
import com.cyberos.app.ui.lang.Lang
import com.cyberos.app.ui.learning.*
import com.cyberos.app.ui.methodology.*
import com.cyberos.app.ui.notes.*
import com.cyberos.app.ui.quiz.QuizScreen
import com.cyberos.app.ui.review.ReviewScreen
import com.cyberos.app.ui.search.SearchScreen
import com.cyberos.app.ui.settings.SettingsScreen
import com.cyberos.app.ui.tasks.*
import com.cyberos.app.ui.research.*
import com.cyberos.app.ui.bugbounty.*
import com.cyberos.app.ui.brief.DailyBriefScreen
import com.cyberos.app.ui.brief.DailyBriefModel
import com.cyberos.app.ui.theme.CyberTheme

@Composable
fun CyberOSApp() {
    val appCtx = LocalContext.current.applicationContext

    val langStore = remember { LangStore(appCtx).also { Lang.current = it.load() } }
    val noteStore = remember { NoteStore(appCtx) }
    val notesState = remember { NotesState(noteStore) }
    val vault = remember { ApiKeyVault(appCtx) }
    val aiSettings = remember { AiSettingsStore(appCtx) }
    val aiClient = remember { AiClient() }
    val aiState = remember { AiState(vault, aiSettings, aiClient) }
    val chatArchiveState = remember { ChatArchiveState(ChatArchiveStore(appCtx)) }
    val councilState = remember { CouncilState(vault, aiSettings, aiClient, CouncilStore(appCtx)) }
    val cardStore = remember { FlashcardStore(appCtx).also { it.ensureSeeded() } }
    val reviewState = remember { ReviewState(cardStore) }
    val methStore = remember { MethodologyStore(appCtx).also { it.ensureSeeded() } }
    val methState = remember { MethodologyState(methStore) }
    val progressStore = remember { ProgressStore(appCtx) }
    val progressState = remember { ProgressState(progressStore) }
    val customPathStore = remember { CustomPathStore(appCtx) }
    // merge user paths into curriculum
    CyberCurriculum.dynamicPaths = customPathStore.all()
    val taskStore = remember { TaskStore(appCtx) }
    val taskState = remember { TaskState(taskStore) }
    val projectStore = remember { ProjectStore(appCtx) }
    val projectState = remember { ProjectState(projectStore) }
    val quizState = remember { QuizState() }
    val researchItemStore = remember { ResearchItemStore(appCtx) }
    val researchSourceStore = remember { ResearchSourceStore(appCtx).also { it.ensureSeeded() } }
    val researchFetcher = remember { ResearchFetcher(researchSourceStore, researchItemStore) }
    val researchState = remember { ResearchState(researchItemStore, researchSourceStore, researchFetcher, appCtx) }
    val bbProgramStore = remember { BugBountyProgramStore(appCtx) }
    val bbFindingStore = remember { BugBountyFindingStore(appCtx) }
    val bbAssetStore = remember { BugBountyAssetStore(appCtx) }
    val bugBountyState = remember { BugBountyState(bbProgramStore, bbFindingStore, bbAssetStore) }
    val checklistProgress = remember { ChecklistProgressStore(appCtx) }

    aiState.ragSource = { notesState.notes }

    val refreshAll: () -> Unit = {
        notesState.reload(); methState.refresh(); progressState.refresh()
        reviewState.start(); taskState.refresh(); projectState.refresh(); chatArchiveState.refresh()
    }

    var editingId by rememberSaveable { mutableStateOf(0L) }
    var notesGraphOpen by rememberSaveable { mutableStateOf(false) }
    var tab by rememberSaveable { mutableStateOf(0) }
    var aiMode by rememberSaveable { mutableStateOf(0) }
    var aiSettingsOpen by rememberSaveable { mutableStateOf(false) }
    var settingsOpen by rememberSaveable { mutableStateOf(false) }
    var openTopicId by rememberSaveable { mutableStateOf("") }
    var methListOpen by rememberSaveable { mutableStateOf(false) }
    var methOpenId by rememberSaveable { mutableStateOf(0L) }
    var graphOpen by rememberSaveable { mutableStateOf(false) }
    var taskEditId by rememberSaveable { mutableStateOf(0L) }
    var projectOpenId by rememberSaveable { mutableStateOf(0L) }
    var searchOpen by rememberSaveable { mutableStateOf(false) }
    var focusOpen by rememberSaveable { mutableStateOf(false) }
    var cardGenOpen by rememberSaveable { mutableStateOf(false) }
    var cardGenSource by rememberSaveable { mutableStateOf("") }
    var quizOpen by rememberSaveable { mutableStateOf<String?>(null) }
    var challengeOpen by rememberSaveable { mutableStateOf(false) }
    var researchOpenId by rememberSaveable { mutableStateOf(0L) }
    var bbOpen by rememberSaveable { mutableStateOf(false) }
    var bbFindingId by rememberSaveable { mutableStateOf(0L) }
    var bbProgramId by rememberSaveable { mutableStateOf(0L) }
    var bbPrefillTitle by rememberSaveable { mutableStateOf("") }
    var bbPrefillVuln by rememberSaveable { mutableStateOf("") }
    var bbLinkedResearchId by rememberSaveable { mutableStateOf(0L) }
    var bbChecklistOpen by rememberSaveable { mutableStateOf(false) }
    var bbAssetsProgramId by rememberSaveable { mutableStateOf(0L) }
    var briefOpen by rememberSaveable { mutableStateOf(false) }
    var noteTemplateId by rememberSaveable { mutableStateOf("") }

    val overlayOpen = editingId != 0L || notesGraphOpen || openTopicId.isNotEmpty() ||
        methListOpen || methOpenId != 0L || aiSettingsOpen || settingsOpen || graphOpen ||
        taskEditId != 0L || projectOpenId != 0L || searchOpen || focusOpen ||
        cardGenOpen || quizOpen != null || challengeOpen || researchOpenId != 0L || bbOpen || bbFindingId != 0L || bbProgramId != 0L || bbChecklistOpen || bbAssetsProgramId != 0L || briefOpen

    CyberTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                if (!overlayOpen) {
                    NavigationBar {
                        NavigationBarItem(selected = tab == 0, onClick = { tab = 0 },
                            icon = { Icon(Icons.Filled.Home, contentDescription = null) },
                            label = { Text(Lang.t("Home", "الرئيسية")) })
                        NavigationBarItem(selected = tab == 1, onClick = { tab = 1 },
                            icon = { Icon(Icons.Filled.PlayArrow, contentDescription = null) },
                            label = { Text(Lang.t("Learn", "تعلّم")) })
                        NavigationBarItem(selected = tab == 2, onClick = { tab = 2 },
                            icon = { Icon(Icons.Filled.Refresh, contentDescription = null) },
                            label = { Text(Lang.t("Review", "مراجعة")) })
                        NavigationBarItem(selected = tab == 3, onClick = { tab = 3 },
                            icon = { Icon(Icons.Filled.List, contentDescription = null) },
                            label = { Text(Lang.t("Tasks", "مهام")) })
                        NavigationBarItem(selected = tab == 4, onClick = { tab = 4 },
                            icon = { Icon(Icons.Filled.Edit, contentDescription = null) },
                            label = { Text(Lang.t("Notes", "ملاحظات")) })
                        NavigationBarItem(selected = tab == 5, onClick = { tab = 5 },
                            icon = { Icon(Icons.Filled.Info, contentDescription = null) },
                            label = { Text("AI") })
                        NavigationBarItem(selected = tab == 6, onClick = { tab = 6 },
                            icon = { Icon(Icons.Filled.Search, contentDescription = null) },
                            label = { Text(Lang.t("Research", "أبحاث")) })
                    }
                }
            },
            floatingActionButton = {
                if (!overlayOpen) {
                    when (tab) {
                        3 -> FloatingActionButton(onClick = { taskEditId = -1L }) {
                            Icon(Icons.Filled.Add, contentDescription = null)
                        }
                        4 -> FloatingActionButton(onClick = { editingId = -1L }) {
                            Icon(Icons.Filled.Add, contentDescription = null)
                        }
                    }
                }
            }
        ) { padding ->
            Box(Modifier.fillMaxSize().padding(padding)) {
                when {
                    editingId != 0L -> NoteEditScreen(
                        note = when {
                            editingId > 0 -> notesState.get(editingId)
                            noteTemplateId.isNotBlank() -> {
                                val tpl = NoteTemplates.byId(noteTemplateId)
                                if (tpl != null) Note(
                                    id = -1L,
                                    title = tpl.title,
                                    body = tpl.body,
                                    tags = tpl.tags,
                                    createdAt = 0L,
                                    updatedAt = 0L
                                ) else null
                            }
                            else -> null
                        },
                        allNotes = notesState.notes,
                        onOpenNote = { id -> editingId = id },
                        onBack = { editingId = 0L; noteTemplateId = "" },
                        onSave = { t, b, tg, folder, pinned ->
                            notesState.upsert(editingId, t, b, tg, folder, pinned)
                            editingId = 0L
                            noteTemplateId = ""
                        },
                        onAskAi = { t, b ->
                            aiState.pendingQuestion = "حلّل الملاحظة دي أمنيًا وصحّح أي معلومة."
                            aiState.pendingContext = "Title: $t\n\nContent:\n$b"
                            editingId = 0L; aiMode = 0; tab = 5
                        },
                        onGenerateCards = { t, b ->
                            cardGenSource = "Title: $t\n\n$b"; editingId = 0L; cardGenOpen = true
                        },
                        onCreateLinkedNote = { linkTitle ->
                            notesState.upsert(
                                -1L,
                                linkTitle,
                                "## Linked from another note\n\n",
                                listOf("linked"),
                                folder = "Inbox",
                                pinned = false
                            )
                            val created = notesState.notes.firstOrNull {
                                it.title.equals(linkTitle, ignoreCase = true)
                            }
                            if (created != null) editingId = created.id
                        },
                        onDuplicate = {
                            if (editingId > 0) {
                                val nid = notesState.duplicate(editingId)
                                if (nid > 0) editingId = nid
                            }
                        },
                        unlinkedMentions = if (editingId > 0) {
                            val n = notesState.get(editingId)
                            if (n != null) notesState.unlinkedMentions(n.title, n.id) else emptyList()
                        } else emptyList()
                    )
                    notesGraphOpen -> NotesGraphScreen(
                        notes = notesState.notes,
                        onBack = { notesGraphOpen = false },
                        onOpen = { id -> notesGraphOpen = false; editingId = id }
                    )
                    taskEditId != 0L -> TaskEditScreen(
                        task = if (taskEditId > 0) taskState.get(taskEditId) else null,
                        projects = projectState.list,
                        onBack = { taskEditId = 0L },
                        onSave = { t, n, s, p, pid, d -> taskState.upsert(taskEditId, t, n, s, p, pid, d); taskEditId = 0L },
                        onDelete = { id -> taskState.delete(id); taskEditId = 0L }
                    )
                    openTopicId.isNotEmpty() -> TopicScreen(
                        topicId = openTopicId, progress = progressState,
                        onBack = { openTopicId = "" },
                        onAskAi = { q ->
                            aiState.mode = ChatMode.AI_TUTOR
                            aiState.pendingQuestion = q
                            openTopicId = ""
                            aiMode = 0
                            tab = 5
                        },
                        onOpenTopic = { openTopicId = it },
                        onOpenQuiz = { tid -> quizState.startForTopic(tid); quizOpen = tid },
                        onCreateNote = { title, body ->
                            notesState.upsert(-1L, title, body, listOf("study", "learning"))
                            openTopicId = ""
                            tab = 4
                        }
                    )
                    quizOpen != null -> QuizScreen(state = quizState, progress = progressState, onClose = { quizOpen = null })
                    challengeOpen -> ChallengeScreen(
                        vault = vault, settingsStore = aiSettings, client = aiClient,
                        progress = progressState, onBack = { challengeOpen = false }
                    )
                    cardGenOpen -> CardGenScreen(
                        source = cardGenSource, vault = vault, settingsStore = aiSettings,
                        client = aiClient, cardStore = cardStore, progress = progressState,
                        onBack = { cardGenOpen = false }
                    )
                    projectOpenId != 0L -> ProjectDetailScreen(
                        projectState = projectState, taskState = taskState,
                        progress = progressState, id = projectOpenId,
                        onBack = { projectOpenId = 0L },
                        onOpenTask = { id -> taskEditId = id }
                    )
                    searchOpen -> SearchScreen(
                        notes = notesState.notes, tasks = taskState.tasks,
                        projects = projectState.list, meths = methState.list,
                        onOpen = { kind, ref ->
                            searchOpen = false
                            when (kind) {
                                "topic" -> openTopicId = ref
                                "note" -> editingId = ref.toLong()
                                "task" -> taskEditId = ref.toLong()
                                "project" -> projectOpenId = ref.toLong()
                                "methodology" -> methOpenId = ref.toLong()
                            }
                        },
                        onBack = { searchOpen = false }
                    )
                    focusOpen -> FocusScreen(progress = progressState, onBack = { focusOpen = false })
                    graphOpen -> KnowledgeGraphScreen(
                        onBack = { graphOpen = false },
                        onOpenTopic = { graphOpen = false; openTopicId = it }
                    )
                    methOpenId != 0L -> MethodologyEditScreen(
                        state = methState, id = methOpenId, progress = progressState,
                        onBack = { methOpenId = 0L }
                    )
                    methListOpen -> MethodologyListScreen(
                        state = methState, onBack = { methListOpen = false },
                        onOpen = { id -> methOpenId = id }
                    )
                    aiSettingsOpen -> AiSettingsScreen(state = aiState, onBack = { aiSettingsOpen = false })
                    settingsOpen -> SettingsScreen(
                        langStore = langStore, noteStore = noteStore, cardStore = cardStore,
                        methStore = methStore, progressStore = progressStore,
                        taskStore = taskStore, projectStore = projectStore,
                        onRestored = refreshAll,
                        onOpenAiSettings = { aiSettingsOpen = true },
                        onBack = { settingsOpen = false }
                    )
                    tab == 0 -> HomeScreen(
                        progress = progressState, cardStore = cardStore,
                        tasks = taskState.tasks,
                        onOpenTopic = { openTopicId = it },
                        onGoReview = { tab = 2 }, onGoNotes = { tab = 4 },
                        onOpenMethodologies = { methListOpen = true },
                        onOpenSearch = { searchOpen = true },
                        onOpenFocus = { focusOpen = true },
                        onOpenSettings = { settingsOpen = true },
                        onOpenQuiz = { quizState.startMixed(); quizOpen = "mixed" },
                        onOpenChallenge = { challengeOpen = true },
                        onOpenBugBounty = { bbOpen = true },
                        onOpenBrief = { briefOpen = true },
                        onOpenAiTutor = {
                            aiState.mode = ChatMode.AI_TUTOR
                            aiMode = 0
                            tab = 5
                        },
                        onOpenResearch = { tab = 6 },
                        openFindingsCount = bugBountyState.findings.count {
                            it.status !in listOf("Closed", "Resolved", "Duplicate", "N/A", "Submitted", "Triaged")
                        },
                        researchPulse = researchState.items.count {
                            System.currentTimeMillis() - it.publishedAt < 72L * 3600_000L
                        }
                    )
                    briefOpen -> DailyBriefScreen(
                        model = DailyBriefModel(
                            cardsDue = cardStore.countDue(System.currentTimeMillis()),
                            streak = progressState.streak,
                            xp = progressState.xp,
                            nextTopic = CyberCurriculum.firstIncompleteTopic { progressState.isCompleted(it) },
                            openFindings = bugBountyState.findings.filter {
                                it.status !in listOf("Closed", "Resolved", "Duplicate", "N/A", "Submitted", "Triaged")
                            },
                            recentWriteups = researchState.items.filter {
                                it.category == "Bug Bounty" || it.tags.contains("writeup")
                            }.take(8),
                            aiSecurityItems = researchState.items.filter {
                                it.category == "AI Security" ||
                                    it.title.contains("LLM", true) ||
                                    it.title.contains("prompt injection", true)
                            }.take(8)
                        ),
                        onBack = { briefOpen = false },
                        onReview = { briefOpen = false; tab = 2 },
                        onOpenTopic = { briefOpen = false; openTopicId = it },
                        onOpenBugBounty = { briefOpen = false; bbOpen = true },
                        onOpenResearch = { briefOpen = false; tab = 6 },
                        onOpenAiTutor = {
                            briefOpen = false
                            aiState.mode = ChatMode.AI_TUTOR
                            aiMode = 0
                            tab = 5
                        },
                        onOpenNotes = { briefOpen = false; tab = 4 }
                    )
                    tab == 1 -> LearningScreen(
                        progress = progressState,
                        onOpenTopic = { openTopicId = it },
                        onOpenGraph = { graphOpen = true },
                        customStore = customPathStore,
                        onCustomChanged = { }
                    )
                    tab == 2 -> ReviewScreen(review = reviewState, progress = progressState)
                    tab == 3 -> TasksScreen(
                        state = taskState, projectState = projectState,
                        progress = progressState,
                        onOpenTask = { id -> taskEditId = id },
                        onOpenProject = { id -> projectOpenId = id }
                    )
                    tab == 4 -> NotesScreen(
                        state = notesState,
                        onOpen = { id -> editingId = id },
                        onOpenTemplate = { tid -> noteTemplateId = tid; editingId = -1L },
                        onOpenGraph = { notesGraphOpen = true }
                    )
                    researchOpenId != 0L -> ResearchDetailScreen(
                        state = researchState, id = researchOpenId,
                        onBack = { researchOpenId = 0L },
                        onAddFinding = { title, vuln, rid ->
                            researchOpenId = 0L
                            bbPrefillTitle = title
                            bbPrefillVuln = vuln
                            bbLinkedResearchId = rid
                            bbFindingId = -1L
                        }
                    )
                    bbFindingId != 0L -> FindingEditScreen(
                        state = bugBountyState,
                        findingId = bbFindingId,
                        prefillTitle = bbPrefillTitle,
                        prefillVuln = bbPrefillVuln,
                        linkedResearchId = bbLinkedResearchId,
                        onBack = {
                            bbFindingId = 0L
                            bbPrefillTitle = ""
                            bbPrefillVuln = ""
                            bbLinkedResearchId = 0L
                            bbOpen = true
                        },
                        onAiReview = { title, md ->
                            aiState.mode = ChatMode.BB_COPILOT
                            aiState.pendingQuestion =
                                "Review this bug bounty report draft as a senior triage assistant. " +
                                "Check clarity of steps, whether impact is evidenced or only claimed, " +
                                "missing severity justification, and suggest improvements. " +
                                "Do NOT invent new vulnerabilities. Mark anything unverified as hypothesis.\n\n" +
                                "Title: $title\n\nReport:\n$md"
                            bbFindingId = 0L
                            bbPrefillTitle = ""
                            bbPrefillVuln = ""
                            bbLinkedResearchId = 0L
                            aiMode = 0
                            tab = 5
                        }
                    )
                    bbProgramId != 0L -> ProgramEditScreen(
                        state = bugBountyState,
                        programId = bbProgramId,
                        onBack = { bbProgramId = 0L; bbOpen = true }
                    )
                    bbChecklistOpen -> ChecklistScreen(
                        progressStore = checklistProgress,
                        onBack = { bbChecklistOpen = false; bbOpen = true }
                    )
                    bbAssetsProgramId != 0L -> AssetsScreen(
                        state = bugBountyState,
                        programId = bbAssetsProgramId,
                        onBack = { bbAssetsProgramId = 0L; bbOpen = true }
                    )
                    bbOpen -> BugBountyScreen(
                        state = bugBountyState,
                        onBack = { bbOpen = false },
                        onOpenFinding = { id -> bbOpen = false; bbFindingId = id },
                        onNewFinding = { bbOpen = false; bbFindingId = -1L },
                        onOpenProgram = { id -> bbOpen = false; bbProgramId = id },
                        onNewProgram = { bbOpen = false; bbProgramId = -1L },
                        onOpenChecklists = { bbOpen = false; bbChecklistOpen = true },
                        onOpenAssets = { pid -> bbOpen = false; bbAssetsProgramId = pid }
                    )
                    tab == 6 -> ResearchScreen(
                        state = researchState,
                        onOpenItem = { id -> researchOpenId = id }
                    )
                    else -> Column(Modifier.fillMaxSize()) {
                        Row(
                            Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilterChip(selected = aiMode == 0, onClick = { aiMode = 0 }, label = { Text(Lang.t("Chat", "محادثة")) })
                            FilterChip(selected = aiMode == 1, onClick = { aiMode = 1 }, label = { Text(Lang.t("Council", "مجلس")) })
                        }
                        Box(Modifier.weight(1f).fillMaxWidth()) {
                            if (aiMode == 0) {
                                AiChatScreen(state = aiState, archiveState = chatArchiveState, onOpenSettings = { aiSettingsOpen = true })
                            } else {
                                CouncilScreen(state = councilState, progress = progressState, onOpenSettings = { aiSettingsOpen = true })
                            }
                        }
                    }
                }
            }
        }
    }
}
