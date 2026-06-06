# MineRust Master Plan

## TL;DR
> **Summary**: Mod Minecraft 1.20.1 (Forge 47.3.0) que combina SecurityCraft + Rust. Sistema de protección bloque a bloque con Tool Cupboard, tiers de defensa (Paja→Madera→Piedra→Metal→HQ), raideo con C4/taladro, armas del mod para PvP, y sleeping bag para respawn PvP.
> **Deliverables**: Mod funcional con TC, bastón de protección, C4, taladro, armas, sleeping bag, assets completos.
> **Effort**: Large (XL)
> **Parallel**: YES - 6 waves
> **Critical Path**: Wave 1 (infra) → Wave 2 (TC) → Wave 3 (bastón + raid) → Wave 4 (PvP + respawn) → Wave 5 (assets + balance)

---

## Context

### Original Request
Crear un mod tipo Rust para Minecraft con sistema de protección de base bloque a bloque (como SecurityCraft), raideo, armas, y respawn estilo Rust. Libertad de construcción de Minecraft, no prefabs.

### Interview Summary
**Concepto**: SecurityCraft + Rust
**Version**: Minecraft 1.20.1, Forge 47.3.0, Mappings: official
**Package**: com.minerust | Mod ID: minerust

**Decisiones clave**:
- TC protege chunks: Tier 1=1 chunk, Tier 2=3x3 chunks
- Upkeep: consume madera/piedra/metal según tier de bloques, decay gradual
- Raid: siempre vulnerable (hardcore)
- PvP: solo cancela daño directo arma melee/rango vanilla
- Daño a protegidos: SOLO C4/taladro del mod
- Sleeping bag: colocable libre, cooldown 1min por jugador, reusable
- Tiers bastón: Paja→Madera→Piedra→Metal→HQ
- Assets completos

### Metis Review (gaps addressed)
- Riesgo scope creep → Guardrails strict V1
- Riesgo persistencia → LevelSavedData + timestamps
- Riesgo bypass vanilla → Lista cerrada de eventos a interceptar
- Riesgo balance → Todo configurable en serverconfig

---

## Work Objectives

### Core Objective
Crear un mod Forge funcional con sistema completo de base-raideo-respawn que capture la esencia de Rust manteniendo la libertad de construcción de Minecraft.

### Deliverables
1. Tool Cupboard (TC) con tiers de área y upkeep
2. Bastón de protección con 5 tiers de defensa
3. C4 y taladro para raideo
4. Armas del mod para PvP
5. Sleeping bag para respawn PvP
6. Sistema de autorización de jugadores
7. Configuración completa
8. Assets: texturas, modelos JSON, sonidos, lang

### Definition of Done (verifiable conditions)
- `./gradlew build` compila sin errores
- `./gradlew runClient` inicia sin crash
- `./gradlew runServer` inicia sin crash
- TC colocable, persistente tras reinicio
- Bastón aplica protección bloque a bloque
- C4 daña bloques protegidos, vanilla TNT no
- Armas del mod funcionan, vanilla espadas no dañan jugadores
- Sleeping bag funciona para respawn PvP con cooldown

### Must Have
- TC crafteable con tiers de área
- Upkeep configurable por bloque/tier
- Bastón con 5 tiers de defensa
- Protección contra break/place/interact no autorizado
- C4 crafteable con textura
- Taladro crafteable con textura
- Al menos 1 arma crafteable con textura
- Sleeping bag con respawn PvP
- Cancelación de daño PvP con armas vanilla
- Configuración serverconfig

### Must NOT Have (guardrails)
- NO prefabs de construcción
- NO clanes/equipos complejos (solo autorización manual)
- NO economía
- NO UI avanzada (solo menús vanilla/inventario)
- NO modificar inventario vanilla
- NO ventanas de raid seguras (siempre vulnerable)
- NO port a Fabric/NeoForge
- NO dimensiones custom
- NO generación de mundo custom

---

## Verification Strategy
> ZERO HUMAN INTERVENTION - all verification is agent-executed.
- **Test decision**: Tests-after (no test framework setup, QA via escenarios agent-ejecutados)
- **QA policy**: Cada task tiene escenarios de happy path + edge case
- **Evidence**: .omo/evidence/task-{N}-{slug}.{ext}

---

## Execution Strategy

### Parallel Execution Waves
> Target: 5-8 tasks per wave. <3 per wave (except final) = under-splitting.

**Wave 1: Fundamentos** (infraestructura compartida)
**Wave 2: Tool Cupboard Core** (protección + persistencia)
**Wave 3: Bastón + Tiers + Raid Tools** (mecánicas de raid)
**Wave 4: Armas + PvP + Sleeping Bag** (combate + respawn)
**Wave 5: Assets + Balance + Polish** (texturas, recetas, config)
**Wave 6: Final Verification** (4 agentes en paralelo)

