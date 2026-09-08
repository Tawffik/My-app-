package com.cyberos.app.methodology

import android.content.Context
import com.cyberos.app.data.JsonCodec
import java.io.File

class MethodologyStore(context: Context) {

    private val file: File = File(context.filesDir, "methodologies.json")
    private var cache: MutableList<Methodology> = load()

    fun all(): List<Methodology> = cache.sortedByDescending { it.updatedAt }
    fun get(id: Long): Methodology? = cache.firstOrNull { it.id == id }
    fun nextId(): Long = (cache.maxOfOrNull { it.id } ?: 0L) + 1L

    @Synchronized
    fun save(m: Methodology) {
        val index = cache.indexOfFirst { it.id == m.id }
        if (index >= 0) cache[index] = m else cache.add(m)
        persist()
    }

    @Synchronized
    fun delete(id: Long) { cache.removeAll { it.id == id }; persist() }

    fun toJson(): String = JsonCodec.methodologiesToJson(cache)

    @Synchronized
    fun replaceAll(text: String) {
        cache = try { JsonCodec.parseMethodologies(text).toMutableList() } catch (_: Exception) { mutableListOf() }
        persist()
    }

    fun ensureSeeded() {
        if (file.exists() || cache.isNotEmpty()) return
        val now = System.currentTimeMillis()
        val demo = Methodology(
            id = 1L,
            title = "API Authorization Testing Methodology (demo)",
            steps = listOf(
                "Discovery: map every endpoint (Swagger, JS files, Burp site map)",
                "Authentication: understand the token mechanism (JWT? OAuth? API keys?)",
                "Horizontal authz: try other users' object IDs (IDOR)",
                "Vertical authz: try lower roles on admin endpoints",
                "Logic: test repeat operations and race conditions",
                "Documentation: record evidence and write the report"
            ).mapIndexed { i, text -> MethodologyStep(id = now + i, objective = text) },
            createdAt = now, updatedAt = now
        )
        cache.add(demo)
        persist()
    }

    private fun persist() { try { file.writeText(toJson()) } catch (_: Exception) { } }
    private fun load(): MutableList<Methodology> {
        if (!file.exists()) return mutableListOf()
        return try { JsonCodec.parseMethodologies(file.readText()).toMutableList() } catch (_: Exception) { mutableListOf() }
    }
}
