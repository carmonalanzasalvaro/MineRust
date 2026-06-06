# MineRust Roadmap — De V1 a Rust completo

> Documento vivo. Cada iteración debe completarse al 100% (build + QA manual) antes de pasar a la siguiente.
> **Regla de oro:** si una feature rompe algo anterior, se arregla antes de continuar.

---

## Estado actual: V1 completada

**Lo que ya funciona:**
- Tool Cupboard (2 tiers) + autorización manual + upkeep/decay
- Bastón de protección (5 tiers: STRAW → HQ)
- C4 direccional (coloca en cualquier cara)
- Taladro de raid (daño progresivo)
- Scrap pistol (arma a distancia del mod)
- Sleeping bag (respawn PvP con cooldown)
- Filtro PvP vanilla (desactiva melee/rango vanilla entre jugadores)
- Anti-bypass vanilla (TNT, lava, fuego, pistones no rompen bloques protegidos)
- Claim debug stick (visualización de chunks + partículas)

---

## Iteración 2 — "Supervivencia y combate" (V1.1)

### Objetivo
Tener un loop de supervivencia completo: conseguir recursos, craftear armas básicas, curarse, y defenderse.

### Features
1. **Nodos de recursos** (árboles, rocas, minerales que dropean más cantidad que vanilla)
2. **Piedra de mano** — Herramienta inicial (equivalente al "rock" de Rust)
3. **Lanza/spear** — Arma melee throwable
4. **Machete** — Arma melee rápida
5. **Crossbow** — Arma a distancia de tier bajo
6. **Escopeta de tubo** (pipe shotgun) — Escopeta crafteable de tier medio
7. **Granadas**
   - F1 Grenade (explosiva básica)
   - Beancan Grenade (inestable, barata)
8. **Médicos**
   - Bandage (cura pequeña, para bleeding)
   - Syringe/Medkit (cura grande)
9. **Armaduras** (4 tiers)
   - Burlap (tela) — tier 1
   - Wood/Roadsign — tier 2
   - Metal — tier 3
   - HQ (high quality) — tier 4
10. **Bolsas** — Extra inventory slots (small/large backpack)
11. **Loot del mundo**
    - Barriles flotantes en playas
    - Crates en radtowns (monumentos básicos)
    - Componentes (scrap, gears, pipes, springs, etc.)
12. **Radiación básica** — Zonas con radiación (cerca de monumentos), requiere protección

### QA Gate
- [ ] Craftear piedra de mano y farmear árbol/roca
- [ ] Craftear lanza, matar mob, lanzarla
- [ ] Craftear crossbow, disparar a otro jugador
- [ ] Craftear escopeta, recargar, disparar
- [ ] Usar bandage mientras tienes "bleeding" (si implementamos bleeding)
- [ ] Equipar armadura y ver reducción de daño
- [ ] Encontrar barrel/crate en el mundo y lootearlo
- [ ] Entrar en zona radiada sin protección → recibir daño

---

## Iteración 3 — "Bases y defensa" (V1.2)

### Objetivo
Poder construir bases defensivas reconocibles como bases de Rust.

### Features
1. **High External Walls** — Muros altos de madera/piedra/metal para rodear bases
2. **Gates** — Puertas grandes para high external walls
3. **Barricades** — Obstáculos temporales (madera con pinchos, etc.)
4. **Traps**
   - Bear Trap — Trampa de oso (daño + bleed)
   - Landmine — Mina terrestre (explota al pisar)
   - Shotgun Trap — Trampa de escopeta automática (requiere munición)
5. **Auto Turret** — Torreta automática (requiere munición + electricidad básica)
6. **Ladders** — Escaleras para raid/trepado (se colocan en paredes enemigas)
7. **Code Lock** — Cerradura con código numérico para puertas y cofres
8. **Key Lock** — Cerradura con llave física (tier bajo)
9. **Double Doors** — Puertas dobles (garage door)
10. **Hatch** — Escotillas en el suelo
11. **Shooting Floor** — Plataformas con almenas para defender

### QA Gate
- [ ] Colocar high external wall alrededor de una base
- [ ] Craftear code lock, poner código, abrir/cerrar puerta
- [ ] Colocar bear trap, pisarla, recibir daño
- [ ] Colocar auto turret, cargar munición, que dispare a jugador enemigo
- [ ] Colocar ladder en pared enemiga y trepar

