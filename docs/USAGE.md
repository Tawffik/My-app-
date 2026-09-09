# CyberOS — User guide

For daily practice and for demoing the app when sharing the repo or interviewing.

## Install

1. Download the latest APK from [GitHub Releases](https://github.com/Tawffik/My-app-/releases), or build with `./gradlew assembleDebug`.
2. Open the app → **Home / Daily Brief**.
3. Optional: **Settings → AI** (base URL, model, API key).
4. Optional: allow **notifications** (Android 13+).

Learning, notes, research storage, and bug bounty work **without** AI.

## Daily Brief

1. Review due flashcards  
2. Continue the next incomplete topic  
3. Open findings that still need attention  
4. Skim research / AI security pulse  

## Learning

**Learn** tab → pick a path → open a topic.

Recommended tracks:

- New to web security → **Web Security BB Roadmap** then **Web Security Basics**  
- Access control deep dive → **Access Control & IDOR Tricks**  
- Modern surface → **AI Security**  

On each topic: read sections · open **sources** · **Create study note** · **Complete** · optional quiz + AI Tutor.

**Add my section** creates a personal path stored on device.

## Notes vault

| Action | How |
|--------|-----|
| Template | Chip row (MOC, Concept, Security, …) |
| Daily | **Daily** button |
| Graph | **Graph** — hubs, orphans, edges |
| Link notes | `[[Exact Title]]` in the body |
| Tags | `#tag` in body or Tags field |
| Folders | e.g. `Bug Bounty/IDOR` |
| Study | **Study** hides answer lines starting with `A:` |
| Missing link | Outgoing link → **Create** |
| Insert link | **Link note** picker |

Suggested structure: MOC note linking `[[IDOR]]`, `[[HPP]]`, concept notes.

## Research

Refresh → filter **Writeups** / **Tips** / **AI Security** → open in browser → **Add Finding** when relevant.

Curated items appear even before the first successful network sync.

## Bug Bounty

1. Add **Program** (authorized only)  
2. Add **Assets**  
3. **Findings** + status  
4. Checklists (including **LLM App**)  
5. **Copy Markdown Report**  
6. Optional **AI Report Review** — triage help, not proof  

## AI modes

| Mode | Use for |
|------|---------|
| AI Security | Learning LLM risks |
| BB Copilot | Hypothesis drafting + validation reminders |
| Teacher / Socratic | Study dialogue |
| Researcher | Structured analysis |

Never treat model output as a confirmed vulnerability.

## Demo script (2–3 minutes)

1. Daily Brief  
2. One learning topic + source link  
3. Note with `[[link]]` + Graph  
4. Research writeup  
5. Finding + Markdown report  
6. Say: **“AI drafts; I verify. Authorized targets only.”**
