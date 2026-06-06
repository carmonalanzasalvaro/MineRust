## 2026-06-06 Task: planning

- Current project is mostly Forge template: `MineRustMod.java`, example `Config.java`, resources, Gradle wrapper.
- Existing README already documents Minecraft/Forge versions and recommended Forge APIs.
- Build artifact exists at `build/libs/minerust-0.1.0.jar`, so initial build has worked previously.
- Plan parser expects top-level tasks as bare numeric checkboxes (`1.`, `2.`) and final wave as `F1.`, `F2.`.

## 2026-06-06 Task: 1. Setup DeferredRegister para Blocks, Items, BlockEntities

- Created `com.minerust.registry` package with `ModBlocks`, `ModItems`, and `ModBlockEntities`.
- `ModBlocks` registers placeholder blocks: `tool_cupboard_tier1`, `tool_cupboard_tier2`, `sleeping_bag`, `c4_charge`.
- `ModItems` registers block items for all blocks plus placeholder items: `protection_staff`, `raid_drill`, `scrap_pistol`.
- `ModBlockEntities` creates the `DeferredRegister` but keeps it empty (compile-safe) until real block entity classes exist.
- `MineRustMod` constructor now calls `init()` on all three registries with the mod event bus.
- Used `BlockBehaviour.Properties.copy()` as placeholder pattern for block properties.
- Build passed successfully after changes.

## 2026-06-06 Task: 2. Setup SimpleChannel Networking

- Created `com.minerust.networking` package with `ModNetworking` and `packet` subpackage.
- `ModNetworking` defines `SimpleChannel CHANNEL` using `NetworkRegistry.newSimpleChannel` with `ResourceLocation(MineRustMod.MODID, "main")` and protocol version `"1"`.
- Added `register()` method that registers 3 packet skeletons with auto-incrementing packet IDs.
- Packet skeletons: `SyncToolCupboardDataPacket`, `SyncBlockProtectionPacket`, `SyncPlayerCooldownsPacket`.
- Each packet has encode/decode/handle methods using `FriendlyByteBuf` and `Supplier<NetworkEvent.Context>`.
- All handlers use `enqueueWork` and `setPacketHandled(true)`; no `net.minecraft.client` imports.
- Build passed successfully after changes.

## 2026-06-06 Task: 3. Rewrite Config.java para MineRust

- Replaced Forge MDK template `Config.java` with MineRust-specific common config.
- Removed template noise: `LOG_DIRT_BLOCK`, `MAGIC_NUMBER`, `MAGIC_NUMBER_INTRODUCTION`, `ITEM_STRINGS`, and item validation.
- Added config values: `TC_TIER1_CHUNK_RADIUS` (default 0), `TC_TIER2_CHUNK_RADIUS` (default 1), upkeep costs per tier (STRAW/WOOD/STONE/METAL/HQ), `DECAY_INTERVAL_HOURS`, `C4_DAMAGE`, `DRILL_DAMAGE_PER_SECOND`, `DRILL_DURABILITY_COST`, `WEAPON_DAMAGE`, `WEAPON_COOLDOWN_TICKS`, `PVP_SLEEPING_BAG_COOLDOWN_SECONDS` (default 60), and `DIRECT_VANILLA_PVP_DAMAGE_FILTER`.
- Kept class name `Config`, package `com.minerust`, and `static final ForgeConfigSpec SPEC` for existing `MineRustMod` registration.
- Used `defineInRange` for all numeric entries and `define` for booleans; loaded into public static fields via `@SubscribeEvent static void onLoad(ModConfigEvent)`.
- Build passed successfully after changes.

## 2026-06-06 Task: 4. Setup LevelSavedData para persistencia server-side

- Created `com.minerust.data` package with `ClaimSavedData extends SavedData`.
- `ClaimSavedData` uses `DATA_NAME = "minerust_claims"` and attaches to Overworld via `ServerLevel#getDataStorage().computeIfAbsent(...)` for global cross-dimension persistence.
- Provides dual `get(...)` overloads: one from `ServerLevel` and one from `MinecraftServer` for convenience.
- Data structures:
  - `Map<String, Map<Long, ToolCupboardData>> claimsByDimension` keyed by dimension ResourceLocation string, then packed chunk coordinates.
  - `Map<String, Map<Long, ProtectedBlockData>> protectedBlocksByDimension` keyed by dimension, then packed `BlockPos.asLong()`.
  - `Map<UUID, PlayerCooldownData> playerCooldowns` keyed by player UUID.
