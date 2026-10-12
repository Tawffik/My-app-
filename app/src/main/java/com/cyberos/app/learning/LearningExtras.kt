package com.cyberos.app.learning

object LearningExtras {

    val paths: List<PathData> = listOf(
        webRoadmapPath().let { it.copy(topics = it.topics + LearningExtrasMore.extraTopicsForRoadmap()) },
        eliteAccessPath().let { it.copy(topics = it.topics + LearningExtrasMore.extraTopicsForElite()) },
        studyMethodPath(),
        agentStudyLabPath()
    )

    private fun sec(heading: String, body: String) = TopicSection(heading, body)

    private fun q(question: String, options: List<String>, correct: Int, explanation: String) =
        QuizQuestion(question, options, correct, explanation)

    private fun webRoadmapPath() = PathData(
        id = "web-bb-roadmap",
        title = "Web Security BB Roadmap",
        description = "Study roadmap: fundamentals to BB workflow.",
        topics = listOf(
            TopicData("rm-study-rules", "Study Rules & Mindset", "Apply early; understand full request path.",
                listOf(sec("Goal", "Browser to DB path understanding."), sec("Mix", "20% input, 70% apply, 10% test.")),
                listOf("rm-web-fundamentals"), listOf("Study mix?" to "20/70/10"),
                resources = listOf("PortSwigger" to "https://portswigger.net/web-security")),
            TopicData("rm-web-fundamentals", "Part 1 — Web Fundamentals", "HTTP, cookies, sessions, CORS.",
                listOf(sec("Core", "Methods, status, headers, AuthN vs AuthZ, SOP, CORS.")),
                listOf("http-fundamentals"), listOf("401 vs 403?" to "Unauth vs forbidden"),
                resources = listOf("MDN HTTP" to "https://developer.mozilla.org/en-US/docs/Web/HTTP")),
            TopicData("rm-html", "Part 2 — HTML security lens", "Forms, iframe, script, DOM surfaces.",
                listOf(sec("Focus", "User-controlled forms, attributes, scripts.")),
                listOf("xss"), listOf("Why forms?" to "Injection and auth entry points")),
            TopicData("rm-css", "Part 3 — CSS enough", "Read UI, do not over-invest.",
                listOf(sec("Scope", "Selectors, box model, flex basics.")),
                listOf("rm-html"), listOf("Why minimal CSS?" to "Time for HTTP and labs")),
            TopicData("rm-js", "Part 4–6 — JS DOM Async", "Required for XSS and client bugs.",
                listOf(sec("JS", "Types, functions, scope."), sec("DOM", "Sources and sinks.")),
                listOf("xss"), listOf("DOM XSS?" to "Source reaches sink unsafely")),
            TopicData("rm-apis", "Part 7–9 — APIs Backend", "Where IDOR and BAC live.",
                listOf(sec("Path", "Route, auth, object load, authz, DB, response.")),
                listOf("idor"), listOf("IDOR hide?" to "Object by ID without ownership check")),
            TopicData("rm-auth", "Part 10–11 — AuthN AuthZ", "Sessions and access control.",
                listOf(sec("AuthZ", "Owner checks; deny by default.")),
                listOf("authentication", "idor"), listOf("Horizontal?" to "Same role, other user data"),
                resources = listOf("PortSwigger AC" to "https://portswigger.net/web-security/access-control")),
            TopicData("rm-sql-project", "Part 12–13 — SQL Project", "Full path in a small lab app.",
                listOf(sec("SQL", "Do not concatenate untrusted input into queries.")),
                listOf("sql-injection"), listOf("SQLi root?" to "Input changes query structure")),
            TopicData("rm-browser-sec", "Part 14–15 — Browser Tools", "SOP CORS cookies; DevTools Burp.",
                listOf(sec("Tools", "Network tab and authorized Burp use.")),
                listOf("rm-bb-workflow"), listOf("Burp?" to "Inspect in-scope requests")),
            TopicData("rm-bb-workflow", "Part 16–18 — BB workflow", "Vuln map and recon flow.",
                listOf(sec("Map", "XSS SQLi SSRF CSRF IDOR BAC SSTI."), sec("Ethics", "Authorized only.")),
                listOf("recon"), listOf("Report?" to "Steps impact evidence")),
            TopicData("rm-js-sec-ai", "Part 17 & 19 — JS Sec AI", "Prototype risks; AI as tutor.",
                listOf(sec("AI", "Hypothesis until you verify.")),
                listOf("ai-for-bug-bounty"), listOf("AI output?" to "Hypothesis until verified")),
            TopicData("rm-cors-deep", "CORS & SOP", "Cross-origin read misconfig.",
                listOf(sec("Danger", "Reflect Origin with credentials.")),
                listOf("rm-browser-sec"), listOf("Bad CORS?" to "Reflect Origin + ACAC true"),
                resources = listOf("PortSwigger CORS" to "https://portswigger.net/web-security/cors")),
            TopicData("rm-file-upload", "File upload surface", "Extension MIME path processing.",
                listOf(sec("Checks", "Extension MIME magic path authz.")),
                listOf("xss"), listOf("Upload focus?" to "Validation gaps across layers")),
            TopicData("rm-ssrf-map", "SSRF mapping", "Server-side URL fetch features.",
                listOf(sec("Find", "Webhooks importers PDF thumbnails.")),
                listOf("ssrf"), listOf("SSRF?" to "Server requests attacker URL"),
                resources = listOf("PortSwigger SSRF" to "https://portswigger.net/web-security/ssrf")),
            TopicData("rm-notes-system", "Part 20–22 — Notes system", "Active recall and weekly builds.",
                listOf(sec("Habit", "Notes templates and weekly lab writeups.")),
                listOf("rm-study-rules"), listOf("Active recall?" to "Retrieve before reread"))
        )
    )

