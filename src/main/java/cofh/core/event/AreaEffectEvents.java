package cofh.core.event;

import cofh.lib.capability.templates.AreaEffectItemWrapper;
import cofh.lib.util.Utils;
import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;
import java.util.function.BiPredicate;

import static cofh.lib.capability.CapabilityAreaEffect.AREA_EFFECT_ITEM_CAPABILITY;
import static cofh.lib.util.Utils.getItemEnchantmentLevel;
import static cofh.lib.util.constants.Constants.ID_COFH_CORE;
import static cofh.lib.util.helpers.AreaEffectHelper.validAreaEffectMiningItem;
import static cofh.lib.util.references.EnsorcReferences.WEEDING;

@Mod.EventBusSubscriber (modid = ID_COFH_CORE)
public class AreaEffectEvents {

    private static final Set<Player> HARVESTING_PLAYERS = new ObjectOpenHashSet<>();
    private static final Set<Player> TILLING_PLAYERS = new ObjectOpenHashSet<>();

    private AreaEffectEvents() {

    }

    @SubscribeEvent (priority = EventPriority.LOW)
    public static void handleBlockBreakEvent(BlockEvent.BreakEvent event) {

        if (!(event.getPlayer() instanceof ServerPlayer player) || Utils.isClientWorld(player.level)) {
            return;
        }
        if (HARVESTING_PLAYERS.contains(player)) {
            return;
        }
        HARVESTING_PLAYERS.add(player);
        ItemStack stack = player.getMainHandItem();
        if (!validAreaEffectMiningItem(stack)) {
            return;
        }
        ImmutableList<BlockPos> areaBlocks = stack.getCapability(AREA_EFFECT_ITEM_CAPABILITY).orElse(new AreaEffectItemWrapper(stack)).getAreaEffectBlocks(event.getPos(), player);
        // TODO: Revisit if performance issues show. This is the most *proper* way to handle this, but is not particularly friendly.
        for (BlockPos pos : areaBlocks) {
            if (stack.isEmpty()) {
                break;
            }
            player.gameMode.destroyBlock(pos);
        }
    }

    @SubscribeEvent (priority = EventPriority.NORMAL)
    public static void handleBreakSpeedEvent(PlayerEvent.BreakSpeed event) {

        if (event.isCanceled()) {
            return;
        }
        Player player = event.getPlayer();
        ItemStack stack = player.getMainHandItem();
        if (!validAreaEffectMiningItem(stack)) {
            return;
        }
        ImmutableList<BlockPos> areaBlocks = stack.getCapability(AREA_EFFECT_ITEM_CAPABILITY).orElse(new AreaEffectItemWrapper(stack)).getAreaEffectBlocks(event.getPos(), player);

        float curHardness = event.getState().getDestroySpeed(player.level, event.getPos());
        if (curHardness <= 0 || areaBlocks.size() <= 1) {
            return;
        }
        float areaMod = Mth.clamp(1.0F - 0.01F * areaBlocks.size(), 0.1F, 1.0F);
        event.setNewSpeed(event.getNewSpeed() * areaMod);

        float maxHardness = getMaxHardness(player.level, areaBlocks, curHardness);
        if (maxHardness > curHardness) {
            event.setNewSpeed(event.getNewSpeed() * curHardness / maxHardness);
        }
    }