- Implemented `save(CompoundTag)` and static `load(CompoundTag)` using `ListTag` of `CompoundTag` entries for each dimension sub-map.
- Nested data classes:
  - `ToolCupboardData`: owner UUID, tier (1/2), chunkX/chunkZ, dimension string, authorized player UUID set, placed time.
  - `ProtectedBlockData`: packed BlockPos, dimension, tier string (STRAW/WOOD/STONE/METAL/HQ), current/max health, placedBy UUID, placed time.
  - `PlayerCooldownData`: player UUID, sleeping bag cooldown end epoch millis, last death time; includes `isSleepingBagOnCooldown()` helper.
- All mutation methods (`addClaim`, `removeClaim`, `addProtectedBlock`, `removeProtectedBlock`, `setPlayerCooldown`, `removePlayerCooldown`, `clearAllData`) call `setDirty()` to ensure persistence.
- Kept compile-safe with no dependencies on future TC/blockhealth classes; tier is an int for TCs and a String for blocks.
- No `net.minecraft.client` imports; no edits to `Config.java` or `MineRustMod.java`.
- Build passed successfully after changes.

## 2026-06-06 Task: 5. Implementar Tool Cupboard Block + BlockEntity

- Created `com.minerust.claim.ToolCupboardBlock` extending `BaseEntityBlock` with a `tier` field (1 or 2).
- `setPlacedBy` sets the BlockEntity owner UUID automatically when placed by a player; setter is also exposed on the BlockEntity for manual assignment if needed.
- Created `com.minerust.claim.ToolCupboardBlockEntity` with fields: `UUID owner`, `Set<UUID> authorizedPlayers`, `int tier`, resource counters (`woodCount`, `stoneCount`, `metalCount`, `highQualityCount`).
- All mutable fields have getters and setters that call `setChanged()` to mark dirty.
- NBT persistence implemented via `saveAdditional(CompoundTag)` and `load(CompoundTag)` for all fields including authorized players list.
- Updated `ModBlocks` to instantiate `ToolCupboardBlock` for tier 1 and tier 2 instead of plain `Block`.
- Updated `ModBlockEntities` to register `TOOL_CUPBOARD` `BlockEntityType` using `BlockEntityType.Builder.of` referencing both TC blocks.
- Sleeping bag and C4 placeholders remain unchanged.
- Build passed successfully after changes.

## 2026-06-06 Task: 6. Implementar sistema de Upkeep y Decay

- Created `com.minerust.claim.ToolCupboardUpkeepManager` as a `@Mod.EventBusSubscriber` using `TickEvent.ServerTickEvent`.
- Added `tryConsume(int wood, int stone, int metal, int highQuality)` to `ToolCupboardBlockEntity` for atomic resource deduction.
- Added `tcPackedPos` (long) to `ClaimSavedData.ToolCupboardData` to locate the TC `BlockEntity` at runtime; updated serialization/deserialization.
- Upkeep manager checks every 1200 ticks (1 minute) but only runs upkeep logic when `System.currentTimeMillis()` exceeds `Config.DECAY_INTERVAL_HOURS` since the last tick.
- Upkeep cost per block tier maps directly to config values: STRAW/WOOD consume wood, STONE consumes stone, METAL consumes metal, HQ consumes highQuality resources.
- Protected blocks are grouped by chunk for efficient lookup; each TC iterates its claimed chunk radius and accumulates costs.
- If the TC `BlockEntity` is missing or `tryConsume` returns false, all affected protected blocks decay by one tier step (HQ -> METAL -> STONE -> WOOD -> STRAW).
- Blocks already at STRAW lose half their max health per decay tick; when health reaches 0 they are removed from `protectedBlocksByDimension` via `removeProtectedBlock`.
- All mutations to `ClaimSavedData` call `setDirty()` either directly or through existing helper methods.
- No overlap validation or authorization checks were added; those remain owned by task 7.
- No `net.minecraft.client` imports were introduced.
- Build passed successfully after changes.