### Dependency Matrix
| Task | Depends On | Blocks |
|------|-----------|--------|
| W1-T1 (Registry) | - | W2-T1, W3-T1, W3-T2, W4-T1, W4-T2 |
| W1-T2 (Networking) | - | W2-T1, W4-T2 |
| W1-T3 (Config) | - | W2-T1, W3-T1, W3-T2, W4-T1 |
| W1-T4 (SavedData) | - | W2-T1 |
| W2-T1 (TC Block/TileEntity) | W1-T1, W1-T2, W1-T4 | W2-T2, W2-T3 |
| W2-T2 (TC Upkeep) | W2-T1 | W5-T1 |
| W2-T3 (TC Auth) | W2-T1 | W5-T1 |
| W3-T1 (Bastón) | W1-T1, W1-T3 | W5-T1 |
| W3-T2 (C4) | W1-T1, W1-T3 | W5-T1 |
| W3-T3 (Taladro) | W1-T1, W1-T3 | W5-T1 |
| W4-T1 (Armas) | W1-T1, W1-T3 | W5-T1 |
| W4-T2 (Sleeping Bag) | W1-T1, W1-T2 | W5-T1 |
| W4-T3 (PvP Filter) | W1-T3 | W5-T1 |
| W5-T1 (Assets) | W2-T2, W2-T3, W3-T1, W3-T2, W3-T3, W4-T1, W4-T2, W4-T3 | - |
| W5-T2 (Recipes + Balance) | W2-T1, W3-T1, W3-T2, W3-T3, W4-T1, W4-T2 | - |
| W5-T3 (Lang + Polish) | W5-T1, W5-T2 | W6-F1-F4 |

### Agent Dispatch Summary
| Wave | Tasks | Categories |
|------|-------|-----------|
| Wave 1 | 4 | unspecified-low |
| Wave 2 | 3 | unspecified-high |
| Wave 3 | 3 | unspecified-high |
| Wave 4 | 3 | unspecified-high |
| Wave 5 | 3 | visual-engineering, unspecified-high |
| Wave 6 | 4 | unspecified-high, deep, oracle |

### Model Preference
- Prefer Kimi-routed categories/agents whenever available.
- Use GPT/Oracle only for verification gates or when the routing layer does not expose a Kimi alternative.
- Do not block implementation solely to force a model; quality gates matter more than model preference.

---

## TODOs

### Wave 1: Fundamentos

- [x] 1. Setup DeferredRegister para Blocks, Items, BlockEntities

  **What to do**: Crear clases de registro usando DeferredRegister para todos los bloques, items y block entities del mod. Incluir placeholders para TC, sleeping bag, C4, taladro, armas, bastón.
  **Must NOT do**: No registrar aún recetas, loot tables, o events.

  **Recommended Agent Profile**:
  - Category: `unspecified-low` - Reason: Tarea de scaffolding estándar de Forge
  - Skills: [] - No skills especiales necesarias

  **Parallelization**: Can Parallel: YES | Wave 1 | Blocks: task 2, task 3, task 4 | Blocked By: -

  **References**:
  - Pattern: `src/main/java/com/minerust/MineRustMod.java:32` - Registry setup
  - Forge API: `DeferredRegister.create(ForgeRegistries.BLOCKS, MODID)`
  - Forge API: `DeferredRegister.create(ForgeRegistries.ITEMS, MODID)`
  - Forge API: `DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MODID)`

  **Acceptance Criteria**:
  - [ ] `./gradlew build` compila sin errores
  - [ ] Todos los DeferredRegister están en clases separadas: `ModBlocks`, `ModItems`, `ModBlockEntities`
  - [ ] MineRustMod.java registra los DeferredRegister en el event bus

  **QA Scenarios**:
  ```
  Scenario: Registry compila
    Tool: Bash
    Steps: ./gradlew build --no-daemon
    Expected: BUILD SUCCESSFUL
    Evidence: .omo/evidence/task-1-build.log
  ```

  **Commit**: YES | Message: `feat(registry): setup DeferredRegister for blocks, items, block entities` | Files: `src/main/java/com/minerust/registry/`

