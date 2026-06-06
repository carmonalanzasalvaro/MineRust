# MineRust Changelog

> Registro de cambios del mod MineRust. Cada entrada debe incluir: versión, fecha, cambios principales (features, fixes, breaking changes), y estado de QA.
> **Regla:** Todo cambio significativo en código o diseño debe añadirse aquí ANTES de considerar la tarea completada.

---

## [Unreleased]

> Cambios en progreso o pendientes de QA manual.

- [PENDIENTE] QA manual Final Wave: runClient/runServer con escenarios reales (TC, staff, C4, PvP, sleeping bag).
- [PENDIENTE] Playtesting de balance: upkeep costs, daño C4/taladro, HP tiers.
- [PENDIENTE] Implementar handlers de networking packets (actualmente skeleton no-op).
- [QA] Security Panel parcialmente confirmado manualmente: orientación del modelo, reserva máxima 30×30 desde nivel 1, upgrade a nivel 20, GUI actual aceptable, descuento visual de inventario, refund y bounds/debug funcionando correctamente. Quedan pendientes autorización, protección, raid, PvP, sleeping bag, persistencia y otros escenarios de dos jugadores.
- [FIXED] Normalizada la terminología visible del Security Panel: ahora habla de cobertura/rango ampliados, no de un segundo panel visible.

### Added (post-V1)
- **Security Panel 3D model** — Replaced `cube_all` placeholder with a Forge OBJ import converted from the user-provided Sketchfab/Blockbench GLTF. The imported OBJ preserves the real TV mesh groups and uses the original 128×128 TV texture with forced opaque alpha to avoid invisible faces/floor.
- **CC BY 4.0 attribution** — Original model "minecraft TV model" by DPancito on Sketchfab (`https://sketchfab.com/3d-models/minecraft-tv-model-6f5987a2c55b419f9d424d32746b0e35`), licensed under CC Attribution. Converted to Forge OBJ (`loader: forge:obj`) because Forge/vanilla 1.20.1 does not load GLTF block models directly without a custom renderer/loader.
- **C4 texturas 3D** — Generadas desde asset OBJ original de CS:GO C4. 6 texturas proyectadas (north/south/east/west/up/down) aplicadas a modelo de bloque completo.
- **Claim debug stick** — Item de desarrollo para inspeccionar claims (owner, nivel, cobertura y límites cubiertos) y visualizar bordes con partículas END_ROD.
- **Launchers** — `server.sh`, `player1.sh`, `player2.sh` para arrancar servidor y clientes de desarrollo.

### Changed (post-V1)
- **Tool Cupboard renamed to Security Panel** — Player-facing name changed from "Tool Cupboard" / "Armario de Herramientas" to "Security Panel" / "Panel de Seguridad". Internal registry ID `minerust:tool_cupboard` and all Java class names preserved. All in-game chat messages, lang files (`en_us.json`, `es_es.json`), README, and CHECKLIST updated.
- **Tool Cupboard single-block refactor** — Replaced separate `tool_cupboard_tier1` / `tool_cupboard_tier2` blocks and items with one `minerust:tool_cupboard`. Player-facing wording now treats the upgrade as Security Panel coverage/range expansion, while the internal BlockEntity state continues to track the implementation detail needed for claim radius.
- **Security Panel interaction flow** — Right-click now opens a Security Panel menu with explicit actions for upgrading coverage, toggling persistent claim-bound previews, and authorizing a nearby player. The panel now faces the placing player and switches from the red-status texture to a green-status texture once upgraded.
- **Security Panel 3D coverage** — Claim authorization applies only inside the 3D volume around the panel. The claim/debug messages report exact block bounds.
- **Security Panel block-centered coverage** — Coverage is now centered on the exact Security Panel block instead of the containing chunk.
- **Security Panel level progression** — Coverage now uses 20 levels. Level 1 starts at a 10×10 footprint, level 20 reaches a 30×30 footprint, and vertical coverage is fixed at 60 total blocks. The Security Panel GUI displays only the horizontal footprint (`10x10` through `30x30`) instead of the vertical claim size.
- **Security Panel placement reservation** — New Security Panels must be far enough apart for maximum 30×30 coverage from the start. Placement checks compare max coverage against max coverage so upgrades cannot later collide with a neighboring panel.
- **Security Panel upgrade economy feedback** — Upgrade item removal now syncs the player inventory immediately while the menu stays open, avoiding delayed visual updates until closing the panel.
- **Security Panel upgrade refund** — Breaking an upgraded Security Panel now drops half of the cumulative diamonds and iron ingots spent on coverage upgrades.
- **Project documentation skill gate** — Strengthened `.opencode/skills/minerust-context/SKILL.md` so any MineRust change must also update `CHANGELOG.md`, `ROADMAP.md`, `README.md`, `CHECKLIST-V1.md`, or explicitly explain why no docs changed. The skill now forbids automatic commit/push unless the user asks.
- **Roadmap dashboard** — Reworked `ROADMAP.md` into a GitHub-friendly project dashboard with status tables, V1 Final Wave checks, historical decisions, technical debt, future iterations, and explicit rules for updating the roadmap when unexpected work appears.