## 2026-06-06 Task: 10. Implementar taladro de raid

- Created `com.minerust.raid.RaidDrillItem` extending `Item` with durability (default 256) and `useOn(UseOnContext)` override.
- `useOn` runs only on server side; calls `RaidDamageHelper.applyRaidDamage` with `Config.DRILL_DAMAGE_PER_SECOND`.
- If the target block is not protected, the method returns `FAIL` with a client message and does not consume durability.
- If damage is applied successfully, item durability is reduced by `Config.DRILL_DURABILITY_COST` via `ItemStack.hurt`.
- Updated `ModItems.RAID_DRILL` to instantiate `RaidDrillItem` instead of the plain `Item` placeholder.
- Added explicit exception in `ClaimProtectionEvents.onRightClickBlock` so holding `ModItems.RAID_DRILL` bypasses the unauthorized-interaction cancellation, similar to the existing C4 exception.
- C4 placement and right-click exceptions remain intact; no other interactions are allowed in enemy claims.
- No vanilla explosion, recipe, asset, or client-side code was introduced.
- Build passed successfully after changes.


## 2026-06-06 Task: 8. Implementar baston de proteccion y tiers por bloque

- Created `com.minerust.blockhealth.ProtectionTier` enum with values STRAW, WOOD, STONE, METAL, HQ.
- Each tier defines `maxHealth` (STRAW=50, WOOD=100, STONE=200, METAL=400, HQ=800) and a vanilla material item (wheat, oak_planks, cobblestone, iron_ingot, diamond).
- Added helpers `next()` to cycle tiers and `fromString(String)` for safe parsing with fallback to STRAW.
- Created `com.minerust.item.ProtectionStaffItem` extending `Item` with `useOn(UseOnContext)` override.
- Staff stores selected tier in the ItemStack NBT under key `SelectedTier`; defaults to STRAW.
- Shift-click (sneak) on any block cycles the staff tier and sends an action-bar message to the player.
- Normal click applies protection to the clicked block only when all validations pass:
  1. A Tool Cupboard claim must cover the block's chunk; otherwise fail with message.
  2. The player must be authorized (owner or in authorized set) in that claim; otherwise fail with message.
  3. The player must have one unit of the tier's required material in inventory, unless in creative mode; otherwise fail with message.
- On success, one material item is consumed (unless creative), and a `ProtectedBlockData` entry is created or overwritten in `ClaimSavedData` with the selected tier and full health.
- `ModItems.PROTECTION_STAFF` now instantiates `ProtectionStaffItem` instead of the plain `Item` placeholder.
- All logic is server-side only (`level.isClientSide` guard, `instanceof ServerLevel` cast); no `net.minecraft.client` imports.
- No C4, drill, explosion/fire/piston anti-bypass, UI screens, assets, recipes, or additional dependencies were added.
- Build passed successfully after changes.

## 2026-06-06 Task: 9. Implementar C4 como herramienta de raid

- Created `com.minerust.raid.C4ChargeBlock` extending `Block` with `use(...)` override for server-side activation.
- Created `com.minerust.raid.RaidDamageHelper` with reusable static `applyRaidDamage(ServerLevel, BlockPos, int, Player)` for task 10 drill.
- `C4ChargeBlock.use(...)` runs only on server (`!level.isClientSide`), iterates all 6 `Direction` values, and calls `RaidDamageHelper.applyRaidDamage` on each adjacent position.
- `RaidDamageHelper` looks up `ClaimSavedData` protected block data by dimension and packed `BlockPos`; if found, subtracts damage from `currentHealth`.
- If protected block health reaches 0 or below, the helper drops resources via `Block.dropResources(...)` with the responsible player, sets the block to `Blocks.AIR`, and calls `ClaimSavedData.removeProtectedBlock(...)`.
- If health remains above 0, `setCurrentHealth(newHealth)` is called and `ClaimSavedData.setDirty()` ensures persistence.
- C4 never affects unprotected blocks; only blocks with an explicit `ProtectedBlockData` entry take damage.
- After processing all 6 neighbors, the C4 block itself is removed via `level.removeBlock(pos, false)`.
- Player feedback is sent via `player.sendSystemMessage(Component.literal(...))` indicating whether any protected blocks were damaged.
- `ModBlocks.C4_CHARGE` now instantiates `C4ChargeBlock` instead of the plain `Block` placeholder.
- No vanilla explosion is used; block destruction is done manually to bypass claim-protection event cancellation.
- No `net.minecraft.client` imports were introduced.
- Build passed successfully after changes.

