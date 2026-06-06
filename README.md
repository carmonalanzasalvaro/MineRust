# MineRust V1

Mod de Minecraft 1.20.1 (Forge 47.3.0) inspirado en Rust: claims de terreno con Tool Cupboard, raideos con C4 y taladro, tiers de protección por bloque, filtro PvP y sleeping bag para respawn.

**Versión actual:** V1 (feature-complete, pendiente de QA manual Final Wave).

---

## Visión general

MineRust añade a Minecraft un sistema de protección territorial al estilo Rust, sin prefabs ni restricciones de inventario vanilla. Los jugadores colocan un **Tool Cupboard (TC)** para reclamar chunks, usan un **bastón de protección** para reforzar bloques individuales con tiers (STRAW → WOOD → STONE → METAL → HQ), y deben mantener el TC con recursos para evitar la degradación. Los rivales pueden atacar con **C4** o un **taladro de raid**, pero las armas y explosiones vanilla están limitadas por diseño.

### Diseño restringido (V1)
- **Sin economía**: no hay tiendas, monedas ni NPCs.
- **Sin clanes/equipos**: autorización es manual por UUID, sin grupos automáticos.
- **Sin prefabs**: no se convierten bloques vanilla en bloques custom.
- **Sin cambios al inventario vanilla**: el jugador sigue crafteando y gestionando items normalmente.
- **Sin dimensiones ni worldgen custom**: todo ocurre en el overworld vanilla.

---

## Features V1 implementadas

| Sistema | Descripción |
|---------|-------------|
| Tool Cupboard (2 tiers) | Tier 1 protege 1 chunk; tier 2 protege 3×3 chunks. |
| Autorización manual | Owner añade UUIDs autorizados; no hay clanes. |
| Upkeep y decay | El TC consume recursos según bloques protegidos; si falla, los bloques bajan de tier progresivamente hasta STRAW y luego pierden HP. |
| Bastón de protección | Aplica tiers STRAW/WOOD/STONE/METAL/HQ a bloques individuales dentro del claim, consumiendo material del inventario. |
| C4 de raid | Colocable en caras de bloques. Al activarse daña los 6 bloques adyacentes protegidos (solo protegidos). |
| Taladro de raid | Daño progresivo sobre bloques protegidos; consume durabilidad. |
| Filtro PvP vanilla | Espadas, arcos, ballestas y tridentes vanilla no dañan entre jugadores. El daño indirecto (TNT, lava, caída) sí persiste. |
| Scrap pistol | Arma del mod que sí permite dañar jugadores; cooldown configurable. |
| Sleeping bag | Colocable libremente. Tras muerte PvP, respawnea en el sleeping bag con cooldown de 60s. Muerte PvE usa vanilla. |
| Anti-bypass de protección | TNT, creepers, lava, fuego, pistones y fluidos no destruyen ni mueven bloques protegidos. |
| Assets originales | Texturas 16×16 pixel art procedural para los 4 bloques y 3 items del mod. |
| Recetas y loot | 7 recetas crafteables, 4 loot tables, creative tab propia y traducciones EN/ES. |

---

## Gameplay loop (V1)

1. **Craftear** un Tool Cupboard tier 1 y colocarlo en tu base.
2. **Autorizar** a tus aliados manualmente (interactuando con el TC).
3. **Proteger** bloques importantes con el bastón, eligiendo tier según material disponible.
4. **Mantener** recursos en el TC para que el upkeep no degrade tus defensas.
5. **Atacar** bases enemigas con C4 (daño en área) o taladro (daño sostenido).
6. **PvP** usando la scrap pistol; las armas vanilla están deshabilitadas entre jugadores.
7. **Colocar** un sleeping bag para respawnar cerca tras morir en PvP.

---

## Construcción y ejecución

Requisitos:
- Java 17 (`/usr/lib/jvm/java-17-openjdk-amd64`)
- Gradle wrapper incluido (no necesitas Gradle global)

```bash
# Compilar y generar el .jar
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
./gradlew build --no-daemon

# El .jar final estará en:
# build/libs/minerust-0.1.0.jar

# Cliente de desarrollo (para QA manual visual)
./gradlew runClient --no-daemon

# Servidor local de desarrollo (para QA manual multijugador)
./gradlew runServer --no-daemon

# Generar configuraciones de ejecución para IntelliJ
./gradlew genIntellijRuns --no-daemon
```

Configuración del mod se genera en `run/config/minerust-common.toml` tras la primera ejecución.

---

## Estructura del proyecto

```
src/main/java/com/minerust/
├── MineRustMod.java              # Clase principal, registra event bus
├── Config.java                   # ForgeConfigSpec con valores de balance
├── claim/
│   ├── ToolCupboardBlock.java
│   ├── ToolCupboardBlockEntity.java
│   ├── ToolCupboardClaimManager.java
│   └── ToolCupboardUpkeepManager.java
├── blockhealth/
│   └── ProtectionTier.java       # Enum de tiers STRAW/WOOD/STONE/METAL/HQ
├── combat/
│   └── ScrapPistolItem.java
├── data/
│   └── ClaimSavedData.java       # Persistencia NBT server-side
├── events/
│   ├── ClaimProtectionEvents.java # Cancelaciones de protección y PvP
│   └── RespawnEvents.java
├── item/
│   └── ProtectionStaffItem.java
├── networking/
│   ├── ModNetworking.java
│   └── packet/                   # 3 packets skeleton (sin sync aún)
├── raid/
│   ├── C4ChargeBlock.java
│   ├── RaidDamageHelper.java
│   └── RaidDrillItem.java
├── registry/
│   ├── ModBlocks.java
│   ├── ModItems.java
│   ├── ModBlockEntities.java
│   └── ModCreativeTabs.java
└── respawn/
    └── SleepingBagBlock.java

src/main/resources/
├── META-INF/mods.toml
├── pack.mcmeta
├── assets/minerust/
│   ├── blockstates/
│   ├── models/block/ & item/
│   ├── textures/block/ & item/
│   └── lang/en_us.json, es_es.json
└── data/minerust/
    ├── recipes/
    └── loot_tables/blocks/
```