- [x] 2. Setup SimpleChannel Networking

  **What to do**: Crear paquete de networking con SimpleChannel para sincronizar datos server→client. Definir IDs de paquetes para: sync TC data, sync block protection, sync player cooldowns.
  **Must NOT do**: No implementar handlers de paquetes aún, solo la infraestructura.

  **Recommended Agent Profile**:
  - Category: `unspecified-low` - Reason: Networking boilerplate estándar
  - Skills: []

  **Parallelization**: Can Parallel: YES | Wave 1 | Blocks: task 5, task 12 | Blocked By: -

  **References**:
  - Forge API: `SimpleChannel`
  - Forge API: `NetworkRegistry.newSimpleChannel(ResourceLocation, () -> version, version::equals, version::equals)`
  - Pattern: Usar `Integer` como protocol version (ej: 1)

  **Acceptance Criteria**:
  - [ ] Clase `ModNetworking` creada con SimpleChannel
  - [ ] Al menos 3 paquetes definidos como inner classes/records
  - [ ] `./gradlew build` compila sin errores

  **QA Scenarios**:
  ```
  Scenario: Networking compila
    Tool: Bash
    Steps: ./gradlew build --no-daemon
    Expected: BUILD SUCCESSFUL
    Evidence: .omo/evidence/task-2-build.log
  ```

  **Commit**: YES | Message: `feat(network): setup SimpleChannel packet infrastructure` | Files: `src/main/java/com/minerust/networking/`

- [x] 3. Rewrite Config.java para MineRust

  **What to do**: Reemplazar Config.java de ejemplo con configuración real del mod. Incluir: raid damage multipliers, upkeep costs per tier, PvP settings, decay intervals, cooldown times.
  **Must NOT do**: No hardcodear valores en otras clases; TODO debe leer de Config.

  **Recommended Agent Profile**:
  - Category: `unspecified-low` - Reason: Configuración estándar ForgeConfigSpec
  - Skills: []

  **Parallelization**: Can Parallel: YES | Wave 1 | Blocks: task 5, task 8, task 9, task 10 | Blocked By: -

  **References**:
  - Pattern: `src/main/java/com/minerust/Config.java:1` - Ejemplo existente
  - Forge API: `ForgeConfigSpec.Builder`, `define`, `defineInRange`
  - Config types: `UPKEEP_WOOD_COST`, `UPKEEP_STONE_COST`, `UPKEEP_METAL_COST`, `UPKEEP_HQ_COST`, `DECAY_INTERVAL_HOURS`, `RAID_DAMAGE_MULTIPLIER`, `PVP_COOLDOWN_SECONDS`, `TC_TIER1_RADIUS`, `TC_TIER2_RADIUS`

  **Acceptance Criteria**:
  - [ ] Config tiene valores para todos los parámetros de balance
  - [ ] `./gradlew build` compila
  - [ ] Archivo de config se genera en `run/config/minerust-common.toml`

  **QA Scenarios**:
  ```
  Scenario: Config se genera correctamente
    Tool: Bash
    Steps: ./gradlew runServer --no-daemon & sleep 30; cat run/config/minerust-common.toml
    Expected: Archivo existe con valores default
    Evidence: .omo/evidence/task-3-config.toml
  ```

  **Commit**: YES | Message: `feat(config): add MineRust configuration specs` | Files: `src/main/java/com/minerust/Config.java`

- [x] 4. Setup LevelSavedData para persistencia server-side

  **What to do**: Crear sistema de persistencia usando LevelSavedData para: TC data (owner, authorized players, resources, tier), block protection data (block positions + tier), player cooldown data.
  **Must NOT do**: No usar Capability para esto (SavedData es más apropiado para datos globales del mundo).

  **Recommended Agent Profile**:
  - Category: `unspecified-high` - Reason: Persistencia crítica, requiere entender NBT
  - Skills: []

  **Parallelization**: Can Parallel: YES | Wave 1 | Blocks: task 5 | Blocked By: -

  **References**:
  - Forge API: `SavedData`, `DimensionDataStorage`
  - Pattern: Extender `SavedData`, implementar `save(CompoundTag)` y constructor que lea CompoundTag
  - Key: Usar `Level.getDataStorage().computeIfAbsent(factory, id)`

  **Acceptance Criteria**:
  - [ ] Clase `ClaimSavedData` creada con load/save de NBT
  - [ ] Almacena: Mapa de chunk→TC data, Mapa de BlockPos→protection tier
  - [ ] Se carga automáticamente al iniciar servidor
  - [ ] Se guarda al apagar servidor

  **QA Scenarios**:
  ```
  Scenario: Persistencia funciona
    Tool: Bash
    Steps: ./gradlew runServer --no-daemon & sleep 30; kill %1; grep -r "Saved" run/saves/*/data/ || echo "No data dir"
    Expected: Datos persisten en directorio del mundo
    Evidence: .omo/evidence/task-4-persist.log
  ```

  **Commit**: YES | Message: `feat(data): setup LevelSavedData for claims and protection` | Files: `src/main/java/com/minerust/data/`

<!-- WAVE 2 TASKS WILL BE INSERTED HERE -->

### Wave 2: Tool Cupboard Core

