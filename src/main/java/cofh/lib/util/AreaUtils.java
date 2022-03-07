package cofh.lib.util;

import cofh.lib.util.helpers.MathHelper;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static cofh.lib.util.references.CoreReferences.*;
import static net.minecraft.world.level.block.Blocks.*;

public class AreaUtils {

    private AreaUtils() {

    }

    public static final int HORZ_MAX = 32;
    public static final int VERT_MAX = 16;

    // region BURNING
    public static void igniteNearbyEntities(Entity entity, Level worldIn, BlockPos pos, int radius, int duration) {

        AABB area = new AABB(pos.offset(-radius, -radius, -radius), pos.offset(1 + radius, 1 + radius, 1 + radius));
        List<LivingEntity> mobs = worldIn.getEntitiesOfClass(LivingEntity.class, area, EntitySelector.ENTITY_STILL_ALIVE);
        mobs.removeIf(Entity::isInWater);
        mobs.removeIf(Entity::fireImmune);
        mobs.removeIf(mob -> mob instanceof EnderMan);
        for (LivingEntity mob : mobs) {
            mob.setSecondsOnFire(duration);
        }
    }

    public static void igniteNearbyGround(Entity entity, Level worldIn, BlockPos pos, int radius, double chance) {

        float f = (float) Math.min(HORZ_MAX, radius);
        float v = (float) Math.min(VERT_MAX, radius);
        float f2 = f * f;
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

        for (BlockPos blockpos : BlockPos.betweenClosed(pos.offset(-f, -v, -f), pos.offset(f, v, f))) {
            double distance = blockpos.distToCenterSqr(entity.position());
            if (distance < f2) {
                mutable.set(blockpos.getX(), blockpos.getY() + 1, blockpos.getZ());
                BlockState blockstate1 = worldIn.getBlockState(mutable);
                if (blockstate1.isAir()) {
                    if (isValidFirePosition(worldIn, mutable, chance)) {
                        worldIn.setBlockAndUpdate(mutable, ((FireBlock) FIRE).getStateForPlacement(worldIn, mutable));
                    }
                }
            }
        }
    }

    public static void igniteSpecial(Entity entity, Level worldIn, BlockPos pos, int radius, boolean campfire, boolean tnt, @Nullable Entity igniter) {

        float f = (float) Math.min(HORZ_MAX, radius);
        float v = (float) Math.min(VERT_MAX, radius);
        float f2 = f * f;

        for (BlockPos blockpos : BlockPos.betweenClosed(pos.offset(-f, -v, -f), pos.offset(f, v, f))) {
            double distance = blockpos.distToCenterSqr(entity.position());
            if (distance < f2) {
                BlockState state = worldIn.getBlockState(blockpos);
                if (campfire && isUnlitCampfire(state)) {
                    worldIn.setBlockAndUpdate(blockpos, state.setValue(BlockStateProperties.LIT, true));
                } else if (tnt && isUnlitTNT(state)) {
                    state.getBlock().onCaughtFire(state, worldIn, blockpos, Direction.UP, igniter instanceof LivingEntity ? (LivingEntity) igniter : null);
                    worldIn.setBlockAndUpdate(blockpos, AIR.defaultBlockState());
                }
            }
        }
    }

    public static boolean isValidFirePosition(Level worldIn, BlockPos pos, double chance) {

        BlockPos below = pos.below();
        BlockState state = worldIn.getBlockState(below);
        if (Block.isFaceFull(state.getCollisionShape(worldIn, below), Direction.UP)) {
            return state.getMaterial().isFlammable() || worldIn.random.nextDouble() < chance; // Random chance.
        }
        return false;
    }

    public static boolean isLitCampfire(BlockState state) {

        return state.getBlock() instanceof CampfireBlock && state.getValue(BlockStateProperties.LIT);
    }

