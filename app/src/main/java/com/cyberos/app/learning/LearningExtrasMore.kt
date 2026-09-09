package com.cyberos.app.learning

/** Additional topics from the long Arabic BB guide + roadmap deeper parts. */
object LearningExtrasMore {
    fun extraTopicsForElite(): List<TopicData> = listOf(
        TopicData(
            id = "ea-json-dup",
            title = "JSON duplicate keys",
            summary = "Two order_id fields in one JSON object — which value wins depends on the parser.",
            sections = listOf(
                TopicSection("Idea", "Send {\"order_id\":\"9941\",\"order_id\":\"9942\"}. Validation may see first; ORM may use last."),
                TopicSection("Practice", "Compare responses in an authorized lab with first-wins vs last-wins parsers."),
                TopicSection("Defense", "Reject duplicate keys; strict schema validation.")
            ),
            related = listOf("ea-hpp", "ea-type-spoof"),
            flashcards = listOf(
                "JSON dup keys risk?" to "Authz and data layer may disagree on the id."
            ),
            quiz = emptyList(),
            resources = listOf("PortSwigger Access Control" to "https://portswigger.net/web-security/access-control")
        ),
        TopicData(
            id = "ea-mass-assignment",
            title = "Mass assignment / extra fields",
            summary = "Client sends role=admin or is_admin=true in JSON the server binds blindly.",
            sections = listOf(
                TopicSection("Idea", "Extra body fields map into models without allowlists."),
                TopicSection("Test", "Add privileged fields only on labs you own; observe if role changes."),
                TopicSection("Defense", "Allowlist bindable fields; never trust client for role.")
            ),
            related = listOf("access-control", "ea-idor-mindset"),
            flashcards = listOf(
                "Mass assignment?" to "Untrusted fields overwrite sensitive model properties."
            )
        ),
        TopicData(
            id = "ea-method-override",
            title = "HTTP method override quirks",
            summary = "X-HTTP-Method-Override or _method may change verb after authz checks.",
            sections = listOf(
                TopicSection("Idea", "Authz middleware checks POST; override turns it into DELETE."),
                TopicSection("Test", "Authorized lab only — map override headers vs final handler."),
                TopicSection("Defense", "Disable overrides or authorize after final method resolution.")
            ),
            related = listOf("rm-web-fundamentals"),
            flashcards = listOf(
                "Override risk?" to "Method used for authz differs from method executed."
            )
        )
    )

    fun extraTopicsForRoadmap(): List<TopicData> = listOf(
        TopicData(
            id = "rm-xss-map",
            title = "Part 16b — XSS map",
            summary = "Reflected, stored, DOM — source → sink thinking.",
            sections = listOf(
                TopicSection("Classes", "Reflected / Stored / DOM."),
                TopicSection("Method", "Find source, find sink, prove execution, impact, fix."),
                TopicSection("Practice", "PortSwigger XSS labs after notes.")
            ),
            related = listOf("xss", "rm-bb-workflow"),
            flashcards = listOf(
                "DOM XSS?" to "Client-side source reaches dangerous sink."
            ),
            resources = listOf("PortSwigger XSS" to "https://portswigger.net/web-security/cross-site-scripting")
        ),
        TopicData(
            id = "rm-ssrf-map",
            title = "Part 16c — SSRF map",
            summary = "Server fetches URL attacker controls — cloud metadata risk.",
            sections = listOf(
                TopicSection("Idea", "Server-side request to attacker-chosen destination."),
                TopicSection("Impact", "Internal services, metadata endpoints, port scan."),
                TopicSection("Practice", "PortSwigger SSRF labs only.")
            ),
            related = listOf("ssrf"),
            flashcards = listOf(
                "SSRF core?" to "Server makes requests on your behalf to unintended targets."
            ),
            resources = listOf("PortSwigger SSRF" to "https://portswigger.net/web-security/ssrf")
        ),
        TopicData(
            id = "rm-csrf-map",
            title = "Part 16d — CSRF map",
            summary = "Browser sends cookies on cross-site requests without intentional user action.",
            sections = listOf(
                TopicSection("Idea", "State-changing requests with session cookie from other origin."),
                TopicSection("Defense", "Anti-CSRF tokens, SameSite, prefer non-cookie auth for APIs.")
            ),
            related = listOf("csrf"),
            flashcards = listOf(
                "CSRF needs?" to "Cookie session + state change + no unpredictable token."
            ),
            resources = listOf("PortSwigger CSRF" to "https://portswigger.net/web-security/csrf")
        ),
        TopicData(
            id = "rm-rules-year",
            title = "Part 24 — Rules against wasted years",
            summary = "No binge watching without output; weekly evidence of practice.",
            sections = listOf(
                TopicSection("Rules", "One topic → notes → lab or flashcards same day."),
                TopicSection("Evidence", "MOC note + completed topic + one writeup digest per week."),
                TopicSection("AI", "Tutor only; you verify everything.")
            ),
            related = listOf("rm-study-rules", "study-daily"),
            flashcards = listOf(
                "Anti-binge rule?" to "Same-day output or the topic is incomplete."
            )
        )
    )
}
