# CyberOS

**Personal Cybersecurity Learning · Research · Bug Bounty Operating System**

CyberOS is an Android app built as a serious personal security workstation: structured learning, research feeds, AI-assisted analysis (with strict human-in-the-loop rules), and a bug bounty workspace — offline-first, privacy-minded, and designed for daily practice.

> Built for learners and practitioners who want a single place to **learn → research → practice → document → review**.

---

## Why this exists

Most security “apps” are either content dumps or disconnected note tools. CyberOS is oriented around an **operational loop**:

1. **Learn** — curriculum paths (Web, Android, **AI Security**, …) with sections, flashcards, and quizzes  
2. **Research** — RSS/Atom/Markdown feeds (writeups, advisories, AI security sources) with categorization  
3. **Practice** — SM-2 reviews, quizzes, challenges, focus sessions  
4. **Document** — structured note templates (security, writeup digest, lab, AI security, BB session)  
5. **Apply** — Bug Bounty workspace (programs, assets, findings, checklists, Markdown reports)  
6. **AI assist** — Tutor / BB Copilot / researcher modes that label **hypotheses**, never silent “confirmed bugs”

---

## Feature map

| Area | What you get |
|------|----------------|
| **Daily Brief** | One screen for today’s focus: cards due, next topic, open findings, AI security pulse |
| **Learning** | Multi-path curriculum including **AI Security** (prompt injection, RAG, agents, output handling, AI for bug bounty) |
| **Review** | Spaced repetition flashcards (SM-2-style scheduling) |
| **Notes** | Templates that force observation / hypothesis / validation / takeaway |
| **Research** | Curated sources, vuln-type filters, Custom Tabs browser, share-to-app, daily writeup pulls |
| **Bug Bounty** | Programs, assets, findings workflow, Web/Recon/API/**LLM App** checklists, Markdown report copy |
| **AI** | Modes: Normal, Teacher, Socratic, Researcher, **AI Security Tutor**, **BB Copilot** + optional Council |
| **Notifications** | Research sync alerts + daily digest worker (Android 13+ needs notification permission) |

---

## Screenshots / demo flow (for portfolio)

Recommended 60-second walkthrough when sharing:

1. Open **Daily Brief** → show the four focus actions  
2. Open **AI Security → Prompt Injection** topic → flashcards  
3. Create a **Writeup Digest** note from a template  
4. Open **Research** → filter Bug Bounty / AI Security  
5. **Add Finding** from a writeup → fill status → **Copy Markdown Report**  
6. Switch AI to **BB Copilot** and show the “hypothesis only” banner  

---

## Architecture (high level)

```
app/
  data/           # Stores (JSON offline), Research pipeline, AI client, BB models
  learning/       # Curriculum, progress, quiz
  flashcards/     # SM-2 scheduling
  ui/             # Jetpack Compose screens (home, brief, research, bugbounty, ai, …)
```

**Principles**

- **Offline-first** local JSON stores (notes, research items, findings, programs, assets)  
- **Untrusted external content** — web/writeups treated as data; redaction + RAG sanitization boundaries  
- **No hardcoded secrets** — API keys in device vault  
- **Testable pure logic** — filters, report generator, templates covered by unit tests  
- **CI** — GitHub Actions builds debug APK and publishes releases  

---

## Tech stack

- Kotlin · Jetpack Compose · Material 3  
- WorkManager (research sync + daily digest)  
- Chrome Custom Tabs (not a full in-app WebView browser)  
- Optional OpenAI-compatible API for AI features  

---

## Build & run

**Requirements:** JDK 17, Android SDK 34, Android Studio or CI.

```bash
git clone https://github.com/Tawffik/My-app-.git
cd My-app-
./gradlew assembleDebug
```

Install the APK from `app/build/outputs/apk/debug/` or from **GitHub Releases**.

**AI (optional):** Settings → configure base URL, model, and API key (stored encrypted on device). Without a key, learning / research / BB features still work.

**Notifications (optional):** Grant notification permission on Android 13+ so research and daily brief alerts can appear.

---

## How to use it day to day

| Time | Action |
|------|--------|
| 5–10 min | **Daily Brief** + review due cards |
| 20–30 min | One curriculum topic (or AI Security path) |
| 10 min | Structured note from a template → extract flashcards if useful |
| 30+ min | Bug bounty: program/assets → findings → checklist → validated report |
| Anytime | Research refresh; save writeups; **Add Finding** when relevant |

**Rules that keep the project professional**

- AI suggestions = **hypotheses** until you reproduce them  
- Only test systems you are **authorized** to test  
- Do not paste secrets into AI chat  
- Prefer evidence in findings over narrative filler  

---

## Curriculum highlights

- Web Security Basics  
- Android Security topics  
- **AI Security** — LLM apps, prompt injection, RAG/vector risks, agents & excessive agency, improper output handling, using AI responsibly in bug bounty  

Aligned in spirit with industry awareness materials such as **OWASP GenAI / LLM Top 10** concepts (educational framing inside the app).

---

## Roadmap (honest)

**Done (foundation → operational loop)**  
Stabilize · Research foundation · Secure browser/share · BB workspace MVP · Notifications MVP · Daily Brief · Note templates · AI Security track · AI Tutor / BB Copilot  

**Next (portfolio polish / depth)**  
- Primary navigation simplification (fewer bottom tabs)  
- AI Report Reviewer on validated findings  
- Stronger backup/restore coverage for all BB/research stores  
- Deeper recommendations (what to study next from your weak areas)  

---

## Security & ethics

CyberOS is for **education and authorized security research**.  
Do not use it to attack systems without permission.  
AI features are designed to refuse inventing CVEs/sources and to stress human verification for bounty submissions.

---

## Author

Built as a personal engineering + security practice project — suitable to demonstrate:

- Android / Kotlin product structure  
- Security-minded UX (hypothesis vs confirmed, untrusted content)  
- Domain depth (web, Android, AI/LLM risks, bug bounty workflow)  

Repository: [github.com/Tawffik/My-app-](https://github.com/Tawffik/My-app-)

---

## License

Personal / educational project. Add an explicit license file if you open the repo publicly for wider contribution.
