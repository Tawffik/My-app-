package com.cyberos.app.learning

/**
 * Extra curriculum paths derived from the user's study materials:
 * - Web Security / Bug Bounty Study Roadmap
 * - Complete Bug Bounty / Access-Control technique notes (Tawfik guide style)
 *
 * Existing CyberCurriculum.paths are NOT replaced — these are additive.
 */
object LearningExtras {

    val paths: List<PathData> = listOf(
        webRoadmapPath(),
        eliteAccessPath(),
        studyMethodPath()
    )

    private fun sec(
        heading: String,
        body: String
    ) = TopicSection(heading, body)

    private fun q(
        question: String,
        options: List<String>,
        correct: Int,
        explanation: String
    ) = QuizQuestion(question, options, correct, explanation)

    private fun webRoadmapPath() = PathData(
        id = "web-bb-roadmap",
        title = "Web Security BB Roadmap",
        description = "Study roadmap: fundamentals → JS/DOM → APIs → authz → labs → BB workflow. Learn → notes → apply.",
        topics = listOf(
            TopicData(
                id = "rm-study-rules",
                title = "Study Rules & Mindset",
                summary = "You are not becoming a frontend/backend developer only — you need to understand the app from the inside.",
                sections = listOf(
                    sec("Goal", "Understand Browser → HTML/DOM/JS → HTTP → API → Backend → DB → Response so you can find and prove issues safely."),
                    sec("Study mix", "20% watch/read · 70% write + apply · 10% self-test. Never binge a full course then say “I’ll apply later”."),
                    sec("AI usage", "Use AI to explain, review, and give hints — you write the notes and the tests."),
                    sec("Practice loop", "Topic → short notes → one small project/lab → explain without looking → next topic.")
                ),
                related = listOf("rm-web-fundamentals", "rm-notes-system"),
                flashcards = listOf(
                    "Ideal study mix?" to "20% input / 70% output / 10% test.",
                    "Why understand the full request path?" to "Vulns live between layers, not in isolated slides."
                ),
                quiz = listOf(
                    q("Main goal of this roadmap?", listOf("Become UI designer only", "Understand web apps end-to-end for secure testing", "Memorize CSS only", "Skip HTTP"), 1, "Inside-out understanding enables authorized testing.")
                ),
                resources = listOf(
                    "MDN Web Docs" to "https://developer.mozilla.org/",
                    "PortSwigger Academy" to "https://portswigger.net/web-security"
                )
            ),
            TopicData(
                id = "rm-web-fundamentals",
                title = "Part 1 — Web Fundamentals",
                summary = "Browser, URL, DNS, HTTP/S, methods, status codes, headers, cookies, sessions, JSON, CORS.",
                sections = listOf(
                    sec("Remember", "Client/Server · URL · DNS · HTTP vs HTTPS · Request/Response · Methods (GET/POST/PUT/PATCH/DELETE/OPTIONS) · Status codes · Headers · Body · Query/Path params · Cookies · Sessions · JSON · Content-Type · AuthN vs AuthZ · SOP · CORS · Client vs Server."),
                    sec("Hands-on", "DevTools → Network → capture a real login POST with JSON body. Ask: who sends it? what headers? where is the session?"),
                    sec("Security connection", "Most bugs are wrong trust of client input, broken authz, or misread status/headers.")
                ),
                related = listOf("http-fundamentals", "rm-html"),
                flashcards = listOf(
                    "AuthN vs AuthZ?" to "Who you are vs what you’re allowed to do.",
                    "Why capture real requests?" to "Theory sticks only when mapped to live traffic."
                ),
                quiz = listOf(
                    q("401 means?", listOf("Not authenticated", "Authenticated but forbidden", "Not found", "Server error"), 0, "401 = unauthenticated; 403 = forbidden.")
                ),
                resources = listOf(
                    "MDN HTTP" to "https://developer.mozilla.org/en-US/docs/Web/HTTP",
                    "PortSwigger HTTP basics" to "https://portswigger.net/web-security"
                )
            ),
            TopicData(
                id = "rm-html",
                title = "Part 2 — HTML (security lens)",
                summary = "Structure, forms, inputs, iframe, script, DOM concept — focus on user-controlled surfaces.",
                sections = listOf(
                    sec("Focus", "Forms, attributes, iframe, script tags, semantic structure, form submission, DOM concept."),
                    sec("Practice", "Build Login + Register + Profile markup yourself (no copy-paste whole pages)."),
                    sec("Security", "User-controlled input lands in forms, URLs, attributes, iframes, scripts, and the DOM.")
                ),
                related = listOf("rm-js", "xss"),
                flashcards = listOf(
                    "Why care about forms?" to "Primary injection and auth entry points.",
                    "iframe risk?" to "Embedding + clickjacking + origin isolation issues."
                ),
                resources = listOf("MDN HTML" to "https://developer.mozilla.org/en-US/docs/Web/HTML")
            ),
            TopicData(
                id = "rm-css",
                title = "Part 3 — CSS (enough for reading UI)",
                summary = "Selectors, box model, display, position — enough to read UI, not become a designer.",
                sections = listOf(
                    sec("Scope", "Selectors, box model, display, position, flexbox basics, responsive basics."),
                    sec("Rule", "Goal is web understanding, not graphic design time sinks.")
                ),
                related = listOf("rm-html"),
                flashcards = listOf("Why minimal CSS?" to "Free time for HTTP, authz, and labs.")
            ),
            TopicData(
                id = "rm-js",
                title = "Part 4–6 — JavaScript, DOM, Async",
                summary = "JS fundamentals, DOM/events, async — required for XSS and client logic bugs.",
                sections = listOf(
                    sec("JS core", "Variables, types, strings, numbers, arrays, objects, conditions, loops, functions, scope, returns."),
                    sec("DOM + events", "How scripts read/write the page; event handlers; sinks/sources mindset for XSS."),
                    sec("Async", "Callbacks/promises/fetch timing — race conditions and delayed trust issues.")
                ),
                related = listOf("xss", "rm-apis"),
                flashcards = listOf(
                    "DOM XSS idea?" to "Source reaches a dangerous sink without safe encoding.",
                    "Why async matters?" to "Checks can pass on stale state."
                ),
                resources = listOf("MDN JS Guide" to "https://developer.mozilla.org/en-US/docs/Web/JavaScript/Guide")
            ),
            TopicData(
                id = "rm-apis",
                title = "Part 7–9 — Fetch, APIs, Backend basics",
                summary = "JSON APIs, request lifecycle, Express-style routes — where IDOR and BAC live.",
                sections = listOf(
                    sec("APIs", "fetch + JSON; REST-style routes; trust boundaries between client and server."),
                    sec("Backend lens", "Request hits route → auth middleware? → object load → authz check? → DB → response."),
                    sec("Practice", "Trace one API: who can call it, which ID is trusted, what is returned.")
                ),
                related = listOf("idor", "access-control"),
                flashcards = listOf(
                    "Where do IDORs hide?" to "Object access by ID without ownership checks.",
                    "Client authz UI?" to "Never enough — server must enforce."
                )
            ),
            TopicData(
                id = "rm-auth",
                title = "Part 10–11 — Authentication & Authorization",
                summary = "Sessions/cookies, login flows, vertical/horizontal access control.",
                sections = listOf(
                    sec("Authentication", "Prove identity; cookie/session/JWT pitfalls; lockout and reset flows."),
                    sec("Authorization", "Object owner checks; role checks; deny by default."),
                    sec("Labs", "PortSwigger Authentication + Access Control labs after notes.")
                ),
                related = listOf("authentication", "access-control", "idor"),
                flashcards = listOf(
                    "Horizontal vs vertical?" to "Same-role other user vs privilege escalation.",
                    "Best default?" to "Deny unless explicit allow."
                ),
                resources = listOf(
                    "PortSwigger Auth" to "https://portswigger.net/web-security/authentication",
                    "PortSwigger Access Control" to "https://portswigger.net/web-security/access-control"
                )
            ),
            TopicData(
                id = "rm-sql-project",
                title = "Part 12–13 — SQL + Mini Web Project",
                summary = "DB queries and a small full-stack project to see the whole path.",
                sections = listOf(
                    sec("SQL", "How user input reaches queries; parameterized queries mindset."),
                    sec("Project", "Tiny app: register/login/posts — then attack your own authz mistakes in a local lab only.")
                ),
                related = listOf("sql-injection", "rm-browser-sec"),
                flashcards = listOf("SQLi root cause?" to "Untrusted input concatenated into query structure.")
            ),
            TopicData(
                id = "rm-browser-sec",
                title = "Part 14–15 — Browser Security & Burp/DevTools",
                summary = "SOP/CORS/cookies flags; daily tooling: DevTools + Burp.",
                sections = listOf(
                    sec("Browser", "SOP, cookies flags, mixed content, storage."),
                    sec("Tooling", "DevTools Network/Application; Burp intercept/repeater for authorized testing.")
                ),
                related = listOf("rm-bb-workflow"),
                flashcards = listOf("Burp role?" to "Inspect and safely replay requests you are allowed to test."),
                resources = listOf("PortSwigger Burp" to "https://portswigger.net/burp")
            ),
            TopicData(
                id = "rm-bb-workflow",
                title = "Part 16–18 — Web Sec topics & BB workflow",
                summary = "Map common vulns + recon workflow: assets → live hosts → endpoints → hypotheses.",
                sections = listOf(
                    sec("Topics map", "XSS, SQLi, SSRF, CSRF, IDOR, BAC, SSTI — each: what / where / how to prove / how to prevent."),
                    sec("Recon flow", "Asset discovery → subdomains → live hosts → endpoints → prioritized tests."),
                    sec("Ethics", "Only in-scope, authorized targets.")
                ),
                related = listOf("recon", "xss", "ssrf"),
                flashcards = listOf(
                    "Recon without testing?" to "Incomplete — but test only in scope.",
                    "Report quality?" to "Clear steps + impact + evidence."
                )
            ),
            TopicData(
                id = "rm-js-sec-ai",
                title = "Part 17 & 19 — JS Security + AI workflow",
                summary = "Prototype pollution basics; disciplined AI-assisted learning.",
                sections = listOf(
                    sec("JS security", "Prototype chain risks; dangerous merges; client trust issues."),
                    sec("AI workflow", "Explain → quiz me → review my note — never “confirm vuln” without reproduce.")
                ),
                related = listOf("ai-for-bug-bounty", "rm-notes-system"),
                flashcards = listOf("AI output is?" to "Hypothesis until you verify.")
            ),
            TopicData(
                id = "rm-notes-system",
                title = "Part 20–22 — Notes, explain-without-looking, weekly projects",
                summary = "Professional study system: notes template, active recall, weekly builds.",
                sections = listOf(
                    sec("Note template", "Topic · What · Why · How to test · Questions · Mistakes."),
                    sec("Explain without looking", "If you can’t teach it, you don’t own it yet."),
                    sec("Weekly project", "One small build or lab writeup every week beats passive watching.")
                ),
                related = listOf("rm-study-rules"),
                flashcards = listOf(
                    "Active recall?" to "Retrieve from memory before re-reading.",
                    "Weekly project purpose?" to "Force application and evidence."
                )
            )
        )
    )

