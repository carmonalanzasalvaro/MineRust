# MineRust Roadmap Dashboard

> Fuente de verdad para que cualquier colaborador vea en GitHub qué existe, qué falta confirmar, qué se descubrió durante el desarrollo y cuál es el siguiente paso.
>
> Regla de oro: cuando aparece un cambio no previsto, se añade aquí como decisión, check de QA o deuda antes de cerrar la tarea.

---

## Vista rápida

| Área | Estado | Evidencia | Siguiente acción |
|---|---:|---|---|
| Build Java/resources | ✅ Verde | `./gradlew build --no-daemon` pasa | Mantener verde tras cada cambio |
| Cliente dev smoke | ✅ Carga recursos | `runClient` carga ResourceManager/atlas sin crash | Falta QA interactivo in-game |
| Security Panel / claims | 🟡 Parcialmente confirmado | Orientación, reserva 30×30, upgrade nivel 20, GUI, descuento visual y refund confirmados | Probar loot, separación positiva, autorización, persistencia y reglas con 2 jugadores |
| Claim debug/bounds | ✅ Confirmado manual | Debug/bounds funcionan y coinciden con límites reales | Mantener como regresión en futuros cambios de claims |
| Protección/raid core | 🟡 Code-complete | Build verde y eventos server-side implementados | Ejecutar Final Wave con player1/player2 |
| PvP/sleeping bag | 🟡 Code-complete | Build verde | Probar muertes PvP/PvE y cooldown |
| Documentación operativa | ✅ Activa | Skill `minerust-context` obliga docs gate | Mantener este dashboard actualizado |

Leyenda: ✅ confirmado automatizado o regla activa · 🟡 implementado pendiente de QA manual · 🔴 bloqueado o roto · ⚪ planificado.

---

## Estado actual: V1 Final Wave

V1 está **code-complete**, pero no debe considerarse release hasta completar QA manual con servidor y dos clientes.

### Entregado

- [x] Security Panel único (`minerust:tool_cupboard`) en vez de tier1/tier2 separados.
- [x] Nombre visible Security Panel / Panel de Seguridad.
- [x] Modelo 3D TV importado desde asset real vía Forge OBJ.
- [x] GUI del Security Panel con upgrade, bounds y autorización cercana.
- [x] Claim centrado en bloque del panel, no en chunk.
- [x] 20 niveles de cobertura horizontal: nivel 1 `10x10`, nivel 20 `30x30`.
- [x] Altura real del claim: 60 bloques totales.
- [x] GUI muestra solo huella horizontal (`10x10`…`30x30`), no `x60`.
- [x] Coste de upgrade proporcional al área horizontal cubierta.
- [x] Inventario se sincroniza visualmente al upgradear sin cerrar menú.
- [x] Romper panel mejorado devuelve la mitad del coste acumulado de upgrades.
- [x] Placement reserva alcance máximo: dos paneles no pueden colocarse tan cerca que luego choquen a `30x30`.
- [x] Bounds visibles persistentes, sin bordes internos, con esquinas reforzadas tipo beacon.
- [x] Debug stick reporta owner, nivel, cobertura y límites.
- [x] Protección por autorización dentro del volumen exacto.
- [x] Anti-bypass básico: TNT, creepers, lava/fuego, pistones/fluidos contra bloques protegidos.
- [x] C4 direccional y taladro de raid.
- [x] Scrap pistol y filtro PvP vanilla.
- [x] Sleeping bag con cooldown PvP.
- [x] Docs gate local en `.opencode/skills/minerust-context/SKILL.md`.

### Confirmado manualmente

- [x] Claim debug/bounds funciona correctamente.
- [x] Modelo del Security Panel orienta correctamente.
- [x] Paneles demasiado cercanos fallan desde nivel 1 por reserva máxima 30×30.
- [x] Security Panel puede subir hasta nivel 20.
- [x] GUI del Security Panel aceptable por ahora.
- [x] Descuento visual de diamonds/iron al upgradear se actualiza sin cerrar menú.
- [x] Refund al romper Security Panel mejorado funciona.
- [x] Bounds visibles coinciden con el volumen real.