    public static boolean isUnlitCampfire(BlockState state) {

        return state.getBlock() instanceof CampfireBlock && !state.getValue(BlockStateProperties.WATERLOGGED) && !state.getValue(BlockStateProperties.LIT);
    }

    public static boolean isUnlitTNT(BlockState state) {

        return state.getBlock() instanceof TntBlock;
    }
    // endregion

    // region FREEZING
    public static void freezeNearbyGround(Entity entity, Level worldIn, BlockPos pos, int radius) {

        BlockState state = SNOW.defaultBlockState();
        float f = (float) Math.min(HORZ_MAX, radius);
        float v = (float) Math.min(VERT_MAX, radius);
        float f2 = f * f;
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

        for (BlockPos blockpos : BlockPos.betweenClosed(pos.offset(-f, -v, -f), pos.offset(f, v, f))) {
            double distance = blockpos.distToCenterSqr(entity.position());
            if (distance < f2) {
                mutable.set(blockpos.getX(), blockpos.getY() + 1, blockpos.getZ());
                BlockState blockstate1 = worldIn.getBlockState(mutable);
                if (blockstate1.isAir()) {
                    if (worldIn.getBiome(mutable).value().getTemperature(blockpos) < 0.8F && isValidSnowPosition(worldIn, mutable)) {
                        worldIn.setBlockAndUpdate(mutable, state);
                    }
                }
            }
        }
    }

    public static void freezeSpecial(Entity entity, Level worldIn, BlockPos pos, int radius, boolean campfire, boolean fire) {

        float f = (float) Math.min(HORZ_MAX, radius);
        float v = (float) Math.min(VERT_MAX, radius);
        float f2 = f * f;

        for (BlockPos blockpos : BlockPos.betweenClosed(pos.offset(-f, -v, -f), pos.offset(f, v, f))) {
            double distance = blockpos.distToCenterSqr(entity.position());
            if (distance < f2) {
                BlockState state = worldIn.getBlockState(blockpos);
                if (campfire && isLitCampfire(state)) {
                    worldIn.setBlockAndUpdate(blockpos, state.setValue(BlockStateProperties.LIT, false));
                } else if (fire && state.getBlock() == FIRE) {
                    worldIn.setBlockAndUpdate(blockpos, AIR.defaultBlockState());
                }
            }
        }
    }

    public static void freezeSurfaceWater(Entity entity, Level worldIn, BlockPos pos, int radius, boolean permanent) {

        BlockState state = permanent ? ICE.defaultBlockState() : FROSTED_ICE.defaultBlockState();
        float f = (float) Math.min(HORZ_MAX, radius);
        float v = (float) Math.min(VERT_MAX, radius);
        float f2 = f * f;
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

        for (BlockPos blockpos : BlockPos.betweenClosed(pos.offset(-f, -v, -f), pos.offset(f, v, f))) {
            double distance = blockpos.distToCenterSqr(entity.position());
            if (distance < f2) {
                mutable.set(blockpos.getX(), blockpos.getY() + 1, blockpos.getZ());
                BlockState blockstate1 = worldIn.getBlockState(mutable);
                if (blockstate1.isAir()) {
                    BlockState blockstate2 = worldIn.getBlockState(blockpos);
                    boolean isFull = blockstate2.getBlock() == WATER && blockstate2.getValue(LiquidBlock.LEVEL) == 0;
                    if (blockstate2.getMaterial() == Material.WATER && isFull && state.canSurvive(worldIn, blockpos) && worldIn.isUnobstructed(state, blockpos, CollisionContext.empty())) {
                        worldIn.setBlockAndUpdate(blockpos, state);
                        if (!permanent) {
                            worldIn.scheduleTick(blockpos, FROSTED_ICE, MathHelper.nextInt(worldIn.random, 60, 120));
                        }
                    }
                }
            }
        }
    }

