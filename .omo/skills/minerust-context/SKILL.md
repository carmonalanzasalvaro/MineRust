# MineRust Context Skill

## Trigger
This skill activates automatically when working in `/home/alvaro/MineRust` or any subdirectory thereof.

## Mandatory Checklist (DO NOT SKIP)

Before implementing ANY change, you MUST complete these steps in order:

1. **Read CHANGELOG.md** — Understand what has been done recently and what state the project is in.
2. **Read ROADMAP.md** — Know which iteration is current and what features are planned.
3. **Read README.md** — Understand build commands, design constraints, and project structure.
4. **Check current iteration** — Only work on features belonging to the current iteration. Do NOT jump ahead.

## After Every Change (MANDATORY)

Before declaring ANY task complete:

1. **Update CHANGELOG.md** — Add entry under `[Unreleased]` with:
   - What was changed (Added, Fixed, Changed)
   - Brief description of the feature/fix
   - Any breaking changes or notes
2. **Run `./gradlew build --no-daemon`** — Must pass BUILD SUCCESSFUL.
3. **Commit to git** — `git add -A && git commit -m "type: description" && git push origin main`
4. **Verify CHANGELOG was committed** — Check that your entry is in the commit.

## Design Constraints (NEVER VIOLATE)

- No economy (no coins, no complex trading)
- No clans/teams complex (manual UUID auth only)
- No prefabs (don't convert vanilla blocks)
- No inventory changes (keep vanilla inventory)
- No custom dimensions/worldgen
- Server-side logic only (no `net.minecraft.client` imports in common code)
- No `TODO/FIXME/HACK/xxx` in production Java
- Always verify with `./gradlew build --no-daemon`

## Current Iteration

Check ROADMAP.md section headers to know which iteration is active.

## Repo

https://github.com/carmonalanzasalvaro/MineRust
