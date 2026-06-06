# MineRust V1 — QA Checklist Paso a Paso

> No pasar a la siguiente feature hasta que la actual tenga ✅ en TODOS sus checks.
> Si algo falla, se arregla antes de continuar.

---

## Feature 1: Security Panel (TC/claim controller)

### 1.1 Colocación básica
- [ ] Colocar Security Panel en suelo plano superflat
- [ ] El Security Panel **permanece** colocado (no desaparece)
- [ ] Se ve visualmente como un bloque (no invisible, no cubo rosa)
- [x] La pantalla apunta hacia el jugador que lo coloca, no siempre hacia la misma dirección
- [ ] Romper el Security Panel con pico devuelve el item
- [x] Mejorar la cobertura/rango del mismo Security Panel con el coste proporcional mostrado y verificar que también permanece
- [x] Tras mejorar la cobertura/rango, el botón/LED rojo del Security Panel cambia a verde

### 1.2 Overlap / Solapamiento
- [ ] Colocar Security Panel en una ubicación libre → éxito
- [x] Intentar colocar OTRO Security Panel demasiado cerca para alcance máximo 30×30 → mensaje de separación y NO se coloca
- [ ] Intentar colocar Security Panel lejano (sin overlap de alcance máximo) → éxito
- [ ] Romper Security Panel original → su zona queda libre
- [ ] Colocar Security Panel en la zona que acabas de liberar → éxito

### 1.3 Persistencia
- [ ] Colocar Security Panel, salir del servidor, volver a entrar
- [ ] El Security Panel sigue ahí, el claim sigue activo
- [x] `/give @s minerust:claim_debug_stick`, click derecho → muestra info del claim

### 1.4 Autorización
- [ ] Player1 coloca Security Panel
- [ ] Player2 (no autorizado) intenta romper bloque dentro del claim → NO puede
- [ ] Player1 hace sneak + click derecho en Security Panel con Player2 cerca (5 bloques) → mensaje "Authorized Player2"
- [ ] Player2 ahora puede romper/colocar bloques dentro del claim
- [ ] Player3 (no autorizado) intenta romper → sigue sin poder

### 1.5 Visualización de claim
- [x] Click derecho en Security Panel abre menú de opciones (no upgrade directo)
- [x] Desde el menú, botón de mejorar cobertura/rango → consume el coste proporcional mostrado y mejora
- [x] Tras mejorar desde el menú, el inventario visible actualiza diamonds/iron sin cerrar el panel
- [x] Romper un Security Panel mejorado → dropea el panel y la mitad de los recursos acumulados de upgrades
- [x] Intentar colocar otro Security Panel demasiado cerca → falla aunque ambos estén en nivel 1, porque se reserva el alcance máximo 30×30
- [ ] Colocar dos Security Panels con al menos 30 bloques de separación horizontal útil → permite que ambos puedan llegar a cobertura máxima sin solaparse
- [x] Desde el menú, botón Show/Hide Claim Bounds → activa/desactiva límites persistentes mientras construyes
- [x] Con límites activados, muestra:
  - Owner UUID
  - Cobertura / rango
  - Centro del panel
  - Límites cubiertos X/Z e Y
- [x] Partículas END_ROD visibles solo en los límites exteriores del claim, sin bordes internos
- [x] Nivel 1 limita construcción/protección a 10×60×10 bloques centrados en el Security Panel
- [x] Nivel 20 limita construcción/protección a 30×60×30 bloques centrados en el Security Panel
- [ ] Fuera del volumen exacto del claim, un jugador no autorizado puede construir como fuera del claim

---

## Feature 2: Bastón de Protección

### 2.1 Aplicar protección
- [ ] Dentro de un claim tuyo, usar bastón en un bloque de stone → aplica tier STRAW
- [ ] Shift + click derecho con bastón → cambia tier (STRAW → WOOD → STONE → METAL → HQ)
- [ ] Aplicar tier WOOD a stone → consume planks del inventario (si no creative)
- [ ] Intentar aplicar fuera de claim → mensaje "No Security Panel claim covers this block"
- [ ] Intentar aplicar en claim enemigo → mensaje "You must be the owner or authorized"

