package com.minerust.claim;

import com.minerust.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ToolCupboardBlockEntity extends BlockEntity {
    private UUID owner;
    private final Set<UUID> authorizedPlayers = new HashSet<>();
    private int tier = 1;
    private int woodCount;
    private int stoneCount;
    private int metalCount;
    private int highQualityCount;

    public ToolCupboardBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.TOOL_CUPBOARD.get(), pos, state);
    }

    public UUID getOwner() {
        return owner;
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
        setChanged();
    }

    public Set<UUID> getAuthorizedPlayers() {
        return authorizedPlayers;
    }

    public void addAuthorizedPlayer(UUID player) {
        authorizedPlayers.add(player);
        setChanged();
    }

    public void removeAuthorizedPlayer(UUID player) {
        authorizedPlayers.remove(player);
        setChanged();
    }

    public void clearAuthorizedPlayers() {
        authorizedPlayers.clear();
        setChanged();
    }

    public int getTier() {
        return tier;
    }

    public void setTier(int tier) {
        this.tier = ToolCupboardClaimManager.clampLevel(tier);
        setChanged();
    }

    public void upgradeOneLevel() {
        setTier(tier + 1);
    }

    public int getWoodCount() {
        return woodCount;
    }

    public void setWoodCount(int count) {
        this.woodCount = count;
        setChanged();
    }

    public int getStoneCount() {
        return stoneCount;
    }

    public void setStoneCount(int count) {
        this.stoneCount = count;
        setChanged();
    }

    public int getMetalCount() {
        return metalCount;
    }

    public void setMetalCount(int count) {
        this.metalCount = count;
        setChanged();
    }

    public int getHighQualityCount() {
        return highQualityCount;
    }

    public void setHighQualityCount(int count) {
        this.highQualityCount = count;
        setChanged();
    }

    public boolean tryConsume(int wood, int stone, int metal, int highQuality) {
        if (woodCount < wood || stoneCount < stone || metalCount < metal || highQualityCount < highQuality) {
            return false;
        }
        woodCount -= wood;
        stoneCount -= stone;
        metalCount -= metal;
        highQualityCount -= highQuality;
        setChanged();
        return true;
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (owner != null) {
            tag.putUUID("owner", owner);
        }
        tag.putInt("tier", tier);
        tag.putInt("woodCount", woodCount);
        tag.putInt("stoneCount", stoneCount);
        tag.putInt("metalCount", metalCount);
        tag.putInt("highQualityCount", highQualityCount);
        ListTag authList = new ListTag();
        for (UUID uuid : authorizedPlayers) {
            CompoundTag u = new CompoundTag();
            u.putUUID("uuid", uuid);
            authList.add(u);
        }
        tag.put("authorizedPlayers", authList);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        owner = tag.hasUUID("owner") ? tag.getUUID("owner") : null;
        tier = tag.contains("tier") ? ToolCupboardClaimManager.clampLevel(tag.getInt("tier")) : ToolCupboardClaimManager.getMinLevel();
        woodCount = tag.getInt("woodCount");
        stoneCount = tag.getInt("stoneCount");
        metalCount = tag.getInt("metalCount");
        highQualityCount = tag.getInt("highQualityCount");
        authorizedPlayers.clear();
        ListTag authList = tag.getList("authorizedPlayers", 10);
        for (int i = 0; i < authList.size(); i++) {
            authorizedPlayers.add(authList.getCompound(i).getUUID("uuid"));
        }
    }
}
