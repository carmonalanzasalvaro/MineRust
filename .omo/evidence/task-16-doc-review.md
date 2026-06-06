# Task 16: Doc Review and Continuation Guide

## Files Reviewed

- `README.md` — rewrote from generic starter text to full V1 documentation.
- `.omo/plans/minerust-master-plan.md` — verified tasks 1-15 marked complete; task 16 acceptance criteria used as guide.
- `.omo/notepads/minerust-master-plan/decisions.md` — verified all design decisions remain valid for V1.
- `.omo/notepads/minerust-master-plan/learnings.md` — appended Task 16 entry.
- `.omo/notepads/minerust-master-plan/issues.md` — left intact; historical fire bypass issues are resolved.
- `.omo/notepads/minerust-master-plan/problems.md` — replaced stale content with current known limitations and priority list.

## Files Changed

- `README.md`
- `.omo/notepads/minerust-master-plan/learnings.md`
- `.omo/notepads/minerust-master-plan/problems.md`
- `.omo/evidence/task-16-doc-review.md` (this file)

## Build Result

- `./gradlew build --no-daemon` — BUILD SUCCESSFUL (documentation-only changes, no Java edits).

## How a New Agent Should Continue

1. **Read `README.md`** first. It contains the current feature list, exact build/run commands, manual QA checklist, known limitations, and design constraints.
2. **Check `.omo/plans/minerust-master-plan.md`** to confirm which tasks are done and what the Final Wave requires.
3. **Review notepads** in `.omo/notepads/minerust-master-plan/` before asking about historical decisions or problems.
4. **Run `./gradlew build --no-daemon`** after any code change.
5. **Do not modify design rules** (no economy, no clans, no prefabs, no vanilla inventory changes, no dimensions) without explicit approval.
6. **Do not add TODO/FIXME/HACK/xxx markers** in production Java; document future work in notepads.
7. **Next immediate step**: Final Wave F1-F4 (plan compliance, code quality, real manual QA, scope fidelity). Task 16 blocks Final Wave; now that Task 16 is complete, the orchestrator can launch F1-F4.
