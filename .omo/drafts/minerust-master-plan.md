# Draft: MineRust - Plan Maestro

## Estado Actual del Proyecto
- **MC Version**: 1.20.1
- **Forge**: 47.3.0
- **Mappings**: official
- **Package**: com.minerust
- **Mod ID**: minerust
- **Java**: 17
- **Build**: Funcionando (jar generado en build/libs/)

## Estructura Base Existente
- MineRustMod.java (clase principal)
- Config.java (configuración ForgeConfigSpec)
- Directorios vacíos: claim/, raid/, blockhealth/, economy/, networking/, commands/, config/, data/

## Roadmap Existente (del README)
1. Claims básicos (/claim, /unclaim, persistencia)
2. Protección de bloques (cancelar BreakBlock en claims ajenos)
3. Teams / parties (grupos para compartir claims)
4. Vida en bloques (HP por bloque, se rompe al llegar a 0)
5. Ventana de raid (horarios de vulnerabilidad)
6. Economía (recursos, tiendas, crafting)
7. Integración servidor (anti-grief, logs, balanceo)

## Respuestas del Usuario

### Concepto Principal
SecurityCraft + Rust: libertad de construcción de Minecraft (NO prefabs), con sistema de protección bloque a bloque usando un bastón/tool.

### Tier de Defensa (bastón)
Paja → Madera → Piedra → Metal → HQ (High Quality)
Cada tier otorga diferente resistencia a los bloques protegidos.

### Sistemas Obligatorios para V1
1. **Tool Cupboard (TC)** - crafteable, se debe alimentar para mantener protección
2. **Bastón de protección** - aplicar tier de defensa bloque a bloque
3. **Sistema de raideo** - ventanas de vulnerabilidad, C4, taladros
4. **Crafteables de raid**: C4, taladro (con texturas)
5. **Armas crafteables** (con texturas)
6. **Respawn**: spawn en casa, cooldown al reaparecer en cama

### PvP vs PvE
- **PvE**: Items del mod + vanilla
- **PvP**: SOLO items del mod (obliga a usar sistema Rust)
- Inventario vanilla NO se toca

### Assets
Completos: texturas para C4, armas, bloques nuevos, sonidos custom.

### Dificultad
Rust moderado: raideos con ventanas programadas, no 24/7 hardcore.

## Análisis Metis - Gaps Identificados

### Decisiones de Diseño Confirmadas

### Tool Cupboard (TC)
- **Protección por chunks en cuadrícula**:
  - Tier 1 TC → 1 chunk
  - Tier 2 TC → 3x3 chunks
- **Upkeep**: Consume madera/piedra/metal según tier de bloques protegidos, decay gradual (pierde 1 tier cada X horas hasta desprotegido)
- **Autorización**: Owner + jugadores autorizados manualmente. No solapamiento permitido.
- El TC tiene tiers que determinan el área de protección

### Sistema de Raid
- **Siempre vulnerable** (no hay ventanas de seguridad)
- Hardcore: cualquier momento es válido para raidear
- **Daño a protegidos**: SOLO C4 y taladro del mod pueden dañar bloques protegidos
- **Bypass vanilla**: TNT vanilla, creepers, lava, fuego, pistones NO afectan bloques protegidos

### Respawn
- **PvE death**: respawn vanilla en cama (como siempre)
- **PvP death**: sistema Rust con sleeping bag (saco)
  - Al morir en PvP, apareces al momento en el saco
  - Cooldown de 1 minuto en el saco antes de poder moverse/liberarse
  - El saco es un ítem del mod, se coloca en cualquier lado
  - No se destruye al usar, cooldown por jugador

### PvP Enforcement
- **Daño directo**: Armas melee/rango vanilla NO causan daño entre jugadores
- **Daño indirecto**: Lava, TNT, empujones, caídas SÍ funcionan (solo se cancela daño directo de arma)

### Tiers de Defensa (Bastón)
Paja → Madera → Piedra → Metal → HQ
- Aplica a bloques vanilla existentes
- Cada tier otorga diferente resistencia a daño
- Coste de material al aplicar según tier

### Suposiciones a Validar
- Protección por bloque via Capability/LevelSavedData sin lag
- PvP cancelable sin tocar inventario (solo cancelar daño a players)
- Tiers aplicables a cualquier bloque vanilla

### Riesgos Principales
- Scope creep: demasiados sistemas en V1
- Persistencia frágil de protección por bloque
- Bypass por mecánicas vanilla (TNT, lava, fuego, pistones)
- Balance de raideo descontrolado

### Guardrails (Must NOT Have V1)
- No prefabs
- No clanes/equipos complejos
- No economía
- No UI avanzada
- No modificar inventario vanilla
- No port a Fabric/NeoForge

## Notas Técnicas Clave
- DeferredRegister para registros
- Capability para datos por mundo/jugador/chunk
- LevelSavedData para persistencia servidor
- SimpleChannel para networking
- CommandDispatcher para comandos
- ForgeEvents para intercepción de eventos