## 2026-06-06 Task: 11. Implementar arma Rust básica del mod

- Created `com.minerust.combat.ScrapPistolItem` extending `Item` with `use(Level, Player, InteractionHand)` override.
- Server-side raycast finds the first entity intersecting the player's look vector up to 20 blocks, respecting block collision via `Level#clip`.
- Damages the closest valid entity using `level.damageSources().playerAttack(player)` with `Config.WEAPON_DAMAGE` as a float.
- Applies item cooldown via `player.getCooldowns().addCooldown(this, Config.WEAPON_COOLDOWN_TICKS)`.
- Returns clear feedback via action-bar message when no entity is hit.
- Updated `ModItems.SCRAP_PISTOL` to instantiate `ScrapPistolItem` instead of the plain `Item` placeholder.
- Kept logic common/server-safe: no `net.minecraft.client` imports, no projectiles, no ammo/reload system, no asset changes.
- Build passed successfully after changes.

## 2026-06-06 Task: 12. Implementar sleeping bag y respawn PvP

- Created `com.minerust.respawn.SleepingBagBlock` extending `Block`.
- `setPlacedBy` stores the placing player's sleeping bag dimension and `BlockPos` (packed) in `ClaimSavedData.PlayerCooldownData`, overwriting any previous bag location.
- Updated `ModBlocks.SLEEPING_BAG` to instantiate `SleepingBagBlock` instead of the plain `Block` placeholder.
- Extended `ClaimSavedData.PlayerCooldownData` with `sleepingBagDimension` (String), `sleepingBagPos` (long), and `lastDeathWasPvp` (boolean); updated `serialize`/`deserialize` for persistence.
- Created `com.minerust.events.RespawnEvents` as `@Mod.EventBusSubscriber`.
- `onLivingDeath(LivingDeathEvent)`: if the dying entity is a `ServerPlayer`, sets `lastDeathWasPvp` to `true` when `event.getSource().getEntity()` is another `ServerPlayer`; otherwise sets it to `false`. Persists via `ClaimSavedData`.
- `onPlayerRespawn(PlayerEvent.PlayerRespawnEvent)`: if `lastDeathWasPvp` is true and a sleeping bag is recorded, validates the target dimension/level, checks the block is still present with `ModBlocks.SLEEPING_BAG.get()`, and teleports the player to the bag position. Then sets the sleeping bag cooldown end to `now + Config.PVP_SLEEPING_BAG_COOLDOWN_SECONDS * 1000L` and clears the PvP death flag.
- If the sleeping bag is missing, the dimension is unloaded, or the block is no longer a sleeping bag, the handler falls back silently to vanilla respawn.
- PvE deaths leave `lastDeathWasPvp` as `false`, so vanilla bed/world-spawn behavior remains untouched.
- No sleeping bag destruction on use, no inventory changes, no client-side imports, no TODO/FIXME/HACK markers.
- Build passed successfully after changes.

## 2026-06-06 Task: 13. Implementar filtro PvP vanilla y anti-bypass de bloques protegidos