### Removed (post-V1)
- **Tiered Tool Cupboard registries/resources** — Removed tier1/tier2 TC recipes, loot tables, blockstates, models, translations, and item/block registry entries; migrated the starter recipe and loot to `tool_cupboard`.

### Fixed (post-V1)
- **Claim bounds internal edges** — Security Panel boundary previews and the claim debug stick now draw one exterior 3D prism around the covered volume instead of outlining internal edges.
- **Claim bounds corner visibility** — Added dense cyan corner beams to Security Panel boundary previews and claim debug stick outlines so claim corners read like beacon-style vertical markers instead of faint smoke columns.
- **TC placement overlap bypass** — `ToolCupboardBlock.setPlacedBy` now checks claim overlap before registering, and `ClaimProtectionEvents.onBlockPlace` cancels unregistered/overlapping TC placements without removing existing claims. This prevents new `minerust:tool_cupboard` placements from overwriting another TC claim.
- **TC auto-removal** — `ToolCupboardBlock.setPlacedBy` registraba claim antes del evento `BlockEvent.EntityPlaceEvent`, causando que `wouldOverlap` detectara el propio claim y cancelara la colocación. Fix: `wouldOverlap` ahora acepta `ignorePos` para excluir el claim recién colocado.
- **TC propiedades de bloque** — Cambiado de `Properties.copy(Blocks.CHEST)` (que causaba comportamientos extraños) a `Properties.copy(Blocks.OAK_PLANKS).strength(2.0f, 3.0f)`.
- **Security Panel occlusion** — Añadido `.noOcclusion()` al bloque para que el modelo no cúbico de TV no oculte el suelo/caras vecinas.
- **C4 modelo roto** — Reemplazado JSON de 27.000 líneas/400 elementos con modelo de caja simple de 2 elementos.
- **Bedrock protection exploit** — El bastón permitía proteger bedrock (y otros bloques indestructibles), haciéndolos rompibles con C4. Fix: `ProtectionStaffItem.applyProtection` ahora rechaza bloques con `destroySpeed < 0`.
- **C4 directional placement** — C4 ahora se coloca en cualquier cara del bloque como un botón, usando `DirectionProperty.FACING`. El modelo rota según la orientación.
- **C4 transparency** — Ajustadas propiedades del bloque para evitar que se vea a través del mundo cuando está contra otros bloques.

---

## [V1] - 2026-06-06

### Estado: Code-complete, QA manual pendiente

Build pasa (`./gradlew build --no-daemon` → BUILD SUCCESSFUL). Todas las features V1 están implementadas pero no verificadas en runtime.

### Added

#### Sistemas core
- **Tool Cupboard (historical V1 wording)** — One Security Panel protects a centered block volume and can expand its coverage/range via owner upgrade with proportional diamond/iron costs. Bloque colocable con BlockEntity que persiste owner, autorizados, recursos y el estado interno de cobertura.
- **Autorización manual** — Owner añade UUIDs autorizados via sneak-right-click en TC con jugador cercano. Sin clanes ni grupos automáticos.
- **Upkeep y decay** — Scheduler server-side consume recursos según bloques protegidos y tiers. Sin recursos: bloques bajan 1 tier cada intervalo configurable hasta STRAW, luego pierden HP.
- **Persistencia server-side** — `ClaimSavedData` (LevelSavedData) guarda claims, bloques protegidos y cooldowns en NBT. Sobrevive reinicios.

#### Protección y raid
- **Bastón de protección** — Aplica tiers STRAW/WOOD/STONE/METAL/HQ a bloques individuales dentro del claim. Consume material del inventario. Shift-click para cambiar tier.
- **C4 de raid** — Colocable en caras de bloques. Al activarse daña los 6 bloques adyacentes protegidos (solo protegidos). No afecta bloques no protegidos.
- **Taladro de raid** — Daño progresivo sobre bloques protegidos al mantener uso. Consume durabilidad. No bypassa protección.
- **Anti-bypass vanilla** — TNT, creepers, lava, fuego, pistones y fluidos no destruyen ni mueven bloques protegidos. Scanner de fuego cada 20 ticks para extinguir fire spread.

