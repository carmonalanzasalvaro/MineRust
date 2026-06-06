# MineRust Context Skill

## Trigger
This skill activates automatically when working in `/home/alvaro/MineRust` or any subdirectory thereof.

## Mandatory Checklist (DO NOT SKIP)

Before implementing ANY change, you MUST complete these steps in order:

1. **Read CHANGELOG.md** — Understand what has been done recently and what state the project is in.
2. **Read ROADMAP.md** — Know which iteration is current and what features are planned.
3. **Read README.md** — Understand build commands, design constraints, and project structure.
4. **Check current iteration** — Only work on features belonging to the current iteration. Do NOT jump ahead.

## Documentation Gate After Every Change (MANDATORY)

Before declaring ANY task complete, inspect the files changed in this turn and update every affected project document. This applies to **any** code, resource, config, balance, gameplay, UI, QA, or design change in `/home/alvaro/MineRust`.

1. **Update `CHANGELOG.md`** — Add entries under `[Unreleased]` with:
   - What was changed (Added, Fixed, Changed)
   - Brief description of the feature/fix
   - Any breaking changes or notes
   - QA status for the change when relevant
2. **Update `ROADMAP.md`** when the change affects project status, iteration scope, future work, design constraints, or QA gates.
3. **Update `README.md`** when the user-facing behavior, setup, commands, feature list, known limitations, or workflow changes.
4. **Update `CHECKLIST-V1.md`** when manual QA steps, expected messages, coverage numbers, or feature acceptance criteria change.
5. **Update `.omo/notepads/` or other planning notes** when the change records a decision future agents must remember.
6. **If no documentation file needs changes**, explicitly say why in the final answer.
7. **Run `./gradlew build --no-daemon`** after code/resource changes — Must pass BUILD SUCCESSFUL.

Never declare work complete with stale docs. Documentation updates are part of the implementation, not optional follow-up.

## Git Policy

- Do **not** commit, amend, or push unless the user explicitly requests it in the current conversation.
- If the user asks for a commit, include the documentation updates in the same commit as the code/resource changes.

## Design Constraints (NEVER VIOLATE)

- No economy (no coins, no complex trading)
- No clans/teams complex (manual UUID auth only)
- No prefabs (don't convert vanilla blocks)
- No inventory changes (keep vanilla inventory)
- No custom dimensions/worldgen
- Server-side logic only (no `net.minecraft.client` imports in common code)
- No `TODO/FIXME/HACK/xxx` in production Java
- Always verify with `./gradlew build --no-daemon`
- Always keep `CHANGELOG.md`, `ROADMAP.md`, `README.md`, and relevant checklists synchronized with gameplay/code changes.

## Current Iteration

Check ROADMAP.md section headers to know which iteration is active.

## Repo

https://github.com/carmonalanzasalvaro/MineRust