- Modified `ClaimProtectionEvents` to add six new event handlers for PvP filter and block bypass protection.
- PvP filter (`Config.DIRECT_VANILLA_PVP_DAMAGE_FILTER`):
  - `AttackEntityEvent` cancels all direct vanilla melee attacks between players. ScrapPistol uses `use()` (right-click), not `attack()`, so it bypasses this event naturally.
  - `LivingHurtEvent` cancels projectile damage between players by checking `directEntity instanceof AbstractArrow`, which covers bows, crossbows, and thrown tridents. ScrapPistol uses `playerAttack` source with player as direct entity, so it is not caught.
  - Indirect environmental damage (lava, TNT explosions, fall damage, potions) is NOT cancelled because it does not match the melee or arrow checks.
- Block bypass protection:
  - `ExplosionEvent.Detonate` removes protected block positions from `getAffectedBlocks()` instead of cancelling the whole explosion, preserving entity damage.
  - `PistonEvent.Pre` uses `PistonStructureResolver` to check `getToPush()` and `getToDestroy()`; cancels the piston if any moved/destroyed block is protected.
  - `BlockEvent.FluidPlaceBlockEvent` cancels when the target position is a protected block, preventing lava/water from creating cobblestone/obsidian/fire at protected positions.
  - `BlockEvent.EntityPlaceEvent` fire check added before the player authorization check: blocks placement of `Blocks.FIRE` and `Blocks.SOUL_FIRE` directly onto protected blocks, even by authorized players or non-player entities (dispensers, etc.).
- Preserved all existing behavior: C4 placement/right-click exceptions, raid drill right-click exception, Tool Cupboard authorization, overlap checks, and break claim removal.
- No changes outside `src/main/java/com/minerust/events/ClaimProtectionEvents.java`; no client imports; no TODO/FIXME/HACK markers.
- Build passed successfully after changes.


## 2026-06-06 Task: 13. Fire bypass verification fix

- Added `TickEvent.ServerTickEvent` handler `onServerTick` in `ClaimProtectionEvents` to mitigate the residual fire bypass.
- Scanner runs every 20 ticks (1 second) on server phase END.
- Iterates all dimensions with protected blocks via `ClaimSavedData.getProtectedBlocksByDimension()`.
- For each protected block position, checks the block itself and all 6 adjacent positions for `Blocks.FIRE` or `Blocks.SOUL_FIRE`.
- Any fire found is immediately replaced with `Blocks.AIR` via `level.setBlock(...)`.
- This prevents natural fire spread and adjacent existing fire from destroying protected flammable blocks, since Forge 1.20.1 does not fire a cancellable event for `FireBlock.tick()` block destruction.
- Combined with existing `BlockEvent.EntityPlaceEvent` fire placement block and `BlockEvent.FluidPlaceBlockEvent` lava-fire block, protected blocks are now fully covered against fire/lava destruction.
- No changes to `ClaimSavedData` schema; scanner is event-side only.
- Build passed successfully after fix.

## 2026-06-06 Task: 14. Crear assets completos para items/bloques del mod

- Assets directory was completely empty before this task; no existing textures, models, blockstates, or sounds.
- Identified 4 blocks needing assets: `tool_cupboard_tier1`, `tool_cupboard_tier2`, `sleeping_bag`, `c4_charge`.
- Identified 7 items needing assets: 4 block-items (same registry names as blocks) + 3 standalone items (`protection_staff`, `raid_drill`, `scrap_pistol`).
- Created 7 original 16x16 PNG textures via Python/Pillow procedural pixel-art generation:
  - Block textures: `tool_cupboard_tier1.png` (wood + rusty metal cabinet with lock), `tool_cupboard_tier2.png` (reinforced dark metal with bolts), `sleeping_bag.png` (olive drab rolled fabric with straps), `c4_charge.png` (red explosive brick with tape, blinking light, wires).
  - Item textures: `protection_staff.png` (wooden staff with glowing teal gem), `raid_drill.png` (hand drill with orange accent), `scrap_pistol.png` (crude scrap-metal pistol with wood grip).
- All textures are original procedural art with no third-party assets; random seed fixed at 42 for reproducibility.
- Created 4 blockstate JSON files in `blockstates/`, each with a single default variant pointing to the corresponding block model.
- Created 4 block model JSON files in `models/block/`, all using `minecraft:block/cube_all` parent with the mod texture.
- Created 7 item model JSON files in `models/item/`:
  - 4 block-items parent to their respective block models.
  - `protection_staff` uses `minecraft:item/generated` parent.
  - `raid_drill` and `scrap_pistol` use `minecraft:item/handheld` parent for proper in-hand rotation.