#### Combate y respawn
- **Filtro PvP vanilla** — Espadas, arcos, ballestas y tridentes vanilla no dañan entre jugadores. Daño indirecto (TNT, lava, caída) persiste.
- **Scrap pistol** — Arma del mod que sí permite dañar jugadores. Raycast 20 bloques, cooldown configurable.
- **Sleeping bag** — Colocable libremente. Tras muerte PvP, respawnea en el sleeping bag con cooldown de 60s. Muerte PvE usa vanilla. No se destruye al usar.

#### Assets y datos
- **Assets originales** — 7 texturas PNG 16×16 pixel art procedural (4 bloques, 3 items). Modelos JSON, blockstates, lang EN/ES.
- **C4 model 3D** — Modelo multi-element convertido de asset OBJ, con textura palette-mapped.
- **Recetas** — Recetas crafteables para Security Panel, sleeping bag, C4, bastón, taladro y pistola.
- **Loot tables** — 4 tablas para bloques (dropean item con `survives_explosion`).
- **Creative tab** — Tab "MineRust" con todos los items, icono scrap pistol.
- **Claim debug stick** — Item de desarrollo para inspeccionar claims y visualizar bordes con partículas.

#### Infraestructura
- **Configuración** — `ForgeConfigSpec` con todos los valores de balance: upkeep costs, decay interval, C4 damage, drill DPS/durability, weapon damage/cooldown, PvP cooldown, TC radius.
- **Networking skeleton** — `SimpleChannel` con 3 packets registrados (sync TC, sync protection, sync cooldowns). Handlers son no-op; lógica crítica es server-side pura.
- **Launchers** — `server.sh`, `player1.sh`, `player2.sh` para ejecutar servidor y clientes de desarrollo.

### Changed
- N/A (primera versión)

### Fixed
- **Task 6** — `lastUpkeepTimestamp` movido de static a `ClaimSavedData` persistido para sobrevivir reinicios.
- **Task 9 (C4)** — Excepciones en `ClaimProtectionEvents` para permitir colocar y activar C4 en claims enemigos sin cancelación.
- **Task 9 (regression)** — `onBlockBreak` restaurado tras corrupción en el fix anterior; flujo limpio de autorización TC.
- **Task 13 (fire bypass)** — Añadido scanner `ServerTickEvent` cada 20 ticks para extinguir fuego en/adjunto a bloques protegidos (Forge no dispara evento para `FireBlock.tick()`).
- **F1 (sleeping bag cooldown)** — Añadido gate de cooldown en `RespawnEvents` antes de teleportar; si está en cooldown usa vanilla respawn.
- **F1 (TC auth path)** — Implementada interacción sneak-right-click para autorizar jugadores sin UI custom.
- **F1 (onBlockBreak regression)** — Fix de `removeClaim` que se ejecutaba tras cancelación para no-autorizados.
- **F1 (drill held behavior)** — Reemplazado daño inmediato por daño progresivo al mantener uso, con `startUsingItem`, `onUseTick`, `releaseUsing`.

### Known Issues / Limitations
1. **QA manual no ejecutado** — Ningún escenario de QA del README ha sido probado en runClient/runServer real. Prioridad: CRÍTICA.
2. **Networking packets skeleton** — Handlers no-op. Sincronización server→client no implementada. Aceptable para V1 (lógica server-side pura).
3. **Sleeping bag y C4 renderizan como cubos** — Modelos usan `cube_all`. Aceptable para V1; futura iteración puede usar modelos planos.
4. **Balance sin playtesting** — Valores configurables pero no validados con partidas reales.
5. **Sin anti-cheat adicional** — No hay validación de paquetes custom ni rate limiting. Asume servidor de confianza.
6. **Java LSP no disponible** — `jdtls` no instalado. Solo `./gradlew build` como verificación automatizada.

---

## Plantilla para nuevas entradas

```markdown
## [VX.Y] - YYYY-MM-DD

### Estado: [Code-complete / QA passed / Released]

### Added
- 

### Changed
- 

### Deprecated
- 

### Removed
- 

### Fixed
- 

### Security
- 
```

---

## Convenciones

- **Versionado:** SemVer simplificado: `V1`, `V1.1`, `V2`.
- **Fechas:** ISO 8601 (`YYYY-MM-DD`).
- **Categorías:** Added, Changed, Deprecated, Removed, Fixed, Security (siguiendo [Keep a Changelog](https://keepachangelog.com/)).
- **QA:** Cada versión debe indicar explícitamente su estado de QA.
