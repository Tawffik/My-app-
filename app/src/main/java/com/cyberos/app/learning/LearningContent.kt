package com.cyberos.app.learning

data class TopicSection(val heading: String, val body: String)
data class QuizQuestion(val question: String, val options: List<String>, val correct: Int, val explanation: String)
data class TopicData(
    val id: String, val title: String, val summary: String,
    val sections: List<TopicSection>, val related: List<String>,
    val flashcards: List<Pair<String, String>>,
    val quiz: List<QuizQuestion> = emptyList()
)
data class PathData(val id: String, val title: String, val description: String, val topics: List<TopicData>)

object CyberCurriculum {

    val paths: List<PathData> = listOf(
        PathData(
            id = "web-basics", title = "Web Security Basics",
            description = "The foundations every security engagement is built on.",
            topics = listOf(
                TopicData(
                    id = "http-fundamentals", title = "HTTP Fundamentals",
                    summary = "Everything on the web travels over HTTP.",
                    sections = listOf(
                        TopicSection("What it is", "HTTP بروتوكول نصي طلب/رد: Request (Method + Path + Headers + Body) و Response (Status + Headers + Body)."),
                        TopicSection("Why it matters", "معظم الثغرات بتعيش في تفاصيل الطلب."),
                        TopicSection("Hands-on", "افتح DevTools → Network → شوف أي request وغيّر قيم."),
                        TopicSection("Takeaways", "Methods الحساسة لازم تكون محمية، و 401 مقابل 403 فرق مهم.")
                    ),
                    related = listOf("web-architecture", "info-disclosure"),
                    flashcards = listOf(
                        "إيه مكونات أي HTTP Request؟" to "Request Line + Headers + Body.",
                        "إيه الفرق بين 401 و 403؟" to "401 = مش متصادق، 403 = متصادق بس ممنوع."
                    ),
                    quiz = listOf(
                        QuizQuestion("إيه الفرق بين 401 و 403؟",
                            listOf("401 مش متصادق — 403 متصادق بس ممنوع","العكس","الاتنين server error","مفيش فرق"),0,
                            "401 = غلط credentials. 403 = ممنوع من المورد."),
                        QuizQuestion("الـ Request Line بيحتوي على إيه؟",
                            listOf("Headers","Method + Path + version","Body","Status"),1,
                            "Method + Path + إصدار HTTP.")
                    )
                ),
                TopicData(
                    id = "web-architecture", title = "Web Architecture",
                    summary = "Risks at every layer.",
                    sections = listOf(
                        TopicSection("Layers", "Browser → CDN → WAF → LB → Server → App → API → DB."),
                        TopicSection("Danger", "اختلاف الـ parsing أساس Request Smuggling."),
                        TopicSection("Hardening", "توحيد الـ parsing، رفض الملتبس.")
                    ),
                    related = listOf("http-fundamentals", "ssrf"),
                    flashcards = listOf(
                        "سبب Web Cache Poisoning؟" to "خزن رد ملوث بسبب unkeyed input.",
                        "ليه parsing مختلف خطير؟" to "طلب واحد يتفسر كتوصيفين."
                    ),
                    quiz = listOf(
                        QuizQuestion("سبب Request Smuggling الجذري؟",
                            listOf("تشفير ضعيف","اختلاف parsing بين الطبقات","بطء DB","باسوردات"),1,
                            "الطبقات تفسر Content-Length بشكل مختلف."),
                        QuizQuestion("علاج Host Header Injection؟",
                            listOf("allowlist حرفية","إخفاء السيرفر","RAM","تعطيل cookies"),0,
                            "تطابق حرفي مع قائمة مسموحة.")
                    )
                ),
                TopicData(
                    id = "recon", title = "Reconnaissance",
                    summary = "Know the target first.",
                    sections = listOf(
                        TopicSection("Types", "Passive (WHOIS, OSINT) و Active (مسح)."),
                        TopicSection("Golden rule", "جوه الـ scope المصرّح بيه بس."),
                        TopicSection("Tools", "amass و subfinder و wayback.")
                    ),
                    related = listOf("info-disclosure", "business-logic"),
                    flashcards = listOf(
                        "الفرق بين السلبي والإيجابي؟" to "السلبي بدون تفاعل، الإيجابي طلبات فعلية.",
                        "ليه الـ scope مهم؟" to "بره الـ scope = كسر قانون."
                    ),
                    quiz = listOf(
                        QuizQuestion("الفرق بين Passive و Active؟",
                            listOf("السلبي أسرع","الإيجابي قانوني","السلبي بدون تفاعل مباشر","مفيش"),2,
                            "Passive = بدون لمس الهدف."),
                        QuizQuestion("قبل active testing؟",
                            listOf("VPN","جوه الـ scope","أدوات محدثة","شهير"),1,
                            "بره الـ scope بيكسر الشروط.")
                    )
                ),
                TopicData(
                    id = "info-disclosure", title = "Information Disclosure",
                    summary = "Easiest bug, sometimes most dangerous.",
                    sections = listOf(
                        TopicSection("Where", "ملفات .git و .env، stack traces، JS."),
                        TopicSection("Hunt", "افحص JS bundles، جرب مسارات معروفة."),
                        TopicSection("Hardening", "أزل debug، امسح الأخطاء.")
                    ),
                    related = listOf("recon", "http-fundamentals"),
                    flashcards = listOf(
                        "فين الـ secrets؟" to "ملفات .env و .git، JS bundles.",
                        "التسريب بيسبب إيه؟" to "بيسهّل استغلال أعمق."
                    ),
                    quiz = listOf(
                        QuizQuestion("أكتر مكان للـ secrets؟",
                            listOf("logo","favicon","JS bundles و .env","إيميل"),2,
                            "Bundles و .env و .git المكشوفة."),
                        QuizQuestion("خطر stack trace؟",
                            listOf("يكشف التقنيات","يزود سرعة","SEO","مفيش"),0,
                            "يكشف التقنيات والإصدارات.")
                    )
                )
            )
        ),
        PathData(
            id = "auth", title = "Authentication & Authorization",
            description = "Where real vulnerabilities live.",
            topics = listOf(
                TopicData(
                    id = "authentication", title = "Authentication",
                    summary = "Proving who you are.",
                    sections = listOf(
                        TopicSection("Mechanisms", "كلمة سر، JWT، OAuth."),
                        TopicSection("Weaknesses", "Brute force، user enumeration، session fixation."),
                        TopicSection("Hardening", "rate limiting، رسائل عامة، session regeneration.")
                    ),
                    related = listOf("session-jwt", "access-control"),
                    flashcards = listOf(
                        "Session Fixation؟" to "المهاجم يحدد session ID قبل الدخول.",
                        "ليه رسائل عامة؟" to "منع user enumeration."
                    ),
                    quiz = listOf(
                        QuizQuestion("Session Fixation؟",
                            listOf("تخمين باسورد","يحدد session ID قبل الدخول","بطء","كوكيز"),1,
                            "ID معروف قبل الدخول ويفضل صالح."),
                        QuizQuestion("رسايل عامة عشان؟",
                            listOf("user enumeration","bandwidth","SEO","UX"),0,
                            "منع تأكيد الإيميلات المسجلة.")
                    )
                ),
                TopicData(
                    id = "access-control", title = "Access Control",
                    summary = "OWASP's most dangerous class.",
                    sections = listOf(
                        TopicSection("Types", "رأسي: عادي → أدمن. أفقي: → بيانات مستخدم تاني."),
                        TopicSection("Why", "التطبيق بيحمي الـ UI مش الـ endpoint."),
                        TopicSection("Hardening", "تحقق server-side، deny by default.")
                    ),
                    related = listOf("idor", "authentication"),
                    flashcards = listOf(
                        "الرأسي مقابل الأفقي؟" to "الرأتي صلاحية أعلى، الأفقي بيانات زيّك.",
                        "إخفاء الزرار مش حماية ليه؟" to "الـ endpoint بيتنادى مباشرة."
                    ),
                    quiz = listOf(
                        QuizQuestion("التخويل الرأسي؟",
                            listOf("عادي → أدمن","بيانات تاني","cookies","TLS"),0,
                            "صلاحية أعلى من دورك."),
                        QuizQuestion("إخفاء الزرار؟",
                            listOf("بيرجع بعد refresh","endpoint بيتنادى مباشرة","UI بيتشفّر","حفظ HTML"),1,
                            "الحماية server-side.")
                    )
                ),
                TopicData(
                    id = "idor", title = "IDOR",
                    summary = "Most common bug bounty finding.",
                    sections = listOf(
                        TopicSection("Idea", "معرف بيتحكم فيه العميل بدون تحقق ملكية."),
                        TopicSection("Detect", "غيّر الـ ID لمعرف تاني وشوف النتيجة."),
                        TopicSection("Hardening", "WHERE owner = current_user.")
                    ),
                    related = listOf("access-control", "business-logic"),
                    flashcards = listOf(
                        "IDOR؟" to "وصول لمورد تاني بدون تحقق.",
                        "التحصين؟" to "فلترة الملكية في الـ query."
                    ),
                    quiz = listOf(
                        QuizQuestion("IDOR في سطر؟",
                            listOf("حقن SQL","وصول بدون تحقق ملكية","تخمين","XSS"),1,
                            "المعرف بدون تحقق."),
                        QuizQuestion("التحصين الجذري؟",
                            listOf("UUIDs","إخفاء IDs","WHERE owner = user","rate limit"),2,
                            "الفلترة في الـ query.")
                    )
                ),
                TopicData(
                    id = "session-jwt", title = "Sessions & JWT",
                    summary = "How the app remembers you.",
                    sections = listOf(
                        TopicSection("JWT", "header.payload.signature."),
                        TopicSection("Pitfalls", "alg=none، confusion، لا صلاحية."),
                        TopicSection("Hardening", "تحقق من alg، secret قوي، jti.")
                    ),
                    related = listOf("authentication", "oauth"),
                    flashcards = listOf(
                        "Algorithm Confusion؟" to "HS256 بالمفتاح العام المعروف.",
                        "jti؟" to "منع replay."
                    ),
                    quiz = listOf(
                        QuizQuestion("Algorithm Confusion؟",
                            listOf("HS256 بالمفتاح العام","payload مش مشفر","token طويل","لا صلاحية"),0,
                            "المفتاح العام معروف."),
                        QuizQuestion("jti؟",
                            listOf("تشفير","مُصدر","منع replay","انتهاء"),2,
                            "معرف فريد يمنع إعادة الاستخدام.")
                    )
                ),
                TopicData(
                    id = "oauth", title = "OAuth 2.0 & OIDC",
                    summary = "Fertile ground for logic flaws.",
                    sections = listOf(
                        TopicSection("Idea", "Authorization Code + PKCE."),
                        TopicSection("Weaknesses", "redirect_uri مفتوح، لا state، replay."),
                        TopicSection("Hardening", "تحقق حرفي، state، PKCE.")
                    ),
                    related = listOf("session-jwt", "authentication"),
                    flashcards = listOf(
                        "state إلزامي ليه؟" to "منع CSRF.",
                        "PKCE؟" to "إثبات حيازة الـ code."
                    ),
                    quiz = listOf(
                        QuizQuestion("state parameter؟",
                            listOf("منع CSRF","scopes","تشفير","اسم"),0,
                            "يربط الطلب بالجلسة."),
                        QuizQuestion("PKCE يحمي من؟",
                            listOf("باسورد","XSS","اختطاف code","ضغط"),2,
                            "code بدون verifier مش كفاية.")
                    )
                )
            )
        ),
        PathData(
            id = "common-vulns", title = "Common Vulnerabilities",
            description = "The bugs you'll find everywhere.",
            topics = listOf(
                TopicData(
                    id = "sql-injection", title = "SQL Injection",
                    summary = "Input executed as SQL.",
                    sections = listOf(
                        TopicSection("What", "قيمة بتندمج في الاستعلام."),
                        TopicSection("Detect", "اقتباس، منطق، blind بـ sleep."),
                        TopicSection("Hardening", "Prepared statements.")
                    ),
                    related = listOf("xss", "info-disclosure"),
                    flashcards = listOf(
                        "SQLi ضد Blind؟" to "العادية نتايج، Blind سلوك الرد.",
                        "الحل؟" to "parameter binding."
                    ),
                    quiz = listOf(
                        QuizQuestion("الحل الجذري للـ SQLi؟",
                            listOf("فلترة","WAF","Prepared statements","إخفاء أخطاء"),2,
                            "فصل SQL عن البيانات."),
                        QuizQuestion("Blind SQLi؟",
                            listOf("خطأ واضح","سلوك الرد","union","500"),1,
                            "زمن أو true/false.")
                    )
                ),
                TopicData(
                    id = "xss", title = "Cross-Site Scripting",
                    summary = "Your code in the victim's browser.",
                    sections = listOf(
                        TopicSection("Types", "Reflected، Stored، DOM."),
                        TopicSection("Hardening", "encoding حسب السياق، CSP، HttpOnly.")
                    ),
                    related = listOf("csrf", "sql-injection"),
                    flashcards = listOf(
                        "Stored ضد Reflected؟" to "Stored يضرب أي زائر.",
                        "encoding بالسياق؟" to "نفس payload بيتقتل في HTML ويعيش في JS."
                    ),
                    quiz = listOf(
                        QuizQuestion("Stored ضد Reflected؟",
                            listOf("أسرع","Stored يضرب أي زائر","Reflected أخطر","مفيش"),1,
                            "Stored بيتخزن."),
                        QuizQuestion("encoding بالسياق؟",
                            listOf("متصفحات","نفس payload HTML ضد JS","CSS","سيرفر"),1,
                            "escaping HTML بتكسر JS string.")
                    )
                ),
                TopicData(
                    id = "csrf", title = "CSRF",
                    summary = "The request isn't yours.",
                    sections = listOf(
                        TopicSection("Idea", "كوكيز بتتبعت تلقائيًا."),
                        TopicSection("Hardening", "tokens، SameSite، Origin."),
                        TopicSection("XSS علاقة", "XSS تكسر أي حماية CSRF.")
                    ),
                    related = listOf("xss", "session-jwt"),
                    flashcards = listOf(
                        "SameSite يحمي؟" to "يمنع إرسال cookie من مواقع تانية.",
                        "XSS و tokens؟" to "XSS تقرا token من الصفحة."
                    ),
                    quiz = listOf(
                        QuizQuestion("SameSite=Strict؟",
                            listOf("يشفر cookie","يمنع إرساله من مواقع تانية","يقصر عمره","يمنع XSS"),1,
                            "طلب CSRF بدون الجلسة."),
                        QuizQuestion("XSS و CSRF tokens؟",
                            listOf("بيشفّر","JS يقرا token من الصفحة","يوقف الجلسة","مفيش علاقة"),1,
                            "نفس origin — token مكشوف.")
                    )
                ),
                TopicData(
                    id = "ssrf", title = "SSRF",
                    summary = "Make the server request for you.",
                    sections = listOf(
                        TopicSection("Targets", "cloud metadata، internal."),
                        TopicSection("Bypass", "أشكال IP، redirects، DNS."),
                        TopicSection("Hardening", "allowlist، منع redirects.")
                    ),
                    related = listOf("web-architecture", "info-disclosure"),
                    flashcards = listOf(
                        "أخطر هدف AWS؟" to "Metadata على 169.254.169.254.",
                        "redirect و الفلتر؟" to "الفلتر يفحص الدومين، redirect ينقلك."
                    ),
                    quiz = listOf(
                        QuizQuestion("أخطر هدف AWS؟",
                            listOf("login","CDN","Metadata 169.254.169.254","CSS"),2,
                            "بيرجع credentials."),
                        QuizQuestion("redirect يتخطى الفلتر؟",
                            listOf("يشفر","يفحص الدومين والـ redirect بعده","مش يشوف","headers"),1,
                            "الفلتر الدومين، redirect للـ IP الداخلي.")
                    )
                ),
                TopicData(
                    id = "business-logic", title = "Business Logic Flaws",
                    summary = "Not broken code — broken logic.",
                    sections = listOf(
                        TopicSection("Idea", "كل خطوة صح بس السيناريو مستحيل."),
                        TopicSection("Examples", "خصم مرتين، race، تخطي خطوة."),
                        TopicSection("Hardening", "server-side، transactions، idempotency.")
                    ),
                    related = listOf("idor", "access-control"),
                    flashcards = listOf(
                        "بيميز المنطق؟" to "الكود سليم بس السيناريو غلط.",
                        "idempotency key؟" to "منع تكرار العملية."
                    ),
                    quiz = listOf(
                        QuizQuestion("بميز ثغرة المنطق؟",
                            listOf("كود بيكسر","كود سليم بس سيناريو غلط","ناقص","SQLi"),1,
                            "كل خطوة صح."),
                        QuizQuestion("idempotency key؟",
                            listOf("منع تكرار العملية","سرقة جلسة","حقن","rate limit"),0,
                            "نفس key = مرة واحدة.")
                    )
                )
            )
        ),
        PathData(
            id = "android-sec", title = "Android Security",
            description = "From inside the APK to the network.",
            topics = listOf(
                TopicData(
                    id = "apk-structure", title = "APK Structure",
                    summary = "An APK isn't a black box.",
                    sections = listOf(
                        TopicSection("Inside", "classes.dex، manifest، assets، lib."),
                        TopicSection("Tools", "jadx، apkanalyzer."),
                        TopicSection("Check", "صلاحيات، exported، secrets، network.")
                    ),
                    related = listOf("manifest-security", "components-android"),
                    flashcards = listOf(
                        "classes.dex؟" to "Dalvik bytecode.",
                        "أول 3 فحوصات؟" to "Manifest، secrets، network."
                    ),
                    quiz = listOf(
                        QuizQuestion("classes.dex؟",
                            listOf("صور","Dalvik bytecode","manifest","شهادة"),1,
                            "الكود المترجم."),
                        QuizQuestion("أول 3 فحوصات؟",
                            listOf("شاشات","حجم","Manifest + secrets + network","تقييمات"),2,
                            "الأهم أمنيًا.")
                    )
                ),
                TopicData(
                    id = "manifest-security", title = "The Manifest, Securely",
                    summary = "First file an attacker reads.",
                    sections = listOf(
                        TopicSection("Look for", "exported، allowBackup، cleartext، debuggable."),
                        TopicSection("Hardening", "exported=false، allowBackup=false.")
                    ),
                    related = listOf("apk-structure", "components-android"),
                    flashcards = listOf(
                        "allowBackup=true؟" to "نقل بيانات بدون تشفير.",
                        "exported activity؟" to "أي تطبيق يناديها."
                    ),
                    quiz = listOf(
                        QuizQuestion("allowBackup=true؟",
                            listOf("نقل بيانات بدون تشفير","يبطئ","يعرض كود","يمنع تحديث"),0,
                            "adb backup يسحب البيانات."),
                        QuizQuestion("exported activity؟",
                            listOf("RAM","launcher","أي تطبيق يناديها","dark mode"),2,
                            "أي تطبيق يبعت intent.")
                    )
                ),
                TopicData(
                    id = "components-android", title = "Android Components",
                    summary = "Activity, Service, Receiver, Provider.",
                    sections = listOf(
                        TopicSection("Types", "شاشة، خلفية، أحداث، بيانات."),
                        TopicSection("Intents", "implicit مع بيانات حساسة = hijacking."),
                        TopicSection("Hardening", "تحقق، custom permission.")
                    ),
                    related = listOf("manifest-security", "webview-security"),
                    flashcards = listOf(
                        "implicit + حساسة؟" to "أي تطبيق يستقبلها.",
                        "Provider مصدّر؟" to "custom permission."
                    ),
                    quiz = listOf(
                        QuizQuestion("implicit + حساسة؟",
                            listOf("تتشفّر","تفشل","أي تطبيق يستقبلها","رفض"),2,
                            "نفس filter = استقبال."),
                        QuizQuestion("Provider مصدّر؟",
                            listOf("تسمية","إخفاء","obfuscation","custom permission"),3,
                            "تحدد مين يقرا.")
                    )
                ),
                TopicData(
                    id = "webview-security", title = "WebView Security",
                    summary = "A browser inside your app.",
                    sections = listOf(
                        TopicSection("Dangerous", "JS interface، file access."),
                        TopicSection("Hardening", "محتوى موثوق، منع file://.")
                    ),
                    related = listOf("components-android", "manifest-security"),
                    flashcards = listOf(
                        "addJavascriptInterface؟" to "يعرّض methods للـ JS.",
                        "allowFileAccess؟" to "JS يقرا ملفات محلية."
                    ),
                    quiz = listOf(
                        QuizQuestion("addJavascriptInterface؟",
                            listOf("يعرّض methods للـ JS","يبطئ","يمنع cookies","layout"),0,
                            "JS الخبيث ينادي الجسر."),
                        QuizQuestion("allowFileAccess؟",
                            listOf("صور أسرع","PDF","offline","JS يقرا ملفات محلية"),3,
                            "تسريب مباشر.")
                    )
                )
            )
        )
    )

    fun findTopic(id: String): TopicData? {
        for (p in paths) for (t in p.topics) if (t.id == id) return t
        return null
    }

    fun topicTitle(id: String): String = findTopic(id)?.title ?: id
    fun totalTopics(): Int = paths.sumOf { it.topics.size }

    fun firstIncompleteTopic(isDone: (String) -> Boolean): TopicData? {
        for (p in paths) for (t in p.topics) if (!isDone(t.id)) return t
        return null
    }

    fun allFlashcards(): List<Triple<String, String, String>> {
        val out = mutableListOf<Triple<String, String, String>>()
        for (p in paths) for (t in p.topics) for (fc in t.flashcards) {
            out.add(Triple(t.id, fc.first, fc.second))
        }
        return out
    }
}
