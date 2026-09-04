package com.cyberos.app.challenges

data class Challenge(
    val id: String,
    val title: String,
    val difficulty: String,
    val scenario: String,
    val clues: List<String>,
    val expectedVuln: String,
    val expectedRootCause: String,
    val expectedImpact: String,
    val topicId: String
)

data class ChallengeVerdict(
    val verdict: String,
    val feedback: String
)

object Challenges {

    val all: List<Challenge> = listOf(
        Challenge(
            id = "ch-01",
            title = "The invoice number game",
            difficulty = "Easy",
            scenario = "التطبيق بيسمح للمستخدم يشوف فواتيره على /api/invoices/1042. غيّرت الرقم لـ 1043 — رجعت فاتورة بتاعة مستخدم تاني بالبيانات كاملة.",
            clues = listOf(
                "المعرف بيتحكم فيه العميل وبيظهر في الـ URL",
                "السيرفر رجّع البيانات من غير أي 403"
            ),
            expectedVuln = "IDOR",
            expectedRootCause = "غياب التحقق من الملكية على مستوى السيرفر — المورد بيتجاب بالـ ID من غير فلترة owner",
            expectedImpact = "تسريب بيانات مستخدمين — ولو في DELETE يوصل لحذف بيانات الغير",
            topicId = "idor"
        ),
        Challenge(
            id = "ch-02",
            title = "The session that never rotates",
            difficulty = "Easy",
            scenario = "لقت link بيبعت session ID جاهز (?sid=abc123). الضحية تسجل دخون بيه — وبعد الدخول الـ session ID فضل نفسه.",
            clues = listOf(
                "الـ session ID اتحدد قبل الدخول",
                "السيرفر مش بيولّد ID جديد بعد المصادقة"
            ),
            expectedVuln = "Session Fixation",
            expectedRootCause = "السيرفر مش بيعمل session regeneration بعد الدخول",
            expectedImpact = "المهاجم يدخل بحساب الضحية — takeover كامل",
            topicId = "authentication"
        ),
        Challenge(
            id = "ch-03",
            title = "The server that fetches URLs",
            difficulty = "Medium",
            scenario = "ميزة import from URL بتاخد أي URL والسيرفر بيعمل طلب ليه. دخلت http://169.254.169.254/latest/meta-data/ — رجعت بيانات حساسة.",
            clues = listOf(
                "الطلب من جهة السيرفر مش المتصفح",
                "الـ IP ده مش من الإنترنت العام",
                "الفلتر بيمنع localhost بس"
            ),
            expectedVuln = "SSRF",
            expectedRootCause = "طلبات لأي URL بدون allowlist — والسيرفر واقف على شبكة بتوصله لموارد داخلية",
            expectedImpact = "وصول للـ cloud metadata — credentials → تصعيد صلاحيات",
            topicId = "ssrf"
        ),
        Challenge(
            id = "ch-04",
            title = "The token that changed its algorithm",
            difficulty = "Medium",
            scenario = "الـ JWT فيه header بيقول RS256. عدّلته لـ HS256 وأعدت التوقيع بالمفتاح العام — التطبيق قبل الـ token المزوّر.",
            clues = listOf(
                "المفتاح العام مش سر",
                "السيرفر مش بيفرض خوارزمية واحدة"
            ),
            expectedVuln = "JWT Algorithm Confusion",
            expectedRootCause = "قبول الخوارزمية من الـ header بدل تثبيتها",
            expectedImpact = "تزوير tokens صالحة لأي مستخدم",
            topicId = "session-jwt"
        ),
        Challenge(
            id = "ch-05",
            title = "The profile name that runs",
            difficulty = "Easy",
            scenario = "ميزة تغيير الاسم بتقبل أي نص. حطيت script في الاسم. كل من يفتح صفحة الفريق بيتنفذ فيه السكربت.",
            clues = listOf(
                "النص بيتخزن زي ما هو",
                "كل زائر بيشوف الاسم",
                "الرد مش بيعمل escaping"
            ),
            expectedVuln = "Stored XSS",
            expectedRootCause = "المدخل بيتخزن وبيترجع من غير output encoding",
            expectedImpact = "سرقة جلسات، keylogging، actions باسم الضحايا",
            topicId = "xss"
        ),
        Challenge(
            id = "ch-06",
            title = "The coupon that never dies",
            difficulty = "Hard",
            scenario = "كوبون خصم 50% بيتفعّل مرة واحدة. ضغطت apply عشر مرات متوازية — الخصم اتخصم 3 مرات.",
            clues = listOf(
                "كل طلب بيتحقق صح لوحده",
                "الطلبات في نفس اللحظة",
                "مفيش قفل أو idempotency"
            ),
            expectedVuln = "Race Condition (Business Logic)",
            expectedRootCause = "التحقق والاستخدام مش ذريين",
            expectedImpact = "استغلال عروض مالية بشكل متكرر",
            topicId = "business-logic"
        )
    )

    fun byId(id: String): Challenge? = all.firstOrNull { it.id == id }
}
