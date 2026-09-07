package com.cyberos.ai

object ResearchDigest {

    private val NL = Char(10).toString()

    fun buildDaily(items: List<Pair<String, AnalysisResult>>): String {
        if (items.isEmpty()) {
            return "No research today. Save something from the browser share sheet."
        }
        val sb = StringBuilder()
        sb.append("CYBEROS DAILY DIGEST").append(NL)
        sb.append("====================").append(NL)
        var index = 1
        for (entry in items) {
            val a = entry.second
            sb.append(index).append(". ").append(entry.first).append(NL)
            sb.append("   type: ").append(a.contentType.name)
              .append(" | confidence: ").append(percent(a.confidence)).append(NL)
            if (a.techniques.isNotEmpty()) {
                sb.append("   techniques: ").append(a.techniques.joinToString(", ")).append(NL)
            }
            index++
        }
        return sb.toString()
    }

    private fun percent(v: Double): String = ((v * 100).toInt()).toString() + "%"
}