- No `sounds.json` was created because none of the Java item/block classes reference custom sound events; all feedback is via chat messages.
- Build verification: `./gradlew build --no-daemon` passed successfully (7 actionable tasks, BUILD SUCCESSFUL).

## 2026-06-06 Task: 15. Crear launcher `server.sh` para el servidor local

- Added `server.sh` at the project root so `./server.sh` resolves its own directory, `cd`s there, and runs `./gradlew runServer --no-daemon`.
- This launcher is executable and can be started from any current working directory because it anchors itself to `${BASH_SOURCE[0]}`.

## 2026-06-06 Task: 15. Implementar claim_debug_stick de desarrollo

- Added `com.minerust.item.ClaimDebugStickItem` as a server-side-only dev tool for claim inspection.
- Right-clicking in air shows the current chunk claim details when present, including owner UUID, tier, TC center chunk, current chunk, and covered min/max chunks derived from the same radius config used by `ToolCupboardClaimManager`.
- If the current chunk is unclaimed, the tool reports that no Tool Cupboard claim covers the chunk.
- On every use it scans a 5-chunk radius around the player and draws claimed chunk borders with server-visible `END_ROD` particles sampled every 2 blocks at `playerY + 1`.
- Registered `minerust:claim_debug_stick` in `ModItems`, added it to the MineRust creative tab, and created the generated stick-based item model plus EN/ES names.
- The intended dev command is `/give @s minerust:claim_debug_stick`.
- Verified assets are packaged in `build/libs/minerust-0.1.0.jar` under `assets/minerust/`: 4 blockstates, 4 block models, 7 item models, 4 block textures, 3 item textures.
- Visual QA note: in-game rendering cannot be verified without `runClient`; sleeping bag and C4 render as full cubes which is acceptable for V1. Future polish could make sleeping bag flat (like carpet) and C4 smaller (like a button/plate) via custom element models.

## 2026-06-06 Task: 15. Anadir recetas, loot, creative tab y balance inicial

- Created 7 recipe JSON files under `src/main/resources/data/minerust/recipes/` using vanilla items only:
  - `tool_cupboard_tier1`: shaped recipe with planks, iron ingot, and chest (conservative claim starter).
  - `tool_cupboard_tier2`: shaped recipe with diamonds, iron ingots, and tier 1 cupboard (expensive upgrade).
  - `sleeping_bag`: shaped recipe with wool and string (simpler than a bed).
  - `c4_charge`: shaped recipe with iron ingots, redstone, gunpowder, and TNT (raid explosive).
  - `protection_staff`: shaped recipe with diamond, emerald, and stick (magical tool).
  - `raid_drill`: shaped recipe with iron pickaxe, iron ingots, redstone, and stick (raid tool).
  - `scrap_pistol`: shaped recipe with iron ingots, redstone, and planks (crude weapon).
- Created 4 block loot table JSON files under `src/main/resources/data/minerust/loot_tables/blocks/`:
  - All 4 blocks (`tool_cupboard_tier1`, `tool_cupboard_tier2`, `sleeping_bag`, `c4_charge`) drop their item form with `minecraft:survives_explosion` condition.
  - Standalone items (`protection_staff`, `raid_drill`, `scrap_pistol`) do not need loot tables.
- Created `com.minerust.registry.ModCreativeTabs` with `DeferredRegister<CreativeModeTab>` using `Registries.CREATIVE_MODE_TAB`.
  - Tab key: `minerust_tab`, title translation key: `itemGroup.minerust`.
  - Icon: `scrap_pistol` (represents the PvP focus of the mod).
  - Display order: tool cupboards tier 1/2, sleeping bag, C4 charge, protection staff, raid drill, scrap pistol.
- Added `ModCreativeTabs.init(modEventBus)` call in `MineRustMod` constructor alongside existing registry inits.
- Created `en_us.json` and `es_es.json` under `src/main/resources/assets/minerust/lang/`:
  - Entries for all 4 blocks, all 7 items, and the creative tab group key.
  - Translation keys match registry names exactly to avoid raw translation keys in-game.
