# MineRust Changelog

> Registro de cambios del mod MineRust. Cada entrada debe incluir: versión, fecha, cambios principales (features, fixes, breaking changes), y estado de QA.
> **Regla:** Todo cambio significativo en código o diseño debe añadirse aquí ANTES de considerar la tarea completada.

---

## [Unreleased]

> Cambios en progreso o pendientes de QA manual.

- [PENDIENTE] QA manual Final Wave: runClient/runServer con escenarios reales (TC, staff, C4, PvP, sleeping bag).
- [PENDIENTE] Playtesting de balance: upkeep costs, daño C4/taladro, HP tiers.
- [PENDIENTE] Implementar handlers de networking packets (actualmente skeleton no-op).

### Added (post-V1)
- **C4 texturas 3D** — Generadas desde asset OBJ original de CS:GO C4. 6 texturas proyectadas (north/south/east/west/up/down) aplicadas a modelo de bloque completo.
- **Claim debug stick** — Item de desarrollo para inspeccionar claims (owner, tier, chunks cubiertos) y visualizar bordes con partículas END_ROD.
- **Launchers** — `server.sh`, `player1.sh`, `player2.sh` para arrancar servidor y clientes de desarrollo.

### Fixed (post-V1)
- **TC auto-removal** — `ToolCupboardBlock.setPlacedBy` registraba claim antes del evento `BlockEvent.EntityPlaceEvent`, causando que `wouldOverlap` detectara el propio claim y cancelara la colocación. Fix: `wouldOverlap` ahora acepta `ignorePos` para excluir el claim recién colocado.
- **TC propiedades de bloque** — Cambiado de `Properties.copy(Blocks.CHEST)` (que causaba comportamientos extraños) a `Properties.copy(Blocks.OAK_PLANKS).strength(2.0f, 3.0f)`.
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
- **Tool Cupboard (2 tiers)** — Tier 1 protege 1 chunk; tier 2 protege 3×3 chunks. Bloque colocable con BlockEntity que persiste owner, autorizados, recursos y tier.
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
- **Recetas** — 7 recetas crafteables (TC tier 1/2, sleeping bag, C4, bastón, taladro, pistola).
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
