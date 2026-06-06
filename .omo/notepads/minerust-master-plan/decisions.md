## 2026-06-06 Task: planning

- Minecraft 1.20.1 + Forge 47.3.0 + official mappings.
- Base package: `com.minerust`; mod id: `minerust`.
- Concept: SecurityCraft + Rust; Minecraft build freedom, no Rust-style prefabs.
- Tool Cupboard protects chunks in a grid: tier 1 = 1 chunk, tier 2 = 3x3 chunks.
- TC authorization: owner + manually authorized UUIDs; overlapping TCs are not allowed.
- Upkeep consumes wood/stone/metal according to protected block tiers; failure causes gradual decay.
- Defense tiers applied by staff: STRAW, WOOD, STONE, METAL, HQ.
- Raid is always vulnerable; no raid-safe windows.
- Only MineRust C4/drill damage protected blocks; vanilla TNT/creepers/lava/fire/pistons must not bypass protection.
- PvP filter cancels only direct vanilla melee/ranged weapon damage between players; indirect damage remains allowed.
- PvE death uses vanilla bed flow. PvP death uses MineRust sleeping bag with 60s cooldown per player; bag is reusable and freely placeable.
- Prefer Kimi-routed agents/models when available; use GPT/Oracle only for critical verification gates or when routing requires it.
