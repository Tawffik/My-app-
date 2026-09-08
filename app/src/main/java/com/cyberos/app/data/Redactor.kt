package com.cyberos.app.data

object Redactor {

    data class Result(val text: String, val found: Int)

    fun redact(text: String): Result {
        var count = 0
        var out = text

        val keyRe = Regex("\\b(?:sk|gsk|rk|xai|rsk)[-_][A-Za-z0-9_-]{16,}\\b")
        count += keyRe.findAll(out).count()
        out = keyRe.replace(out, "[REDACTED]")

        val authRe = Regex("(?i)\\b(authorization)\\s*:\\s*\\S.*")
        count += authRe.findAll(out).count()
        out = authRe.replace(out, "Authorization: [REDACTED]")

        val bearerRe = Regex("(?i)\\b(bearer)\\s+[A-Za-z0-9\\-._~+/]+=*")
        count += bearerRe.findAll(out).count()
        out = bearerRe.replace(out, "Bearer [REDACTED]")

        val jwtRe = Regex("\\beyJ[A-Za-z0-9_-]{8,}\\.[A-Za-z0-9_-]{8,}\\.[A-Za-z0-9_-]*")
        count += jwtRe.findAll(out).count()
        out = jwtRe.replace(out, "[REDACTED:JWT]")

        val labeledRe = Regex("(?i)\\b(api[_-]?key|apikey|secret|token|password|passwd|client[_-]?secret)\\b\\s*[:=]\\s*\"?([^\\s,\"']{6,})")
        count += labeledRe.findAll(out).count()
        out = labeledRe.replace(out) { m -> m.groupValues[1] + "=[REDACTED]" }

        val hexRe = Regex("\\b[0-9a-fA-F]{32,}\\b")
        count += hexRe.findAll(out).count()
        out = hexRe.replace(out, "[REDACTED:HEX]")

        val mailRe = Regex("\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\\b")
        count += mailRe.findAll(out).count()
        out = mailRe.replace(out, "[REDACTED:EMAIL]")

        return Result(out, count)
    }
}