    private fun eliteAccessPath() = PathData(
        id = "elite-access-tricks",
        title = "Access Control & IDOR Tricks",
        description = "Advanced bypass patterns: encoding, null bytes, parameter pollution, type spoofing, JSON quirks — study then practice only on authorized labs.",
        topics = listOf(
            TopicData(
                id = "ea-idor-mindset",
                title = "IDOR mindset",
                summary = "IDs in path/query/body are untrusted. Server must prove ownership.",
                sections = listOf(
                    sec("Idea", "Change object identifiers belonging to another user while staying authenticated as yourself."),
                    sec("Where", "Path `/api/orders/9941`, query, JSON body, headers, WebSocket messages."),
                    sec("Proof", "Authorized lab only: show access to another user’s object with your session."),
                    sec("Defense", "Server-side ownership checks; opaque IDs; deny by default.")
                ),
                related = listOf("idor", "access-control"),
                flashcards = listOf(
                    "IDOR core?" to "Access object by ID without authorization check.",
                    "UI hide enough?" to "No — call the API directly in labs."
                ),
                quiz = listOf(
                    q("Best IDOR defense?", listOf("Hide button only", "Server ownership check", "Longer JWT", "HTTPS only"), 1, "Authorization must be server-side.")
                ),
                resources = listOf(
                    "PortSwigger Access Control" to "https://portswigger.net/web-security/access-control",
                    "PortSwigger IDOR" to "https://portswigger.net/web-security/access-control/idor"
                )
            ),
            TopicData(
                id = "ea-encoding-null",
                title = "Encoding & Null-byte tricks",
                summary = "Servers/parsers may stop or mis-handle special sequences — classic filter gaps.",
                sections = listOf(
                    sec("Null byte idea", "Some legacy stacks treated `%00` / `\\0` as end-of-string, truncating path or validation."),
                    sec("Encodings", "URL encoding, double encoding, Unicode weirdness can bypass naive filters."),
                    sec("Practice rule", "Only on labs; document exact payload and which layer decoded it."),
                    sec("Modern note", "Many frameworks fixed classic null-byte path cuts — still useful as a thinking model for parser differentials.")
                ),
                related = listOf("ea-idor-mindset", "ea-hpp"),
                flashcards = listOf(
                    "Null byte historically did what?" to "Truncated strings in some C-like or legacy validators.",
                    "Double encoding?" to "Bypass filters that decode once."
                ),
                resources = listOf(
                    "OWASP Testing" to "https://owasp.org/www-project-web-security-testing-guide/"
                )
            ),
            TopicData(
                id = "ea-hpp",
                title = "HTTP Parameter Pollution",
                summary = "Duplicate parameters: which value does authz see vs which value does the query use?",
                sections = listOf(
                    sec("Idea", "`order_id=9941&order_id=9942` — frameworks pick first, last, or array."),
                    sec("Impact", "Access-control layer may validate 9941 while DB uses 9942."),
                    sec("JSON twin", "Duplicate keys in JSON — parsers differ on first vs last."),
                    sec("Test method", "In authorized lab, pollute id fields and compare responses.")
                ),
                related = listOf("ea-type-spoof", "access-control"),
                flashcards = listOf(
                    "HPP risk?" to "Split brain between validation and use.",
                    "JSON duplicate keys?" to "Parser-defined winner; security checks can disagree."
                ),
                quiz = listOf(
                    q("HPP is dangerous when?", listOf("Always harmless", "Validation reads one value, logic another", "Only with FTP", "Only offline"), 1, "Inconsistent parameter selection.")
                )
            ),
            TopicData(
                id = "ea-type-spoof",
                title = "Type spoofing",
                summary = "Send string/array/boolean where server expected int — break validation layers.",
                sections = listOf(
                    sec("Idea", "Server expects integer id; send `\"9942\"`, `[\"9942\"]`, or `true` and observe casting."),
                    sec("Why it works", "Validation layer and ORM/query layer cast differently."),
                    sec("Practice", "Map each type’s response in a safe lab; note error vs data leak.")
                ),
                related = listOf("ea-hpp", "ea-idor-mindset"),
                flashcards = listOf(
                    "Type spoof goal?" to "Slip past type checks into weak authz paths.",
                    "Array id risk?" to "Some frameworks iterate or take index 0 unexpectedly."
                )
            ),
            TopicData(
                id = "ea-path-query",
                title = "Path vs Query vs Body authority",
                summary = "Which location does the server trust for the object id?",
                sections = listOf(
                    sec("Idea", "Path says 9941, body says 9942 — which wins?"),
                    sec("Auth confusion", "Middleware may authorize path id; handler may load body id."),
                    sec("Method", "Change one channel at a time; record behavior matrix.")
                ),
                related = listOf("ea-idor-mindset", "rm-apis"),
                flashcards = listOf(
                    "Why multi-channel ids?" to "Inconsistent source of truth for authorization."
                )
            ),
            TopicData(
                id = "ea-apply-loop",
                title = "Apply loop: note → lab → report",
                summary = "Turn each trick into a structured note and an authorized test checklist.",
                sections = listOf(
                    sec("Note", "Observation / Hypothesis / How to test / Expected safe behavior / Evidence fields."),
                    sec("Lab", "PortSwigger or local app only."),
                    sec("Report", "Steps, impacted object, account used, fix suggestion.")
                ),
                related = listOf("rm-notes-system"),
                flashcards = listOf("Hypothesis vs confirmed?" to "Confirmed only after reproduce + impact.")
            )
        )
    )

