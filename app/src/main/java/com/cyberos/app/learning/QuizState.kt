package com.cyberos.app.learning

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.cyberos.app.ui.lang.Lang

class QuizState {

    var questions by mutableStateOf<List<Pair<String, QuizQuestion>>>(emptyList())
        private set
    var quizTitle by mutableStateOf("")
        private set
    var index by mutableStateOf(0)
        private set
    var selected by mutableStateOf(-1)
        private set
    var revealed by mutableStateOf(false)
        private set
    var correctCount by mutableStateOf(0)
        private set
    var finished by mutableStateOf(false)
        private set

    fun current(): Pair<String, QuizQuestion>? = questions.getOrNull(index)

    fun load(title: String, items: List<Pair<String, QuizQuestion>>) {
        quizTitle = title
        questions = items
        index = 0
        selected = -1
        revealed = false
        correctCount = 0
        finished = items.isEmpty()
    }

    fun startForTopic(topicId: String) {
        val t = CyberCurriculum.findTopic(topicId) ?: return
        load(t.title, t.quiz.map { t.title to shuffledForDisplay(it) })
    }

    fun startMixed(count: Int = 10) {
        val all = CyberCurriculum.paths
            .flatMap { p -> p.topics }
            .flatMap { t -> t.quiz.map { t.title to it } }
        val picked = all.shuffled().take(count)
        load(Lang.t("Mixed Quiz", "اختبار متنوع"), picked.map { it.first to shuffledForDisplay(it.second) })
    }

    fun select(option: Int) {
        if (revealed || finished) return
        val q = questions.getOrNull(index)?.second ?: return
        selected = option
        revealed = true
        if (option == q.correct) correctCount += 1
    }

    fun next() {
        if (!revealed || finished) return
        if (index + 1 >= questions.size) {
            finished = true
        } else {
            index += 1
            selected = -1
            revealed = false
        }
    }

    companion object {
        fun shuffledForDisplay(q: QuizQuestion): QuizQuestion {
            val order = q.options.indices.shuffled()
            return q.copy(
                options = order.map { q.options[it] },
                correct = order.indexOf(q.correct)
            )
        }
    }
}
