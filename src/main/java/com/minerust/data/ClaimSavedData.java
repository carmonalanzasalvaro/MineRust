package com.minerust.data;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.*;

/**
 * Server-side persistent storage for MineRust claim data, protected blocks, and player cooldowns.
 * Attached to the Overworld so it is global across all dimensions.
 */
public class ClaimSavedData extends SavedData {

    private static final String DATA_NAME = "minerust_claims";

    // --- Data stores ---

    /**
     * Tool cupboard claims keyed by dimension ResourceLocation string,
     * then by packed chunk position (chunkX, chunkZ).
     */
    private final Map<String, Map<Long, ToolCupboardData>> claimsByDimension = new HashMap<>();

    /**
     * Protected blocks keyed by dimension ResourceLocation string,
     * then by packed BlockPos.
     */
    private final Map<String, Map<Long, ProtectedBlockData>> protectedBlocksByDimension = new HashMap<>();

    /**
     * Player cooldowns keyed by player UUID.
     */
    private final Map<UUID, PlayerCooldownData> playerCooldowns = new HashMap<>();

    /**
     * Last time upkeep was processed. Epoch millis.
     */
    private long lastUpkeepTimestamp = 0;

    public ClaimSavedData() {
    }

    // --- Accessors ---

    public Map<String, Map<Long, ToolCupboardData>> getClaimsByDimension() {
        return claimsByDimension;
    }

    public Map<String, Map<Long, ProtectedBlockData>> getProtectedBlocksByDimension() {
        return protectedBlocksByDimension;
    }

    public Map<UUID, PlayerCooldownData> getPlayerCooldowns() {
        return playerCooldowns;
    }

    public long getLastUpkeepTimestamp() {
        return lastUpkeepTimestamp;
    }

    public void setLastUpkeepTimestamp(long value) {
        this.lastUpkeepTimestamp = value;
        setDirty();
    }

    // --- Mutators (call setDirty) ---

    public void addClaim(String dimension, int chunkX, int chunkZ, ToolCupboardData data) {
        claimsByDimension
            .computeIfAbsent(dimension, k -> new HashMap<>())
            .put(packChunk(chunkX, chunkZ), data);
        setDirty();
    }

    public void removeClaim(String dimension, int chunkX, int chunkZ) {
        Map<Long, ToolCupboardData> dimClaims = claimsByDimension.get(dimension);
        if (dimClaims != null) {
            dimClaims.remove(packChunk(chunkX, chunkZ));
            if (dimClaims.isEmpty()) {
                claimsByDimension.remove(dimension);
            }
            setDirty();
        }
    }

    public void addProtectedBlock(String dimension, BlockPos pos, ProtectedBlockData data) {
        protectedBlocksByDimension
            .computeIfAbsent(dimension, k -> new HashMap<>())
            .put(pos.asLong(), data);
        setDirty();
    }

    public void removeProtectedBlock(String dimension, BlockPos pos) {
        Map<Long, ProtectedBlockData> dimBlocks = protectedBlocksByDimension.get(dimension);
        if (dimBlocks != null) {
            dimBlocks.remove(pos.asLong());
            if (dimBlocks.isEmpty()) {
                protectedBlocksByDimension.remove(dimension);
            }
            setDirty();
        }
    }

    public void setPlayerCooldown(UUID playerUuid, PlayerCooldownData data) {
        playerCooldowns.put(playerUuid, data);
        setDirty();
    }

    public void removePlayerCooldown(UUID playerUuid) {
        if (playerCooldowns.remove(playerUuid) != null) {
            setDirty();
        }
    }

    public void clearAllData() {
        claimsByDimension.clear();
        protectedBlocksByDimension.clear();
        playerCooldowns.clear();
        setDirty();
    }

    public ToolCupboardData getClaimAtChunk(String dimension, int chunkX, int chunkZ) {
        Map<Long, ToolCupboardData> dimClaims = claimsByDimension.get(dimension);
        if (dimClaims == null) {
            return null;
        }
        return dimClaims.get(packChunk(chunkX, chunkZ));
    }

    // --- Serialization ---

    @Override
    public CompoundTag save(CompoundTag tag) {
        tag.put("claims", serializeClaims());
        tag.put("protectedBlocks", serializeProtectedBlocks());
        tag.put("cooldowns", serializeCooldowns());
        tag.putLong("lastUpkeepTimestamp", lastUpkeepTimestamp);
        return tag;
    }

    public static ClaimSavedData load(CompoundTag tag) {
        ClaimSavedData data = new ClaimSavedData();
        data.deserializeClaims(tag.getList("claims", 10)); // 10 = CompoundTag
        data.deserializeProtectedBlocks(tag.getList("protectedBlocks", 10));
        data.deserializeCooldowns(tag.getList("cooldowns", 10));
        if (tag.contains("lastUpkeepTimestamp")) {
            data.lastUpkeepTimestamp = tag.getLong("lastUpkeepTimestamp");
        }
        return data;
    }