- [x] 5. Implementar Tool Cupboard Block + BlockEntity

  **What to do**: Crear bloque TC con BlockEntity. El bloque debe tener 2 tiers (determina área de protección). BlockEntity almacena: owner UUID, lista de autorizados, recursos de upkeep, tier actual.
  **Must NOT do**: No implementar aún el sistema de upkeep ni autorización (solo almacenar datos).

  **Recommended Agent Profile**:
  - Category: `unspecified-high` - Reason: BlockEntity + NBT + GUI básica
  - Skills: []

  **Parallelization**: Can Parallel: NO | Wave 2 | Blocks: task 6, task 7 | Blocked By: task 1, task 2, task 4

  **References**:
  - Forge API: `BaseEntityBlock`, `BlockEntity`
  - Pattern: `DeferredRegister` para registrar bloque + block entity
  - NBT: `saveAdditional(CompoundTag)`, `load(CompoundTag)`

  **Acceptance Criteria**:
  - [ ] TC bloque colocable en mundo
  - [ ] BlockEntity persiste owner, authorized, resources, tier
  - [ ] TC tiene 2 tiers crafteables separadamente
  - [ ] `./gradlew build` compila
  - [ ] `./gradlew runClient` inicia sin crash

  **QA Scenarios**:
  ```
  Scenario: TC colocable
    Tool: Bash + runClient
    Steps: Colocar TC, verificar que tiene BlockEntity
    Expected: TC se coloca, al romperlo dropea el ítem
    Evidence: .omo/evidence/task-5-tc-place.mp4
  ```

  **Commit**: YES | Message: `feat(tc): add Tool Cupboard block and block entity` | Files: `src/main/java/com/minerust/claim/`

- [x] 6. Implementar sistema de Upkeep y Decay

  **What to do**: Scheduler server-side que consume recursos del TC según bloques protegidos y sus tiers. Si no hay recursos, los bloques pierden 1 tier cada X horas (configurable) hasta quedar desprotegidos.
  **Must NOT do**: No implementar UI para añadir recursos aún (solo slots internos o comandos).

  **Recommended Agent Profile**:
  - Category: `unspecified-high` - Reason: Lógica de scheduling + balance
  - Skills: []

  **Parallelization**: Can Parallel: NO | Wave 2 | Blocks: task 13 | Blocked By: task 5

  **References**:
  - Forge API: `ServerTickEvent` o `LevelTickEvent` para scheduling
  - Config: `Config.UPKEEP_*`, `Config.DECAY_INTERVAL_HOURS`
  - Pattern: Usar timestamp en SavedData para calcular tiempo offline

  **Acceptance Criteria**:
  - [ ] Upkeep consume recursos según fórmula: SUM(bloques * cost_per_tier)
  - [ ] Sin recursos: decay reduce tier cada intervalo configurable
  - [ ] Decay persiste tras reinicio (timestamp-based)
  - [ ] Configurable via `minerust-common.toml`

  **QA Scenarios**:
  ```
  Scenario: Upkeep consume recursos
    Tool: Bash + runServer
    Steps: Colocar TC, proteger bloques, esperar intervalo de upkeep
    Expected: Recursos disminuyen en TC
    Evidence: .omo/evidence/task-6-upkeep.log

  Scenario: Decay sin recursos
    Tool: Bash + runServer
    Steps: Vaciar recursos TC, esperar intervalo de decay
    Expected: Bloques protegidos bajan de tier
    Evidence: .omo/evidence/task-6-decay.log
  ```

  **Commit**: YES | Message: `feat(tc): add upkeep and decay system` | Files: `src/main/java/com/minerust/claim/`

- [x] 7. Implementar autorización y protección de chunks

  **What to do**: Sistema de autorización manual (owner añade jugadores por UUID/nombre). Protección de chunks basada en tier del TC. Cancelar BlockEvent.BreakBlock, BlockEvent.PlaceBlock, PlayerInteractEvent dentro de chunks protegidos para no-autorizados.
  **Must NOT do**: No cancelar eventos para owner/autorizados. No implementar comandos aún.

  **Recommended Agent Profile**:
  - Category: `unspecified-high` - Reason: Event handling crítico
  - Skills: []

  **Parallelization**: Can Parallel: NO | Wave 2 | Blocks: task 13 | Blocked By: task 5

  **References**:
  - Forge Events: `BlockEvent.BreakBlock`, `BlockEvent.EntityPlaceEvent`, `PlayerInteractEvent.RightClickBlock`
  - Pattern: `event.setCanceled(true)` para cancelar
  - Check: Verificar si jugador es owner o en lista autorizada

  **Acceptance Criteria**:
  - [ ] No-autorizado NO puede romper bloques en chunk protegido
  - [ ] No-autorizado NO puede colocar bloques en chunk protegido
  - [ ] No-autorizado NO puede abrir cofres/puertas en chunk protegido
  - [ ] Owner y autorizados SÍ pueden interactuar
  - [ ] Sin TC en chunk: comportamiento vanilla

  **QA Scenarios**:
  ```
  Scenario: Protección activa
    Tool: Bash + runServer
    Steps: PlayerA coloca TC, PlayerB intenta romper bloque en chunk
    Expected: Evento cancelado, bloque no se rompe
    Evidence: .omo/evidence/task-7-protection.log

  Scenario: Autorizado puede construir
    Tool: Bash + runServer
    Steps: PlayerA autoriza a PlayerB, PlayerB rompe bloque
    Expected: Bloque se rompe normalmente
    Evidence: .omo/evidence/task-7-auth.log
  ```

  **Commit**: YES | Message: `feat(tc): add chunk protection and player authorization` | Files: `src/main/java/com/minerust/claim/`

