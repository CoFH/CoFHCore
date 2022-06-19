package cofh.lib.util.helpers;

import cofh.lib.util.RayTracer;
import com.google.common.collect.ImmutableList;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static cofh.lib.capability.CapabilityAreaEffect.AREA_EFFECT_ITEM_CAPABILITY;
import static cofh.lib.util.Utils.getEnchantment;
import static cofh.lib.util.Utils.getItemEnchantmentLevel;
import static cofh.lib.util.constants.Constants.ID_ENSORCELLATION;
import static cofh.lib.util.references.EnsorcIDs.ID_EXCAVATING;

public class AreaEffectHelper {

    private AreaEffectHelper() {

    }

    public static boolean validAreaEffectItem(ItemStack stack) {

        return stack.getCapability(AREA_EFFECT_ITEM_CAPABILITY).isPresent() || stack.getItem() instanceof DiggerItem;
    }

    public static boolean validAreaEffectMiningItem(ItemStack stack) {

        return stack.getCapability(AREA_EFFECT_ITEM_CAPABILITY).isPresent() || stack.getItem() instanceof DiggerItem;
    }

    /**
     * Basically the "default" AOE behavior.
     */
    public static ImmutableList<BlockPos> getAreaEffectBlocks(ItemStack stack, BlockPos pos, Player player) {

        int encExcavating = getItemEnchantmentLevel(getEnchantment(ID_ENSORCELLATION, ID_EXCAVATING), stack);
        if (encExcavating > 0) {
            return getBreakableBlocksRadius(stack, pos, player, encExcavating);
        }
        return ImmutableList.of();
    }

    // region FLUID
    public static ImmutableList<BlockPos> getBucketableBlocksRadius(ItemStack stack, BlockPos pos, Player player, int radius) {

        List<BlockPos> area;
        Level world = player.getCommandSenderWorld();
        Item tool = stack.getItem();

        BlockHitResult traceResult = RayTracer.retrace(player, ClipContext.Fluid.SOURCE_ONLY);
        if (traceResult.getType() == HitResult.Type.MISS || player.isSecondaryUseActive() || radius <= 0) {
            return ImmutableList.of();
        }
        int yMin = -1;
        int yMax = 2 * radius - 1;

        switch (traceResult.getDirection()) {
            case DOWN:
            case UP:
                area = BlockPos.betweenClosedStream(pos.offset(-radius, 0, -radius), pos.offset(radius, 0, radius))
                        .filter(blockPos -> isBucketable(tool, stack, world, blockPos))
                        .map(BlockPos::immutable)
                        .collect(Collectors.toList());
                break;
            case NORTH:
            case SOUTH:
                area = BlockPos.betweenClosedStream(pos.offset(-radius, yMin, 0), pos.offset(radius, yMax, 0))
                        .filter(blockPos -> isBucketable(tool, stack, world, blockPos))
                        .map(BlockPos::immutable)
                        .collect(Collectors.toList());
                break;
            default:
                area = BlockPos.betweenClosedStream(pos.offset(0, yMin, -radius), pos.offset(0, yMax, radius))
                        .filter(blockPos -> isBucketable(tool, stack, world, blockPos))
                        .map(BlockPos::immutable)
                        .collect(Collectors.toList());
                break;
        }
        area.remove(pos);
        return ImmutableList.copyOf(area);
    }
    // endregion