    // --- Helpers ---

    private static long packChunk(int x, int z) {
        return ((long) x << 32) | (z & 0xFFFFFFFFL);
    }

    private static int unpackChunkX(long packed) {
        return (int) (packed >> 32);
    }

    private static int unpackChunkZ(long packed) {
        return (int) packed;
    }

    // --- Serialize / Deserialize claims ---

    private ListTag serializeClaims() {
        ListTag list = new ListTag();
        for (Map.Entry<String, Map<Long, ToolCupboardData>> dimEntry : claimsByDimension.entrySet()) {
            CompoundTag dimTag = new CompoundTag();
            dimTag.putString("dimension", dimEntry.getKey());
            ListTag chunkList = new ListTag();
            for (Map.Entry<Long, ToolCupboardData> chunkEntry : dimEntry.getValue().entrySet()) {
                CompoundTag chunkTag = new CompoundTag();
                chunkTag.putLong("packedChunk", chunkEntry.getKey());
                chunkTag.put("data", chunkEntry.getValue().serialize());
                chunkList.add(chunkTag);
            }
            dimTag.put("chunks", chunkList);
            list.add(dimTag);
        }
        return list;
    }

    private void deserializeClaims(ListTag list) {
        claimsByDimension.clear();
        for (int i = 0; i < list.size(); i++) {
            CompoundTag dimTag = list.getCompound(i);
            String dimension = dimTag.getString("dimension");
            ListTag chunkList = dimTag.getList("chunks", 10);
            Map<Long, ToolCupboardData> dimMap = new HashMap<>();
            for (int j = 0; j < chunkList.size(); j++) {
                CompoundTag chunkTag = chunkList.getCompound(j);
                long packedChunk = chunkTag.getLong("packedChunk");
                ToolCupboardData data = ToolCupboardData.deserialize(chunkTag.getCompound("data"));
                dimMap.put(packedChunk, data);
            }
            claimsByDimension.put(dimension, dimMap);
        }
    }

    // --- Serialize / Deserialize protected blocks ---

    private ListTag serializeProtectedBlocks() {
        ListTag list = new ListTag();
        for (Map.Entry<String, Map<Long, ProtectedBlockData>> dimEntry : protectedBlocksByDimension.entrySet()) {
            CompoundTag dimTag = new CompoundTag();
            dimTag.putString("dimension", dimEntry.getKey());
            ListTag blockList = new ListTag();
            for (Map.Entry<Long, ProtectedBlockData> blockEntry : dimEntry.getValue().entrySet()) {
                CompoundTag blockTag = new CompoundTag();
                blockTag.putLong("packedPos", blockEntry.getKey());
                blockTag.put("data", blockEntry.getValue().serialize());
                blockList.add(blockTag);
            }
            dimTag.put("blocks", blockList);
            list.add(dimTag);
        }
        return list;
    }

    private void deserializeProtectedBlocks(ListTag list) {
        protectedBlocksByDimension.clear();
        for (int i = 0; i < list.size(); i++) {
            CompoundTag dimTag = list.getCompound(i);
            String dimension = dimTag.getString("dimension");
            ListTag blockList = dimTag.getList("blocks", 10);
            Map<Long, ProtectedBlockData> dimMap = new HashMap<>();
            for (int j = 0; j < blockList.size(); j++) {
                CompoundTag blockTag = blockList.getCompound(j);
                long packedPos = blockTag.getLong("packedPos");
                ProtectedBlockData data = ProtectedBlockData.deserialize(blockTag.getCompound("data"));
                dimMap.put(packedPos, data);
            }
            protectedBlocksByDimension.put(dimension, dimMap);
        }
    }

    // --- Serialize / Deserialize cooldowns ---

    private ListTag serializeCooldowns() {
        ListTag list = new ListTag();
        for (Map.Entry<UUID, PlayerCooldownData> entry : playerCooldowns.entrySet()) {
            CompoundTag tag = new CompoundTag();
            tag.putUUID("player", entry.getKey());
            tag.put("data", entry.getValue().serialize());
            list.add(tag);
        }
        return list;
    }

    private void deserializeCooldowns(ListTag list) {
        playerCooldowns.clear();
        for (int i = 0; i < list.size(); i++) {
            CompoundTag tag = list.getCompound(i);
            UUID playerUuid = tag.getUUID("player");
            PlayerCooldownData data = PlayerCooldownData.deserialize(tag.getCompound("data"));
            playerCooldowns.put(playerUuid, data);
        }
    }

    // --- Global accessor ---