    private fun studyMethodPath() = PathData(
        id = "study-os",
        title = "Study OS (Notes & Apply)",
        description = "How to use CyberOS daily: notes templates, flashcards, explain-out-loud, open sources in browser.",
        topics = listOf(
            TopicData(
                id = "study-daily",
                title = "Daily learning ritual",
                summary = "Brief → one topic → notes → cards → optional lab.",
                sections = listOf(
                    sec("Order", "1) Daily Brief 2) One curriculum topic 3) Security/Writeup note 4) Due flashcards 5) Optional authorized practice."),
                    sec("Time boxing", "25–40 minutes focused beats 3 hours passive video.")
                ),
                related = listOf("rm-study-rules"),
                flashcards = listOf("First open?" to "Daily Brief then one topic.")
            ),
            TopicData(
                id = "study-notes-apply",
                title = "Notes that force application",
                summary = "Every note should produce a test idea or a flashcard.",
                sections = listOf(
                    sec("Template", "Observation · Hypothesis · Validation plan · Takeaway."),
                    sec("Output", "If no card and no test idea, the note is incomplete.")
                ),
                related = listOf("rm-notes-system"),
                flashcards = listOf("Incomplete note?" to "No card and no test idea.")
            ),
            TopicData(
                id = "study-sources",
                title = "Open sources while studying",
                summary = "Use resource links on topics + Research writeups + PortSwigger.",
                sections = listOf(
                    sec("In-app", "Topic → Resources opens Chrome Custom Tabs."),
                    sec("Research", "Writeups / Tips categories for continuous input."),
                    sec("Rule", "Read actively: one paragraph → one sentence in your words.")
                ),
                related = listOf("rm-web-fundamentals"),
                flashcards = listOf("Active reading?" to "Rewrite in your words immediately."),
                resources = listOf(
                    "PortSwigger Academy" to "https://portswigger.net/web-security",
                    "Bug Bounty Daily" to "https://bugbountydaily.com/"
                )
            )
        )
    )
}
