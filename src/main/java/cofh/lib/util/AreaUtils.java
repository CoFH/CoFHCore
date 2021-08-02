package cofh.lib.util;

import cofh.lib.util.helpers.MathHelper;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.EndermanEntity;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static cofh.lib.util.references.CoreReferences.*;
import static net.minecraft.block.Blocks.*;

public class AreaUtils {

    private AreaUtils() {

    }

    public static final int HORZ_MAX = 16;
    public static final int VERT_MAX = 8;

    // region BURNING
    public static void igniteNearbyEntities(Entity entity, World worldIn, BlockPos pos, int radius, int duration) {

        AxisAlignedBB area = new AxisAlignedBB(pos.add(-radius, -radius, -radius), pos.add(1 + radius, 1 + radius, 1 + radius));
        List<LivingEntity> mobs = worldIn.getEntitiesWithinAABB(LivingEntity.class, area, EntityPredicates.IS_ALIVE);
        mobs.removeIf(Entity::isInWater);
        mobs.removeIf(Entity::isImmuneToFire);
        mobs.removeIf(mob -> mob instanceof EndermanEntity);
        for (LivingEntity mob : mobs) {
            mob.setFire(duration);
        }
    }

    public static void igniteNearbyGround(Entity entity, World worldIn, BlockPos pos, int radius, double chance) {

        float f = (float) Math.min(HORZ_MAX, radius);
        float v = (float) Math.min(VERT_MAX, radius);
        float f2 = f * f;
        BlockPos.Mutable mutable = new BlockPos.Mutable();

        for (BlockPos blockpos : BlockPos.getAllInBoxMutable(pos.add(-f, -v, -f), pos.add(f, v, f))) {
            double distance = blockpos.distanceSq(entity.getPositionVec(), true);
            if (distance < f2) {
                mutable.setPos(blockpos.getX(), blockpos.getY() + 1, blockpos.getZ());
                BlockState blockstate1 = worldIn.getBlockState(mutable);
                if (blockstate1.isAir(worldIn, mutable)) {
                    if (isValidFirePosition(worldIn, mutable, chance)) {
                        worldIn.setBlockState(mutable, ((FireBlock) FIRE).getStateForPlacement(worldIn, mutable));
                    }
                }
            }
        }
    }

    public static void igniteSpecial(Entity entity, World worldIn, BlockPos pos, int radius, boolean campfire, boolean tnt, @Nullable Entity igniter) {

        float f = (float) Math.min(HORZ_MAX, radius);
        float v = (float) Math.min(VERT_MAX, radius);
        float f2 = f * f;

        for (BlockPos blockpos : BlockPos.getAllInBoxMutable(pos.add(-f, -v, -f), pos.add(f, v, f))) {
            double distance = blockpos.distanceSq(entity.getPositionVec(), true);
            if (distance < f2) {
                BlockState state = worldIn.getBlockState(blockpos);
                if (campfire && isUnlitCampfire(state)) {
                    worldIn.setBlockState(blockpos, state.with(BlockStateProperties.LIT, true));
                } else if (tnt && isUnlitTNT(state)) {
                    state.getBlock().catchFire(state, worldIn, blockpos, Direction.UP, igniter instanceof LivingEntity ? (LivingEntity) igniter : null);
                    worldIn.setBlockState(blockpos, AIR.getDefaultState());
                }
            }
        }
    }

    public static boolean isValidFirePosition(World worldIn, BlockPos pos, double chance) {

        BlockPos below = pos.down();
        BlockState state = worldIn.getBlockState(below);
        if (Block.doesSideFillSquare(state.getCollisionShape(worldIn, below), Direction.UP)) {
            return state.getMaterial().isFlammable() || worldIn.rand.nextDouble() < chance; // Random chance.
        }
        return false;
    }

    public static boolean isLitCampfire(BlockState state) {

        return state.getBlock() instanceof CampfireBlock && state.get(BlockStateProperties.LIT);
    }

    public static boolean isUnlitCampfire(BlockState state) {

        return state.getBlock() instanceof CampfireBlock && !state.get(BlockStateProperties.WATERLOGGED) && !state.get(BlockStateProperties.LIT);
    }