### Wave 3: Bastón + Tiers + Raid Tools

- [x] 8. Implementar bastón de protección y tiers por bloque

  **What to do**: Crear item `protection_staff` que aplica tiers de defensa a bloques existentes dentro de área TC autorizada. Tiers: STRAW, WOOD, STONE, METAL, HQ. Guardar tier por BlockPos en SavedData.
  **Must NOT do**: No convertir bloques vanilla en bloques custom. No crear prefabs. No aplicar protección fuera de área TC.

  **Recommended Agent Profile**:
  - Category: `unspecified-high` - Reason: Item interaction + SavedData + permisos
  - Skills: []

  **Parallelization**: Can Parallel: NO | Wave 3 | Blocks: task 9, task 10, task 13 | Blocked By: task 3, task 4, task 7

  **References**:
  - Forge Events/API: `Item#useOn`, `UseOnContext`, `BlockPos`
  - Data: `ClaimSavedData` task 4
  - Rules: solo owner/autorizados pueden aplicar tiers

  **Acceptance Criteria**:
  - [ ] Bastón aplica tier a un bloque existente si el jugador está autorizado
  - [ ] Bastón falla fuera de área TC con mensaje claro
  - [ ] Bastón falla para no-autorizados
  - [ ] Tier persiste tras reinicio
  - [ ] Coste de material por tier se consume del inventario del jugador o TC según config/default elegido

  **QA Scenarios**:
  ```
  Scenario: Player autorizado protege stone a WOOD
    Tool: runClient/runServer
    Steps: PlayerA coloca TC, usa bastón en minecraft:stone con material suficiente
    Expected: SavedData registra tier WOOD para esa BlockPos
    Evidence: .omo/evidence/task-8-staff-protect.log

  Scenario: PlayerB no autorizado falla
    Tool: runServer
    Steps: PlayerB usa bastón dentro del TC de PlayerA
    Expected: No cambia SavedData, mensaje de permiso denegado
    Evidence: .omo/evidence/task-8-staff-denied.log
  ```

  **Commit**: YES | Message: `feat(protection): add staff and block defense tiers` | Files: `src/main/java/com/minerust/blockhealth/`, `src/main/java/com/minerust/item/`

- [x] 9. Implementar C4 como herramienta de raid

  **What to do**: Crear item/bloque `c4_charge` colocable en caras de bloques. Al activarse, daña SOLO bloques protegidos por tier según config. Debe ignorar/desactivar daño vanilla a bloques no protegidos si aplica.
  **Must NOT do**: No usar explosión vanilla destructiva para romper protegidos. No permitir C4 fuera de reglas de raid.

  **Recommended Agent Profile**:
  - Category: `unspecified-high` - Reason: Mecánica de daño custom + protección contra bypass
  - Skills: []

  **Parallelization**: Can Parallel: NO | Wave 3 | Blocks: task 13, task 15 | Blocked By: task 3, task 8

  **References**:
  - Forge Events: explosion events for visual-only explosion
  - Config: C4 damage per tier
  - Data: protection tier from task 8

  **Acceptance Criteria**:
  - [ ] C4 se puede craftear/obtener y colocar
  - [ ] C4 reduce HP/tier de bloque protegido según config
  - [ ] C4 no rompe bloques no protegidos por accidente
  - [ ] C4 tiene feedback sonoro/visual placeholder si assets aún no están
  - [ ] `./gradlew build` pasa

  **QA Scenarios**:
  ```
  Scenario: C4 daña bloque protegido
    Tool: runServer
    Steps: Proteger stone a WOOD, colocar C4, activar
    Expected: Tier/HP disminuye o bloque se rompe según daño config
    Evidence: .omo/evidence/task-9-c4-protected.log
  ```

  **Commit**: YES | Message: `feat(raid): add C4 custom raid damage` | Files: `src/main/java/com/minerust/raid/`

