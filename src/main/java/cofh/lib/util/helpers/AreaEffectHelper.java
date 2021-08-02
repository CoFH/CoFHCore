package cofh.lib.util.helpers;

import cofh.lib.util.RayTracer;
import com.google.common.collect.ImmutableList;
import net.minecraft.block.BlockState;
import net.minecraft.block.IBucketPickupHandler;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.HoeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static cofh.lib.capability.CapabilityAreaEffect.AREA_EFFECT_ITEM_CAPABILITY;
import static cofh.lib.util.Utils.getItemEnchantmentLevel;
import static cofh.lib.util.references.EnsorcReferences.*;
import static net.minecraft.util.Direction.DOWN;

public class AreaEffectHelper {

    private AreaEffectHelper() {

    }

    public static boolean validAreaEffectItem(ItemStack stack) {

        return stack.getCapability(AREA_EFFECT_ITEM_CAPABILITY).isPresent() || stack.getItem() instanceof ToolItem;
    }

    public static boolean validAreaEffectMiningItem(ItemStack stack) {

        return stack.getCapability(AREA_EFFECT_ITEM_CAPABILITY).isPresent() || stack.getItem() instanceof ToolItem;
    }

    /**
     * Basically the "default" AOE behavior.
     */
    public static ImmutableList<BlockPos> getAreaEffectBlocks(ItemStack stack, BlockPos pos, PlayerEntity player) {

        int encExcavating = getItemEnchantmentLevel(EXCAVATING, stack);
        if (encExcavating > 0) {
            return getBreakableBlocksRadius(stack, pos, player, encExcavating);
        }
        int encTilling = getItemEnchantmentLevel(TILLING, stack);
        if (encTilling > 0) {
            return getTillableBlocksRadius(stack, pos, player, encTilling);
        }
        int encFurrowing = getItemEnchantmentLevel(FURROWING, stack);
        if (encFurrowing > 0) {
            return getTillableBlocksLine(stack, pos, player, encFurrowing * 2);
        }
        return ImmutableList.of();
    }

    // region FLUID
    public static ImmutableList<BlockPos> getBucketableBlocksRadius(ItemStack stack, BlockPos pos, PlayerEntity player, int radius) {

        List<BlockPos> area;
        World world = player.getEntityWorld();
        Item tool = stack.getItem();

        BlockRayTraceResult traceResult = RayTracer.retrace(player, RayTraceContext.FluidMode.SOURCE_ONLY);
        if (traceResult.getType() == RayTraceResult.Type.MISS || player.isSecondaryUseActive() || radius <= 0) {
            return ImmutableList.of();
        }
        int yMin = -1;
        int yMax = 2 * radius - 1;

        switch (traceResult.getFace()) {
            case DOWN:
            case UP:
                area = BlockPos.getAllInBox(pos.add(-radius, 0, -radius), pos.add(radius, 0, radius))
                        .filter(blockPos -> isBucketable(tool, stack, world, blockPos))
                        .map(BlockPos::toImmutable)
                        .collect(Collectors.toList());
                break;
            case NORTH:
            case SOUTH:
                area = BlockPos.getAllInBox(pos.add(-radius, yMin, 0), pos.add(radius, yMax, 0))
                        .filter(blockPos -> isBucketable(tool, stack, world, blockPos))
                        .map(BlockPos::toImmutable)
                        .collect(Collectors.toList());
                break;
            default:
                area = BlockPos.getAllInBox(pos.add(0, yMin, -radius), pos.add(0, yMax, radius))
                        .filter(blockPos -> isBucketable(tool, stack, world, blockPos))
                        .map(BlockPos::toImmutable)
                        .collect(Collectors.toList());
                break;
        }
        area.remove(pos);
        return ImmutableList.copyOf(area);
    }
    // endregion

