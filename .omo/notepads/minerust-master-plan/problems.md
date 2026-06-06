## 2026-06-06 Task: planning

- No blockers remaining for planning.
- Implementation should not start by adding everything in one agent prompt; follow waves in `.omo/plans/minerust-master-plan.md`.

## 2026-06-06 Task: 16. Limitaciones conocidas actuales (post-implementación V1)

1. **QA manual no ejecutado**
   - Ninguno de los escenarios de QA manual del README ha sido ejecutado aún (runClient/runServer con interacción real).
   - El Final Wave F3 debe ejecutarlos antes de aprobar V1.
   - Prioridad: CRÍTICA.

2. **Java LSP no disponible**
   - `jdtls` no está instalado en este workspace.
   - No hay autocompletado, navegación de símbolos ni diagnóstico en tiempo real.
   - `./gradlew build --no-daemon` es la única verificación automatizada confiable.
   - Prioridad: BAJA (no bloquea V1, mejora developer experience).

3. **Networking packets skeleton sin implementar**
   - `ModNetworking` registra 3 paquetes pero los handlers son no-op.
   - La sincronización server→client no está implementada; el juego funciona porque toda la lógica crítica es server-side.
   - Para V1 esto es aceptable, pero cualquier feature que requiera UI o feedback visual en client-side necesitará completar los handlers.
   - Prioridad: MEDIA (para V2 o polish).

4. **Modelos de sleeping bag y C4 son cubos**
   - Ambos usan `minecraft:block/cube_all`; visualmente no representan un saco de dormir plano ni una carga explosiva pequeña.
   - Aceptable para V1; puede mejorarse con modelos custom (carpet/button) en futura iteración.
   - Prioridad: BAJA.

5. **Balance sin playtesting real**
   - Upkeep costs, daño de C4/taladro, HP de tiers y costes de material son configurables pero no han sido validados con partidas reales.
   - Posible que valores por defecto sean excesivamente altos o bajos.
   - Prioridad: MEDIA (ajustar tras QA manual).

6. **Sin anti-cheat adicional**
   - No hay validación de paquetes custom ni rate limiting server-side.
   - El mod asume servidor de confianza.
   - Prioridad: BAJA (fuera de scope V1).

7. **Sleeping bag cooldown por jugador usa un solo timestamp global**
   - El cooldown actual es por jugador que muere, no por quién lo mató.
   - Si el diseño final requiere cooldown por asesino, requiere cambio de schema en `PlayerCooldownData`.
   - Prioridad: BAJA (diseño actual es aceptable para V1).