---

## Iteración 4 — "Raiding avanzado" (V1.3)

### Objetivo
Tener múltiples opciones de raid con diferentes costes/beneficios.

### Features
1. **Satchel Charge** — Explosivo barato pero inconsistente (puede no explotar)
2. **Rocket Launcher** — Lanzacohetes
   - Rocket básico
   - High Velocity Rocket
   - Incendiary Rocket
3. **Explosive 5.56** — Munición explosiva para rifles
4. **Timed Explosive Charge** — C4 mejorado (nuestro actual es esto)
5. **Pickaxe raiding** — Romper puertas de madera a picos (muy lento)
6. **Fire raiding** — Flechas de fuego para quemar puertas de madera
7. **Raiding balance**
   - Costes de cada explosivo vs. HP de cada tier de puerta/pared
   - Tabla de eficiencia visible para jugadores

### QA Gate
- [ ] Raidar puerta de madera con satchels
- [ ] Raidar puerta metal con rockets
- [ ] Comparar coste C4 vs rockets vs satchels para misma puerta
- [ ] Que fire arrows quemen puerta de madera

---

## Iteración 5 — "Progresión y economía simple" (V1.4)

### Objetivo
Sistema de progresión que obligue a los jugadores a avanzar en tiers.

### Features
1. **Workbench tiers** (3 niveles)
   - Tier 1: armas melee, crossbow, armadura burlap
   - Tier 2: escopeta, pistol, armadura metal
   - Tier 3: rifle, rockets, armadura HQ, C4
   - Sin workbench del tier adecuado, no puedes craftear
2. **Research Table** — Aprender blueprints
   - Poner un item en la mesa + scrap → aprendes el blueprint
   - Una vez aprendido, puedes craftearlo en el workbench adecuado
3. **Recycler** — Máquina que rompe items en componentes
   - Metes un arma rota → sacas springs, gears, scrap
4. **Componentes** (items no crafteables, solo loot)
   - Semi Body, Rifle Body, SMG Body
   - Spring, Gear, Pipe, Rope, Sheet Metal
   - Tech Trash, Targeting Computer
5. **Scrap** — Moneda de progresión (no es economía de jugadores, es progresión PvE)
6. **Vending Machine** — Intercambiar items por otros items
   - Sin moneda, trueque directo
   - Ejemplo: pones 100 scrap y sacas 1 rifle body

### QA Gate
- [ ] Intentar craftear rifle sin workbench tier 3 → falla
- [ ] Colocar workbench tier 3, intentar de nuevo → éxito
- [ ] Poner item en research table, pagar scrap, aprender blueprint
- [ ] Meter item en recycler, obtener componentes
- [ ] Configurar vending machine con oferta, otro jugador compra

---

## Iteración 6 — "Mundo y PvE" (V1.5)

### Objetivo
El mundo no es solo PvP, tiene peligros ambientales y eventos PvE.

### Features
1. **Monumentos** (estructuras prefabricadas en el mundo)
   - Radtown pequeña (casas abandonadas con loot y radiación)
   - Gas Station
   - Supermarket
   - Lighthouse
   - Warehouse
2. **Airdrop** — Avión que pasa dejando un paquete con loot bueno
3. **Chinook** — Helicóptero que deja locked crate (requiere tiempo para abrir)
4. **Attack Helicopter** — Heli hostil que ataca jugadores con armadura/arma
5. **Bradley** — Tanque que patrulla monumento militar, dropea loot bueno
6. **Scientists** — NPCs hostiles en monumentos (con pistolas, se cubren)
7. **Radiation avanzada** — Necesitas traje hazmat para entrar a monumentos altos
8. **Hazmat Suit** — Armadura especial contra radiación
9. **Keycards** — Tarjetas para abrir puertas en monumentos
   - Green card → abre puerta verde → acceso a blue card
   - Blue card → acceso a red card
   - Red card → loot militar
10. **Animales mejorados** — Osos, lobos más peligrosos, mejor AI

### QA Gate
- [ ] Visitar radtown, encontrar barrel/crate, lootear
- [ ] Ver airdrop caer, ir a por él, pelear con otros jugadores
- [ ] Entrar en monumento con radiación sin hazmat → morir
- [ ] Equipar hazmat, entrar, sobrevivir
- [ ] Encontrar green card, usarla en puerta correspondiente
- [ ] Heli ataca a jugador con armadura metal, no ataca a desnudo