    public static void freezeAllWater(Entity entity, Level worldIn, BlockPos pos, int radius, boolean permanent) {

        BlockState state = permanent ? ICE.defaultBlockState() : FROSTED_ICE.defaultBlockState();
        float f = (float) Math.min(HORZ_MAX, radius);
        float v = (float) Math.min(VERT_MAX, radius);
        float f2 = f * f;

        for (BlockPos blockpos : BlockPos.betweenClosed(pos.offset(-f, -v, -f), pos.offset(f, v, f))) {
            double distance = blockpos.distToCenterSqr(entity.position());
            if (distance < f2) {
                BlockState blockstate2 = worldIn.getBlockState(blockpos);
                boolean isFull = blockstate2.getBlock() == WATER && blockstate2.getValue(LiquidBlock.LEVEL) == 0;
                if (blockstate2.getMaterial() == Material.WATER && isFull && state.canSurvive(worldIn, blockpos) && worldIn.isUnobstructed(state, blockpos, CollisionContext.empty())) {
                    worldIn.setBlockAndUpdate(blockpos, state);
                    if (!permanent) {
                        worldIn.scheduleTick(blockpos, FROSTED_ICE, MathHelper.nextInt(worldIn.random, 60, 120));
                    }
                }
            }
        }
    }

    public static void freezeSurfaceLava(Entity entity, Level worldIn, BlockPos pos, int radius, boolean permanent) {

        if (GLOSSED_MAGMA == null && !permanent) {
            return;
        }
        BlockState state = permanent ? OBSIDIAN.defaultBlockState() : GLOSSED_MAGMA.defaultBlockState();
        float f = (float) Math.min(HORZ_MAX, radius);
        float v = (float) Math.min(VERT_MAX, radius);
        float f2 = f * f;
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

        for (BlockPos blockpos : BlockPos.betweenClosed(pos.offset(-f, -v, -f), pos.offset(f, v, f))) {
            double distance = blockpos.distToCenterSqr(entity.position());
            if (distance < f2) {
                mutable.set(blockpos.getX(), blockpos.getY() + 1, blockpos.getZ());
                BlockState blockstate1 = worldIn.getBlockState(mutable);
                if (blockstate1.isAir()) {
                    BlockState blockstate2 = worldIn.getBlockState(blockpos);
                    boolean isFull = blockstate2.getBlock() == LAVA && blockstate2.getValue(LiquidBlock.LEVEL) == 0;
                    if (blockstate2.getMaterial() == Material.LAVA && isFull && state.canSurvive(worldIn, blockpos) && worldIn.isUnobstructed(state, blockpos, CollisionContext.empty())) {
                        worldIn.setBlockAndUpdate(blockpos, state);
                        if (!permanent) {
                            worldIn.scheduleTick(blockpos, GLOSSED_MAGMA, MathHelper.nextInt(worldIn.random, 60, 120));
                        }
                    }
                }
            }
        }
    }

    public static void freezeAllLava(Entity entity, Level worldIn, BlockPos pos, int radius, boolean permanent) {

        if (GLOSSED_MAGMA == null && !permanent) {
            return;
        }
        BlockState state = permanent ? OBSIDIAN.defaultBlockState() : GLOSSED_MAGMA.defaultBlockState();
        float f = (float) Math.min(HORZ_MAX, radius);
        float v = (float) Math.min(VERT_MAX, radius);
        float f2 = f * f;

        for (BlockPos blockpos : BlockPos.betweenClosed(pos.offset(-f, -v, -f), pos.offset(f, v, f))) {
            double distance = blockpos.distToCenterSqr(entity.position());
            if (distance < f2) {
                BlockState blockstate2 = worldIn.getBlockState(blockpos);
                boolean isFull = blockstate2.getBlock() == LAVA && blockstate2.getValue(LiquidBlock.LEVEL) == 0;
                if (blockstate2.getMaterial() == Material.LAVA && isFull && state.canSurvive(worldIn, blockpos) && worldIn.isUnobstructed(state, blockpos, CollisionContext.empty())) {
                    worldIn.setBlockAndUpdate(blockpos, state);
                    if (!permanent) {
                        worldIn.scheduleTick(blockpos, GLOSSED_MAGMA, MathHelper.nextInt(worldIn.random, 60, 120));
                    }
                }
            }
        }
    }