    private fun eliteAccessPath() = PathData(
        id = "elite-access-tricks",
        title = "Access Control & IDOR Tricks",
        description = "HPP, type spoof, mass assignment — labs only.",
        topics = listOf(
            TopicData("ea-idor-mindset", "IDOR mindset", "Server must prove ownership.",
                listOf(sec("Idea", "Access other user object with your session."), sec("Defense", "Server ownership checks.")),
                listOf("idor"), listOf("IDOR core?" to "Object by ID without authz"),
                quiz = listOf(q("Best defense?", listOf("Hide button", "Server ownership check", "Long JWT", "HTTPS"), 1, "Server authz.")),
                resources = listOf("PortSwigger IDOR" to "https://portswigger.net/web-security/access-control/idor")),
            TopicData("ea-encoding-null", "Encoding & null-byte", "Parser differentials.",
                listOf(sec("Idea", "Encoding and legacy truncation gaps.")),
                listOf("ea-idor-mindset"), listOf("Double encoding?" to "Bypass single decode filters")),
            TopicData("ea-hpp", "HTTP Parameter Pollution", "Duplicate params split validation vs use.",
                listOf(sec("Idea", "order_id twice; first vs last."), sec("JSON", "Duplicate keys parser-defined.")),
                listOf("ea-type-spoof"), listOf("HPP risk?" to "Validation and logic disagree")),
            TopicData("ea-type-spoof", "Type spoofing", "Wrong JSON types for ids.",
                listOf(sec("Idea", "Array or bool where int expected.")),
                listOf("ea-hpp"), listOf("Goal?" to "Slip past type checks")),
            TopicData("ea-path-query", "Path vs Query vs Body", "Which channel is authoritative?",
                listOf(sec("Method", "Change one channel; record matrix.")),
                listOf("ea-idor-mindset"), listOf("Multi-channel?" to "Inconsistent authz source")),
            TopicData("ea-mass-assignment", "Mass assignment", "Hidden fields change role or price.",
                listOf(sec("Defense", "Allow-list writable fields.")),
                listOf("access-control"), listOf("Mass assignment?" to "Unexpected fields bind privileged props")),
            TopicData("ea-method-override", "HTTP method override", "Override headers remap verbs.",
                listOf(sec("Risk", "Filters skip effective method.")),
                listOf("csrf"), listOf("Why matters?" to "Security hooks may miss effective verb")),
            TopicData("ea-idor-variants", "IDOR variants checklist", "Numeric UUID file export share links.",
                listOf(sec("Blind", "State change without data read.")),
                listOf("ea-idor-mindset"), listOf("Blind IDOR?" to "Effect without data in response")),
            TopicData("ea-apply-loop", "Apply loop", "Note → lab → report.",
                listOf(sec("Rule", "Hypothesis until reproduce and impact.")),
                listOf("rm-notes-system"), listOf("Confirmed?" to "After reproduce + impact"))
        )
    )