    // region MINING
    public static ImmutableList<BlockPos> getBreakableBlocksRadius(ItemStack stack, BlockPos pos, Player player, int radius) {

        List<BlockPos> area;
        Level world = player.getCommandSenderWorld();
        Item tool = stack.getItem();

        BlockHitResult traceResult = RayTracer.retrace(player, ClipContext.Fluid.NONE);
        if (traceResult.getType() == HitResult.Type.MISS || player.isSecondaryUseActive() || !canToolAffect(tool, stack, world, pos) || radius <= 0) {
            return ImmutableList.of();
        }
        int yMin = -1;
        int yMax = 2 * radius - 1;

        switch (traceResult.getDirection()) {
            case DOWN:
            case UP:
                area = BlockPos.betweenClosedStream(pos.offset(-radius, 0, -radius), pos.offset(radius, 0, radius))
                        .filter(blockPos -> canToolAffect(tool, stack, world, blockPos))
                        .map(BlockPos::immutable)
                        .collect(Collectors.toList());
                break;
            case NORTH:
            case SOUTH:
                area = BlockPos.betweenClosedStream(pos.offset(-radius, yMin, 0), pos.offset(radius, yMax, 0))
                        .filter(blockPos -> canToolAffect(tool, stack, world, blockPos))
                        .map(BlockPos::immutable)
                        .collect(Collectors.toList());
                break;
            default:
                area = BlockPos.betweenClosedStream(pos.offset(0, yMin, -radius), pos.offset(0, yMax, radius))
                        .filter(blockPos -> canToolAffect(tool, stack, world, blockPos))
                        .map(BlockPos::immutable)
                        .collect(Collectors.toList());
                break;
        }
        area.remove(pos);
        return ImmutableList.copyOf(area);
    }

    public static ImmutableList<BlockPos> getBreakableBlocksDepth(ItemStack stack, BlockPos pos, Player player, int radius, int depth) {

        List<BlockPos> area;
        Level world = player.getCommandSenderWorld();
        Item tool = stack.getItem();

        BlockHitResult traceResult = RayTracer.retrace(player, ClipContext.Fluid.NONE);
        if (traceResult.getType() == HitResult.Type.MISS || player.isSecondaryUseActive() || !canToolAffect(tool, stack, world, pos) || (radius <= 0 && depth <= 0)) {
            return ImmutableList.of();
        }
        int dMin = depth;
        int dMax = 0;

        int yMin = -1;
        int yMax = 2 * radius - 1;

        switch (traceResult.getDirection()) {
            case DOWN:
                dMin = 0;
                dMax = depth;
            case UP:
                area = BlockPos.betweenClosedStream(pos.offset(-radius, -dMin, -radius), pos.offset(radius, dMax, radius))
                        .filter(blockPos -> canToolAffect(tool, stack, world, blockPos))
                        .map(BlockPos::immutable)
                        .collect(Collectors.toList());
                break;
            case NORTH:
                dMin = 0;
                dMax = depth;
            case SOUTH:
                area = BlockPos.betweenClosedStream(pos.offset(-radius, yMin, -dMin), pos.offset(radius, yMax, dMax))
                        .filter(blockPos -> canToolAffect(tool, stack, world, blockPos))
                        .map(BlockPos::immutable)
                        .collect(Collectors.toList());
                break;
            case WEST:
                dMin = 0;
                dMax = depth;
            default:
                area = BlockPos.betweenClosedStream(pos.offset(-dMin, yMin, -radius), pos.offset(dMax, yMax, radius))
                        .filter(blockPos -> canToolAffect(tool, stack, world, blockPos))
                        .map(BlockPos::immutable)
                        .collect(Collectors.toList());
                break;

        }
        area.remove(pos);
        return ImmutableList.copyOf(area);
    }

    public static ImmutableList<BlockPos> getBreakableBlocksLine(ItemStack stack, BlockPos pos, Player player, int length) {

        ArrayList<BlockPos> area = new ArrayList<>();
        Level world = player.getCommandSenderWorld();
        Item tool = stack.getItem();

        BlockPos query;
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();

        if (player.isSecondaryUseActive() || !canToolAffect(tool, stack, world, pos) || length <= 0) {
            return ImmutableList.of();
        }
        switch (player.getDirection()) {
            case SOUTH:
                for (int k = z + 1; k < z + length + 1; ++k) {
                    query = new BlockPos(x, y, k);
                    if (!canToolAffect(tool, stack, world, query)) {
                        break;
                    }
                    area.add(query);
                }
                break;
            case WEST:
                for (int i = x - 1; i > x - length - 1; --i) {
                    query = new BlockPos(i, y, z);
                    if (!canToolAffect(tool, stack, world, query)) {
                        break;
                    }
                    area.add(query);
                }
                break;
            case NORTH:
                for (int k = z - 1; k > z - length - 1; --k) {
                    query = new BlockPos(x, y, k);
                    if (!canToolAffect(tool, stack, world, query)) {
                        break;
                    }
                    area.add(query);
                }
                break;
            case EAST:
                for (int i = x + 1; i < x + length + 1; ++i) {
                    query = new BlockPos(i, y, z);
                    if (!canToolAffect(tool, stack, world, query)) {
                        break;
                    }
                    area.add(query);
                }
                break;
        }
        return ImmutableList.copyOf(area);
    }
    // endregion