### Pendiente de confirmar manualmente

- [ ] Confirmar loot/drop base del Security Panel al romper uno sin upgrades.
- [ ] Confirmar que dos paneles suficientemente separados pueden subir ambos a nivel 20 sin solaparse.
- [ ] Revisar posible mejora futura de GUI aunque la versión actual queda aceptada para V1.
- [ ] Confirmar que fuera del volumen exacto un no autorizado puede construir/romper normal.
- [ ] Confirmar autorización de un segundo jugador desde menú.
- [ ] Confirmar protección staff en claim propio, fuera de claim y claim enemigo.
- [ ] Confirmar C4/taladro contra protegidos y no protegidos.
- [ ] Confirmar anti-bypass vanilla con TNT, creeper, fuego, lava, pistón y fluidos.
- [ ] Confirmar PvP vanilla bloqueado y scrap pistol permitido.
- [ ] Confirmar sleeping bag: PvP usa bag con cooldown, PvE usa vanilla.

---

## Histórico de rumbo y decisiones descubiertas

| Fecha | Decisión / descubrimiento | Impacto en roadmap | Estado |
|---|---|---|---|
| 2026-06-06 | GLTF no se carga directo como block model Forge 1.20.1 sin renderer/loader custom. | Convertir asset TV real a OBJ y documentar licencia CC BY 4.0. | ✅ Hecho |
| 2026-06-06 | Modelo no cúbico oculta suelo/caras vecinas si usa occlusion normal. | Añadir `.noOcclusion()` al Security Panel. | ✅ Hecho |
| 2026-06-06 | Tier1/tier2 separados no encajan con el diseño deseado. | Un solo item/bloque `minerust:tool_cupboard` con niveles internos. | ✅ Hecho |
| 2026-06-06 | Claim por chunk no permite cobertura exacta ni UX clara. | Claims pasan a volumen centrado por posición del panel. | ✅ Hecho |
| 2026-06-06 | Altura “30 arriba y 30 abajo” generaba 61 bloques totales si se incluía el bloque central simétrico. | Ajustar cálculo a 60 bloques totales y ocultar altura en GUI. | ✅ Hecho |
| 2026-06-06 | Upgrade descontaba items server-side pero el menú no refrescaba inventario. | Forzar sync/broadcast de inventario tras consumir coste. | ✅ Hecho |
| 2026-06-06 | Placement por nivel actual permitía paneles que luego colisionaban al upgradear. | Reservar desde placement el alcance máximo 30×30 contra paneles existentes. | ✅ Hecho |
| 2026-06-06 | Las tareas de código dejaban docs parciales si no había regla explícita. | Endurecer skill local: changelog/roadmap/readme/checklist son parte del cierre. | ✅ Hecho |

Si aparece una decisión nueva durante implementación o QA, añadir una fila aquí antes de cerrar.

---

## QA Final Wave V1

### Preparación

- [ ] `./gradlew build --no-daemon` → `BUILD SUCCESSFUL`.
- [ ] `./gradlew runServer --no-daemon` o `./server.sh` arranca sin crash.
- [ ] `./player1.sh` conecta a localhost.
- [ ] `./player2.sh` conecta a localhost.
- [ ] OP/creative configurado para dar items.
- [ ] Capturas guardadas en `run/screenshots/` para evidencias visuales.

### Security Panel

- [ ] `/give @s minerust:tool_cupboard` entrega un Security Panel con nombre correcto.
- [x] Colocación en suelo plano mantiene el bloque y orienta la pantalla hacia el jugador.
- [x] Menú abre con click derecho.
- [x] Nivel inicial muestra `Level 1/20` y área `10x10`.
- [x] Botón upgrade exige coste proporcional y no permite upgrade sin items suficientes.
- [x] Upgrade consume items y el inventario visible se actualiza sin cerrar menú.
- [x] Nivel máximo muestra `Level 20/20`, área `30x30` y `Next: Max level`.
- [x] Romper panel mejorado devuelve item + mitad de recursos acumulados de upgrades.
- [x] Placement demasiado cercano muestra mensaje de separación por máximo alcance.
- [ ] Placement separado permite dos paneles coexistiendo y subir ambos sin colisión.