    public static boolean isValidSnowPosition(Level worldIn, BlockPos pos) {

        BlockState state = worldIn.getBlockState(pos.below());
        Block block = state.getBlock();
        if (block == ICE || block == PACKED_ICE || block == BARRIER || block == FROSTED_ICE || block == GLOSSED_MAGMA) {
            return false;
        }
        return Block.isFaceFull(state.getCollisionShape(worldIn, pos.below()), Direction.UP) || block == SNOW && state.getValue(SnowLayerBlock.LAYERS) == 8;
    }
    // endregion

    // region AREA TRANSFORMS / MISC
    private static boolean isValidLightningBoltPosition(Level worldIn, BlockPos pos, double chance) {

        BlockPos below = pos.below();
        BlockState state = worldIn.getBlockState(below);
        if (worldIn.canSeeSky(pos) && Block.isFaceFull(state.getCollisionShape(worldIn, below), Direction.UP)) {
            return worldIn.random.nextDouble() < chance; // Random chance.
        }
        return false;
    }

    public static void transformArea(Entity entity, Level worldIn, BlockPos pos, BlockState replaceable, BlockState replacement, int radius, boolean requireAir) {

        float f = (float) Math.min(HORZ_MAX, radius);
        float v = (float) Math.min(VERT_MAX, radius);
        float f2 = f * f;
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

        if (requireAir) {
            for (BlockPos iterPos : BlockPos.betweenClosed(pos.offset(-f, -v, -f), pos.offset(f, v, f))) {
                double distance = iterPos.distToCenterSqr(entity.position());
                if (distance < f2) {
                    mutable.set(iterPos.getX(), iterPos.getY() + 1, iterPos.getZ());
                    BlockState blockstate1 = worldIn.getBlockState(mutable);
                    if (blockstate1.isAir()) {
                        if (worldIn.getBlockState(iterPos) == replaceable) {
                            worldIn.setBlockAndUpdate(iterPos, replacement);
                        }
                    }
                }
            }
        } else {
            for (BlockPos iterPos : BlockPos.betweenClosed(pos.offset(-f, -v, -f), pos.offset(f, v, f))) {
                double distance = iterPos.distToCenterSqr(entity.position());
                if (distance < f2) {
                    if (worldIn.getBlockState(iterPos) == replaceable) {
                        worldIn.setBlockAndUpdate(iterPos, replacement);
                    }
                }
            }
        }
    }

    public static void transformArea(Entity entity, Level worldIn, BlockPos pos, Set<BlockState> replaceable, BlockState replacement, int radius, boolean requireAir) {

        float f = (float) Math.min(HORZ_MAX, radius);
        float v = (float) Math.min(VERT_MAX, radius);
        float f2 = f * f;
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

        if (requireAir) {
            for (BlockPos blockpos : BlockPos.betweenClosed(pos.offset(-f, -v, -f), pos.offset(f, v, f))) {
                double distance = blockpos.distToCenterSqr(entity.position());
                if (distance < f2) {
                    mutable.set(blockpos.getX(), blockpos.getY() + 1, blockpos.getZ());
                    BlockState blockstate1 = worldIn.getBlockState(mutable);
                    if (blockstate1.isAir()) {
                        if (replaceable.contains(worldIn.getBlockState(blockpos))) {
                            worldIn.setBlockAndUpdate(blockpos, replacement);
                        }
                    }
                }
            }
        } else {
            for (BlockPos iterPos : BlockPos.betweenClosed(pos.offset(-f, -v, -f), pos.offset(f, v, f))) {
                if (iterPos.closerToCenterThan(entity.position(), f)) {
                    if (replaceable.contains(worldIn.getBlockState(iterPos))) {
                        worldIn.setBlockAndUpdate(iterPos, replacement);
                    }
                }
            }
        }
    }