    // region PLACING
    public static ImmutableList<BlockPos> getPlaceableBlocksRadius(ItemStack stack, BlockPos pos, Player player, int radius) {

        List<BlockPos> area;
        Level world = player.getCommandSenderWorld();
        Item tool = stack.getItem();

        BlockHitResult traceResult = RayTracer.retrace(player, ClipContext.Fluid.NONE);
        if (traceResult.getType() == HitResult.Type.MISS || player.isSecondaryUseActive() || !canToolAffect(tool, stack, world, pos) || radius <= 0) {
            return ImmutableList.of();
        }
        int yMin = -1;
        int yMax = 2 * radius - 1;

        switch (traceResult.getDirection()) {
            case DOWN:
            case UP:
                area = BlockPos.betweenClosedStream(pos.offset(-radius, 0, -radius), pos.offset(radius, 0, radius))
                        .filter(blockPos -> canToolAffect(tool, stack, world, blockPos))
                        .map(BlockPos::immutable)
                        .collect(Collectors.toList());
                break;
            case NORTH:
            case SOUTH:
                area = BlockPos.betweenClosedStream(pos.offset(-radius, yMin, 0), pos.offset(radius, yMax, 0))
                        .filter(blockPos -> canToolAffect(tool, stack, world, blockPos))
                        .map(BlockPos::immutable)
                        .collect(Collectors.toList());
                break;
            default:
                area = BlockPos.betweenClosedStream(pos.offset(0, yMin, -radius), pos.offset(0, yMax, radius))
                        .filter(blockPos -> canToolAffect(tool, stack, world, blockPos))
                        .map(BlockPos::immutable)
                        .collect(Collectors.toList());
                break;
        }
        return ImmutableList.copyOf(area);
    }
    // endregion

    // region SICKLE
    public static ImmutableList<BlockPos> getBlocksCentered(ItemStack stack, BlockPos pos, Player player, int radius, int height) {

        List<BlockPos> area;
        Level world = player.getCommandSenderWorld();
        Item tool = stack.getItem();

        if (player.isSecondaryUseActive() || !canToolAffect(tool, stack, world, pos) || (radius <= 0 && height <= 0)) {
            return ImmutableList.of();
        }
        area = BlockPos.betweenClosedStream(pos.offset(-radius, -height, -radius), pos.offset(radius, height, radius))
                .filter(blockPos -> canToolAffect(tool, stack, world, blockPos))
                .map(BlockPos::immutable)
                .collect(Collectors.toList());
        area.remove(pos);
        return ImmutableList.copyOf(area);
    }
    // endregion

    // region HELPERS
    private static boolean canToolAffect(Item toolItem, ItemStack toolStack, Level world, BlockPos pos) {

        BlockState state = world.getBlockState(pos);
        if (state.getDestroySpeed(world, pos) < 0) {
            return false;
        }
        return toolItem.isCorrectToolForDrops(toolStack, state) || !state.requiresCorrectToolForDrops() && toolItem.getDestroySpeed(toolStack, state) > 1.0F;
    }

    private static boolean canHoeAffect(Level world, BlockPos pos, boolean weeding) {

        // TODO King Lemming FIXME
        //        BlockState state = world.getBlockState(pos);
        //        if (HoeItem.TILLABLES.containsKey(state.getBlock())) {
        //            BlockPos up = pos.above();
        //            BlockState stateUp = world.getBlockState(up);
        //            return world.isEmptyBlock(up) || (weeding && (stateUp.getMaterial() == Material.PLANT || stateUp.getMaterial() == Material.REPLACEABLE_PLANT) && stateUp.getDestroySpeed(world, up) <= 0.0F);
        //        }
        return false;
    }

    private static boolean isBucketable(Item toolItem, ItemStack toolStack, Level world, BlockPos pos) {

        BlockState state = world.getBlockState(pos);
        return state.getBlock() instanceof BucketPickup;
    }
    // endregion
}