    public static boolean isUnlitTNT(BlockState state) {

        return state.getBlock() instanceof TNTBlock;
    }
    // endregion

    // region FREEZING
    public static void freezeNearbyGround(Entity entity, World worldIn, BlockPos pos, int radius) {

        BlockState state = SNOW.getDefaultState();
        float f = (float) Math.min(HORZ_MAX, radius);
        float v = (float) Math.min(VERT_MAX, radius);
        float f2 = f * f;
        BlockPos.Mutable mutable = new BlockPos.Mutable();

        for (BlockPos blockpos : BlockPos.getAllInBoxMutable(pos.add(-f, -v, -f), pos.add(f, v, f))) {
            double distance = blockpos.distanceSq(entity.getPositionVec(), true);
            if (distance < f2) {
                mutable.setPos(blockpos.getX(), blockpos.getY() + 1, blockpos.getZ());
                BlockState blockstate1 = worldIn.getBlockState(mutable);
                if (blockstate1.isAir(worldIn, mutable)) {
                    if (worldIn.getBiome(mutable).getTemperature(blockpos) < 0.8F && isValidSnowPosition(worldIn, mutable)) {
                        worldIn.setBlockState(mutable, state);
                    }
                }
            }
        }
    }

    public static void freezeSpecial(Entity entity, World worldIn, BlockPos pos, int radius, boolean campfire, boolean fire) {

        float f = (float) Math.min(HORZ_MAX, radius);
        float v = (float) Math.min(VERT_MAX, radius);
        float f2 = f * f;

        for (BlockPos blockpos : BlockPos.getAllInBoxMutable(pos.add(-f, -v, -f), pos.add(f, v, f))) {
            double distance = blockpos.distanceSq(entity.getPositionVec(), true);
            if (distance < f2) {
                BlockState state = worldIn.getBlockState(blockpos);
                if (campfire && isLitCampfire(state)) {
                    worldIn.setBlockState(blockpos, state.with(BlockStateProperties.LIT, false));
                } else if (fire && state.getBlock() == FIRE) {
                    worldIn.setBlockState(blockpos, AIR.getDefaultState());
                }
            }
        }
    }

    public static void freezeSurfaceWater(Entity entity, World worldIn, BlockPos pos, int radius, boolean permanent) {

        BlockState state = permanent ? ICE.getDefaultState() : FROSTED_ICE.getDefaultState();
        float f = (float) Math.min(HORZ_MAX, radius);
        float v = (float) Math.min(VERT_MAX, radius);
        float f2 = f * f;
        BlockPos.Mutable mutable = new BlockPos.Mutable();

        for (BlockPos blockpos : BlockPos.getAllInBoxMutable(pos.add(-f, -v, -f), pos.add(f, v, f))) {
            double distance = blockpos.distanceSq(entity.getPositionVec(), true);
            if (distance < f2) {
                mutable.setPos(blockpos.getX(), blockpos.getY() + 1, blockpos.getZ());
                BlockState blockstate1 = worldIn.getBlockState(mutable);
                if (blockstate1.isAir(worldIn, mutable)) {
                    BlockState blockstate2 = worldIn.getBlockState(blockpos);
                    boolean isFull = blockstate2.getBlock() == WATER && blockstate2.get(FlowingFluidBlock.LEVEL) == 0;
                    if (blockstate2.getMaterial() == Material.WATER && isFull && state.isValidPosition(worldIn, blockpos) && worldIn.placedBlockCollides(state, blockpos, ISelectionContext.dummy())) {
                        worldIn.setBlockState(blockpos, state);
                        if (!permanent) {
                            worldIn.getPendingBlockTicks().scheduleTick(blockpos, FROSTED_ICE, MathHelper.nextInt(worldIn.rand, 60, 120));
                        }
                    }
                }
            }
        }
    }