    /**
     * Retrieves the global ClaimSavedData instance attached to the Overworld.
     * This ensures the data is shared across all dimensions.
     */
    public static ClaimSavedData get(ServerLevel level) {
        ServerLevel overworld = level.getServer().getLevel(Level.OVERWORLD);
        return overworld.getDataStorage().computeIfAbsent(
            ClaimSavedData::load,
            ClaimSavedData::new,
            DATA_NAME
        );
    }

    /**
     * Retrieves the global ClaimSavedData instance attached to the Overworld.
     */
    public static ClaimSavedData get(net.minecraft.server.MinecraftServer server) {
        ServerLevel overworld = server.getLevel(Level.OVERWORLD);
        return overworld.getDataStorage().computeIfAbsent(
            ClaimSavedData::load,
            ClaimSavedData::new,
            DATA_NAME
        );
    }

    // ============================
    // Nested data classes
    // ============================

    /**
     * Represents a single Tool Cupboard claim.
     */
    public static class ToolCupboardData {
        private UUID owner;
        private int tier; // 1 or 2
        private int chunkX;
        private int chunkZ;
        private String dimension;
        private final Set<UUID> authorizedPlayers = new HashSet<>();
        private long placedTime; // epoch millis
        private long tcPackedPos; // BlockPos of the TC block entity

        public ToolCupboardData() {
        }

        public ToolCupboardData(UUID owner, int tier, int chunkX, int chunkZ, String dimension) {
            this.owner = owner;
            this.tier = tier;
            this.chunkX = chunkX;
            this.chunkZ = chunkZ;
            this.dimension = dimension;
            this.placedTime = System.currentTimeMillis();
        }

        public UUID getOwner() { return owner; }
        public void setOwner(UUID owner) { this.owner = owner; }

        public int getTier() { return tier; }
        public void setTier(int tier) { this.tier = tier; }

        public int getChunkX() { return chunkX; }
        public void setChunkX(int chunkX) { this.chunkX = chunkX; }

        public int getChunkZ() { return chunkZ; }
        public void setChunkZ(int chunkZ) { this.chunkZ = chunkZ; }

        public String getDimension() { return dimension; }
        public void setDimension(String dimension) { this.dimension = dimension; }

        public Set<UUID> getAuthorizedPlayers() { return authorizedPlayers; }

        public long getPlacedTime() { return placedTime; }
        public void setPlacedTime(long placedTime) { this.placedTime = placedTime; }

        public long getTcPackedPos() { return tcPackedPos; }
        public void setTcPackedPos(long tcPackedPos) { this.tcPackedPos = tcPackedPos; }

        public CompoundTag serialize() {
            CompoundTag tag = new CompoundTag();
            tag.putUUID("owner", owner);
            tag.putInt("tier", tier);
            tag.putInt("chunkX", chunkX);
            tag.putInt("chunkZ", chunkZ);
            tag.putString("dimension", dimension);
            ListTag authList = new ListTag();
            for (UUID uuid : authorizedPlayers) {
                CompoundTag u = new CompoundTag();
                u.putUUID("uuid", uuid);
                authList.add(u);
            }
            tag.put("authorized", authList);
            tag.putLong("placedTime", placedTime);
            tag.putLong("tcPackedPos", tcPackedPos);
            return tag;
        }

        public static ToolCupboardData deserialize(CompoundTag tag) {
            ToolCupboardData data = new ToolCupboardData();
            data.owner = tag.getUUID("owner");
            data.tier = tag.getInt("tier");
            data.chunkX = tag.getInt("chunkX");
            data.chunkZ = tag.getInt("chunkZ");
            data.dimension = tag.getString("dimension");
            ListTag authList = tag.getList("authorized", 10);
            for (int i = 0; i < authList.size(); i++) {
                data.authorizedPlayers.add(authList.getCompound(i).getUUID("uuid"));
            }
            data.placedTime = tag.getLong("placedTime");
            data.tcPackedPos = tag.getLong("tcPackedPos");
            return data;
        }
    }

    /**
     * Represents a protected block within a claim.
     */
    public static class ProtectedBlockData {
        private long packedPos;
        private String dimension;
        private String tier; // STRAW, WOOD, STONE, METAL, HQ
        private int currentHealth;
        private int maxHealth;
        private UUID placedBy;
        private long placedTime;

        public ProtectedBlockData() {
        }

        public ProtectedBlockData(long packedPos, String dimension, String tier, int maxHealth, UUID placedBy) {
            this.packedPos = packedPos;
            this.dimension = dimension;
            this.tier = tier;
            this.maxHealth = maxHealth;
            this.currentHealth = maxHealth;
            this.placedBy = placedBy;
            this.placedTime = System.currentTimeMillis();
        }

        public long getPackedPos() { return packedPos; }
        public void setPackedPos(long packedPos) { this.packedPos = packedPos; }