    @SubscribeEvent (priority = EventPriority.LOW)
    public static void handleBlockToolModificationEvent(BlockEvent.BlockToolModificationEvent event) {

        ToolAction action = event.getToolAction();
        if (event.isCanceled() || action != ToolActions.HOE_TILL) {
            return;
        }
        UseOnContext context = event.getContext();
        if (context == null || context instanceof CoFHIgnoreUseOnContext) { //yes, i know this is a gamer way to stop recursion, but it prevents some weird client/server issues
            return;
        }
        Player player = event.getPlayer();
        if (player == null) {
            return;
        }
        Level level = context.getLevel();
        BlockPos target = context.getClickedPos();
        ItemStack stack = context.getItemInHand();
        BlockState original = event.getState();
        BlockState tilled = event.getFinalState();
        boolean simulate = event.isSimulated();
        boolean weeding = getItemEnchantmentLevel(WEEDING, stack) > 0;
        // WEEDING
        // If has weeding and replaceable block above preventing tilling, simulate event at location without block above.
        BiPredicate<BlockState, BlockPlaceContext> replace = (state, ctx) -> !state.isAir() && (state.canBeReplaced(ctx) || state.is(BlockTags.FLOWERS)) && !state.hasBlockEntity();
        BlockPlaceContext blockContext = new BlockPlaceContext(context);
        if (original.equals(tilled) && weeding) {
            BlockPos up = target.above();
            BlockState upState = level.getBlockState(up);
            if (replace.test(upState, blockContext)) {
                level.setBlock(up, Blocks.AIR.defaultBlockState(), 180);
                tilled = original.getToolModifiedState(CoFHIgnoreUseOnContext.copy(context), action, true);
                level.setBlock(up, upState, 180);
                if (tilled != null && !simulate) {
                    level.destroyBlock(up, !player.abilities.instabuild);
                }
            }
        }
        if (tilled == null) {
            return;
        }
        event.setFinalState(tilled);
        if (simulate) {
            return;
        }
        // FURROWING and TILLING
        ImmutableList<BlockPos> areaBlocks = stack.getCapability(AREA_EFFECT_ITEM_CAPABILITY).orElse(new AreaEffectItemWrapper(stack)).getAreaEffectBlocks(target, player);
        for (BlockPos pos : areaBlocks) {
            if (stack.isEmpty()) {
                break;
            }
            BlockPos up = pos.above();
            BlockState upState = level.getBlockState(up);
            if (weeding && replace.test(upState, blockContext)) {
                level.setBlock(up, Blocks.AIR.defaultBlockState(), 180);
                tilled = level.getBlockState(pos).getToolModifiedState(getContextAt(context, pos), action, false);
                level.setBlock(up, upState, 180);
                if (tilled != null) {
                    level.destroyBlock(up, !player.abilities.instabuild);
                }
            } else {
                tilled = level.getBlockState(pos).getToolModifiedState(getContextAt(context, pos), action, false);
            }
            if (tilled != null) {
                level.setBlockAndUpdate(pos, tilled);
                stack.hurtAndBreak(1, player, (entity) -> {
                    entity.broadcastBreakEvent(event.getContext().getHand());
                });
            }
        }
    }

    @SubscribeEvent (priority = EventPriority.LOWEST)
    public static void handleTickEndEvent(TickEvent.ServerTickEvent event) {

        if (event.phase == TickEvent.Phase.END) {
            HARVESTING_PLAYERS.clear();
            TILLING_PLAYERS.clear();
        }
    }

    // region HELPERS
    private static float getMaxHardness(BlockGetter world, List<BlockPos> areaBlocks, float curHardness) {

        float maxHardness = curHardness;
        float testHardness;

        for (BlockPos pos : areaBlocks) {
            testHardness = world.getBlockState(pos).getDestroySpeed(world, pos);
            if (testHardness > maxHardness) {
                maxHardness = testHardness;
            }
        }
        return maxHardness;
    }

    // Used to reproduce the use context, but with the proper position as some BlockToolModifications are context-dependent.
    private static UseOnContext getContextAt(UseOnContext context, BlockPos pos) {

        BlockPos og = context.getClickedPos();
        Vec3 loc = context.getClickLocation().add(pos.getX() - og.getX(), pos.getY() - og.getY(), pos.getZ() - og.getZ());
        return new CoFHIgnoreUseOnContext(context.getLevel(), context.getPlayer(), context.getHand(), context.getItemInHand(),
                new BlockHitResult(loc, context.getClickedFace(), pos, context.isInside()));
    }