### Claims, visualización y autorización

- [x] `Show/Hide Claim Bounds` activa/desactiva partículas persistentes.
- [x] Bounds dibuja un prisma exterior sin líneas internas.
- [x] Esquinas cyan/beacon son visibles a distancia razonable.
- [x] Debug stick muestra owner, nivel, cobertura, centro del panel y límites X/Z/Y.
- [ ] No autorizado no puede romper/colocar dentro del volumen exacto.
- [ ] No autorizado sí puede romper/colocar fuera del volumen exacto.
- [ ] Owner autoriza Player2 desde menú y Player2 puede construir dentro.
- [ ] Persistencia: reiniciar servidor conserva panel, owner, nivel, autorizados y claim.

### Protección y raid

- [ ] Bastón protege bloque dentro del claim propio y consume material correcto.
- [ ] Bastón falla fuera de claim con mensaje correcto.
- [ ] Bastón falla en claim enemigo sin autorización.
- [ ] Bastón rechaza bedrock/barrier/command blocks.
- [ ] C4 colocado en claim enemigo daña bloques protegidos adyacentes.
- [ ] C4 no daña bloques no protegidos.
- [ ] Taladro aplica daño progresivo y consume durabilidad.
- [ ] Taladro no daña bloques no protegidos.

### Anti-bypass y PvP

- [ ] TNT vanilla no rompe bloque protegido.
- [ ] Creeper no rompe bloque protegido.
- [ ] Lava/fuego no transforma/quema bloque protegido.
- [ ] Pistón/fluidos no mueven ni reemplazan bloque protegido.
- [ ] Espada/arco/ballesta/tridente vanilla no dañan otro jugador.
- [ ] Scrap pistol sí daña otro jugador con cooldown.
- [ ] Daño indirecto permitido sigue funcionando cuando aplica.

### Sleeping bag

- [ ] Sleeping bag se coloca y renderiza.
- [ ] Muerte PvP respawnea en bag y activa cooldown.
- [ ] Morir de nuevo durante cooldown usa respawn vanilla.
- [ ] Muerte PvE usa respawn vanilla.

---

## Deuda técnica / mejoras detectadas

| Prioridad | Item | Motivo | Confirmación esperada |
|---|---|---|---|
| Alta | QA manual Final Wave | Build/smoke no sustituyen jugar escenarios reales. | Checklist V1 completada con capturas. |
| Alta | Networking packets skeleton | Actualmente algunos handlers son no-op; revisar si todos siguen siendo necesarios. | Decidir: implementar sync real o eliminar packets no usados. |
| Media | Renombrar semántica interna `tier`→`level` | El campo funciona, pero confunde al leer código. | Refactor seguro manteniendo NBT `tier` por compatibilidad. |
| Media | Balance de upgrades/upkeep/raid | Costes calculados por área, sin playtesting. | Tabla de balance tras 1-2 sesiones reales. |
| Media | Modelos sleeping bag/C4 | Algunos modelos siguen simples. | Mejoras visuales sin romper hitbox/placement. |
| Baja | Mensajes i18n completos | Hay mensajes literales en Java. | Migrar textos visibles a lang files. |

---

## Iteraciones futuras

### V1.1 — Supervivencia y combate

Objetivo: loop de supervivencia completo: conseguir recursos, craftear armas básicas, curarse y defenderse.

- [ ] Nodos de recursos con drops estilo Rust.
- [ ] Piedra de mano inicial.
- [ ] Lanza throwable.
- [ ] Machete.
- [ ] Crossbow.
- [ ] Pipe shotgun.
- [ ] F1 grenade y beancan grenade.
- [ ] Bandage y syringe/medkit.
- [ ] Armaduras: burlap, wood/roadsign, metal, HQ.
- [ ] Backpacks small/large sin romper inventario vanilla.
- [ ] Barriles/crates/componentes básicos.
- [ ] Radiación básica.