- [x] 10. Implementar taladro de raid

  **What to do**: Crear item `raid_drill` que al mantener uso sobre bloque protegido aplica daño progresivo. Debe consumir durabilidad/energía configurable y respetar tiers.
  **Must NOT do**: No permitir instabreak. No dañar bloques no protegidos más rápido que vanilla.

  **Recommended Agent Profile**:
  - Category: `unspecified-high` - Reason: Item tick/use duration + daño incremental
  - Skills: []

  **Parallelization**: Can Parallel: YES | Wave 3 | Blocks: task 13, task 15 | Blocked By: task 3, task 8

  **References**:
  - Minecraft API: item use duration, durability
  - Data: protection tiers from task 8
  - Config: drill damage per second, durability cost

  **Acceptance Criteria**:
  - [ ] Taladro daña progresivamente bloques protegidos
  - [ ] Taladro consume durabilidad/combustible según config
  - [ ] Taladro no bypass permisos de protección fuera de mecánica raid
  - [ ] Taladro muestra feedback básico (mensaje/sonido)

  **QA Scenarios**:
  ```
  Scenario: Taladro reduce tier/HP
    Tool: runClient
    Steps: Usar taladro 5 segundos sobre bloque WOOD protegido
    Expected: Daño acumulado registrado en SavedData
    Evidence: .omo/evidence/task-10-drill-damage.log
  ```

  **Commit**: YES | Message: `feat(raid): add raid drill progressive damage` | Files: `src/main/java/com/minerust/raid/`

### Wave 4: Armas + PvP + Sleeping Bag

- [x] 11. Implementar arma Rust básica del mod

  **What to do**: Crear al menos un arma crafteable del mod (`rust_rifle` o `scrap_pistol`) con daño configurable y comportamiento PvP válido. Registrar item, uso, sonido placeholder y cooldown.
  **Must NOT do**: No crear sistema completo de balística avanzada en V1. No añadir dependencias externas.

  **Recommended Agent Profile**:
  - Category: `unspecified-high` - Reason: Combat item + cooldown + config
  - Skills: []

  **Parallelization**: Can Parallel: YES | Wave 4 | Blocks: task 13, task 15 | Blocked By: task 1, task 3

  **References**:
  - Minecraft API: projectile items or custom item use
  - Config: mod weapon damage/cooldown
  - PvP rule: mod weapons allowed between players

  **Acceptance Criteria**:
  - [ ] Arma del mod daña jugadores
  - [ ] Arma tiene cooldown configurable
  - [ ] Arma se registra en creative tab/items
  - [ ] Build pasa

  **QA Scenarios**:
  ```
  Scenario: Arma mod daña PlayerB
    Tool: runServer
    Steps: PlayerA golpea/dispara a PlayerB con minerust weapon
    Expected: Daño aplicado según config
    Evidence: .omo/evidence/task-11-mod-weapon.log
  ```

  **Commit**: YES | Message: `feat(combat): add first Rust weapon item` | Files: `src/main/java/com/minerust/combat/`

- [x] 12. Implementar sleeping bag y respawn PvP

  **What to do**: Crear bloque/item `sleeping_bag` colocable libremente. Si jugador muere por PvP directo, respawnea en su sleeping bag y queda bajo cooldown 60s por jugador. Si muere por PvE, usar respawn vanilla.
  **Must NOT do**: No reemplazar cama vanilla para muertes PvE. No destruir sleeping bag al usar.

  **Recommended Agent Profile**:
  - Category: `unspecified-high` - Reason: Death/respawn event handling + player state
  - Skills: []

  **Parallelization**: Can Parallel: NO | Wave 4 | Blocks: task 15 | Blocked By: task 1, task 2, task 4

  **References**:
  - Forge Events: player death/respawn events
  - Data: player cooldowns in SavedData
  - Rule: PvE death → vanilla bed; PvP death → sleeping bag

  **Acceptance Criteria**:
  - [ ] Sleeping bag colocable y persistente
  - [ ] PvP death respawnea en sleeping bag
  - [ ] PvE death usa vanilla bed
  - [ ] Cooldown de 60s por jugador se aplica tras respawn PvP
  - [ ] Sleeping bag no se destruye al usar

  **QA Scenarios**:
  ```
  Scenario: PvP respawn en sleeping bag
    Tool: runServer
    Steps: PlayerA coloca sleeping bag, PlayerB mata PlayerA
    Expected: PlayerA respawnea en sleeping bag, cooldown activo
    Evidence: .omo/evidence/task-12-pvp-respawn.log
  ```

  **Commit**: YES | Message: `feat(respawn): add sleeping bag PvP respawn` | Files: `src/main/java/com/minerust/respawn/`

