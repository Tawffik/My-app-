# CyberOS

**Personal Cybersecurity Learning · Research · Notes · Bug Bounty Operating System**

[![Android CI](https://github.com/Tawffik/My-app-/actions/workflows/android.yml/badge.svg)](https://github.com/Tawffik/My-app-/actions)
[![Release](https://img.shields.io/github/v/release/Tawffik/My-app-?include_prereleases)](https://github.com/Tawffik/My-app-/releases)
[![License](https://img.shields.io/badge/license-Personal%20%2F%20Educational-blue)](#license)

CyberOS is an **Android** app designed as a serious personal security workstation — not a content dump.

It combines structured learning, research feeds, an Obsidian-style notes vault, spaced repetition, AI tutors (with strict human-in-the-loop rules), and a bug bounty workspace.

> **Loop:** Learn → Note → Review → Research → Apply → Report

**Latest:** `v1.1.0` · Kotlin · Jetpack Compose · Offline-first

---

## Why it exists

Most “security apps” are either static articles or disconnected note tools.  
CyberOS is built around a **daily operational loop** suitable for real practice and for demonstrating product + security engineering skill in a portfolio.

| You need | CyberOS gives you |
|----------|-------------------|
| Structured study | Multi-path curriculum + quizzes + flashcards |
| A place for knowledge | Notes vault with `[[wiki links]]`, tags, folders, graph |
| Fresh writeups | Research feeds + curated library + Tips channels |
| Bounty workflow | Programs, assets, findings, checklists, Markdown reports |
| AI without false confidence | Tutor / BB Copilot that label **hypotheses**, never silent “confirmed vulns” |

---

## Feature overview

### 0. Navigation
Primary bottom bar: **Home · Learn · Research · Notes · AI**  
Review, Tasks, Bug Bounty, Methodologies open from **Home** (keeps the bar uncluttered).

### 1. Daily Brief & Start today
**Start today** runs a closed loop: flashcards → one topic → study note → optional finding/writeup (+XP).

### 1b. Daily Brief
One home ritual screen: due flashcards, next topic, findings that need attention, research / AI security pulse.

### 2. Learning paths
Built-in paths (additive; users can add custom sections):

| Path | Focus |
|------|--------|
| **Web Security Basics** | HTTP, architecture, recon, disclosure |
| **Authentication & Authorization** | AuthN, access control, IDOR, sessions/JWT, OAuth |
| **Common Vulnerabilities** | SQLi, XSS, CSRF, SSRF, business logic |
| **Android Security** | APK, manifest, components, WebView |
| **AI Security** | LLM apps, prompt injection, RAG, agents, output handling, AI for bug bounty |
| **Web Security BB Roadmap** | Fundamentals → JS/DOM → APIs → authz → Burp → workflow → notes system |
| **Access Control & IDOR Tricks** | Encoding, HPP, type spoofing, path/body authority, JSON quirks |
| **Study OS** | Daily ritual, notes that force application, open sources while studying |

Each topic: sections · flashcards · quiz · **open sources in browser** · **Ask AI Tutor** · **Create study note**.

### 3. Notes vault (Obsidian-inspired)

| Capability | Detail |
|------------|--------|
| **Wiki links** | `[[Note Title]]` with outgoing links + **backlinks** |
| **Tags** | Explicit tags + inline `#tags` |
| **Folders** | e.g. `Bug Bounty/IDOR`, `Daily`, `MOC` |
| **Pinned notes** | Keep hubs on top |
| **Daily note** | One click for today’s journal |
| **Templates** | Security · Writeup Digest · Lab · AI Security · BB Session · **MOC** · **Concept** |
| **Graph** | Hubs, orphans, edge list (mobile-friendly) |
| **Study mode** | Hide `A:` answers for active recall |
| **Link picker** | Insert link to an existing note |
| **Create from missing link** | Turn unresolved `[[Title]]` into a new note |
| **Unlinked mentions** | Notes that mention a title without `[[]]` |
| **Duplicate / Copy Markdown** | Fast reuse and export snippet |

### 4. Research & writeups
- Live **RSS / Atom / Markdown / URL-list** sources  
- **Curated offline library** of high-signal writeups  
- Aggregators: SecurityCipher daily list, Awesome Bugbounty Writeups, HackerOne disclosed archive list  
- Categories: Writeups · Bug Bounty · Tips · AI Security · …  
- Vuln-type filters · Custom Tabs · share-into-app · **Add Finding**  
- Channel hubs under Tips (X / Telegram / SecurityCipher — open in browser)

### 5. Bug Bounty workspace
Programs · Assets · Findings · Checklists (Web / Recon / API / **LLM App**) · Markdown report · **AI Report Review** (hypothesis assist only)

### 6. AI (optional API key)
Modes: Normal · Teacher · Socratic · Researcher · **AI Security Tutor** · **BB Copilot** · optional Council  

Rules: hypotheses until reproduced · no silent “confirmed vuln” for bounty · works offline without a key for non-AI features

### 7. Review & practice
SM-2-style flashcards · topic quizzes · card generation from notes (when AI configured)

### 8. Notifications
Research sync (~6h) · daily digest · Android 13+ permission required

---

## Architecture (short)

```
app/
  data/          # JSON stores, research pipeline, AI client, BB models, WikiLinks
  learning/      # Curriculum + custom paths + progress
  flashcards/    # SM-2 scheduling
  ui/            # Jetpack Compose by feature
```

Principles: offline-first · untrusted external content · no secrets in git · unit-tested pure logic · CI releases  

Details: [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md) · [docs/FEATURES.md](docs/FEATURES.md)

---

## Tech stack

Kotlin · Jetpack Compose · Material 3 · WorkManager · Chrome Custom Tabs · optional OpenAI-compatible API · GitHub Actions

---

## Data safety

**Settings → Export** creates a full JSON backup (notes, cards, progress, tasks, research, bug bounty, custom paths, checklist prefs).

## Build & install

**Requirements:** JDK 17, Android SDK 34.

```bash
git clone https://github.com/Tawffik/My-app-.git
cd My-app-
./gradlew assembleDebug
```

Or install the latest APK from **[Releases](https://github.com/Tawffik/My-app-/releases)**.

---

## Day-to-day usage

| Time | Action |
|------|--------|
| 5–10 min | Daily Brief + due cards |
| 20–30 min | One curriculum topic + study note |
| 10 min | Link notes / review graph orphans |
| 30+ min | Authorized BB workflow |
| Anytime | Research refresh · Add Finding |

Full guide: [docs/USAGE.md](docs/USAGE.md)

---

## Interview / portfolio demo (2–3 min)

1. **Daily Brief** — operational loop  
2. **Learn** — topic + open source  
3. **Notes** — `[[wiki link]]` + Graph  
4. **Research** — Writeups filter  
5. **Finding** — Markdown report + AI review as *hypothesis assist*  
6. Line: *“AI drafts; I verify before any submission. Only authorized targets.”*

---

## Security & ethics

Educational / personal research only. Test systems you own or are authorized to test. See [SECURITY.md](SECURITY.md).

---

## Roadmap (honest)

**Done:** research pipeline · BB workspace · AI modes · Daily Brief · notes vault · writeup library · BB roadmap & access-control curriculum · CI  

**Next candidates:** fewer primary tabs · full backup/restore · stronger finding “next action” · optional Markdown preview  

---

## Documentation

| Doc | Purpose |
|-----|---------|
| [USAGE.md](docs/USAGE.md) | How to use + demo script |
| [ARCHITECTURE.md](docs/ARCHITECTURE.md) | Technical structure |
| [FEATURES.md](docs/FEATURES.md) | Feature checklist |
| [CONTRIBUTING.md](CONTRIBUTING.md) | Contribution rules |
| [SECURITY.md](SECURITY.md) | Security expectations |

---

## Author

Built as a **personal engineering + security practice** project by [Tawfik](https://github.com/Tawffik).

Demonstrates: Android/Kotlin product structure · security-minded UX · web/Android/AI domain depth · research ingestion · offline-first design.

Repo: [github.com/Tawffik/My-app-](https://github.com/Tawffik/My-app-)

---

## License

Personal / educational project. Add an explicit open-source license if you invite public contributions.