    public static void freezeAllWater(Entity entity, World worldIn, BlockPos pos, int radius, boolean permanent) {

        BlockState state = permanent ? ICE.getDefaultState() : FROSTED_ICE.getDefaultState();
        float f = (float) Math.min(HORZ_MAX, radius);
        float v = (float) Math.min(VERT_MAX, radius);
        float f2 = f * f;

        for (BlockPos blockpos : BlockPos.getAllInBoxMutable(pos.add(-f, -v, -f), pos.add(f, v, f))) {
            double distance = blockpos.distanceSq(entity.getPositionVec(), true);
            if (distance < f2) {
                BlockState blockstate2 = worldIn.getBlockState(blockpos);
                boolean isFull = blockstate2.getBlock() == WATER && blockstate2.get(FlowingFluidBlock.LEVEL) == 0;
                if (blockstate2.getMaterial() == Material.WATER && isFull && state.isValidPosition(worldIn, blockpos) && worldIn.placedBlockCollides(state, blockpos, ISelectionContext.dummy())) {
                    worldIn.setBlockState(blockpos, state);
                    if (!permanent) {
                        worldIn.getPendingBlockTicks().scheduleTick(blockpos, FROSTED_ICE, MathHelper.nextInt(worldIn.rand, 60, 120));
                    }
                }
            }
        }
    }

    public static void freezeSurfaceLava(Entity entity, World worldIn, BlockPos pos, int radius, boolean permanent) {

        if (GLOSSED_MAGMA == null && !permanent) {
            return;
        }
        BlockState state = permanent ? OBSIDIAN.getDefaultState() : GLOSSED_MAGMA.getDefaultState();
        float f = (float) Math.min(HORZ_MAX, radius);
        float v = (float) Math.min(VERT_MAX, radius);
        float f2 = f * f;
        BlockPos.Mutable mutable = new BlockPos.Mutable();

        for (BlockPos blockpos : BlockPos.getAllInBoxMutable(pos.add(-f, -v, -f), pos.add(f, v, f))) {
            double distance = blockpos.distanceSq(entity.getPositionVec(), true);
            if (distance < f2) {
                mutable.setPos(blockpos.getX(), blockpos.getY() + 1, blockpos.getZ());
                BlockState blockstate1 = worldIn.getBlockState(mutable);
                if (blockstate1.isAir(worldIn, mutable)) {
                    BlockState blockstate2 = worldIn.getBlockState(blockpos);
                    boolean isFull = blockstate2.getBlock() == LAVA && blockstate2.get(FlowingFluidBlock.LEVEL) == 0;
                    if (blockstate2.getMaterial() == Material.LAVA && isFull && state.isValidPosition(worldIn, blockpos) && worldIn.placedBlockCollides(state, blockpos, ISelectionContext.dummy())) {
                        worldIn.setBlockState(blockpos, state);
                        if (!permanent) {
                            worldIn.getPendingBlockTicks().scheduleTick(blockpos, GLOSSED_MAGMA, MathHelper.nextInt(worldIn.rand, 60, 120));
                        }
                    }
                }
            }
        }
    }

    public static void freezeAllLava(Entity entity, World worldIn, BlockPos pos, int radius, boolean permanent) {

        if (GLOSSED_MAGMA == null && !permanent) {
            return;
        }
        BlockState state = permanent ? OBSIDIAN.getDefaultState() : GLOSSED_MAGMA.getDefaultState();
        float f = (float) Math.min(HORZ_MAX, radius);
        float v = (float) Math.min(VERT_MAX, radius);
        float f2 = f * f;

        for (BlockPos blockpos : BlockPos.getAllInBoxMutable(pos.add(-f, -v, -f), pos.add(f, v, f))) {
            double distance = blockpos.distanceSq(entity.getPositionVec(), true);
            if (distance < f2) {
                BlockState blockstate2 = worldIn.getBlockState(blockpos);
                boolean isFull = blockstate2.getBlock() == LAVA && blockstate2.get(FlowingFluidBlock.LEVEL) == 0;
                if (blockstate2.getMaterial() == Material.LAVA && isFull && state.isValidPosition(worldIn, blockpos) && worldIn.placedBlockCollides(state, blockpos, ISelectionContext.dummy())) {
                    worldIn.setBlockState(blockpos, state);
                    if (!permanent) {
                        worldIn.getPendingBlockTicks().scheduleTick(blockpos, GLOSSED_MAGMA, MathHelper.nextInt(worldIn.rand, 60, 120));
                    }
                }
            }
        }
    }