    // region MINING
    public static ImmutableList<BlockPos> getBreakableBlocksRadius(ItemStack stack, BlockPos pos, PlayerEntity player, int radius) {

        List<BlockPos> area;
        World world = player.getEntityWorld();
        Item tool = stack.getItem();

        BlockRayTraceResult traceResult = RayTracer.retrace(player, RayTraceContext.FluidMode.NONE);
        if (traceResult.getType() == RayTraceResult.Type.MISS || player.isSecondaryUseActive() || !canToolAffect(tool, stack, world, pos) || radius <= 0) {
            return ImmutableList.of();
        }
        int yMin = -1;
        int yMax = 2 * radius - 1;

        switch (traceResult.getFace()) {
            case DOWN:
            case UP:
                area = BlockPos.getAllInBox(pos.add(-radius, 0, -radius), pos.add(radius, 0, radius))
                        .filter(blockPos -> canToolAffect(tool, stack, world, blockPos))
                        .map(BlockPos::toImmutable)
                        .collect(Collectors.toList());
                break;
            case NORTH:
            case SOUTH:
                area = BlockPos.getAllInBox(pos.add(-radius, yMin, 0), pos.add(radius, yMax, 0))
                        .filter(blockPos -> canToolAffect(tool, stack, world, blockPos))
                        .map(BlockPos::toImmutable)
                        .collect(Collectors.toList());
                break;
            default:
                area = BlockPos.getAllInBox(pos.add(0, yMin, -radius), pos.add(0, yMax, radius))
                        .filter(blockPos -> canToolAffect(tool, stack, world, blockPos))
                        .map(BlockPos::toImmutable)
                        .collect(Collectors.toList());
                break;
        }
        area.remove(pos);
        return ImmutableList.copyOf(area);
    }

    public static ImmutableList<BlockPos> getBreakableBlocksDepth(ItemStack stack, BlockPos pos, PlayerEntity player, int radius, int depth) {

        List<BlockPos> area;
        World world = player.getEntityWorld();
        Item tool = stack.getItem();

        BlockRayTraceResult traceResult = RayTracer.retrace(player, RayTraceContext.FluidMode.NONE);
        if (traceResult.getType() == RayTraceResult.Type.MISS || player.isSecondaryUseActive() || !canToolAffect(tool, stack, world, pos) || (radius <= 0 && depth <= 0)) {
            return ImmutableList.of();
        }
        int dMin = depth;
        int dMax = 0;

        int yMin = -1;
        int yMax = 2 * radius - 1;

        switch (traceResult.getFace()) {
            case DOWN:
                dMin = 0;
                dMax = depth;
            case UP:
                area = BlockPos.getAllInBox(pos.add(-radius, -dMin, -radius), pos.add(radius, dMax, radius))
                        .filter(blockPos -> canToolAffect(tool, stack, world, blockPos))
                        .map(BlockPos::toImmutable)
                        .collect(Collectors.toList());
                break;
            case NORTH:
                dMin = 0;
                dMax = depth;
            case SOUTH:
                area = BlockPos.getAllInBox(pos.add(-radius, yMin, -dMin), pos.add(radius, yMax, dMax))
                        .filter(blockPos -> canToolAffect(tool, stack, world, blockPos))
                        .map(BlockPos::toImmutable)
                        .collect(Collectors.toList());
                break;
            case WEST:
                dMin = 0;
                dMax = depth;
            default:
                area = BlockPos.getAllInBox(pos.add(-dMin, yMin, -radius), pos.add(dMax, yMax, radius))
                        .filter(blockPos -> canToolAffect(tool, stack, world, blockPos))
                        .map(BlockPos::toImmutable)
                        .collect(Collectors.toList());
                break;

        }
        area.remove(pos);
        return ImmutableList.copyOf(area);
    }