    private static class CoFHIgnoreUseOnContext extends UseOnContext {

        public CoFHIgnoreUseOnContext(Player player, InteractionHand hand, BlockHitResult result) {

            super(player, hand, result);
        }

        public CoFHIgnoreUseOnContext(Level level, @Nullable Player player, InteractionHand hand, ItemStack stack, BlockHitResult result) {

            super(level, player, hand, stack, result);
        }

        public static UseOnContext copy(UseOnContext context) {

            return new CoFHIgnoreUseOnContext(context.getLevel(), context.getPlayer(), context.getHand(), context.getItemInHand(),
                    new BlockHitResult(context.getClickLocation(), context.getClickedFace(), context.getClickedPos(), context.isInside()));
        }

    }

    //    private static boolean tryHarvestBlock(PlayerInteractionManager manager, BlockPos pos) {
    //
    //        BlockState blockstate = manager.world.getBlockState(pos);
    //        int exp = net.minecraftforge.common.ForgeHooks.onBlockBreakEvent(manager.world, manager.getGameType(), manager.player, pos);
    //        if (exp == -1) {
    //            return false;
    //        } else {
    //            TileEntity tileentity = manager.world.getTileEntity(pos);
    //            Block block = blockstate.getBlock();
    //            if ((block instanceof CommandBlockBlock || block instanceof StructureBlock || block instanceof JigsawBlock) && !manager.player.canUseCommandBlock()) {
    //                manager.world.notifyBlockUpdate(pos, blockstate, blockstate, 3);
    //                return false;
    //            } else if (manager.player.getHeldItemMainhand().onBlockStartBreak(pos, manager.player)) {
    //                return false;
    //            } else if (manager.player.blockActionRestricted(manager.world, pos, manager.getGameType())) {
    //                return false;
    //            } else {
    //                if (manager.isCreative()) {
    //                    removeBlock(manager, pos, false);
    //                    return true;
    //                } else {
    //                    ItemStack itemstack = manager.player.getHeldItemMainhand();
    //                    ItemStack itemstack1 = itemstack.copy();
    //                    boolean flag1 = blockstate.canHarvestBlock(manager.world, pos, manager.player);
    //                    itemstack.onBlockDestroyed(manager.world, blockstate, pos, manager.player);
    //                    if (itemstack.isEmpty() && !itemstack1.isEmpty()) {
    //                        net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem(manager.player, itemstack1, Hand.MAIN_HAND);
    //                    }
    //                    boolean flag = removeBlock(manager, pos, flag1);
    //
    //                    if (flag && flag1) {
    //                        block.harvestBlock(manager.world, manager.player, pos, blockstate, tileentity, itemstack1);
    //                    }
    //                    if (flag && exp > 0)
    //                        blockstate.getBlock().dropXpOnBlockBreak(manager.world, pos, exp);
    //                    return true;
    //                }
    //            }
    //        }
    //    }
    //
    //    private static boolean removeBlock(PlayerInteractionManager manager, BlockPos pos, boolean canHarvest) {
    //
    //        BlockState state = manager.world.getBlockState(pos);
    //        boolean removed = removedByPlayer(manager.world.getBlockState(pos), manager.world, pos, manager.player, canHarvest, manager.world.getFluidState(pos));
    //        if (removed)
    //            state.getBlock().onPlayerDestroy(manager.world, pos, state);
    //        return removed;
    //    }
    //
    //    private static boolean removedByPlayer(BlockState state, Level level, BlockPos pos, PlayerEntity player, boolean willHarvest, FluidState fluid) {
    //
    //        if (false) {
    //            state.getBlock().onBlockHarvested(world, pos, state, player);
    //        }
    //        return world.setBlockState(pos, fluid.getBlockState(), world.isRemote ? 11 : 3);
    //    }
    // endregion
}