    public static boolean isValidSnowPosition(World worldIn, BlockPos pos) {

        BlockState state = worldIn.getBlockState(pos.down());
        Block block = state.getBlock();
        if (block == ICE || block == PACKED_ICE || block == BARRIER || block == FROSTED_ICE || block == GLOSSED_MAGMA) {
            return false;
        }
        return Block.doesSideFillSquare(state.getCollisionShape(worldIn, pos.down()), Direction.UP) || block == SNOW && state.get(SnowBlock.LAYERS) == 8;
    }
    // endregion

    // region AREA TRANSFORMS / MISC
    private static boolean isValidLightningBoltPosition(World worldIn, BlockPos pos, double chance) {

        BlockPos below = pos.down();
        BlockState state = worldIn.getBlockState(below);
        if (worldIn.canSeeSky(pos) && Block.doesSideFillSquare(state.getCollisionShape(worldIn, below), Direction.UP)) {
            return worldIn.rand.nextDouble() < chance; // Random chance.
        }
        return false;
    }

    public static void transformArea(Entity entity, World worldIn, BlockPos pos, BlockState replaceable, BlockState replacement, int radius, boolean requireAir) {

        float f = (float) Math.min(HORZ_MAX, radius);
        float v = (float) Math.min(VERT_MAX, radius);
        float f2 = f * f;
        BlockPos.Mutable mutable = new BlockPos.Mutable();

        if (requireAir) {
            for (BlockPos iterPos : BlockPos.getAllInBoxMutable(pos.add(-f, -v, -f), pos.add(f, v, f))) {
                double distance = iterPos.distanceSq(entity.getPositionVec(), true);
                if (distance < f2) {
                    mutable.setPos(iterPos.getX(), iterPos.getY() + 1, iterPos.getZ());
                    BlockState blockstate1 = worldIn.getBlockState(mutable);
                    if (blockstate1.isAir(worldIn, mutable)) {
                        if (worldIn.getBlockState(iterPos) == replaceable) {
                            worldIn.setBlockState(iterPos, replacement);
                        }
                    }
                }
            }
        } else {
            for (BlockPos iterPos : BlockPos.getAllInBoxMutable(pos.add(-f, -v, -f), pos.add(f, v, f))) {
                double distance = iterPos.distanceSq(entity.getPositionVec(), true);
                if (distance < f2) {
                    if (worldIn.getBlockState(iterPos) == replaceable) {
                        worldIn.setBlockState(iterPos, replacement);
                    }
                }
            }
        }
    }

    public static void transformArea(Entity entity, World worldIn, BlockPos pos, Set<BlockState> replaceable, BlockState replacement, int radius, boolean requireAir) {

        float f = (float) Math.min(HORZ_MAX, radius);
        float v = (float) Math.min(VERT_MAX, radius);
        float f2 = f * f;
        BlockPos.Mutable mutable = new BlockPos.Mutable();

        if (requireAir) {
            for (BlockPos blockpos : BlockPos.getAllInBoxMutable(pos.add(-f, -v, -f), pos.add(f, v, f))) {
                double distance = blockpos.distanceSq(entity.getPositionVec(), true);
                if (distance < f2) {
                    mutable.setPos(blockpos.getX(), blockpos.getY() + 1, blockpos.getZ());
                    BlockState blockstate1 = worldIn.getBlockState(mutable);
                    if (blockstate1.isAir(worldIn, mutable)) {
                        if (replaceable.contains(worldIn.getBlockState(blockpos))) {
                            worldIn.setBlockState(blockpos, replacement);
                        }
                    }
                }
            }
        } else {
            for (BlockPos iterPos : BlockPos.getAllInBoxMutable(pos.add(-f, -v, -f), pos.add(f, v, f))) {
                if (iterPos.withinDistance(entity.getPositionVec(), f)) {
                    if (replaceable.contains(worldIn.getBlockState(iterPos))) {
                        worldIn.setBlockState(iterPos, replacement);
                    }
                }
            }
        }
    }

    public static void transformGrass(Entity entity, World worldIn, BlockPos pos, int radius) {

        transformArea(entity, worldIn, pos, DIRT.getDefaultState(), GRASS_BLOCK.getDefaultState(), radius, true);
    }