- [x] 13. Implementar filtro PvP vanilla y anti-bypass de bloques protegidos

  **What to do**: Cancelar daño directo jugador→jugador con armas melee/rango vanilla. Permitir daño indirecto confirmado por diseño. Cancelar daño vanilla a bloques protegidos: TNT, creepers, lava, fire, pistons.
  **Must NOT do**: No cancelar lava/TNT indirecta contra jugadores. No tocar inventario vanilla.

  **Recommended Agent Profile**:
  - Category: `unspecified-high` - Reason: Event matrix crítica anti-exploit
  - Skills: ["security-review"] - Relevante por auditoría de bypass/exploit de reglas

  **Parallelization**: Can Parallel: NO | Wave 4 | Blocks: task 15 | Blocked By: task 7, task 8, task 11

  **References**:
  - Forge Events: `LivingHurtEvent`, explosion events, piston events, fire/fluid events
  - Rule: only direct vanilla weapon damage is cancelled for PvP
  - Rule: only mod C4/drill damage protected blocks

  **Acceptance Criteria**:
  - [ ] `minecraft:iron_sword` no daña PlayerB si lo usa PlayerA directamente
  - [ ] Arma del mod sí daña PlayerB
  - [ ] TNT vanilla no rompe bloque protegido
  - [ ] Creeper/fire/lava/piston no rompe/mueve bloque protegido
  - [ ] Daño indirecto a jugadores no se cancela si no es arma directa

  **QA Scenarios**:
  ```
  Scenario: Espada vanilla no daña PvP
    Tool: runServer
    Steps: PlayerA ataca PlayerB con iron_sword
    Expected: Damage event cancelled
    Evidence: .omo/evidence/task-13-vanilla-pvp-cancel.log

  Scenario: TNT vanilla no rompe protegido
    Tool: runServer
    Steps: TNT explota junto a stone protegido WOOD
    Expected: stone protegido permanece
    Evidence: .omo/evidence/task-13-tnt-blocked.log
  ```

  **Commit**: YES | Message: `feat(protection): enforce PvP and protected-block bypass rules` | Files: `src/main/java/com/minerust/events/`

### Wave 5: Assets + Balance + Polish

- [x] 14. Crear assets completos para items/bloques del mod

  **What to do**: Crear texturas PNG, modelos JSON, blockstates y sonidos para TC, sleeping bag, bastón, C4, taladro y arma. Si no hay assets free verificados, generar assets originales simples y consistentes.
  **Must NOT do**: No usar assets con licencia dudosa. No dejar missing texture morado/negro.

  **Recommended Agent Profile**:
  - Category: `visual-engineering` - Reason: Trabajo visual/assets obligatorio
  - Skills: ["frontend-design", "high-end-visual-design", "design-taste-frontend"] - Relevante por calidad visual anti-placeholder

  **Parallelization**: Can Parallel: YES | Wave 5 | Blocks: task 16 | Blocked By: task 5, task 8, task 9, task 10, task 11, task 12

  **References**:
  - Resource paths: `src/main/resources/assets/minerust/`
  - Minecraft model format: `models/item/*.json`, `models/block/*.json`, `blockstates/*.json`
  - Lang: `lang/en_us.json`, `lang/es_es.json`

  **Acceptance Criteria**:
  - [ ] Cada item/bloque tiene textura PNG
  - [ ] Cada item/bloque tiene modelo JSON válido
  - [ ] Sonidos OGG o reuse explícito vanilla documentado
  - [ ] No hay missing textures en runClient
  - [ ] Assets tienen licencia propia o free documentada

  **QA Scenarios**:
  ```
  Scenario: No missing texture
    Tool: runClient
    Steps: Abrir creative inventory y visualizar todos los items minerust
    Expected: Todos renderizan con textura correcta
    Evidence: .omo/evidence/task-14-assets-screenshot.png
  ```

  **Commit**: YES | Message: `feat(assets): add MineRust textures models and sounds` | Files: `src/main/resources/assets/minerust/`