---

## QA checklist para agentes futuros (Final Wave)

Esta checklist debe ejecutarse manualmente antes de dar V1 por terminada. **No marques como pasado sin haber ejecutado el escenario.**

### Build automatizado
- [ ] `./gradlew build --no-daemon` → `BUILD SUCCESSFUL`

### Tool Cupboard y autorización
- [ ] Colocar TC tier 1; al romperlo dropea el item.
- [ ] Reiniciar servidor; TC y claim persisten.
- [ ] Jugador autorizado puede romper/colocar bloques dentro del claim.
- [ ] Jugador no autorizado NO puede romper ni colocar bloques.

### Bastón de protección
- [ ] Bastón aplica tier WOOD a stone dentro del claim (consume planks).
- [ ] Bastón falla fuera de claim con mensaje.
- [ ] Bastón falla para jugador no autorizado.
- [ ] Tier persiste tras reinicio.

### Raid (C4 y taladro)
- [ ] C4 colocado en claim enemigo y activado daña bloques protegidos adyacentes.
- [ ] C4 no daña bloques no protegidos.
- [ ] Taladro aplica daño progresivo a bloque protegido y consume durabilidad.
- [ ] Taladro no daña bloques no protegidos (feedback de fallo).

### Anti-bypass vanilla
- [ ] TNT vanilla explota junto a stone protegido → stone permanece.
- [ ] Creeper explota junto a stone protegido → stone permanece.
- [ ] Lava vertida sobre stone protegido → no se transforma en cobblestone/obsidiana.
- [ ] Fuego colocado junto a bloque protegido de madera → no se quema (scanner server-tick lo extingue).
- [ ] Pistón intenta empujar stone protegido → evento cancelado.

### PvP y armas
- [ ] PlayerA ataca PlayerB con espada de hierro vanilla → daño cancelado.
- [ ] PlayerA dispara a PlayerB con arco vanilla → daño cancelado.
- [ ] PlayerA usa scrap pistol contra PlayerB → daño aplicado según config.
- [ ] PlayerA empuja a PlayerB a lava → daño por lava persiste (indirecto permitido).

### Sleeping bag
- [ ] Sleeping bag colocable en cualquier superficie.
- [ ] PlayerA muere por PvP → respawnea en sleeping bag, cooldown activo.
- [ ] PlayerA muere por PvE (mob/caída) → respawn vanilla (cama/spawn mundial).
- [ ] Sleeping bag no se destruye al respawnar.

### Assets y datos
- [ ] Creative tab "MineRust" muestra todos los items con texturas correctas.
- [ ] Recetas crafteables aparecen en mesa de crafteo.
- [ ] Bloques dropean su item al romperse.
- [ ] Tooltips y nombres renderizan en español si el cliente está en ES.

---

## Limitaciones conocidas (V1)

1. **Manual runClient/runServer no verificado en este entorno**
   - Las tareas 1-15 pasaron build, pero los escenarios de QA manual arriba listados **aún no han sido ejecutados**. El Final Wave F3 debe hacerlo.
2. **Java LSP no disponible**
   - `jdtls` no está instalado, por lo que no hay autocompletado ni diagnóstico en tiempo real en este workspace. El build de Gradle es la única verificación automatizada.
3. **Networking packets son skeleton**
   - `ModNetworking` registra 3 paquetes pero los handlers son no-op. La sincronización server→client no está implementada; el juego funciona porque toda la lógica crítica es server-side.
4. **Sleeping bag y C4 renderizan como cubos**
   - Los modelos usan `cube_all`. Para V1 es aceptable; futura iteración puede usar modelos planos (cama/botón).
5. **Balance inicial conservador sin playtesting real**
   - Upkeep, daño de C4/taladro, HP de tiers y costes de material son configurables pero no han sido ajustados con partidas reales.
6. **Sin anti-cheat server-side adicional**
   - El mod asume un servidor de confianza; no hay validaciones de paquetes custom ni rate limiting.

---

## Workflow para agentes futuros

1. **Leer este README** para entender el estado actual y las reglas de diseño.
2. **Revisar `.omo/plans/minerust-master-plan.md`** para ver qué tareas están completas y cuál es la siguiente.
3. **Consultar notepads** en `.omo/notepads/minerust-master-plan/` antes de preguntar por decisiones o problemas históricos.
4. **Ejecutar `./gradlew build --no-daemon`** tras cualquier cambio Java o recursos.
5. **No introducir TODO/FIXME/HACK/xxx** en código Java de producción; documenta futuras mejoras en notepads o issues.
6. **No modificar reglas de diseño** (no economía, no clanes, no prefabs, no inventario vanilla, no dimensiones) sin aprobación explícita.
7. **Si necesitas debuggear**: usa `./gradlew runServer --no-daemon` y conecta un cliente con `./gradlew runClient --no-daemon`; el código es server-side puro salvo assets.

---

## Notas técnicas

- **Java toolchain:** 17 (`/usr/lib/jvm/java-17-openjdk-amd64`)
- **Mappings:** official (nombres de Mojang)
- **Mod ID:** `minerust`
- **Package base:** `com.minerust`
- **Clase principal:** `MineRustMod`

---

## Autor

alvaro
