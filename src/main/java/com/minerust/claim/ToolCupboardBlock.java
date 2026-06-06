package com.minerust.claim;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.network.NetworkHooks;
import com.minerust.menu.SecurityPanelMenu;
import com.minerust.networking.ModNetworking;
import com.minerust.networking.packet.SyncSecurityPanelMenuPacket;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import com.minerust.MineRustMod;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = MineRustMod.MODID)
public class ToolCupboardBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty UPGRADED = BooleanProperty.create("upgraded");
    private static final DustParticleOptions CORNER_BEAM_PARTICLE = new DustParticleOptions(new Vector3f(0.15F, 0.85F, 1.00F), 2.8F);
    private static final Map<UUID, BlockPos> ACTIVE_BOUNDARY_PREVIEWS = new HashMap<>();

    public ToolCupboardBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(UPGRADED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
        builder.add(FACING, UPGRADED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite())
                .setValue(UPGRADED, false);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ToolCupboardBlockEntity(pos, state);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (!level.isClientSide && placer instanceof Player player) {
            ServerLevel serverLevel = (ServerLevel) level;
            if (ToolCupboardClaimManager.wouldOverlapMaxCoverage(serverLevel, pos)) {
                return;
            }
            if (level.getBlockEntity(pos) instanceof ToolCupboardBlockEntity be) {
                be.setOwner(player.getUUID());
                be.setTier(ToolCupboardClaimManager.getMinLevel());
                serverLevel.setBlock(pos, state.setValue(UPGRADED, false), 3);
                ToolCupboardClaimManager.registerClaim(serverLevel, pos, be);
                be.setChanged();
                serverLevel.sendBlockUpdated(pos, state, state, 3);
            }
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        if (!(level.getBlockEntity(pos) instanceof ToolCupboardBlockEntity be)) {
            return InteractionResult.PASS;
        }
        if (!player.getUUID().equals(be.getOwner())) {
            player.displayClientMessage(Component.literal("You are not the owner of this Security Panel."), true);
            return InteractionResult.CONSUME;
        }

        if (player instanceof ServerPlayer serverPlayer) {
            MenuProvider provider = new SimpleMenuProvider(
                    (containerId, inventory, menuPlayer) -> new SecurityPanelMenu(containerId, inventory, pos),
                    Component.literal("Security Panel")
            );
            NetworkHooks.openScreen(serverPlayer, provider, buffer -> buffer.writeBlockPos(pos));
        }
        return InteractionResult.CONSUME;
    }

    public static boolean upgradeSecurityPanel(ServerLevel level, BlockPos pos, Player player, ToolCupboardBlockEntity be) {
        int currentLevel = be.getTier();
        int nextLevel = currentLevel + 1;
        if (currentLevel >= ToolCupboardClaimManager.getMaxLevel()) {
            player.displayClientMessage(Component.literal("This Security Panel already has maximum coverage."), true);
            return true;
        }
        if (ToolCupboardClaimManager.wouldOverlap(level, pos, nextLevel, pos)) {
            player.displayClientMessage(Component.literal("Cannot upgrade: the expanded coverage would overlap another Security Panel."), true);
            return true;
        }
        UpgradeCost cost = getUpgradeCost(nextLevel);
        if (!hasItemCount(player, Items.DIAMOND, cost.diamonds()) || !hasItemCount(player, Items.IRON_INGOT, cost.ironIngots())) {
            player.displayClientMessage(Component.literal("Upgrade to level " + nextLevel + " requires " + describeCost(cost) + "."), true);
            return true;
        }

        be.setTier(nextLevel);
        if (!ToolCupboardClaimManager.updateClaimTier(level, pos, be)) {
            be.setTier(currentLevel);
            player.displayClientMessage(Component.literal("Cannot upgrade: the expanded claim would overlap another Security Panel."), true);
            return true;
        }
        removeItemCount(player, Items.DIAMOND, cost.diamonds());
        removeItemCount(player, Items.IRON_INGOT, cost.ironIngots());
        player.getInventory().setChanged();
        if (player instanceof ServerPlayer inventoryPlayer) {
            inventoryPlayer.containerMenu.broadcastChanges();
            inventoryPlayer.inventoryMenu.broadcastChanges();
        }
        BlockState state = level.getBlockState(pos);
        if (state.hasProperty(UPGRADED)) {
            level.setBlock(pos, state.setValue(UPGRADED, true), 3);
        }
        if (level.getBlockEntity(pos) instanceof ToolCupboardBlockEntity updatedBe) {
            updatedBe.setOwner(be.getOwner());
            updatedBe.getAuthorizedPlayers().clear();
            updatedBe.getAuthorizedPlayers().addAll(be.getAuthorizedPlayers());
            updatedBe.setTier(nextLevel);
            updatedBe.setChanged();
        }
        if (player instanceof ServerPlayer serverPlayer && serverPlayer.containerMenu instanceof SecurityPanelMenu) {
            ModNetworking.CHANNEL.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new SyncSecurityPanelMenuPacket(nextLevel));
        }
        player.displayClientMessage(Component.literal("Security Panel upgraded to level " + nextLevel + "."), true);
        return true;
    }

    public static boolean authorizeNearbyPlayer(ServerLevel serverLevel, Player player, ToolCupboardBlockEntity be, BlockPos pos) {
        UUID targetUuid = null;
        String targetName = null;
        double bestDistSq = Double.MAX_VALUE;
        double range = 5.0;

        for (ServerPlayer other : serverLevel.players()) {
            if (other.getUUID().equals(player.getUUID())) {
                continue;
            }
            if (be.getAuthorizedPlayers().contains(other.getUUID())) {
                continue;
            }
            double distSq = other.distanceToSqr(player.getX(), player.getY(), player.getZ());
            if (distSq <= range * range && distSq < bestDistSq) {
                bestDistSq = distSq;
                targetUuid = other.getUUID();
                targetName = other.getName().getString();
            }
        }

        if (targetUuid == null) {
            player.displayClientMessage(Component.literal("No unauthorized players nearby to authorize."), true);
            return true;
        }

        be.addAuthorizedPlayer(targetUuid);
        ToolCupboardClaimManager.registerClaim(serverLevel, pos, be);

        player.displayClientMessage(Component.literal("Authorized " + targetName + "."), true);

        return true;
    }

    public static void toggleClaimBounds(ServerLevel level, Player player, ToolCupboardBlockEntity be, BlockPos pos) {
        UUID playerUuid = player.getUUID();
        BlockPos activePos = ACTIVE_BOUNDARY_PREVIEWS.get(playerUuid);
        if (pos.equals(activePos)) {
            ACTIVE_BOUNDARY_PREVIEWS.remove(playerUuid);
            player.displayClientMessage(Component.literal("Security Panel claim bounds hidden."), true);
            return;
        }

        ACTIVE_BOUNDARY_PREVIEWS.put(playerUuid, pos);
        player.displayClientMessage(Component.literal("Security Panel claim bounds shown."), true);
        showClaimBounds(level, player, be, pos);
    }

    public static void showClaimBounds(ServerLevel level, Player player, ToolCupboardBlockEntity be, BlockPos pos) {
        ToolCupboardClaimManager.ClaimBounds bounds = ToolCupboardClaimManager.getBounds(pos, be.getTier());

        player.displayClientMessage(Component.literal("Owner UUID: " + be.getOwner()), false);
        player.displayClientMessage(Component.literal(describeLevel(be.getTier())), false);
        player.displayClientMessage(Component.literal("Security Panel coverage: " + describeArea(bounds)), false);
        player.displayClientMessage(Component.literal("Panel center: " + pos.getX() + ", " + pos.getY() + ", " + pos.getZ()), false);
        player.displayClientMessage(Component.literal("Covered X/Z: " + bounds.minX() + ", " + bounds.minZ() + " -> " + bounds.maxX() + ", " + bounds.maxZ()), false);
        player.displayClientMessage(Component.literal("Covered Y: " + bounds.minY() + " -> " + bounds.maxY()), false);

        outlineClaimVolume(level, bounds);
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.getServer().getTickCount() % 20 != 0) {
            return;
        }
        if (ACTIVE_BOUNDARY_PREVIEWS.isEmpty()) {
            return;
        }

        Iterator<Map.Entry<UUID, BlockPos>> iterator = ACTIVE_BOUNDARY_PREVIEWS.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, BlockPos> entry = iterator.next();
            ServerPlayer player = event.getServer().getPlayerList().getPlayer(entry.getKey());
            if (player == null) {
                iterator.remove();
                continue;
            }
            ServerLevel level = player.serverLevel();
            BlockPos pos = entry.getValue();
            if (!(level.getBlockEntity(pos) instanceof ToolCupboardBlockEntity be)) {
                iterator.remove();
                continue;
            }
            if (!player.getUUID().equals(be.getOwner()) && !be.getAuthorizedPlayers().contains(player.getUUID())) {
                iterator.remove();
                continue;
            }
            emitClaimBounds(level, player, be, pos);
        }
    }

    private static void emitClaimBounds(ServerLevel level, Player player, ToolCupboardBlockEntity be, BlockPos pos) {
        outlineClaimVolume(level, ToolCupboardClaimManager.getBounds(pos, be.getTier()));
    }

    private static void outlineClaimVolume(ServerLevel level, ToolCupboardClaimManager.ClaimBounds bounds) {
        double minX = bounds.minX();
        double minY = bounds.minY();
        double minZ = bounds.minZ();
        double maxX = bounds.maxX() + 1.0D;
        double maxY = bounds.maxY() + 1.0D;
        double maxZ = bounds.maxZ() + 1.0D;

        for (double x = minX; x <= maxX; x += 2.0D) {
            particle(level, x, minY, minZ);
            particle(level, x, minY, maxZ);
            particle(level, x, maxY, minZ);
            particle(level, x, maxY, maxZ);
        }
        for (double z = minZ; z <= maxZ; z += 2.0D) {
            particle(level, minX, minY, z);
            particle(level, maxX, minY, z);
            particle(level, minX, maxY, z);
            particle(level, maxX, maxY, z);
        }
        for (double y = minY; y <= maxY; y += 2.0D) {
            particle(level, minX, y, minZ);
            particle(level, maxX, y, minZ);
            particle(level, minX, y, maxZ);
            particle(level, maxX, y, maxZ);
        }

        cornerBeam(level, minX, minY, minZ, maxY);
        cornerBeam(level, maxX, minY, minZ, maxY);
        cornerBeam(level, minX, minY, maxZ, maxY);
        cornerBeam(level, maxX, minY, maxZ, maxY);
    }

    private static void particle(ServerLevel level, double x, double y, double z) {
        level.sendParticles(ParticleTypes.END_ROD, x, y, z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
    }

    private static void cornerBeam(ServerLevel level, double x, double minY, double z, double maxY) {
        for (double y = minY; y <= maxY; y += 1.0D) {
            beamParticle(level, x, y, z);
            beamParticle(level, x + 0.18D, y, z);
            beamParticle(level, x - 0.18D, y, z);
            beamParticle(level, x, y, z + 0.18D);
            beamParticle(level, x, y, z - 0.18D);
            level.sendParticles(ParticleTypes.END_ROD, x, y, z, 1, 0.0D, 0.04D, 0.0D, 0.0D);
        }
    }

    private static void beamParticle(ServerLevel level, double x, double y, double z) {
        level.sendParticles(CORNER_BEAM_PARTICLE, x, y, z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
    }

    public static String describeArea(int tier) {
        int footprint = ToolCupboardClaimManager.getFootprintSize(tier);
        return footprint + "x" + footprint;
    }

    public static String describeLevel(int tier) {
        return "Level " + ToolCupboardClaimManager.clampLevel(tier) + "/" + ToolCupboardClaimManager.getMaxLevel();
    }

    public static boolean canUpgrade(int tier) {
        return tier < ToolCupboardClaimManager.getMaxLevel();
    }

    public static String describeNextUpgradeCost(int currentTier) {
        if (!canUpgrade(currentTier)) {
            return "Max level";
        }
        return describeCost(getUpgradeCost(currentTier + 1));
    }

    private static String describeArea(ToolCupboardClaimManager.ClaimBounds bounds) {
        return bounds.sizeX() + "x" + bounds.sizeY() + "x" + bounds.sizeZ() + " blocks";
    }

    private static UpgradeCost getUpgradeCost(int targetLevel) {
        int footprint = ToolCupboardClaimManager.getFootprintSize(targetLevel);
        int horizontalArea = footprint * footprint;
        int diamonds = Math.max(1, (horizontalArea + 99) / 100);
        int ironIngots = Math.max(1, (horizontalArea + 19) / 20);
        return new UpgradeCost(diamonds, ironIngots);
    }

    private static UpgradeCost getCumulativeUpgradeCost(int level) {
        int diamonds = 0;
        int ironIngots = 0;
        for (int targetLevel = ToolCupboardClaimManager.getMinLevel() + 1; targetLevel <= ToolCupboardClaimManager.clampLevel(level); targetLevel++) {
            UpgradeCost cost = getUpgradeCost(targetLevel);
            diamonds += cost.diamonds();
            ironIngots += cost.ironIngots();
        }
        return new UpgradeCost(diamonds, ironIngots);
    }

    private static void dropUpgradeRefund(Level level, BlockPos pos, int levelValue) {
        if (level.isClientSide) {
            return;
        }
        UpgradeCost spent = getCumulativeUpgradeCost(levelValue);
        int refundDiamonds = spent.diamonds() / 2;
        int refundIronIngots = spent.ironIngots() / 2;
        if (refundDiamonds > 0) {
            Block.popResource(level, pos, new ItemStack(Items.DIAMOND, refundDiamonds));
        }
        if (refundIronIngots > 0) {
            Block.popResource(level, pos, new ItemStack(Items.IRON_INGOT, refundIronIngots));
        }
    }

    private static String describeCost(UpgradeCost cost) {
        return cost.diamonds() + " diamonds and " + cost.ironIngots() + " iron ingots";
    }

    private record UpgradeCost(int diamonds, int ironIngots) {
    }

    private static boolean hasItemCount(Player player, Item item, int requiredCount) {
        int found = 0;
        for (ItemStack stack : player.getInventory().items) {
            if (stack.is(item)) {
                found += stack.getCount();
                if (found >= requiredCount) {
                    return true;
                }
            }
        }
        return false;
    }

    private static void removeItemCount(Player player, Item item, int count) {
        int remaining = count;
        for (ItemStack stack : player.getInventory().items) {
            if (!stack.is(item)) {
                continue;
            }
            int removed = Math.min(stack.getCount(), remaining);
            stack.shrink(removed);
            remaining -= removed;
            if (remaining == 0) {
                return;
            }
        }
    }

    @Override
    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide && level.getBlockEntity(pos) instanceof ToolCupboardBlockEntity be) {
            dropUpgradeRefund(level, pos, be.getTier());
        }
        super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }
}