- Conservative balance encoded through recipes:
  - Tier 2 cupboard requires tier 1 + diamonds (significant investment for +1 chunk radius).
  - C4 requires TNT + gunpowder + iron (expensive raid tool).
  - Raid drill requires an iron pickaxe base (mid-tier tool).
  - Scrap pistol requires only iron + wood + redstone (entry-level weapon).
  - No new ores, economy, or configurable recipe values introduced for V1.
- Build verification: `./gradlew build --no-daemon` passed successfully (7 actionable tasks, BUILD SUCCESSFUL).

## 2026-06-06 Task: protection staff / raid drill visibility

- Added a server-side player-tick overlay in `ClaimProtectionEvents` that runs every 10 ticks and only scans protected blocks within ~16 blocks in the same dimension.
- While holding `minerust:protection_staff`, the server sends small green dust particles around protected blocks placed by the player (`placedBy == player UUID`).
- While holding `minerust:raid_drill`, the server sends small red dust particles around protected blocks placed by other players (`placedBy != player UUID`).
- The overlay is intentionally server-visible and lightweight, so it works without any client renderer or packet layer.
- QA follow-up: recipe validation in crafting table requires `runClient` or dedicated server testing; no compile-time validation exists for recipe JSON correctness beyond schema.

## 2026-06-06 Task: 16. QA integral y documentación procedural para agentes

- Rewrote `README.md` from generic starter text to full MineRust V1 usage and agent workflow documentation.
  - Sections added: overview, design constraints, feature list, gameplay loop, build/run commands, exact project structure, manual QA checklist, known limitations, future-agent workflow.
- Updated `.omo/notepads/minerust-master-plan/problems.md` replacing stale "No blockers remaining for planning" with current known limitations and priority list.
- Appended Task 16 section to `.omo/notepads/minerust-master-plan/learnings.md` (this entry).
- Created `.omo/evidence/task-16-doc-review.md` with concise doc review and continuation guide for future agents.
- Build verification after documentation-only changes: `./gradlew build --no-daemon` passed successfully (no Java changes, expected pass).

## 2026-06-06 Task: F1 blocker fix - Sleeping bag cooldown enforcement

- Added cooldown gate in `RespawnEvents.onPlayerRespawn` before resolving the sleeping bag teleport.
- If `cooldownData.isSleepingBagOnCooldown()` returns true, `lastDeathWasPvp` is cleared, persisted, and the handler returns early so vanilla respawn remains in effect.
- If not on cooldown, existing validation chain (dimension, level load, block presence) executes unchanged; successful teleport sets `sleepingBagCooldownEnd` to `System.currentTimeMillis() + Config.PVP_SLEEPING_BAG_COOLDOWN_SECONDS * 1000L`.
- Preserved PvE behavior (returns early when `lastDeathWasPvp` is false), bag non-destruction, and server-side-only execution.
- Only file modified: `src/main/java/com/minerust/events/RespawnEvents.java`.
- Build verification: `./gradlew build --no-daemon` passed successfully (7 actionable tasks, BUILD SUCCESSFUL).

## 2026-06-06 Task: F1 blocker fix - Manual Tool Cupboard authorization path

- Implemented vanilla interaction path for TC owner to authorize nearby players without custom UI screens.
- Interaction: TC owner sneak-right-clicks the TC block while an unauthorized player is within 5 blocks.
- The closest unauthorized player is selected and their UUID is added to both the BlockEntity authorized set and the ClaimSavedData claim entries via `ToolCupboardClaimManager.registerClaim`.
- Non-owner interactions are rejected with an action-bar message and do not weaken claim protection.
- Unauthorized players interacting with a TC receive an action-bar denial message via `ClaimProtectionEvents.onRightClickBlock`.
- Files modified: `src/main/java/com/minerust/claim/ToolCupboardBlock.java` (added `use()` override), `src/main/java/com/minerust/events/ClaimProtectionEvents.java` (added unauthorized feedback).
- Existing `ToolCupboardBlockEntity.addAuthorizedPlayer(UUID)` and `ToolCupboardClaimManager.registerClaim(ServerLevel, BlockPos, ToolCupboardBlockEntity)` were sufficient for data sync; no changes needed to BlockEntity or ClaimManager.
- Build verification: `./gradlew build --no-daemon` passed successfully (7 actionable tasks, BUILD SUCCESSFUL).

