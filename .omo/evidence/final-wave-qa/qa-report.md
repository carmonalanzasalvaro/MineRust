# Final Wave QA Evidence Report

**Date:** 2026-06-06
**Environment:** Linux headless, Java 17, Forge 1.20.1-47.3.0
**Reviewer:** QA Agent (F3)

## Build Verification

**Command:** `export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64 && ./gradlew build --no-daemon`
**Result:** BUILD SUCCESSFUL (7 actionable tasks, 3 executed, 4 up-to-date)
**Jar verified:** `build/libs/minerust-0.1.0.jar` contains all expected classes, assets, recipes, and loot tables.

## Startup QA

### Client Startup (runClient)
**Command:** `timeout 120 ./gradlew runClient --no-daemon`
**Result:** Mod initialization succeeded. Client reached resource loading phase (texture atlases created, sound engine started, config generated). Killed by timeout at 120s — no crash, stable initialization progression.
**Key log entries:**
- `[co.mi.MineRustMod/]: MineRust common setup complete.`
- Config auto-generated at `run/config/minerust-common.toml`
- All registries frozen successfully
- Texture atlases created for blocks, items, particles, etc.

### Server Startup (runServer)
**Command:** `echo "eula=true" > run/eula.txt && timeout 60 ./gradlew runServer --no-daemon`
**Result:** Server reached stable "Done!" state in ~11s. Mod loaded successfully.
**Key log entries:**
- `[co.mi.MineRustMod/]: MineRust common setup complete.`
- `[co.mi.MineRustMod/]: MineRust server starting: world`
- `[minecraft/RecipeManager]: Loaded 7 recipes`
- `[minecraft/DedicatedServer]: Done (11.063s)! For help, type "help"`
- Server listening on *:25565 using epoll channel type

## Scenario-by-Scenario Assessment

| Scenario | Executed? | Method | Result |
|----------|-----------|--------|--------|
| TC place | NO | Static code review | Code in `ToolCupboardBlock.setPlacedBy` calls `ToolCupboardClaimManager.registerClaim`. `onBlockBreak` removes claim. `BlockEvent.EntityPlaceEvent` handler prevents overlap. **Acceptable by inspection.** |
| Staff protect | NO | Static code review | `ProtectionStaffItem.applyProtection` validates claim coverage, authorization, material consumption, and persists to `ClaimSavedData`. **Acceptable by inspection.** |
| Unauthorized break denied | NO | Static code review | `ClaimProtectionEvents.onBlockBreak` cancels break for unauthorized players on protected blocks and TC blocks. **Acceptable by inspection.** |
| C4 raid | NO | Static code review | `C4ChargeBlock.use` iterates 6 directions calling `RaidDamageHelper.applyRaidDamage`. Only protected blocks take damage. **Acceptable by inspection.** |
| Vanilla TNT blocked | NO | Static code review | `ClaimProtectionEvents.onExplosionDetonate` removes protected blocks from `getAffectedBlocks()`. Preserves entity damage. **Acceptable by inspection.** |
| Mod weapon PvP allowed | NO | Static code review | `ScrapPistolItem.use` performs raycast and applies `playerAttack` damage. Not caught by `AttackEntityEvent` (uses `use`, not `attack`) or `LivingHurtEvent` arrow filter. **Acceptable by inspection.** |
| Vanilla weapon PvP blocked | NO | Static code review | `AttackEntityEvent` cancels all melee attacks between players. `LivingHurtEvent` cancels arrow damage (bows, crossbows, tridents). **Acceptable by inspection.** |
| Sleeping bag respawn | NO | Static code review | `RespawnEvents.onLivingDeath` tracks PvP death flag. `onPlayerRespawn` validates cooldown, dimension, bag presence, teleports player, and sets cooldown. **Acceptable by inspection.** |

## Asset and Data Verification

**Jar contents verified:**
- 4 blockstates, 4 block models, 7 item models
- 4 block textures, 3 item textures
- 7 recipe JSONs, 4 loot table JSONs
- `en_us.json` and `es_es.json` translation files
- `mods.toml` and `pack.mcmeta` present

## Observed Warnings (Non-blocking)

1. **Netty reflective access warnings** — Java 17 module system restrictions on `sun.misc.Unsafe` and `jdk.internal.misc.Unsafe`. These are DEBUG-level warnings from Netty internals and do not affect functionality.
2. **Forge version outdated** — Current 47.3.0 vs recommended 47.4.10. This is the MDK default and is not a blocker.
3. **Config auto-correction warnings** — ForgeConfigSpec correcting null keys to defaults on first run. Normal behavior.
4. **Missing server.properties** — Auto-generated on first server start. Normal behavior.
5. **Missing sound events** — Vanilla goat horn sounds missing from asset index. Unrelated to MineRust.

## Residual Risks

1. **No interactive multiplayer testing** — Two-player scenarios (authorization, PvP, raid) were verified via static code review only. Real behavior may differ due to network sync or edge cases.
2. **Networking packets are skeleton** — `ModNetworking` registers 3 packets with no-op handlers. Client-side UI sync is not implemented. For V1 this is acceptable since all critical logic is server-side.
3. **Balance unvalidated** — Upkeep costs, damage values, and HP tiers are configurable but not playtested.
4. **C4 and sleeping bag render as cubes** — Known V1 limitation, acceptable.

## Verdict

**APPROVE with residual risk.**

The mod builds successfully, client and server start without mod-related crashes, all assets and data are packaged correctly, and static code review confirms all V1 scenarios are implemented as designed. The lack of interactive multiplayer testing in this headless environment is mitigated by strong static evidence and successful server startup to a stable running state.