- [x] 15. Añadir recetas, loot, creative tab y balance inicial

  **What to do**: Añadir recetas JSON para TC tiers, bastón, C4, taladro, arma y sleeping bag. Añadir loot tables si aplica, creative tab, lang entries y valores de balance inicial en Config.
  **Must NOT do**: No añadir economía ni nuevos minerales en V1.

  **Recommended Agent Profile**:
  - Category: `unspecified-high` - Reason: Integración de datos + balance gameplay
  - Skills: []

  **Parallelization**: Can Parallel: YES | Wave 5 | Blocks: task 16 | Blocked By: task 8, task 9, task 10, task 11, task 12, task 13

  **References**:
  - Resource paths: `src/main/resources/data/minerust/recipes/`
  - Forge creative tab registration if needed
  - Balance defaults: conservative, configurable

  **Acceptance Criteria**:
  - [ ] Todos los items V1 son crafteables
  - [ ] Creative tab MineRust contiene todos los items/bloques
  - [ ] Lang entries EN/ES no muestran translation keys raw
  - [ ] Build y runClient pasan

  **QA Scenarios**:
  ```
  Scenario: Recetas cargan
    Tool: runClient
    Steps: Abrir recipe book/JEI si disponible o usar /recipe give
    Expected: Recetas minerust aparecen y producen items correctos
    Evidence: .omo/evidence/task-15-recipes.log
  ```

  **Commit**: YES | Message: `feat(data): add recipes creative tab and initial balance` | Files: `src/main/resources/data/minerust/`, `src/main/java/com/minerust/registry/`

- [x] 16. QA integral y documentación procedural para agentes

  **What to do**: Actualizar README y crear documentación procedural en `.omo/notepads/minerust-master-plan/` explicando estado, decisiones, comandos de QA, y cómo continuar iteraciones futuras.
  **Must NOT do**: No cambiar reglas de diseño sin aprobación. No ocultar problemas conocidos.

  **Recommended Agent Profile**:
  - Category: `writing` - Reason: Documentación técnica y procedural
  - Skills: []

  **Parallelization**: Can Parallel: NO | Wave 5 | Blocks: Final Wave | Blocked By: task 14, task 15

  **References**:
  - README actual: `README.md`
  - Notepad path: `.omo/notepads/minerust-master-plan/`
  - Evidence path: `.omo/evidence/`

  **Acceptance Criteria**:
  - [ ] README documenta cómo compilar, probar y jugar V1
  - [ ] Notepad contiene decisions.md, learnings.md, issues.md, problems.md
  - [ ] Hay checklist de QA reproducible para agentes futuros
  - [ ] Problemas conocidos documentados con prioridad

  **QA Scenarios**:
  ```
  Scenario: Agente nuevo puede continuar
    Tool: Read
    Steps: Leer README + notepads sin historial de chat
    Expected: Se entiende estado actual, próximos pasos y comandos
    Evidence: .omo/evidence/task-16-doc-review.md
  ```

  **Commit**: YES | Message: `docs: document MineRust V1 workflow and agent context` | Files: `README.md`, `.omo/notepads/minerust-master-plan/`

## Final Verification Wave (MANDATORY)
> 4 review agents run in PARALLEL. ALL must APPROVE. If any reviewer rejects, fix and re-run the rejecting reviewer before reporting completion.
> Agents verify each iteration before the next wave; do not require manual user approval between implementation waves.

- [x] F1. Plan Compliance Audit — oracle
- [x] F2. Code Quality Review — unspecified-high
- [x] F3. Real Manual QA — unspecified-high
- [x] F4. Scope Fidelity Check — deep

### Final Wave Details

**F1. Plan Compliance Audit — oracle**
- Verify every implemented feature maps to this plan.
- Verify no Must NOT Have was added accidentally.
- Verdict format: `APPROVE` or `REJECT` with blocking issues.

**F2. Code Quality Review — unspecified-high, Kimi preferred**
- Inspect changed Java/resource files.
- Check for server/client side misuse, null-safety issues, event cancellation mistakes, and over-broad handlers.
- Run `./gradlew build --no-daemon`.
- Verdict format: `APPROVE` or `REJECT`.

**F3. Real Manual QA — unspecified-high, Kimi preferred**
- Launch client/server as needed.
- Execute scenarios: TC place, staff protect, unauthorized break denied, C4 raid, vanilla TNT blocked, mod weapon PvP allowed, vanilla weapon PvP blocked, sleeping bag respawn.
- Capture evidence in `.omo/evidence/final-wave-qa/`.
- Verdict format: `APPROVE` or `REJECT`.

**F4. Scope Fidelity Check — deep, Kimi preferred**
- Compare implementation against user intent: SecurityCraft + Rust, no prefabs, Minecraft build freedom.
- Check that PvE inventory/vanilla gameplay remains untouched except explicit PvP damage filter.
- Verdict format: `APPROVE` or `REJECT`.

## Commit Strategy
- Cada wave = 1 commit con mensaje descriptivo
- Commits squash opcional al final
- Formato: `feat(scope): description`

## Success Criteria
- Build exit code 0
- Client inicia sin crash
- Server inicia sin crash
- TC colocable y persistente
- Upkeep funciona (consume recursos)
- Bastón protege bloques
- C4 daña protegidos
- Vanilla TNT no daña protegidos
- Armas del mod funcionan
- Espada vanilla no daña jugadores
- Sleeping bag respawn con cooldown
- Todos los items tienen texturas
- Config funciona