### 2.2 Anti-bedrock
- [ ] Intentar proteger bedrock → mensaje "This block cannot be protected" y NO se protege
- [ ] Intentar proteger barrier → mismo mensaje
- [ ] Intentar proteger command block → mismo mensaje

### 2.3 Persistencia
- [ ] Proteger bloque, salir, volver a entrar
- [ ] El bloque sigue protegido (debug stick o intentar romperlo con otro jugador)

### 2.4 Partículas visuales
- [ ] Sostener bastón → bloques protegidos por TI se marcan con partículas VERDES
- [ ] Los bloques protegidos por OTRO jugador NO se marcan verdes
- [ ] Las partículas son visibles y no spamean demasiado

---

## Feature 3: C4 de Raid

### 3.1 Colocación direccional
- [ ] Colocar C4 en cara superior de un bloque → se pega mirando hacia arriba
- [ ] Colocar C4 en cara norte de un bloque → se pega mirando hacia norte
- [ ] Colocar C4 en cara lateral de una pared → se pega a la pared correctamente
- [ ] El C4 no se ve transparente (no se ve el fondo a través)

### 3.2 Activación
- [ ] Click derecho en C4 colocado → desaparece y daña bloques adyacentes
- [ ] Si no hay bloques protegidos adyacentes → mensaje "no adjacent protected blocks were affected"
- [ ] Si hay bloques protegidos adyacentes → mensaje "damaged" y el C4 desaparece

### 3.3 Daño a protegidos
- [ ] Proteger 6 bloques alrededor de donde colocarás C4 (con bastón)
- [ ] Colocar C4 en el centro, activarlo
- [ ] Los 6 bloques adyacentes protegidos reciben daño (debug: verificar que bajan de HP)

### 3.4 No dañar no-protegidos
- [ ] Colocar C4 rodeado de bloques NO protegidos
- [ ] Activar C4 → los bloques no protegidos NO reciben daño

---

## Feature 4: Taladro de Raid

### 4.1 Uso básico
- [ ] Equipar taladro, click derecho en bloque protegido → empieza a usar
- [ ] Mantener click → daño progresivo cada segundo
- [ ] Soltar click → deja de dañar
- [ ] El taladro consume durabilidad

### 4.2 Visualización
- [ ] Sostener taladro → bloques protegidos por OTROS jugadores se marcan ROJOS
- [ ] Bloques protegidos por TI no se marcan rojos
- [ ] Bloques NO protegidos no se marcan

### 4.3 No funciona en no-protegidos
- [ ] Click derecho en bloque NO protegido → mensaje "This block is not protected"

---

## Feature 5: Scrap Pistol

### 5.1 Daño a jugadores
- [ ] Player1 dispara scrap pistol a Player2 → Player2 recibe daño
- [ ] Cooldown funciona (no puede spamear clicks)

### 5.2 No daña mobs (si está configurado así)
- [ ] Disparar a un mob → ¿recibe daño? (verificar según config)

---

## Feature 6: Sleeping Bag

### 6.1 Colocación
- [ ] Colocar sleeping bag en cualquier superficie
- [ ] Se ve como un bloque plano

### 6.2 Respawn PvP
- [ ] Player1 mata a Player2 en PvP
- [ ] Player2 respawnea en sleeping bag (no en spawn mundial)
- [ ] Sleeping bag NO se destruye

### 6.3 Cooldown
- [ ] Morir de nuevo en PvP dentro de 60 segundos → respawn vanilla (no en bag)
- [ ] Esperar 60 segundos, morir de nuevo → respawn en bag

### 6.4 Respawn PvE
- [ ] Morir por caída/mob (PvE) → respawn vanilla (cama o spawn mundial)
- [ ] NO usa sleeping bag para muerte PvE

---

## Feature 7: Filtro PvP

### 7.1 Daño vanilla cancelado
- [ ] Player1 pega a Player2 con espada de hierro → Player2 NO recibe daño
- [ ] Player1 dispara arco a Player2 → Player2 NO recibe daño
- [ ] Player1 tira tridente a Player2 → Player2 NO recibe daño

### 7.2 Scrap pistol funciona
- [ ] Player1 dispara scrap pistol a Player2 → Player2 SÍ recibe daño

