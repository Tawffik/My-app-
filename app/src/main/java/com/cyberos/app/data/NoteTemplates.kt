package com.cyberos.app.data

/**
 * Structured note templates that enforce the Learn → Analyze → Remember → Apply loop.
 */
data class NoteTemplate(
    val id: String,
    val title: String,
    val tags: List<String>,
    val body: String
)

object NoteTemplates {
    val ALL: List<NoteTemplate> = listOf(
        NoteTemplate(
            id = "security",
            title = "Security Note",
            tags = listOf("security", "fact"),
            body = """
## Context
Program / system / lab:

## Observation (facts only)
-

## Hypothesis (unverified)
-

## Validation plan
-

## Impact / root cause
-

## Takeaway (1 line for flashcards)
-

## Links
- [[]]
""".trimIndent()
        ),
        NoteTemplate(
            id = "writeup",
            title = "Writeup Digest",
            tags = listOf("writeup", "research"),
            body = """
## Source
Title / URL:

## Vulnerability class
-

## Attack chain (short)
1.
2.
3.

## What I would try on my authorized target
-

## What I learned
-

## Linked concepts
- [[]]

## Flashcard candidates
Q:
A:
""".trimIndent()
        ),
        NoteTemplate(
            id = "lab",
            title = "Lab Session",
            tags = listOf("lab", "practice"),
            body = """
## Lab / challenge
-

## Goal
-

## Steps taken
1.

## Result
-

## Mistake / insight
-

## Related notes
- [[]]

## Next practice
-
""".trimIndent()
        ),
        NoteTemplate(
            id = "ai-security",
            title = "AI Security Note",
            tags = listOf("ai-security", "hypothesis"),
            body = """
## LLM surface
Chat / RAG / Agent / Tooling:

## Risk (OWASP LLM style)
Prompt injection | RAG | Agency | Output handling | Other:

## Observation
-

## Hypothesis (lab-only / authorized)
-

## Defense idea
-

## Links
- [[]]

## Takeaway
-
""".trimIndent()
        ),
        NoteTemplate(
            id = "bb-session",
            title = "Bug Bounty Session",
            tags = listOf("bug-bounty", "session"),
            body = """
## Program
-

## Assets touched
-

## Hypotheses tested
-

## Evidence collected
-

## Status
Idea | Investigating | Validated | Dead end

## Notes graph
- [[]]

## Next action
-
""".trimIndent()
        ),
        NoteTemplate(
            id = "moc",
            title = "Map of Content (MOC)",
            tags = listOf("moc", "index"),
            body = """
## Purpose
Hub note that indexes related notes (Obsidian-style MOC).

## Core ideas
-

## Linked notes
- [[]]
- [[]]
- [[]]

## Open questions
-

## Resources
-
""".trimIndent()
        ),
        NoteTemplate(
            id = "concept",
            title = "Concept Card",
            tags = listOf("concept", "study"),
            body = """
## Concept
-

## In my words
-

## Example / lab
-

## Related
- [[]]

## One flashcard
Q:
A:
""".trimIndent()
        )
    )

    fun byId(id: String): NoteTemplate? = ALL.firstOrNull { it.id == id }
}
