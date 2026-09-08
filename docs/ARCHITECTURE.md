# CyberOS — Architecture Notes

## Modules (logical)

| Package | Responsibility |
|---------|----------------|
| `data` | Persistence, research pipeline, AI client, bug bounty models, notifications |
| `learning` | Curriculum graph, progress, quiz state |
| `flashcards` | Card store + due scheduling |
| `ui.*` | Compose UI by feature area |

## Persistence

Offline-first JSON files under app private storage, for example:

- notes, tasks, projects  
- research items / sources  
- bug bounty programs, findings, assets  
- checklist progress (shared preferences)  

Suitable for a personal OS scale; Room can be introduced later if item counts grow large.

## Research pipeline

1. `ResearchSourceStore` seeds Tier-1/Tier-2 sources (merge-on-upgrade by URL)  
2. `ResearchFetcher` pulls RSS/Atom or Markdown indexes  
3. Categorization + optional vulnerability typing (`BugBountyFilters`)  
4. `ResearchSyncWorker` (WorkManager, ~6h, network required)  
5. UI filters + detail + Custom Tabs  

## AI boundary

- API key in `ApiKeyVault` (device-side)  
- Agent system prompts in `Agents` (tutor / copilot / council / card gen)  
- Chat modes in `ChatMode`  
- Untrusted note/web context expected to be wrapped/sanitized before model use  
- Outputs treated as **assistive**, not authoritative findings  

## Bug bounty domain

```
Program → Assets
       → Findings (status, severity, evidence fields, linked research ids)
Checklists (Web / Recon / API / LLM App)
Report = Markdown generator from finding fields
```

## Background work

- `ResearchSyncWorker` — feed refresh + optional notification  
- `DailyDigestWorker` — 24h local briefing notification  

## CI

GitHub Actions: test + assemble + release APK on `main`.