    private fun studyMethodPath() = PathData(
        id = "study-os",
        title = "Study OS (Notes & Apply)",
        description = "Daily ritual with notes and cards.",
        topics = listOf(
            TopicData("study-daily", "Daily ritual", "Brief → topic → note → cards.",
                listOf(sec("Order", "Brief, one topic, note, due cards.")),
                listOf("rm-study-rules"), listOf("First?" to "Brief then one topic")),
            TopicData("study-notes-apply", "Notes that apply", "Every note yields test or card.",
                listOf(sec("Template", "Observation Hypothesis Validation Takeaway.")),
                listOf("rm-notes-system"), listOf("Incomplete?" to "No card and no test idea")),
            TopicData("study-sources", "Open sources", "Resources and research while studying.",
                listOf(sec("Rule", "Rewrite in your words.")),
                listOf("rm-web-fundamentals"), listOf("Active reading?" to "Rewrite immediately"),
                resources = listOf("BugBountyDaily" to "https://bugbountydaily.com/"))
        )
    )

    /** Six-week AI & Agent Security Study Lab — mobile notebook + resources. Users can still add more via "Add my section". */
    private fun agentStudyLabPath() = PathData(
        id = "ai-agent-study-lab",
        title = "AI & Agent Study Lab",
        description = "LLM apps → injection → RAG → tools/agents → MCP → evidence. Authorized labs only. Extensible via custom sections.",
        topics = listOf(
            TopicData(
                "aas-w1-architecture", "Week 1 — App architecture & threat model",
                "Model vs application security; trust boundaries; diagnostic.",
                listOf(
                    sec("Goal", "Draw user → API → LLM → tools with trust boundaries."),
                    sec("Rule", "Prompt wording alone is not a security boundary."),
                    sec("Diagnostic", "Use in-app progress: comfortable with HTTP/authz before deep labs.")
                ),
                listOf("llm-basics", "aas-w2-injection"),
                listOf("Trust boundary?" to "Where trust level changes between components."),
                listOf(q("Primary failure mode of LLM apps?", listOf("Only GPU heat", "Mixing instructions and data / over-trusted tools", "Too much RAM", "Dark mode"), 1, "Data treated as instructions or excessive agency.")),
                resources = listOf(
                    "Study plan (repo)" to "https://github.com/Tawffik/ai-agent-security-study/blob/main/STUDY_PLAN.md",
                    "OWASP LLM Top 10 2026" to "https://genai.owasp.org/resource/owasp-genai-llm-top-10-2026/"
                )
            ),
            TopicData(
                "aas-w2-injection", "Week 2 — Direct & indirect injection",
                "Impact validation on authorized labs.",
                listOf(
                    sec("Direct", "Attacker types into the chat box."),
                    sec("Indirect", "Instructions in retrieved pages, tickets, or docs."),
                    sec("Impact", "Need crossed trust boundary + real effect — not jailbreak text alone.")
                ),
                listOf("prompt-injection", "aas-w3-rag"),
                listOf("Direct vs indirect?" to "Chat vs retrieved/third-party content."),
                listOf(q("Indirect injection often rides on:", listOf("BGP only", "RAG / email / web content", "Battery", "Font files"), 1, "Untrusted content enters context.")),
                resources = listOf(
                    "PortSwigger LLM path" to "https://portswigger.net/web-security/learning-paths/llm-attacks",
                    "Excessive agency lab" to "https://portswigger.net/web-security/llm-attacks/lab-exploiting-llm-apis-with-excessive-agency",
                    "Indirect injection lab" to "https://portswigger.net/web-security/llm-attacks/lab-indirect-prompt-injection",
                    "IPI paper" to "https://arxiv.org/abs/2302.12173"
                )
            ),
            TopicData(
                "aas-w3-rag", "Week 3 — RAG & isolation",
                "Retrieval pipeline, tenant isolation, poisoned documents.",
                listOf(
                    sec("Pipeline", "Ingest → chunk → embed → retrieve → generate."),
                    sec("Isolation", "Cross-user retrieval is a confidentiality failure."),
                    sec("Poison", "Hostile documents are data that can act like instructions.")
                ),
                listOf("rag-security", "aas-w4-tools"),
                listOf("RAG chunks are?" to "Untrusted data until authorized and validated."),
                listOf(q("Cross-tenant retrieval primarily hits:", listOf("Color contrast", "Confidentiality / authorization", "Bluetooth", "DNSSEC"), 1, "Wrong user data in context.")),
                resources = listOf(
                    "OWASP LLM Top 10 2026" to "https://genai.owasp.org/resource/owasp-genai-llm-top-10-2026/"
                )
            ),
            TopicData(
                "aas-w4-tools", "Week 4 — Tools, agency, chains",
                "Least privilege tools; confused deputy; multi-step impact.",
                listOf(
                    sec("Agency", "Grant minimum tools and arguments for the task."),
                    sec("Deputy", "Privileged service acting on untrusted instructions."),
                    sec("Chains", "LLM step → classic vuln (SQLi, SSRF, command injection) via tools.")
                ),
                listOf("agent-security", "aas-w5-mcp"),
                listOf("Least agency?" to "Minimum autonomy and tool power required."),
                listOf(q("High-impact tool calls should require:", listOf("More emojis", "Server-side authz and often human approval", "Only client JS checks", "Larger font"), 1, "Server must enforce authorization.")),
                resources = listOf(
                    "LLM APIs vuln lab" to "https://portswigger.net/web-security/llm-attacks/lab-exploiting-vulnerabilities-in-llm-apis",
                    "Agentic Top 10 (OWASP)" to "https://genai.owasp.org/resource/owasp-top-10-for-agentic-applications-for-2026/"
                )
            ),
            TopicData(
                "aas-w5-mcp", "Week 5 — MCP, memory, output handling",
                "Tool poisoning, hidden context, unsafe rendering.",
                listOf(
                    sec("MCP", "Tool descriptions/responses can inject instructions."),
                    sec("Memory", "Poisoned long-term context steers later sessions."),
                    sec("Output", "Model HTML/JS in a web UI can become XSS.")
                ),
                listOf("mcp-security", "aas-w6-capstone"),
                listOf("Tool poisoning?" to "Hostile text in tool metadata or responses."),
                listOf(q("Raw model HTML in a page risks:", listOf("Only slow CSS", "XSS / content injection", "Stronger TLS", "Airplane mode"), 1, "Encode and validate outputs.")),
                resources = listOf(
                    "MCP security practices" to "https://modelcontextprotocol.io/docs/2026-07-28/tutorials/security/security-best-practices",
                    "OWASP MCP Tool Poisoning" to "https://owasp.org/www-community/attacks/MCP_Tool_Poisoning",
                    "Output handling lab" to "https://portswigger.net/web-security/llm-attacks/lab-exploiting-insecure-output-handling-in-llms",
                    "Agent Control Standard" to "https://genai.owasp.org/resource/agent-control-standard-acs/"
                )
            ),
            TopicData(
                "aas-w6-capstone", "Week 6 — Research skills & capstone",
                "Read papers critically; optional garak; write evidence-based report.",
                listOf(
                    sec("Paper", "Question, threat model, method, limits — not marketing."),
                    sec("Capstone", "Local mock agent in study repo: safe vs unsafe modes."),
                    sec("Report", "Attack surface, boundary, chain, proof, impact, remediation.")
                ),
                listOf("ai-lab-method", "ai-for-bug-bounty"),
                listOf("AI claim status?" to "Hypothesis until you reproduce."),
                listOf(q("Before using AI text in a bounty report:", listOf("Submit immediately", "Manually verify impact and stay in scope", "Only change fonts", "Disable logging"), 1, "Human verification is mandatory.")),
                resources = listOf(
                    "Capstone + curriculum repo" to "https://github.com/Tawffik/ai-agent-security-study",
                    "garak" to "https://github.com/NVIDIA/garak",
                    "Glossary" to "https://github.com/Tawffik/ai-agent-security-study/blob/main/GLOSSARY.md"
                )
            ),
            TopicData(
                "aas-add-modules", "How to add any new module",
                "Use Add my section on Learn, or extend this path in code.",
                listOf(
                    sec("In the app", "Learn → Add my section → title/description → add topics with optional Source URL (opens in Custom Tabs)."),
                    sec("In code", "Append PathData to LearningExtras or topics under CyberCurriculum; keep unique topic ids."),
                    sec("Notebook", "Mirror progress in Notes vault or the study repo PROGRESS.md."),
                    sec("Ethics", "Only authorized labs and owned systems.")
                ),
                listOf("aas-w1-architecture", "study-sources"),
                listOf("Custom path store?" to "Persists user paths in app private JSON."),
                listOf(q("Custom topic Source URL opens in:", listOf("Silent root shell", "Custom Tabs / browser", "SMS", "GPU"), 1, "resources are opened as study links.")),
                resources = listOf(
                    "CyberOS repo" to "https://github.com/Tawffik/My-app-",
                    "Study lab repo" to "https://github.com/Tawffik/ai-agent-security-study"
                )
            )
        )
    )
}