    public static void transformMycelium(Entity entity, World worldIn, BlockPos pos, int radius) {

        Set<BlockState> replaceable = new ObjectOpenHashSet<>();
        Collections.addAll(replaceable, DIRT.getDefaultState(), GRASS_BLOCK.getDefaultState());
        transformArea(entity, worldIn, pos, replaceable, MYCELIUM.getDefaultState(), radius, true);
    }

    public static void transformSignalAir(Entity entity, World worldIn, BlockPos pos, int radius) {

        Set<BlockState> replaceable = new ObjectOpenHashSet<>();
        Collections.addAll(replaceable, AIR.getDefaultState(), CAVE_AIR.getDefaultState());
        transformArea(entity, worldIn, pos, replaceable, SIGNAL_AIR.getDefaultState(), radius, false);
    }

    public static void transformGlowAir(Entity entity, World worldIn, BlockPos pos, int radius) {

        Set<BlockState> replaceable = new ObjectOpenHashSet<>();
        Collections.addAll(replaceable, AIR.getDefaultState(), CAVE_AIR.getDefaultState());
        transformArea(entity, worldIn, pos, replaceable, GLOW_AIR.getDefaultState(), radius, false);
    }

    public static void transformEnderAir(Entity entity, World worldIn, BlockPos pos, int radius) {

        Set<BlockState> replaceable = new ObjectOpenHashSet<>();
        Collections.addAll(replaceable, AIR.getDefaultState(), CAVE_AIR.getDefaultState());
        transformArea(entity, worldIn, pos, replaceable, ENDER_AIR.getDefaultState(), radius, false);
    }

    public static void zapNearbyGround(Entity entity, World worldIn, BlockPos pos, int radius, double chance, int max) {

        float f = (float) Math.min(HORZ_MAX, radius);
        float v = (float) Math.min(VERT_MAX, radius);
        float f2 = f * f;
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        int count = 0;

        for (BlockPos blockpos : BlockPos.getAllInBoxMutable(pos.add(-f, -v, -f), pos.add(f, v, f))) {
            if (count >= max) {
                return;
            }
            double distance = blockpos.distanceSq(entity.getPositionVec(), true);
            if (distance < f2) {
                mutable.setPos(blockpos.getX(), blockpos.getY() + 1, blockpos.getZ());
                BlockState blockstate1 = worldIn.getBlockState(mutable);
                if (blockstate1.isAir(worldIn, mutable)) {
                    if (isValidLightningBoltPosition(worldIn, mutable, chance)) {
                        worldIn.setBlockState(mutable, LIGHTNING_AIR.getDefaultState());
                        ++count;
                    }
                }
            }
        }
    }

    public static void growMushrooms(Entity entity, World worldIn, BlockPos pos, int radius, int count) {

        float f = (float) Math.min(HORZ_MAX, radius);
        float v = (float) Math.min(VERT_MAX, radius);
        float f2 = f * f;
        int grow = 0;
        BlockPos.Mutable mutable = new BlockPos.Mutable();

        mutable.setPos(entity.getPosition().up());
        BlockState blockstate1 = worldIn.getBlockState(mutable);
        if (blockstate1.isAir(worldIn, mutable)) {
            if (isValidMushroomPosition(worldIn, entity.getPosition(), 1.0)) {
                worldIn.setBlockState(mutable, worldIn.rand.nextBoolean() ? BROWN_MUSHROOM.getDefaultState() : RED_MUSHROOM.getDefaultState());
                ++grow;
            }
        }
        for (BlockPos iterPos : BlockPos.getAllInBoxMutable(pos.add(-f, -v, -f), pos.add(f, v, f))) {
            if (grow >= count) {
                return;
            }
            double distance = iterPos.distanceSq(entity.getPositionVec(), true);
            if (distance < f2) {
                mutable.setPos(iterPos.getX(), iterPos.getY() + 1, iterPos.getZ());
                blockstate1 = worldIn.getBlockState(mutable);
                if (blockstate1.isAir(worldIn, mutable)) {
                    if (isValidMushroomPosition(worldIn, iterPos, 0.5 - (distance / f2))) {
                        worldIn.setBlockState(mutable, worldIn.rand.nextBoolean() ? BROWN_MUSHROOM.getDefaultState() : RED_MUSHROOM.getDefaultState());
                        ++grow;
                    }
                }
            }
        }
    }