## 2026-06-06 Task: F1 blocker fix follow-up - Repair `onBlockBreak` regression

- Regression introduced in previous authorization fix: `ClaimProtectionEvents.onBlockBreak` canceled unauthorized TC breaks but then still called `ToolCupboardClaimManager.removeClaim` due to missing `return` after the cancellation.
- Fix: restored clean branch flow inside the `ToolCupboardBlock` handler:
  1. Unauthorized → show action-bar message, `event.setCanceled(true)`, `return`.
  2. Authorized → `removeClaim(...)` and `return`.
  3. Non-TC blocks → generic `isAuthorized` check.
- Removed redundant nested `level.getBlockState(pos).getBlock() instanceof ToolCupboardBlock` check.
- Only file modified: `src/main/java/com/minerust/events/ClaimProtectionEvents.java`.
- Build verification: `./gradlew build --no-daemon` passed successfully (7 actionable tasks, BUILD SUCCESSFUL).

## 2026-06-06 Task: F1 blocker fix - RaidDrillItem held/progressive behavior

- Replaced one-click immediate damage in `RaidDrillItem` with held-use progressive damage.
- `useOn` now validates the target block is protected via `ClaimSavedData` before starting use; unprotected blocks return FAIL with feedback and consume no durability.
- On a protected block, `useOn` stores the target `BlockPos` and current game time in the `ItemStack` NBT, then calls `player.startUsingItem(hand)`.
- Overrode `getUseDuration` to return 72000 ticks (same as bows) for indefinite holding.
- Overrode `onUseTick` to apply `RaidDamageHelper.applyRaidDamage` with `Config.DRILL_DAMAGE_PER_SECOND` once every 20 ticks (1 second).
- Durability cost `Config.DRILL_DURABILITY_COST` is consumed per damage tick via `ItemStack.hurt`.
- Safe stop conditions: block no longer protected/loaded, `applyRaidDamage` returns false, stack breaks (`isEmpty`), or player stops use.
- Overrode `releaseUsing` to clean up NBT tags (`DrillPosX/Y/Z`, `DrillLastTick`) when use ends.
- Preserved existing `ClaimProtectionEvents` drill bypass by keeping the same `RaidDrillItem` class and `ModItems.RAID_DRILL` registry.
- No changes to `RaidDamageHelper`, no client imports, no energy systems or additional dependencies.
- Build verification: `./gradlew build --no-daemon` passed successfully (7 actionable tasks, BUILD SUCCESSFUL).

## 2026-06-06 Task: two client launcher scripts

- Added `player1.sh` and `player2.sh` in the project root to launch Forge dev clients with offline usernames `player1` and `player2`.
- Both scripts `cd` to their own directory before running `./gradlew runClient --no-daemon --args="--username <name>"` so they work from any terminal path.

## 2026-06-06 Task: C4 asset conversion from Cubik OBJ

- Converted the provided `/home/alvaro/Descargas/minecraft-3d-model-csgo-c4.zip` asset by unpacking `source/CubikModel.zip` and reading `CubikModel.obj` / `CubikModel.mtl` directly from the nested archive.
- The OBJ had 400 groups and 0.125-unit grid spacing, so the MineRust C4 block model was generated as scaled Minecraft `elements` rather than loading OBJ at runtime.
- Added a palette-based `c4_charge.png` texture and mapped each converted cuboid face to the nearest extracted source color so the rendered block keeps the C4 red/black/gray/brown variation.
- Updated `models/block/c4_charge.json` to replace the `cube_all` placeholder with the converted multi-element model, while keeping the item model parented to the block model.
- The result is centered, scaled to fit comfortably inside a single block, and intended to be Forge 1.20.1 JSON-safe.