### 7.3 Daño indirecto permitido
- [ ] Player1 empuja a Player2 a lava → Player2 recibe daño de lava
- [ ] Player1 usa TNT cerca de Player2 → Player2 recibe daño de explosión

---

## Feature 8: Anti-Bypass

### 8.1 TNT/Creeper
- [ ] Proteger bloque de stone
- [ ] Explosión de TNT junto al stone → stone permanece intacto
- [ ] Creeper explota junto al stone → stone permanece

### 8.2 Fuego
- [ ] Proteger bloque de madera
- [ ] Encender fuego junto a él → el fuego se apaga (scanner server-tick)
- [ ] La madera NO se quema

### 8.3 Lava
- [ ] Proteger bloque
- [ ] Verter lava sobre él → el bloque NO se transforma

### 8.4 Pistones
- [ ] Proteger bloque
- [ ] Pistón intenta empujarlo → se cancela

---

## Feature 9: Claim Debug Stick

- [x] Click derecho en aire → muestra info del claim actual (o "No claim")
- [x] Partículas visibles en bordes del volumen claimado
- [x] Info correcta: owner, nivel, cobertura y límites cubiertos

---

## Estado Global

- [x] `./gradlew build --no-daemon` → BUILD SUCCESSFUL (2026-06-06)
- [x] Sin errores en consola del servidor
- [x] Sin errores en consola del cliente (crash anterior de claim_debug_stick NO se reproduce)
- [x] Server.properties: `online-mode=false`, `enforce-secure-profile=false`
- [ ] Ops configurados para testing

### QA Automatizado Completado (Agente)

**Build y recursos:**
- ✅ Build pasa sin errores
- ✅ No hay referencias a `tool_cupboard_tier1` ni `tool_cupboard_tier2` en código Java ni recursos
- ✅ Modelo importado desde el GLTF real mediante Forge OBJ (`loader: forge:obj`), no recreado a mano
- ✅ Blockstate, recipe, loot table y lang files verificados
- ✅ Textura original 128x128 PNG válida; alpha 0 convertido a opaco para evitar caras/suelo invisibles
- ✅ `ModBlocks.TOOL_CUPBOARD` usa `.noOcclusion()` para que el modelo no cúbico no esconda suelo/caras vecinas
- ✅ Cliente carga textures en atlas sin errores de modelo

**Code Review:**
- ✅ ToolCupboardBlock: overlap check con reserva máxima en placement, upgrade por nivel, coste proporcional, refund parcial y mensajes al jugador correctos
- ✅ ToolCupboardBlockEntity: level clamp 1-20, persistencia NBT, owner/auth players
- ✅ ToolCupboardClaimManager: register/update/remove/wouldOverlap lógica correcta. Nivel 1 = 10×60×10 bloques centrados, Nivel 20 = 30×60×30 bloques centrados
- ✅ ClaimDebugStickItem: reporta owner, nivel, cobertura y límites cubiertos. Partículas END_ROD en bordes
- ✅ ClaimProtectionEvents: overlap prevention en place event, protección de bloques, anti-bypass (TNT, fuego, lava, pistones)

**Notas de implementación:**
- El campo interno `tier` sigue existiendo como nivel interno 1-20. El jugador ve "coverage upgrade" no "tier 2 panel"
- Upgrade fall-safe: si `updateClaimTier` devuelve false, hace rollback a tier 1
- Fix aplicado: crash `NullPointerException: Registry Object not present: minerust:claim_debug_stick` resuelto (ModCreativeTabs ahora referencia correctamente)

---

## Instrucciones de testing

1. Terminal 1: `cd /home/alvaro/MineRust && ./server.sh`
2. Terminal 2: `cd /home/alvaro/MineRust && ./player1.sh`
3. (Opcional) Terminal 3: `cd /home/alvaro/MineRust && ./player2.sh`
4. En Minecraft: Multiplayer → Direct Connect → `localhost`
5. Usar comandos OP para darte items: `/give @s minerust:tool_cupboard`, etc.
6. Screenshot (F2) de cada feature que funcione
7. Reportar fallos con: qué esperabas, qué pasó, screenshot si aplica