    private static boolean isValidMushroomPosition(World worldIn, BlockPos pos, double chance) {

        Block block = worldIn.getBlockState(pos).getBlock();
        return worldIn.rand.nextDouble() < chance && (block == MYCELIUM || block == PODZOL);
    }

    public static void growPlants(Entity entity, World worldIn, BlockPos pos, int radius) {

        float f = (float) Math.min(HORZ_MAX, radius);
        float v = (float) Math.min(VERT_MAX, radius);
        float f2 = f * f;

        BlockState state;
        for (BlockPos iterPos : BlockPos.getAllInBoxMutable(pos.add(-f, -v, -f), pos.add(f, v, f))) {
            double distance = iterPos.distanceSq(entity.getPositionVec(), true);
            if (distance < f2) {
                state = worldIn.getBlockState(iterPos);
                if (state.getBlock() instanceof IGrowable) {
                    IGrowable growable = (IGrowable) state.getBlock();
                    if (growable.canGrow(worldIn, iterPos, state, worldIn.isRemote)) {
                        if (!worldIn.isRemote) {
                            if (growable.canUseBonemeal(worldIn, worldIn.rand, iterPos, state)) {
                                // TODO: Remove try/catch when Mojang fixes base issue.
                                try {
                                    growable.grow((ServerWorld) worldIn, worldIn.rand, iterPos, state);
                                } catch (Exception e) {
                                    // Vanilla issue causes bamboo to crash if grown close to world height
                                    if (!(growable instanceof BambooBlock)) {
                                        throw e;
                                    }
                                }
                                // growable.grow((ServerWorld) worldIn, worldIn.rand, pos, state);
                                // ++grow;
                            }
                        }
                    }
                }
            }
        }
    }

    public static void growPlants(Entity entity, World worldIn, BlockPos pos, int radius, int count) {

        float f = (float) Math.min(HORZ_MAX, radius);
        float v = (float) Math.min(VERT_MAX, radius);
        float f2 = f * f;
        int grow = 0;

        BlockState state = worldIn.getBlockState(entity.getPosition());
        if (state.getBlock() instanceof IGrowable) {
            IGrowable growable = (IGrowable) state.getBlock();
            if (growable.canGrow(worldIn, pos, state, worldIn.isRemote)) {
                if (!worldIn.isRemote) {
                    if (growable.canUseBonemeal(worldIn, worldIn.rand, pos, state)) {
                        // TODO: Remove try/catch when Mojang fixes base issue.
                        try {
                            growable.grow((ServerWorld) worldIn, worldIn.rand, pos, state);
                            ++grow;
                        } catch (Exception e) {
                            // Vanilla issue causes bamboo to crash if grown close to world height
                            if (!(growable instanceof BambooBlock)) {
                                throw e;
                            }
                        }
                    }
                }
            }
        }
        for (BlockPos iterPos : BlockPos.getAllInBoxMutable(pos.add(-f, -v, -f), pos.add(f, v, f))) {
            if (grow >= count) {
                return;
            }
            double distance = iterPos.distanceSq(entity.getPositionVec(), true);
            if (distance < f2) {
                state = worldIn.getBlockState(iterPos);
                if (state.getBlock() instanceof IGrowable) {
                    IGrowable growable = (IGrowable) state.getBlock();
                    if (growable.canGrow(worldIn, iterPos, state, worldIn.isRemote)) {
                        if (!worldIn.isRemote) {
                            if (growable.canUseBonemeal(worldIn, worldIn.rand, iterPos, state)) {
                                // TODO: Remove try/catch when Mojang fixes base issue.
                                try {
                                    growable.grow((ServerWorld) worldIn, worldIn.rand, iterPos, state);
                                    ++grow;
                                } catch (Exception e) {
                                    // Vanilla issue causes bamboo to crash if grown close to world height
                                    if (!(growable instanceof BambooBlock)) {
                                        throw e;
                                    }
                                }
                                // growable.grow((ServerWorld) worldIn, worldIn.rand, pos, state);
                                // ++grow;
                            }
                        }
                    }
                }
            }
        }
    }
    // endregion
}
