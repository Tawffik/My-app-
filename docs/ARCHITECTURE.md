# CyberOS — Architecture

## Layout

| Area | Responsibility |
|------|----------------|
| `data/` | Persistence, research pipeline, AI client, bug bounty models, WikiLinks, notifications |
| `learning/` | Curriculum (`CyberCurriculum`, `LearningExtras`), custom paths, progress, quiz |
| `flashcards/` | Card store + SM-2-style scheduling |
| `ui/` | Compose UI by feature |

## Persistence

Offline-first JSON under app private storage: notes, research, bug bounty entities, checklists, custom learning paths, progress/XP.

## Notes model

`WikiLinks` extracts `[[targets]]` and `#tags`. Backlinks and graph edges are derived at runtime.

## Research pipeline

1. Seed/merge default sources  
2. Fetch RSS/Atom, MARKDOWN lists, or LINKLIST archives  
3. Categorize + vuln type + dedupe  
4. One-time curated writeup seed  
5. `ResearchSyncWorker` (~6h)  

## Learning model

```
CyberCurriculum.paths
  + LearningExtras (+ More topics)
  + CustomPathStore
= allPaths()
```

## AI boundary

Device-side API key vault. Modes stress hypothesis vs confirmed. Non-AI features work without a key.

## Bug bounty domain

Program → Assets → Findings; checklists; Markdown report from finding fields.

## CI

GitHub Actions on `main`: tests + assembleDebug + release APK when configured.