    public static void transformGrass(Entity entity, Level worldIn, BlockPos pos, int radius) {

        transformArea(entity, worldIn, pos, DIRT.defaultBlockState(), GRASS_BLOCK.defaultBlockState(), radius, true);
    }

    public static void transformMycelium(Entity entity, Level worldIn, BlockPos pos, int radius) {

        Set<BlockState> replaceable = new ObjectOpenHashSet<>();
        Collections.addAll(replaceable, DIRT.defaultBlockState(), GRASS_BLOCK.defaultBlockState());
        transformArea(entity, worldIn, pos, replaceable, MYCELIUM.defaultBlockState(), radius, true);
    }

    public static void transformSignalAir(Entity entity, Level worldIn, BlockPos pos, int radius) {

        Set<BlockState> replaceable = new ObjectOpenHashSet<>();
        Collections.addAll(replaceable, AIR.defaultBlockState(), CAVE_AIR.defaultBlockState());
        transformArea(entity, worldIn, pos, replaceable, SIGNAL_AIR.defaultBlockState(), radius, false);
    }

    public static void transformGlowAir(Entity entity, Level worldIn, BlockPos pos, int radius) {

        Set<BlockState> replaceable = new ObjectOpenHashSet<>();
        Collections.addAll(replaceable, AIR.defaultBlockState(), CAVE_AIR.defaultBlockState());
        transformArea(entity, worldIn, pos, replaceable, GLOW_AIR.defaultBlockState(), radius, false);
    }

    public static void transformEnderAir(Entity entity, Level worldIn, BlockPos pos, int radius) {

        Set<BlockState> replaceable = new ObjectOpenHashSet<>();
        Collections.addAll(replaceable, AIR.defaultBlockState(), CAVE_AIR.defaultBlockState());
        transformArea(entity, worldIn, pos, replaceable, ENDER_AIR.defaultBlockState(), radius, false);
    }

    public static void zapNearbyGround(Entity entity, Level worldIn, BlockPos pos, int radius, double chance, int max) {

        float f = (float) Math.min(HORZ_MAX, radius);
        float v = (float) Math.min(VERT_MAX, radius);
        float f2 = f * f;
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        int count = 0;

        for (BlockPos blockpos : BlockPos.betweenClosed(pos.offset(-f, -v, -f), pos.offset(f, v, f))) {
            if (count >= max) {
                return;
            }
            double distance = blockpos.distToCenterSqr(entity.position());
            if (distance < f2) {
                mutable.set(blockpos.getX(), blockpos.getY() + 1, blockpos.getZ());
                BlockState blockstate1 = worldIn.getBlockState(mutable);
                if (blockstate1.isAir()) {
                    if (isValidLightningBoltPosition(worldIn, mutable, chance)) {
                        worldIn.setBlockAndUpdate(mutable, LIGHTNING_AIR.defaultBlockState());
                        ++count;
                    }
                }
            }
        }
    }