        public String getDimension() { return dimension; }
        public void setDimension(String dimension) { this.dimension = dimension; }

        public String getTier() { return tier; }
        public void setTier(String tier) { this.tier = tier; }

        public int getCurrentHealth() { return currentHealth; }
        public void setCurrentHealth(int currentHealth) { this.currentHealth = currentHealth; }

        public int getMaxHealth() { return maxHealth; }
        public void setMaxHealth(int maxHealth) { this.maxHealth = maxHealth; }

        public UUID getPlacedBy() { return placedBy; }
        public void setPlacedBy(UUID placedBy) { this.placedBy = placedBy; }

        public long getPlacedTime() { return placedTime; }
        public void setPlacedTime(long placedTime) { this.placedTime = placedTime; }

        public CompoundTag serialize() {
            CompoundTag tag = new CompoundTag();
            tag.putLong("packedPos", packedPos);
            tag.putString("dimension", dimension);
            tag.putString("tier", tier);
            tag.putInt("currentHealth", currentHealth);
            tag.putInt("maxHealth", maxHealth);
            tag.putUUID("placedBy", placedBy);
            tag.putLong("placedTime", placedTime);
            return tag;
        }

        public static ProtectedBlockData deserialize(CompoundTag tag) {
            ProtectedBlockData data = new ProtectedBlockData();
            data.packedPos = tag.getLong("packedPos");
            data.dimension = tag.getString("dimension");
            data.tier = tag.getString("tier");
            data.currentHealth = tag.getInt("currentHealth");
            data.maxHealth = tag.getInt("maxHealth");
            data.placedBy = tag.getUUID("placedBy");
            data.placedTime = tag.getLong("placedTime");
            return data;
        }
    }

    /**
     * Represents cooldown state for a single player.
     */
    public static class PlayerCooldownData {
        private UUID playerUuid;
        private long sleepingBagCooldownEnd; // epoch millis
        private long lastDeathTime; // epoch millis
        private String sleepingBagDimension;
        private long sleepingBagPos;
        private boolean lastDeathWasPvp;

        public PlayerCooldownData() {
        }

        public PlayerCooldownData(UUID playerUuid) {
            this.playerUuid = playerUuid;
        }

        public UUID getPlayerUuid() { return playerUuid; }
        public void setPlayerUuid(UUID playerUuid) { this.playerUuid = playerUuid; }

        public long getSleepingBagCooldownEnd() { return sleepingBagCooldownEnd; }
        public void setSleepingBagCooldownEnd(long sleepingBagCooldownEnd) { this.sleepingBagCooldownEnd = sleepingBagCooldownEnd; }

        public long getLastDeathTime() { return lastDeathTime; }
        public void setLastDeathTime(long lastDeathTime) { this.lastDeathTime = lastDeathTime; }

        public String getSleepingBagDimension() { return sleepingBagDimension; }
        public void setSleepingBagDimension(String sleepingBagDimension) { this.sleepingBagDimension = sleepingBagDimension; }

        public long getSleepingBagPos() { return sleepingBagPos; }
        public void setSleepingBagPos(long sleepingBagPos) { this.sleepingBagPos = sleepingBagPos; }

        public boolean isLastDeathWasPvp() { return lastDeathWasPvp; }
        public void setLastDeathWasPvp(boolean lastDeathWasPvp) { this.lastDeathWasPvp = lastDeathWasPvp; }

        public boolean isSleepingBagOnCooldown() {
            return System.currentTimeMillis() < sleepingBagCooldownEnd;
        }

        public CompoundTag serialize() {
            CompoundTag tag = new CompoundTag();
            tag.putUUID("player", playerUuid);
            tag.putLong("sleepingBagCooldownEnd", sleepingBagCooldownEnd);
            tag.putLong("lastDeathTime", lastDeathTime);
            tag.putString("sleepingBagDimension", sleepingBagDimension != null ? sleepingBagDimension : "");
            tag.putLong("sleepingBagPos", sleepingBagPos);
            tag.putBoolean("lastDeathWasPvp", lastDeathWasPvp);
            return tag;
        }

        public static PlayerCooldownData deserialize(CompoundTag tag) {
            PlayerCooldownData data = new PlayerCooldownData();
            data.playerUuid = tag.getUUID("player");
            data.sleepingBagCooldownEnd = tag.getLong("sleepingBagCooldownEnd");
            data.lastDeathTime = tag.getLong("lastDeathTime");
            data.sleepingBagDimension = tag.getString("sleepingBagDimension");
            if (data.sleepingBagDimension.isEmpty()) {
                data.sleepingBagDimension = null;
            }
            data.sleepingBagPos = tag.getLong("sleepingBagPos");
            data.lastDeathWasPvp = tag.getBoolean("lastDeathWasPvp");
            return data;
        }
    }
}
