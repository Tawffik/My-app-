# CyberOS — User Guide

Short guide for daily use and for demoing the app in interviews.

## First launch

1. Install the latest APK from **GitHub Releases**.  
2. Open the app → **Home**.  
3. (Optional) **Settings → AI** — set API base URL, model, and key.  
4. (Optional) Allow **notifications** when the system asks (Android 13+).  

Learning, notes, research storage, and bug bounty modules work **without** AI.

## Daily Brief

**Home → Open Brief**

Use it as a checklist, not a dashboard to ignore:

1. Review due flashcards  
2. Continue the next incomplete topic  
3. Open findings that still need attention  
4. Run an **AI Security** tutor session when you are on that track  

Then skim **Recent writeups** / **AI Security pulse** and open Research if something matters.

## Learning

**Learn** tab → pick a path → open a topic.

Each topic is structured as:

- Sections (concept → why → hands-on → takeaways)  
- Flashcards  
- Quiz  

**AI Security path** (recommended modern track):

- LLM Application Basics  
- Prompt Injection  
- RAG & Vector Security  
- Agents, Tools & Excessive Agency  
- Improper Output Handling  
- Using AI in Bug Bounty  

Mark topics complete as you finish; progress feeds streak/XP and Daily Brief.

## Notes (structured)

**Notes** tab → choose a **template** chip (do not start from a blank page when learning):

| Template | Use when |
|----------|----------|
| Security Note | Any technical observation |
| Writeup Digest | After reading a public writeup |
| Lab Session | After a lab/challenge |
| AI Security Note | LLM/RAG/agent notes |
| Bug Bounty Session | End of a hunting session |

Always separate **Observation (facts)** from **Hypothesis (unverified)**.

From a note you can:

- Ask AI to analyze (key required)  
- Generate flashcards  

## Research

**Research** tab:

- Pull feeds (manual refresh + background sync about every 6 hours)  
- Filter by category (Bug Bounty, AI Security, …) and vulnerability type when relevant  
- Open items in **Chrome Custom Tabs**  
- Share text/links into CyberOS from other apps when configured  

From an item detail: **Add Finding** links the writeup into the bug bounty workspace.

## Bug Bounty workspace

**Home → Bug Bounty Workspace**

1. **Programs** — add the program you are authorized to test  
2. **Assets** — domains/URLs/APIs in scope  
3. **Findings** — status workflow from Idea → … → Submitted  
4. **Checklists** — Web / Recon / API / **LLM App**  
5. On a finding: **Copy Markdown Report** after you have real evidence  

Never paste production secrets into notes or AI chat.

## AI modes

**AI** tab — mode chips:

| Mode | Intent |
|------|--------|
| Normal | General help |
| Teacher | Step-by-step teaching |
| Socratic | Questions first |
| Researcher | Structured analysis |
| **AI Security** | Teach LLM risks (authorized learning) |
| **BB Copilot** | Bounty hypotheses + validation reminders |

Banners under AI Security / BB Copilot exist on purpose: **no silent “confirmed vulnerability” claims**.

## Interview demo script (2–3 minutes)

1. Show **Daily Brief** and explain the learn→research→apply loop.  
2. Open **AI Security → Prompt Injection** and one quiz question.  
3. Show a **template note** with hypothesis clearly labeled.  
4. Show **Research** filters and a writeup.  
5. Show a **Finding** + Markdown report.  
6. Show **BB Copilot** mode and say: “AI drafts; I verify before submit.”  

That narrative matches how strong security engineering teams actually work.