    public static ImmutableList<BlockPos> getBreakableBlocksLine(ItemStack stack, BlockPos pos, PlayerEntity player, int length) {

        ArrayList<BlockPos> area = new ArrayList<>();
        World world = player.getEntityWorld();
        Item tool = stack.getItem();

        BlockPos query;
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();

        if (player.isSecondaryUseActive() || !canToolAffect(tool, stack, world, pos) || length <= 0) {
            return ImmutableList.of();
        }
        switch (player.getHorizontalFacing()) {
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

    // region HOE
    public static ImmutableList<BlockPos> getTillableBlocksRadius(ItemStack stack, BlockPos pos, PlayerEntity player, int radius) {

        List<BlockPos> area;
        World world = player.getEntityWorld();
        boolean weeding = getItemEnchantmentLevel(WEEDING, stack) > 0;

        BlockRayTraceResult traceResult = RayTracer.retrace(player, RayTraceContext.FluidMode.NONE);
        if (traceResult.getType() == RayTraceResult.Type.MISS || traceResult.getFace() == DOWN || player.isSecondaryUseActive() || !canHoeAffect(world, pos, weeding) || radius <= 0) {
            return ImmutableList.of();
        }
        area = BlockPos.getAllInBox(pos.add(-radius, 0, -radius), pos.add(radius, 0, radius))
                .filter(blockPos -> canHoeAffect(world, blockPos, weeding))
                .map(BlockPos::toImmutable)
                .collect(Collectors.toList());
        area.remove(pos);
        return ImmutableList.copyOf(area);
    }

    public static ImmutableList<BlockPos> getTillableBlocksLine(ItemStack stack, BlockPos pos, PlayerEntity player, int length) {

        List<BlockPos> area;
        World world = player.getEntityWorld();
        boolean weeding = getItemEnchantmentLevel(WEEDING, stack) > 0;

        if (player.isSecondaryUseActive() || !canHoeAffect(world, pos, weeding) || length <= 0) {
            return ImmutableList.of();
        }
        switch (player.getHorizontalFacing()) {
            case SOUTH:
                area = BlockPos.getAllInBox(pos.add(0, 0, 1), pos.add(0, 0, length + 1))
                        .filter(blockPos -> canHoeAffect(world, blockPos, weeding))
                        .map(BlockPos::toImmutable)
                        .collect(Collectors.toList());
                break;
            case WEST:
                area = BlockPos.getAllInBox(pos.add(-1, 0, 0), pos.add(-(length + 1), 0, 0))
                        .filter(blockPos -> canHoeAffect(world, blockPos, weeding))
                        .map(BlockPos::toImmutable)
                        .collect(Collectors.toList());
                break;
            case NORTH:
                area = BlockPos.getAllInBox(pos.add(0, 0, -1), pos.add(0, 0, -(length + 1)))
                        .filter(blockPos -> canHoeAffect(world, blockPos, weeding))
                        .map(BlockPos::toImmutable)
                        .collect(Collectors.toList());
                break;
            case EAST:
                area = BlockPos.getAllInBox(pos.add(1, 0, 0), pos.add(length + 1, 0, 0))
                        .filter(blockPos -> canHoeAffect(world, blockPos, weeding))
                        .map(BlockPos::toImmutable)
                        .collect(Collectors.toList());
                break;
            default:
                area = ImmutableList.of();
        }
        area.remove(pos);
        return ImmutableList.copyOf(area);
    }
    // endregion

    // region SICKLE
    public static ImmutableList<BlockPos> getBlocksCentered(ItemStack stack, BlockPos pos, PlayerEntity player, int radius, int height) {

        List<BlockPos> area;
        World world = player.getEntityWorld();
        Item tool = stack.getItem();

        if (player.isSecondaryUseActive() || !canToolAffect(tool, stack, world, pos) || (radius <= 0 && height <= 0)) {
            return ImmutableList.of();
        }
        area = BlockPos.getAllInBox(pos.add(-radius, -height, -radius), pos.add(radius, height, radius))
                .filter(blockPos -> canToolAffect(tool, stack, world, blockPos))
                .map(BlockPos::toImmutable)
                .collect(Collectors.toList());
        area.remove(pos);
        return ImmutableList.copyOf(area);
    }
    // endregion

    // region HELPERS
    private static boolean canToolAffect(Item toolItem, ItemStack toolStack, World world, BlockPos pos) {

        BlockState state = world.getBlockState(pos);
        if (state.getBlockHardness(world, pos) < 0) {
            return false;
        }
        return toolItem.canHarvestBlock(toolStack, state) || !state.getRequiresTool() && toolItem.getDestroySpeed(toolStack, state) > 1.0F;
    }

    private static boolean canHoeAffect(World world, BlockPos pos, boolean weeding) {

        BlockState state = world.getBlockState(pos);
        if (HoeItem.HOE_LOOKUP.containsKey(state.getBlock())) {
            BlockPos up = pos.up();
            BlockState stateUp = world.getBlockState(up);
            return world.isAirBlock(up) || (weeding && (stateUp.getMaterial() == Material.PLANTS || stateUp.getMaterial() == Material.TALL_PLANTS) && stateUp.getBlockHardness(world, up) <= 0.0F);
        }
        return false;
    }

    private static boolean isBucketable(Item toolItem, ItemStack toolStack, World world, BlockPos pos) {

        BlockState state = world.getBlockState(pos);
        return state.getBlock() instanceof IBucketPickupHandler;
    }
    // endregion
}
