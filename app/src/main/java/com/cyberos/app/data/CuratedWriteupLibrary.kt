package com.cyberos.app.data

/**
 * High-signal writeups & tips curated for reading + practice.
 * Sources include BugBountyDaily-style collections, classic Google VRP posts,
 * PortSwigger research, AI/prompt-injection writeups, and methodology tips.
 *
 * Seeded once into ResearchItemStore so the app is useful offline before the first sync.
 */
object CuratedWriteupLibrary {

    data class Entry(
        val title: String,
        val link: String,
        val category: String,
        val tags: List<String>,
        val summary: String
    )

    val WRITEUPS: List<Entry> = listOf(
        // AI / prompt injection (modern surface)
        Entry(
            "Prompt injection to RCE in AI agents (Trail of Bits)",
            "https://blog.trailofbits.com/2025/10/22/prompt-injection-to-rce-in-ai-agents/",
            "AI Security",
            listOf("writeup", "ai", "prompt-injection", "rce"),
            "Trail of Bits research: how prompt injection against agents can reach RCE. Read impact path, then note one defensive control. Link opens full article."
        ),
        Entry(
            "EchoLeak — AIM Labs",
            "https://www.aim.security/aim-labs/aim-labs-echoleak-blogpost",
            "AI Security",
            listOf("writeup", "ai", "prompt-injection"),
            "AIM Labs EchoLeak: LLM-assisted data leakage patterns. Study the chain, capture takeaways in a study note. Full writeup via Open link."
        ),
        Entry(
            "Google Bard data exfiltration (Embrace The Red)",
            "https://embracethered.com/blog/posts/2023/google-bard-data-exfiltration/",
            "AI Security",
            listOf("writeup", "ai", "data-exfiltration", "prompt-injection"),
            "Embrace The Red: Bard/markdown exfiltration patterns. Map to CSP and output handling. Open the link for PoC discussion."
        ),
        Entry(
            "Google AI Studio mass data exfil",
            "https://embracethered.com/blog/posts/2024/google-aistudio-mass-data-exfil/",
            "AI Security",
            listOf("writeup", "ai", "prompt-injection"),
            "Curated — AI product data exfiltration patterns."
        ),
        Entry(
            "GitHub Copilot RCE via prompt injection",
            "https://embracethered.com/blog/posts/2025/github-copilot-remote-code-execution-via-prompt-injection/",
            "AI Security",
            listOf("writeup", "ai", "rce", "prompt-injection"),
            "Curated — coding-agent prompt injection impact."
        ),
        Entry(
            "From MCP to shell (Veria Labs)",
            "https://verialabs.com/blog/from-mcp-to-shell/",
            "AI Security",
            listOf("writeup", "ai", "mcp", "rce"),
            "Curated — MCP tool-chain abuse to shell."
        ),
        Entry(
            "Mitigating prompt injection attacks (Google Security Blog)",
            "https://security.googleblog.com/2025/06/mitigating-prompt-injection-attacks.html",
            "AI Security",
            listOf("writeup", "ai", "defense"),
            "Curated — defensive framing from Google."
        ),
        // Classic high-value BB writeups
        Entry(
            "Accidental \$70k Google Pixel lock screen bypass",
            "https://bugs.xdavidhu.me/google/2022/11/10/accidental-70k-google-pixel-lock-screen-bypass/",
            "Bug Bounty",
            listOf("writeup", "authentication", "race-condition", "client-side"),
            "Curated — Google VRP, client-side auth race."
        ),
        Entry(
            "Opening the unopenable — Google Cloud SSRF",
            "https://bugs.xdavidhu.me/google/2021/12/31/opening-the-unopenable-story-of-a-google-cloud-ssrf/",
            "Bug Bounty",
            listOf("writeup", "ssrf", "cloud", "rce"),
            "Curated — Google Cloud SSRF chain."
        ),
        Entry(
            "I built a TV that plays all of your private YouTube videos",
            "https://bugs.xdavidhu.me/google/2021/04/05/i-built-a-tv-that-plays-all-of-your-private-youtube-videos/",
            "Bug Bounty",
            listOf("writeup", "idor", "csrf", "authentication"),
            "Curated — privacy IDOR/CSRF class story."
        ),
        Entry(
            "Embedded YouTube player told me what you were watching",
            "https://bugs.xdavidhu.me/google/2021/01/18/the-embedded-youtube-player-told-me-what-you-were-watching-and-more/",
            "Bug Bounty",
            listOf("writeup", "idor", "client-side"),
            "Curated — client-side privacy leak."
        ),
        Entry(
            "Stealing private videos one frame at a time",
            "https://bugs.xdavidhu.me/google/2021/01/11/stealing-your-private-videos-one-frame-at-a-time/",
            "Bug Bounty",
            listOf("writeup", "idor", "server-side"),
            "Curated — server-side IDOR media access."
        ),
        Entry(
            "Google hack \$50,000 (Landh)",
            "https://www.landh.tech/blog/20240304-google-hack-50000/",
            "Bug Bounty",
            listOf("writeup", "google", "vrp"),
            "Curated — high-impact Google research story."
        ),
        Entry(
            "Obtaining global admin in every Entra ID tenant with actor tokens",
            "https://dirkjanm.io/obtaining-global-admin-in-every-entra-id-tenant-with-actor-tokens/",
            "Bug Bounty",
            listOf("writeup", "authentication", "jwt", "cloud"),
            "Curated — identity token abuse research."
        ),
        Entry(
            "Hacking high-profile targets (Vitor Falcao)",
            "https://vitorfalcao.com/posts/hacking-high-profile-targets/",
            "Bug Bounty",
            listOf("writeup", "xss", "csrf", "idor", "cors"),
            "Curated — multi-class client/server chain notes."
        ),
        Entry(
            "Intigriti 0525 writeup (Vitor Falcao)",
            "https://vitorfalcao.com/posts/intigriti-0525-writeup/",
            "Bug Bounty",
            listOf("writeup", "xss", "dom-clobbering"),
            "Curated — client-side challenge methodology."
        ),
        Entry(
            "Automating CSPT discovery",
            "https://vitorfalcao.com/posts/automating-cspt-discovery/",
            "Bug Bounty",
            listOf("writeup", "cspt", "xss", "client-side"),
            "Curated — client-side path traversal automation."
        ),
        Entry(
            "3 months as a full-time bug bounty hunter",
            "https://vitorfalcao.com/posts/3-months-as-a-full-time-bug-bounty-hunter/",
            "Tips",
            listOf("tips", "methodology", "career"),
            "Curated — process and mindset notes."
        ),
        // PortSwigger / technique deep dives
        Entry(
            "Hijacking service workers via DOM clobbering (PortSwigger)",
            "https://portswigger.net/research/hijacking-service-workers-via-dom-clobbering",
            "Web Security",
            listOf("writeup", "xss", "dom-clobbering"),
            "Curated — service worker + clobbering."
        ),
        Entry(
            "Cookie Chaos — Host/Secure cookie prefix bypasses",
            "https://portswigger.net/research/cookie-chaos-how-to-bypass-host-and-secure-cookie-prefixes",
            "Web Security",
            listOf("writeup", "cookies", "authentication"),
            "Curated — cookie prefix edge cases."
        ),
        Entry(
            "Portable data exfiltration (PortSwigger)",
            "https://portswigger.net/research/portable-data-exfiltration",
            "Web Security",
            listOf("writeup", "xss", "ssrf"),
            "Curated — exfil techniques research."
        ),
        Entry(
            "Web cache entanglement (PortSwigger)",
            "https://portswigger.net/research/web-cache-entanglement",
            "Web Security",
            listOf("writeup", "cache", "xss"),
            "Curated — cache key confusion class."
        ),
        Entry(
            "Practical web cache poisoning (PortSwigger)",
            "https://portswigger.net/research/practical-web-cache-poisoning",
            "Web Security",
            listOf("writeup", "cache-poisoning"),
            "Curated — foundational cache poisoning."
        ),
        Entry(
            "Hidden OAuth attack vectors (PortSwigger)",
            "https://portswigger.net/research/hidden-oauth-attack-vectors",
            "Authentication",
            listOf("writeup", "oauth", "xss", "csrf"),
            "Curated — OAuth edge-case catalog."
        ),
        Entry(
            "SAML roulette (PortSwigger)",
            "https://portswigger.net/research/saml-roulette-the-hacker-always-wins",
            "Authentication",
            listOf("writeup", "saml", "authentication"),
            "Curated — SAML attack surface."
        ),
        // Server-side classics / modern
        Entry(
            "Syntax confusion — ambiguous parsing exploits (YesWeHack)",
            "https://www.yeswehack.com/learn-bug-bounty/syntax-confusion-ambiguous-parsing-exploits",
            "Bug Bounty",
            listOf("writeup", "parser", "ssrf", "ssti"),
            "Curated — parser differentials methodology."
        ),
        Entry(
            "Server-side template injection exploitation (YesWeHack)",
            "https://www.yeswehack.com/learn-bug-bounty/server-side-template-injection-exploitation",
            "Bug Bounty",
            listOf("writeup", "ssti", "rce"),
            "Curated — SSTI practice path."
        ),
        Entry(
            "Salesforce security research (enumerated.ie)",
            "https://www.enumerated.ie/index/salesforce",
            "Bug Bounty",
            listOf("writeup", "salesforce", "idor", "sqli"),
            "Curated — large SaaS attack surface notes."
        ),
        Entry(
            "Critical bugs in Adobe Experience Manager (Assetnote)",
            "https://slcyber.io/assetnote-security-research-center/finding-critical-bugs-in-adobe-experience-manager",
            "Bug Bounty",
            listOf("writeup", "ssrf", "xxe", "cms"),
            "Curated — AEM research methodology."
        ),
        Entry(
            "Blind SSRF chains (Assetnote)",
            "https://blog.assetnote.io/2021/01/13/blind-ssrf-chains/",
            "Bug Bounty",
            listOf("writeup", "ssrf", "xxe"),
            "Curated — chaining blind SSRF."
        ),
        Entry(
            "Next.js middleware bypass analysis (CVE-2025-29927)",
            "https://www.assetnote.io/resources/research/doing-the-due-diligence-analyzing-the-next-js-middleware-bypass-cve-2025-29927",
            "Bug Bounty",
            listOf("writeup", "authentication", "cve"),
            "Curated — framework middleware auth bypass class."
        ),
        Entry(
            "Novel SQL injection in PDO prepared statements (Assetnote)",
            "https://slcyber.io/assetnote-security-research-center/a-novel-technique-for-sql-injection-in-pdos-prepared-statements/",
            "Bug Bounty",
            listOf("writeup", "sqli"),
            "Curated — unexpected SQLi class."
        ),
        // Client-side technique
        Entry(
            "DOM XSS — cookie overwrite + innerHTML quirk",
            "https://elmahdi4.wordpress.com/2025/09/26/dom-xss-bypassing-server-side-cookie-overwrite-chrome-innerhtml-quirk-and-json-injection/",
            "Bug Bounty",
            listOf("writeup", "xss", "dom"),
            "Curated — modern DOM XSS tricks."
        ),
        Entry(
            "XSS without semicolon and parentheses (Huli)",
            "https://blog.huli.tw/2025/09/15/en/xss-without-semicolon-and-parentheses/",
            "Web Security",
            listOf("writeup", "xss"),
            "Curated — constrained XSS payloads."
        ),
        Entry(
            "CSPT account takeover via cache deception",
            "https://zere.es/posts/cache-deception-cspt-account-takeover/",
            "Bug Bounty",
            listOf("writeup", "cache-deception", "cspt", "ato"),
            "Curated — cache deception + CSPT chain."
        ),
        Entry(
            "Web cache deception attack (Omer Gil)",
            "https://omergil.blogspot.com/2017/02/web-cache-deception-attack.html",
            "Web Security",
            listOf("writeup", "cache-deception"),
            "Curated — original web cache deception."
        ),
        // Tips / methodology
        Entry(
            "Pentesting addon/plugin ecosystems (Intigriti)",
            "https://www.intigriti.com/researchers/blog/hacking-tools/pentesting-addon-plugin-ecosystems",
            "Tips",
            listOf("tips", "methodology", "plugins"),
            "Curated tip — extension/plugin attack surface."
        ),
        Entry(
            "Hacking Google Firebase targets (Intigriti)",
            "https://www.intigriti.com/researchers/blog/hacking-tools/hacking-google-firebase-targets",
            "Tips",
            listOf("tips", "firebase", "cloud"),
            "Curated tip — Firebase hunting checklist mindset."
        ),
        Entry(
            "Exploiting advanced XXE (Intigriti)",
            "https://www.intigriti.com/researchers/blog/hacking-tools/exploiting-advanced-xxe-vulnerabilities",
            "Tips",
            listOf("tips", "xxe", "methodology"),
            "Curated tip — XXE beyond basics."
        ),
        Entry(
            "JWT vulnerabilities & attacks guide (PentesterLab)",
            "https://pentesterlab.com/blog/jwt-vulnerabilities-attacks-guide",
            "Tips",
            listOf("tips", "jwt", "authentication"),
            "Curated tip — JWT testing map."
        ),
        Entry(
            "Bug Bounty Daily hub",
            "https://bugbountydaily.com/",
            "Tips",
            listOf("tips", "aggregator", "writeups"),
            "Curated hub — live aggregator of high-signal BB articles (open in browser)."
        ),
        Entry(
            "Awesome Bug Bounty Writeups (GitHub)",
            "https://github.com/devanshbatham/Awesome-Bugbounty-Writeups",
            "Tips",
            listOf("tips", "aggregator", "github"),
            "Curated hub — writeups organized by vulnerability class."
        ),
        Entry(
            "HolyTips — notes & checklists",
            "https://github.com/HolyBugx/HolyTips",
            "Tips",
            listOf("tips", "checklist", "methodology"),
            "Curated hub — community tips/checklists for BB."
        ),
        // --- Additional from BugBountyDaily-style sheet ---
        Entry(
            "PortSwigger — DOM-based AngularJS sandbox escapes",
            "https://portswigger.net/research/dom-based-angularjs-sandbox-escapes",
            "Bug Bounty",
            listOf("writeup", "xss", "prototype-pollution"),
            "Curated — classic client-side research."
        ),
        Entry(
            "Web Cache Deception Attack (Omer Gil)",
            "https://omergil.blogspot.com/2017/02/web-cache-deception-attack.html",
            "Bug Bounty",
            listOf("writeup", "cache-deception"),
            "Curated — foundational cache deception."
        ),
        Entry(
            "Practical web cache poisoning (PortSwigger)",
            "https://portswigger.net/research/practical-web-cache-poisoning",
            "Bug Bounty",
            listOf("writeup", "cache-poisoning"),
            "Curated — cache poisoning methodology."
        ),
        Entry(
            "Gotta cache 'em all (PortSwigger)",
            "https://portswigger.net/research/gotta-cache-em-all",
            "Bug Bounty",
            listOf("writeup", "cache"),
            "Curated — cache key research."
        ),
        Entry(
            "Cookie Chaos — Host/Secure prefix bypasses",
            "https://portswigger.net/research/cookie-chaos-how-to-bypass-host-and-secure-cookie-prefixes",
            "Bug Bounty",
            listOf("writeup", "cookies", "authentication"),
            "Curated — cookie prefix edge cases."
        ),
        Entry(
            "Hidden OAuth attack vectors (PortSwigger)",
            "https://portswigger.net/research/hidden-oauth-attack-vectors",
            "Authentication",
            listOf("writeup", "oauth"),
            "Curated — OAuth attack surface catalog."
        ),
        Entry(
            "SAML roulette (PortSwigger)",
            "https://portswigger.net/research/saml-roulette-the-hacker-always-wins",
            "Authentication",
            listOf("writeup", "saml"),
            "Curated — SAML issues research."
        ),
        Entry(
            "Hijacking service workers via DOM clobbering",
            "https://portswigger.net/research/hijacking-service-workers-via-dom-clobbering",
            "Bug Bounty",
            listOf("writeup", "xss", "dom-clobbering"),
            "Curated — service worker + clobbering."
        ),
        Entry(
            "Next.js middleware bypass analysis (Assetnote)",
            "https://www.assetnote.io/resources/research/doing-the-due-diligence-analyzing-the-next-js-middleware-bypass-cve-2025-29927",
            "Bug Bounty",
            listOf("writeup", "authentication", "cve"),
            "Curated — framework middleware auth bypass."
        ),
        Entry(
            "Blind SSRF chains (Assetnote)",
            "https://blog.assetnote.io/2021/01/13/blind-ssrf-chains/",
            "Bug Bounty",
            listOf("writeup", "ssrf"),
            "Curated — chaining blind SSRF."
        ),
        Entry(
            "Novel SQLi in PDO prepared statements (Assetnote)",
            "https://slcyber.io/assetnote-security-research-center/a-novel-technique-for-sql-injection-in-pdos-prepared-statements/",
            "Bug Bounty",
            listOf("writeup", "sqli"),
            "Curated — unexpected SQLi class."
        ),
        Entry(
            "Critical bugs in Adobe Experience Manager (Assetnote)",
            "https://slcyber.io/assetnote-security-research-center/finding-critical-bugs-in-adobe-experience-manager",
            "Bug Bounty",
            listOf("writeup", "ssrf", "xxe"),
            "Curated — AEM research methodology."
        ),
        Entry(
            "Obtaining global admin in every Entra ID tenant (actor tokens)",
            "https://dirkjanm.io/obtaining-global-admin-in-every-entra-id-tenant-with-actor-tokens/",
            "Bug Bounty",
            listOf("writeup", "authentication", "cloud"),
            "Curated — identity token abuse research."
        ),
        Entry(
            "GitHub Copilot RCE via prompt injection (Embrace The Red)",
            "https://embracethered.com/blog/posts/2025/github-copilot-remote-code-execution-via-prompt-injection/",
            "AI Security",
            listOf("writeup", "ai", "prompt-injection", "rce"),
            "Curated — coding-agent prompt injection."
        ),
        Entry(
            "From MCP to shell (Veria Labs)",
            "https://verialabs.com/blog/from-mcp-to-shell/",
            "AI Security",
            listOf("writeup", "ai", "mcp", "rce"),
            "Curated — MCP tool-chain abuse."
        ),
        Entry(
            "EchoLeak — AIM Labs",
            "https://www.aim.security/aim-labs/aim-labs-echoleak-blogpost",
            "AI Security",
            listOf("writeup", "ai", "prompt-injection"),
            "AIM Labs EchoLeak: LLM-assisted data leakage patterns. Study the chain, capture takeaways in a study note. Full writeup via Open link."
        ),
        Entry(
            "Prompt injection to RCE in AI agents (Trail of Bits)",
            "https://blog.trailofbits.com/2025/10/22/prompt-injection-to-rce-in-ai-agents/",
            "AI Security",
            listOf("writeup", "ai", "rce"),
            "Curated — agent injection paths."
        ),
        Entry(
            "Google Bard data exfiltration (Embrace The Red)",
            "https://embracethered.com/blog/posts/2023/google-bard-data-exfiltration/",
            "AI Security",
            listOf("writeup", "ai", "data-exfiltration"),
            "Curated — classic LLM exfil patterns."
        ),
        Entry(
            "Accidental $70k Google Pixel lock screen bypass",
            "https://bugs.xdavidhu.me/google/2022/11/10/accidental-70k-google-pixel-lock-screen-bypass/",
            "Bug Bounty",
            listOf("writeup", "authentication", "client-side"),
            "Curated — Google VRP client-side race."
        ),
        Entry(
            "Opening the unopenable — Google Cloud SSRF",
            "https://bugs.xdavidhu.me/google/2021/12/31/opening-the-unopenable-story-of-a-google-cloud-ssrf/",
            "Bug Bounty",
            listOf("writeup", "ssrf", "cloud"),
            "Curated — Google Cloud SSRF chain."
        ),
        Entry(
            "Hacking high-profile targets (Vitor Falcao)",
            "https://vitorfalcao.com/posts/hacking-high-profile-targets/",
            "Bug Bounty",
            listOf("writeup", "xss", "idor", "cors"),
            "Curated — multi-class methodology notes."
        ),
        Entry(
            "Automating CSPT discovery",
            "https://vitorfalcao.com/posts/automating-cspt-discovery/",
            "Bug Bounty",
            listOf("writeup", "cspt", "client-side"),
            "Curated — client-side path traversal automation."
        ),
        Entry(
            "Syntax confusion — ambiguous parsing (YesWeHack)",
            "https://www.yeswehack.com/learn-bug-bounty/syntax-confusion-ambiguous-parsing-exploits",
            "Bug Bounty",
            listOf("writeup", "parser"),
            "Curated — parser differentials methodology."
        ),
        Entry(
            "Server-side template injection exploitation (YesWeHack)",
            "https://www.yeswehack.com/learn-bug-bounty/server-side-template-injection-exploitation",
            "Bug Bounty",
            listOf("writeup", "ssti", "rce"),
            "Curated — SSTI practice path."
        ),
        Entry(
            "JWT vulnerabilities & attacks guide (PentesterLab)",
            "https://pentesterlab.com/blog/jwt-vulnerabilities-attacks-guide",
            "Tips",
            listOf("tips", "jwt", "authentication"),
            "Curated tip — JWT testing map."
        ),
        Entry(
            "Exploiting advanced XXE (Intigriti)",
            "https://www.intigriti.com/researchers/blog/hacking-tools/exploiting-advanced-xxe-vulnerabilities",
            "Tips",
            listOf("tips", "xxe"),
            "Curated tip — XXE beyond basics."
        ),
        Entry(
            "Hacking Google Firebase targets (Intigriti)",
            "https://www.intigriti.com/researchers/blog/hacking-tools/hacking-google-firebase-targets",
            "Tips",
            listOf("tips", "firebase"),
            "Curated tip — Firebase hunting mindset."
        ),
        Entry(
            "Bug Bounty Daily hub",
            "https://bugbountydaily.com/",
            "Tips",
            listOf("tips", "aggregator", "writeups"),
            "Curated hub — live aggregator of high-signal BB articles."
        ),
        Entry(
            "Awesome Bug Bounty Writeups (GitHub)",
            "https://github.com/devanshbatham/Awesome-Bugbounty-Writeups",
            "Tips",
            listOf("tips", "aggregator", "github"),
            "Curated hub — writeups by vulnerability class."
        ),
        Entry(
            "HolyTips — notes & checklists",
            "https://github.com/HolyBugx/HolyTips",
            "Tips",
            listOf("tips", "checklist"),
            "Curated hub — community tips/checklists."
        )
    )

    /** Manual follow list (Telegram/X cannot be polled without APIs). */
    val CHANNEL_HINTS: List<Pair<String, String>> = listOf(
        "X @bountywriteups" to "https://x.com/bountywriteups",
        "Telegram dailybountywriteup" to "https://t.me/dailybountywriteup",
        "Telegram GitBook_s" to "https://t.me/GitBook_s",
        "Telegram thebugbountyhunter" to "https://t.me/thebugbountyhunter",
        "Telegram bugbounty_tech" to "https://t.me/bugbounty_tech",
        "Telegram bug_bounty_channel" to "https://t.me/bug_bounty_channel",
        "SecurityCipher" to "https://securitycipher.com"
    )
}
