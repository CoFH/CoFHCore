package cofh.core.util.helpers;

import cofh.lib.api.block.IHarvestable;
import cofh.lib.util.raytracer.RayTracer;
import com.google.common.collect.ImmutableList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.ITagManager;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static cofh.core.capability.CapabilityAreaEffect.AREA_EFFECT_ITEM_CAPABILITY;
import static cofh.lib.util.Utils.getItemEnchantmentLevel;
import static cofh.lib.util.references.EnsorcReferences.*;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.*;

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

    public static ImmutableList<BlockPos> getBreakableWoodenBlocksVertical(ItemStack stack, BlockPos pos, Player player, int length) {

        Level world = player.getCommandSenderWorld();
        Item tool = stack.getItem();
        if (length <= 0 || !canToolAffect(tool, stack, world, pos) || player.isSecondaryUseActive()) {
            return ImmutableList.of();
        }
        BlockHitResult traceResult = RayTracer.retrace(player, ClipContext.Fluid.NONE);
        if (traceResult.getType() == HitResult.Type.MISS) {
            return ImmutableList.of();
        }

        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        ITagManager<Block> tags = ForgeRegistries.BLOCKS.tags();
        if (tags == null) {
            return ImmutableList.of();
        }

        Predicate<BlockPos> exact = p -> world.getBlockState(p).is(block) && canToolAffect(tool, stack, world, p);
        // Match logs based on tag
        Predicate<BlockPos> match = tags.getReverseTag(state.getBlock()).map(rev -> {
            if (rev.containsTag(BlockTags.LOGS)) {
                return rev.getTagKeys().filter(key -> key.location().getPath().contains("_logs")).findAny().map(key -> exact.or(p -> world.getBlockState(p).is(key))).orElse(exact);
            }
            return exact;
        }).orElse(exact);

        BlockPos.MutableBlockPos mutable = pos.mutable();
        ImmutableList.Builder<BlockPos> builder = ImmutableList.builder();
        for (int i = 0; i < length; ++i) {
            mutable.move(0, 1, 0);
            if (!match.test(mutable)) {
                break;
            }
            builder.add(mutable.immutable());
        }
        return builder.build();
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

    // region HOE
    public static ImmutableList<BlockPos> getTillableBlocksRadius(ItemStack stack, BlockPos pos, Player player, int radius) {

        List<BlockPos> area;
        Level world = player.getCommandSenderWorld();

        BlockHitResult traceResult = RayTracer.retrace(player, ClipContext.Fluid.NONE);
        UseOnContext context = new UseOnContext(world, player, InteractionHand.MAIN_HAND, player.getMainHandItem(), traceResult);
        if (traceResult.getType() == HitResult.Type.MISS || player.isSecondaryUseActive() || !canHoeAffect(world, player, context) || radius <= 0) {
            return ImmutableList.of();
        }
        area = BlockPos.betweenClosedStream(pos.offset(-radius, 0, -radius), pos.offset(radius, 0, radius))
                .filter(blockPos -> canHoeAffect(world, player, getContextAt(context, blockPos)))
                .map(BlockPos::immutable)
                .collect(Collectors.toList());
        area.remove(pos);
        return ImmutableList.copyOf(area);
    }

    public static ImmutableList<BlockPos> getTillableBlocksLine(ItemStack stack, BlockPos pos, Player player, int length) {

        List<BlockPos> area;
        Level world = player.getCommandSenderWorld();

        BlockHitResult traceResult = RayTracer.retrace(player, ClipContext.Fluid.NONE);
        UseOnContext context = new UseOnContext(world, player, InteractionHand.MAIN_HAND, player.getMainHandItem(), traceResult);
        if (traceResult.getType() == HitResult.Type.MISS || player.isSecondaryUseActive() || !canHoeAffect(world, player, context) || length <= 0) {
            return ImmutableList.of();
        }
        area = switch (player.getDirection()) {
            case SOUTH -> BlockPos.betweenClosedStream(pos.offset(0, 0, 1), pos.offset(0, 0, length + 1))
                    .filter(blockPos -> canHoeAffect(world, player, getContextAt(context, blockPos)))
                    .map(BlockPos::immutable)
                    .collect(Collectors.toList());
            case WEST -> BlockPos.betweenClosedStream(pos.offset(-1, 0, 0), pos.offset(-(length + 1), 0, 0))
                    .filter(blockPos -> canHoeAffect(world, player, getContextAt(context, blockPos)))
                    .map(BlockPos::immutable)
                    .collect(Collectors.toList());
            case NORTH -> BlockPos.betweenClosedStream(pos.offset(0, 0, -1), pos.offset(0, 0, -(length + 1)))
                    .filter(blockPos -> canHoeAffect(world, player, getContextAt(context, blockPos)))
                    .map(BlockPos::immutable)
                    .collect(Collectors.toList());
            case EAST -> BlockPos.betweenClosedStream(pos.offset(1, 0, 0), pos.offset(length + 1, 0, 0))
                    .filter(blockPos -> canHoeAffect(world, player, getContextAt(context, blockPos)))
                    .map(BlockPos::immutable)
                    .collect(Collectors.toList());
            default -> ImmutableList.of();
        };
        area.remove(pos);
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

    public static ImmutableList<BlockPos> getMatureBlocksCentered(ItemStack stack, BlockPos pos, Player player, int radius, int height) {

        List<BlockPos> area;
        Level world = player.getCommandSenderWorld();
        Item tool = stack.getItem();

        if (player.isSecondaryUseActive() || !canToolAffect(tool, stack, world, pos) || !isMature(world, pos) || (radius <= 0 && height <= 0)) {
            return ImmutableList.of();
        }
        area = BlockPos.betweenClosedStream(pos.offset(-radius, -height, -radius), pos.offset(radius, height, radius))
                .filter(blockPos -> canToolAffect(tool, stack, world, blockPos) && isMature(world, blockPos))
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

    private static boolean canHoeAffect(Level level, Player player, UseOnContext context) {

        // TODO: revisit if performance issues show.
        BlockState state = level.getBlockState(context.getClickedPos()).getToolModifiedState(context, ToolActions.HOE_TILL, true);
        return state != null;
    }

    private static UseOnContext getContextAt(UseOnContext context, BlockPos pos) {

        BlockPos og = context.getClickedPos();
        Vec3 loc = context.getClickLocation().add(pos.getX() - og.getX(), pos.getY() - og.getY(), pos.getZ() - og.getZ());
        return new UseOnContext(context.getLevel(), context.getPlayer(), context.getHand(), context.getItemInHand(),
                new BlockHitResult(loc, context.getClickedFace(), pos, context.isInside()));
    }

    private static boolean isBucketable(Item toolItem, ItemStack toolStack, Level world, BlockPos pos) {

        BlockState state = world.getBlockState(pos);
        return state.getBlock() instanceof BucketPickup;
    }

    public static boolean isMature(Level level, BlockPos pos) {

        return isMature(level, pos, level.getBlockState(pos));
    }

    public static boolean isMature(Level level, BlockPos pos, BlockState state) {

        Block block = state.getBlock();
        if (block instanceof IHarvestable harvestable) {
            return harvestable.canHarvest(state);
        }
        if (block instanceof CropBlock crop) {
            return crop.isMaxAge(state);
        }
        if (block instanceof StemBlock) {
            return false;
        }
        if (block instanceof GrowingPlantBlock plant) {
            BlockState root = level.getBlockState(pos.relative(plant.growthDirection.getOpposite()));
            return root.is(plant.getBodyBlock());
        }
        if (block instanceof BambooBlock || block instanceof CactusBlock || block instanceof SugarCaneBlock) {
            BlockState below = level.getBlockState(pos.relative(Direction.DOWN));
            return below.is(block);
        }
        if (block instanceof DoublePlantBlock) {
            return !state.is(Blocks.SMALL_DRIPLEAF);
        }
        if (block instanceof LeavesBlock) {
            return !state.getOptionalValue(LeavesBlock.PERSISTENT).orElse(false);
        }
        Material material = state.getMaterial();
        return state.getOptionalValue(AGE_1).map(v -> v >= 1)
                .or(() -> state.getOptionalValue(AGE_2).map(v -> v >= 2))
                .or(() -> state.getOptionalValue(AGE_3).map(v -> v >= 3))
                .or(() -> state.getOptionalValue(AGE_5).map(v -> v >= 5))
                .or(() -> state.getOptionalValue(AGE_7).map(v -> v >= 7))
                .or(() -> state.getOptionalValue(AGE_15).map(v -> v >= 15))
                .or(() -> state.getOptionalValue(AGE_25).map(v -> v >= 25))
                .orElse(block instanceof BigDripleafBlock || block instanceof BigDripleafStemBlock ||
                        block instanceof HugeMushroomBlock || state.is(BlockTags.TALL_FLOWERS) ||
                        material.equals(Material.VEGETABLE) || material.equals(Material.MOSS));
    }
    // endregion
}