    public static void growMushrooms(Entity entity, Level worldIn, BlockPos pos, int radius, int count) {

        float f = (float) Math.min(HORZ_MAX, radius);
        float v = (float) Math.min(VERT_MAX, radius);
        float f2 = f * f;
        int grow = 0;
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

        mutable.set(entity.blockPosition().above());
        BlockState blockstate1 = worldIn.getBlockState(mutable);
        if (blockstate1.isAir()) {
            if (isValidMushroomPosition(worldIn, entity.blockPosition(), 1.0)) {
                worldIn.setBlockAndUpdate(mutable, worldIn.random.nextBoolean() ? BROWN_MUSHROOM.defaultBlockState() : RED_MUSHROOM.defaultBlockState());
                ++grow;
            }
        }
        for (BlockPos iterPos : BlockPos.betweenClosed(pos.offset(-f, -v, -f), pos.offset(f, v, f))) {
            if (grow >= count) {
                return;
            }
            double distance = iterPos.distToCenterSqr(entity.position());
            if (distance < f2) {
                mutable.set(iterPos.getX(), iterPos.getY() + 1, iterPos.getZ());
                blockstate1 = worldIn.getBlockState(mutable);
                if (blockstate1.isAir()) {
                    if (isValidMushroomPosition(worldIn, iterPos, 0.5 - (distance / f2))) {
                        worldIn.setBlockAndUpdate(mutable, worldIn.random.nextBoolean() ? BROWN_MUSHROOM.defaultBlockState() : RED_MUSHROOM.defaultBlockState());
                        ++grow;
                    }
                }
            }
        }
    }

    private static boolean isValidMushroomPosition(Level worldIn, BlockPos pos, double chance) {

        Block block = worldIn.getBlockState(pos).getBlock();
        return worldIn.random.nextDouble() < chance && (block == MYCELIUM || block == PODZOL);
    }

    public static void growPlants(Entity entity, Level worldIn, BlockPos pos, int radius) {

        float f = (float) Math.min(HORZ_MAX, radius);
        float v = (float) Math.min(VERT_MAX, radius);
        float f2 = f * f;

        BlockState state;
        for (BlockPos iterPos : BlockPos.betweenClosed(pos.offset(-f, -v, -f), pos.offset(f, v, f))) {
            double distance = iterPos.distToCenterSqr(entity.position());
            if (distance < f2) {
                state = worldIn.getBlockState(iterPos);
                if (state.getBlock() instanceof BonemealableBlock growable) {
                    if (growable.isValidBonemealTarget(worldIn, iterPos, state, worldIn.isClientSide)) {
                        if (!worldIn.isClientSide) {
                            if (growable.isBonemealSuccess(worldIn, worldIn.random, iterPos, state)) {
                                // TODO: Remove try/catch when Mojang fixes base issue.
                                try {
                                    growable.performBonemeal((ServerLevel) worldIn, worldIn.random, iterPos, state);
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

    public static void growPlants(Entity entity, Level worldIn, BlockPos pos, int radius, int count) {

        float f = (float) Math.min(HORZ_MAX, radius);
        float v = (float) Math.min(VERT_MAX, radius);
        float f2 = f * f;
        int grow = 0;

        BlockState state = worldIn.getBlockState(entity.blockPosition());
        if (state.getBlock() instanceof BonemealableBlock growable) {
            if (growable.isValidBonemealTarget(worldIn, pos, state, worldIn.isClientSide)) {
                if (!worldIn.isClientSide) {
                    if (growable.isBonemealSuccess(worldIn, worldIn.random, pos, state)) {
                        // TODO: Remove try/catch when Mojang fixes base issue.
                        try {
                            growable.performBonemeal((ServerLevel) worldIn, worldIn.random, pos, state);
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
        for (BlockPos iterPos : BlockPos.betweenClosed(pos.offset(-f, -v, -f), pos.offset(f, v, f))) {
            if (grow >= count) {
                return;
            }
            double distance = iterPos.distToCenterSqr(entity.position());
            if (distance < f2) {
                state = worldIn.getBlockState(iterPos);
                if (state.getBlock() instanceof BonemealableBlock growable) {
                    if (growable.isValidBonemealTarget(worldIn, iterPos, state, worldIn.isClientSide)) {
                        if (!worldIn.isClientSide) {
                            if (growable.isBonemealSuccess(worldIn, worldIn.random, iterPos, state)) {
                                // TODO: Remove try/catch when Mojang fixes base issue.
                                try {
                                    growable.performBonemeal((ServerLevel) worldIn, worldIn.random, iterPos, state);
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
