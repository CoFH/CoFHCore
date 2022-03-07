package cofh.core.event;

import cofh.lib.capability.templates.AreaEffectItemWrapper;
import cofh.lib.util.Utils;
import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.Set;

import static cofh.lib.capability.CapabilityAreaEffect.AREA_EFFECT_ITEM_CAPABILITY;
import static cofh.lib.util.constants.Constants.ID_COFH_CORE;
import static cofh.lib.util.helpers.AreaEffectHelper.validAreaEffectMiningItem;

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

/*  TODO Lemming, HoeItem.TILLABLES has changed quite a lot, this Event is also deprecated for removal.. Last version..
    @SubscribeEvent (priority = EventPriority.LOW)
    public static void handleUseHoeEvent(UseHoeEvent event) {

        if (event.isCanceled() || event.getResult() == Event.Result.ALLOW) {
            return;
        }
        Player player = event.getPlayer();
        ItemStack stack = event.getContext().getItemInHand();
        BlockPos target = event.getContext().getClickedPos();
        Level world = player.level;
        BlockState targetTilled = TILLABLES.get(world.getBlockState(target).getBlock());
        BlockPos up = target.above();
        boolean weeding = getItemEnchantmentLevel(WEEDING, stack) > 0;

        if (targetTilled == null || !world.isEmptyBlock(up) && !weeding) {
            return;
        }
        if (Utils.isClientWorld(world)) {
            player.swing(event.getContext().getHand());
            return;
        }
        if (TILLING_PLAYERS.contains(player)) {
            return;
        }
        TILLING_PLAYERS.add(player);
        ImmutableList<BlockPos> areaBlocks = stack.getCapability(AREA_EFFECT_ITEM_CAPABILITY).orElse(new AreaEffectItemWrapper(stack)).getAreaEffectBlocks(target, player);
        for (BlockPos pos : areaBlocks) {
            if (stack.isEmpty()) {
                break;
            }
            BlockState tilled = TILLABLES.get(world.getBlockState(pos).getBlock());
            if (tilled != null) {
                world.setBlockAndUpdate(pos, tilled);
                if (weeding) {
                    up = pos.above();
                    if (!world.isEmptyBlock(up)) {
                        world.destroyBlock(up, !player.getAbilities().instabuild);
                    }
                }
                stack.hurtAndBreak(1, player, (entity) -> {
                    entity.broadcastBreakEvent(event.getContext().getHand());
                });
            }
        }
        world.setBlockAndUpdate(target, targetTilled);
        if (weeding) {
            up = target.above();
            if (!world.isEmptyBlock(up)) {
                world.destroyBlock(up, !player.getAbilities().instabuild);
            }
        }
        world.playSound(player, target, SoundEvents.HOE_TILL, SoundSource.BLOCKS, 1.0F, 1.0F);
        event.setResult(Event.Result.ALLOW);
    }*/

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
    //    private static boolean removedByPlayer(BlockState state, World world, BlockPos pos, PlayerEntity player, boolean willHarvest, FluidState fluid) {
    //
    //        if (false) {
    //            state.getBlock().onBlockHarvested(world, pos, state, player);
    //        }
    //        return world.setBlockState(pos, fluid.getBlockState(), world.isRemote ? 11 : 3);
    //    }
    // endregion
}