QA gate V1.1:
- [ ] Farmear árbol/roca con herramienta inicial.
- [ ] Usar lanza, crossbow y shotgun contra mobs/jugadores.
- [ ] Curarse con bandage/medkit.
- [ ] Confirmar reducción de daño por armadura.
- [ ] Lootear barrel/crate.
- [ ] Entrar en zona radiada sin protección y recibir daño.

### V1.2 — Bases y defensa

- [ ] High external walls.
- [ ] Gates.
- [ ] Barricades.
- [ ] Bear trap.
- [ ] Landmine.
- [ ] Shotgun trap.
- [ ] Auto turret.
- [ ] Ladders.
- [ ] Code lock y key lock.
- [ ] Double doors, garage door, hatch.
- [ ] Shooting floor pieces.

### V1.3 — Raiding avanzado

- [ ] Satchel charge.
- [ ] Rocket launcher: basic, HV, incendiary.
- [ ] Explosive 5.56.
- [ ] Timed explosive charge balance pass.
- [ ] Pickaxe/fire raiding para puertas de madera.
- [ ] Tabla visible de eficiencia de raid.

### V1.4 — Progresión sin economía compleja

Nota: sigue prohibida una economía de jugadores tipo monedas/NPCs compradores. Esta iteración trata progresión PvE/crafting.

- [ ] Workbench tiers.
- [ ] Research table y blueprints.
- [ ] Recycler.
- [ ] Componentes no crafteables.
- [ ] Scrap como recurso de progresión, no moneda de economía global.
- [ ] Vending machine con trueque directo.

### V1.5 — Mundo y PvE

- [ ] Monumentos básicos.
- [ ] Airdrop.
- [ ] Chinook/locked crate.
- [ ] Attack helicopter.
- [ ] Bradley.
- [ ] Scientists.
- [ ] Radiación avanzada.
- [ ] Hazmat suit.
- [ ] Keycards.
- [ ] Animales mejorados.

### V1.6 — Calidad de vida

- [ ] Mapa con posición, zonas claimadas y monumentos descubiertos.
- [ ] Flashlight, lasersight, silenciador, holosight.
- [ ] Comida/agua.
- [ ] Sleeping bags múltiples con selección.
- [ ] Boats.
- [ ] Horses.
- [ ] Mixing table.

### V2.0 — Electricidad y automatización

- [ ] Wind turbine / solar panel.
- [ ] Small/large battery.
- [ ] Switch, timer, pressure pad.
- [ ] HBHF sensor, laser detector.
- [ ] Auto turret eléctrica, SAM site, CCTV, door controller.
- [ ] AND/OR/NOT, memory cell, counter.

---

## Reglas de actualización del roadmap

- [ ] Cada cambio de comportamiento añade o actualiza checks de QA.
- [ ] Cada decisión descubierta durante implementación se añade al histórico.
- [ ] Cada deuda o mejora encontrada se añade a deuda técnica o iteración futura.
- [ ] Si una feature planificada cambia de rumbo, se actualiza aquí antes de cerrar la tarea.
- [ ] `CHANGELOG.md`, `README.md` y `CHECKLIST-V1.md` deben quedar sincronizados si el cambio afecta usuario, QA o estado.

---

## Reglas de diseño

1. Sin economía compleja de jugadores.
2. Sin clanes/equipos automáticos; autorización manual por UUID.
3. Sin prefabs que conviertan Minecraft en otro juego.
4. Sin cambios al inventario vanilla como contrato base.
5. Sin dimensiones/worldgen custom en V1.
6. Lógica crítica server-side; cliente solo para UI/render.
7. No imports `net.minecraft.client` en código común.
8. Si una feature rompe algo anterior, se arregla antes de avanzar.

---

## Repo

https://github.com/carmonalanzasalvaro/MineRust