---

## Iteración 7 — "Calidad de vida" (V1.6)

### Objetivo
Cosas que no son core pero hacen la experiencia mucho mejor.

### Features
1. **Mapa** (UI simple)
   - Muestra tu posición, chunks claimados, monumentos descubiertos
   - Marcadores personales
2. **Equipamiento táctico**
   - Flashlight (arma con luz)
   - Lasersight (mejora puntería)
   - Silenciador (reduce sonido)
   - Holosight (mira holográfica)
3. **Comida y agua**
   - Camping (carne de cerdo/ciervo más comida que vanilla)
   - Purificar agua
   - Conservas (latas de comida que no se estropean)
4. **Sleeping bag mejorado**
   - Múltiple sleeping bags por jugador
   - UI para seleccionar en qué bag respawnear
5. **Boats** — Barcos básicos para navegar
6. **Horses** — Caballos para moverse rápido
7. **Heli vendor** — Vendedor que llega en heli y vende items raros
8. **Mixing Table** — Craftear medicinas avanzadas

### QA Gate
- [ ] Abrir mapa, ver posición y claims
- [ ] Craftear flashlight, usar de noche
- [ ] Respawnear en sleeping bag seleccionado
- [ ] Montar caballo, moverse rápido

---

## Iteración 8 — "Electricidad y automatización" (V2.0)

### Objetivo
Sistema eléctrico completo como en Rust.

### Features
1. **Generadores**
   - Wind Turbine (genera electricidad del viento)
   - Solar Panel (de día)
   - Small/Large Battery (almacena)
2. **Componentes eléctricos**
   - Switch (interruptor manual)
   - Timer (activa durante X segundos)
   - Pressure Pad (se activa al pisar)
   - HBHF Sensor (detecta heat/movement)
   - Laser Detector (rayo láser que detecta)
3. **Consumidores**
   - Auto Turret (ahora requiere electricidad)
   - SAM Site (anti-aéreo, dispara a heli/avión)
   - CCTV Camera (ver otras zonas en pantalla)
   - Door Controller (abre puertas remotamente)
   - Heater (calefacción para base)
   - Lights (luces eléctricas)
4. **Combinadores**
   - AND, OR, NOT gates
   - Memory Cell (flip-flop)
   - Counter (cuenta pulsos)

### QA Gate
- [ ] Conectar wind turbine → battery → auto turret → funciona
- [ ] Colocar HBHF sensor, pasar cerca → activa luz
- [ ] Colocar CCTV, ver otra zona en pantalla

---

## Reglas de diseño (aplican a todas las iteraciones)

1. **Sin economía de jugadores** — No hay tiendas, monedas, ni NPCs vendedores que compren. Solo trueque directo o vending machines.
2. **Sin clanes complejos** — Autorización manual por UUID, sin grupos automáticos.
3. **Sin prefabs** — No convertir bloques vanilla en custom. Minecraft es Minecraft.
4. **Sin cambiar inventario vanilla** — El jugador gestiona items normalmente.
5. **Sin dimensiones ni worldgen custom** — Todo en overworld vanilla.
6. **Server-side first** — Lógica crítica en servidor. Cliente solo para render y UI básica.
7. **No imports `net.minecraft.client`** en código común.

---

## Prioridad de QA por iteración

Cada iteración requiere:
1. `./gradlew build --no-daemon` → BUILD SUCCESSFUL
2. Test manual mínimo 30 min con `player1.sh` + `player2.sh`
3. Capturas de pantalla de features nuevas en `run/screenshots/`
4. Actualizar CHANGELOG.md antes de mergear
5. Commit claro en git: `feat(iteration-X): descripción`

---

## Notas para futuras IAs

- Este documento es la fuente de verdad para el roadmap.
- Nunca saltar una iteración. Completar V1.1 antes de tocar V1.2.
- Si el usuario pide algo que no está en la iteración actual, anotarlo en "Pendiente" pero no implementar hasta que toque.
- Siempre actualizar CHANGELOG.md con cada cambio significativo.
- El repo está en: https://github.com/carmonalanzasalvaro/MineRust
