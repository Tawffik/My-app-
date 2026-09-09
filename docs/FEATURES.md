# CyberOS — Feature checklist

Complete inventory of shipped capabilities (v1.0.35+). Useful for README readers, demos, and CV bullet mapping.

## Core loop

- [x] Daily Brief (due cards, next topic, findings, research pulse)
- [x] Offline-first local storage
- [x] English UI default (Arabic strings where wired via Lang)
- [x] GitHub Actions CI + APK releases

## Learning

- [x] Web Security Basics path
- [x] Authentication & Authorization path
- [x] Common Vulnerabilities path
- [x] Android Security path
- [x] AI Security path (OWASP LLM-oriented topics)
- [x] Web Security Bug Bounty Roadmap path
- [x] Access Control & IDOR Tricks path
- [x] Study OS path
- [x] Per-topic sections, flashcards, quizzes
- [x] Open study sources in Custom Tabs
- [x] Ask AI Tutor from topic
- [x] Create study note from topic
- [x] User-defined custom learning sections
- [x] Progress / XP completion tracking
- [x] Knowledge graph screen (topics)

## Notes vault

- [x] Create / edit / delete notes
- [x] Search (title, body, tags, folder)
- [x] Folders + folder filter
- [x] Tags + inline `#tags` + tag filter
- [x] Pin / unpin
- [x] Daily note
- [x] Templates (Security, Writeup, Lab, AI, BB Session, MOC, Concept)
- [x] Wiki links `[[Title]]`
- [x] Outgoing links panel
- [x] Backlinks panel
- [x] Create note from missing link
- [x] Link picker (insert existing note)
- [x] Notes graph (hubs, orphans, edges)
- [x] Unlinked mentions
- [x] Study mode (hide answers)
- [x] Copy as Markdown
- [x] Duplicate note
- [x] AI analyze note
- [x] Generate flashcards from note

## Research

- [x] Multi-source refresh (RSS / Atom / Markdown / link lists)
- [x] Category filters including Writeups, Tips, AI Security, Bug Bounty
- [x] Vulnerability-type filters for writeups
- [x] Curated offline writeup seed library
- [x] Channel hub entries (X / Telegram / SecurityCipher) under Tips
- [x] Bookmark / mark read
- [x] Custom Tabs open
- [x] Share-into-app receiver
- [x] Add Finding from research item
- [x] Background ResearchSyncWorker
- [x] Dedup + categorization helpers

## Bug Bounty workspace

- [x] Programs CRUD
- [x] Assets per program
- [x] Findings with status workflow
- [x] Linked research ids
- [x] Checklists (Web, Recon, API, LLM App)
- [x] Markdown report generator + copy
- [x] AI Report Review (BB Copilot)

## AI

- [x] Device-side API key vault
- [x] Configurable base URL / model
- [x] Chat modes: Normal, Teacher, Socratic, Researcher, AI Security, BB Copilot
- [x] Optional Council mode
- [x] Chat archive
- [x] Hypothesis-oriented system prompts

## Review & practice

- [x] Flashcard store + SM-2-style scheduling
- [x] Review screen
- [x] Topic quizzes
- [x] Card generation from text (AI-assisted when configured)

## Platform

- [x] WorkManager daily digest
- [x] Notification channels for research/digest
- [x] Material 3 Compose UI

## Not goals (intentionally deferred)

- Full desktop Obsidian parity (canvas plugins, live preview engine)
- Scraping Telegram/X without official APIs
- Silent auto-submit of bounty reports
- Local LLM runtime in-app (optional future)
