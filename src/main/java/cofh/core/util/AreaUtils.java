package cofh.core.util;

import cofh.lib.util.Utils;
import cofh.lib.util.helpers.MathHelper;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Endermite;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import static cofh.core.init.CoreBlocks.*;
import static cofh.core.init.CoreMobEffects.*;
import static net.minecraft.world.level.block.Blocks.*;

public class AreaUtils {

    private AreaUtils() {

    }

    public static final int HORZ_MAX = 32;
    public static final int VERT_MAX = 16;

    public static final Set<BlockState> REPLACEABLE_AIR = new ObjectOpenHashSet<>(new BlockState[]{AIR.defaultBlockState(), CAVE_AIR.defaultBlockState()});

    public static final IEffectApplier IGNITE_ENTITIES = (target, duration, power, source) -> {

        if (!target.fireImmune() && !target.isInWater() && target.getRemainingFireTicks() <= 0) {
            target.setSecondsOnFire(duration / 20);
        }
        if (target instanceof LivingEntity) {
            LivingEntity living = (LivingEntity) target;
            living.removeEffect(CHILLED.get());
        }
    };

    public static final IEffectApplier CHILL_ENTITIES = (target, duration, power, source) -> {

        if (target.getRemainingFireTicks() > 0) {
            target.setRemainingFireTicks(0);
        }
        if (target instanceof LivingEntity) {
            LivingEntity living = (LivingEntity) target;
            living.addEffect(new MobEffectInstance(CHILLED.get(), duration, power));
        }
    };

    public static final IEffectApplier SUNDER_ENTITIES = (target, duration, power, source) -> {

        if (target instanceof LivingEntity) {
            LivingEntity living = (LivingEntity) target;
            living.addEffect(new MobEffectInstance(SUNDERED.get(), duration, power));
        }
    };

    public static final IEffectApplier SHOCK_ENTITIES = (target, duration, power, source) -> {

        if (target instanceof LivingEntity) {
            LivingEntity living = (LivingEntity) target;
            if (!living.hasEffect(LIGHTNING_RESISTANCE.get())) {
                living.addEffect(new MobEffectInstance(SHOCKED.get(), duration, power));
            }
        }
    };

    public static final IBlockTransformer FIRE_TRANSFORM = (world, pos, face, entity) -> {

        boolean succeeded = false;
        BlockState state = world.getBlockState(pos);

        if (state.isAir()) {
            if (BaseFireBlock.canBePlacedAt(world, pos, face)) {
                succeeded |= world.setBlock(pos, BaseFireBlock.getState(world, pos), 11);
            }
        }
        return succeeded;
    };

    public static final IBlockTransformer FIRE_TRANSFORM_SPECIAL = (world, pos, face, entity) -> {

        boolean succeeded = false;
        BlockState state = world.getBlockState(pos);
        if (isUnlitCampfire(state)) {
            succeeded = world.setBlockAndUpdate(pos, state.setValue(BlockStateProperties.LIT, true));
        } else if (isUnlitTNT(state)) {
            state.getBlock().onCaughtFire(state, world, pos, Direction.UP, entity instanceof LivingEntity ? (LivingEntity) entity : null);
            succeeded = world.setBlockAndUpdate(pos, AIR.defaultBlockState());
        } else if (state.getBlock() == ICE || state.getBlock() == FROSTED_ICE) {
            succeeded = world.setBlockAndUpdate(pos, WATER.defaultBlockState());
        } else if (state.getBlock() == SNOW) {
            succeeded = world.setBlockAndUpdate(pos, AIR.defaultBlockState());
        }
        return succeeded;
    };


    public static final IBlockTransformer ICE_TRANSFORM_TEMPORARY = (world, pos, face, entity) -> {

        boolean succeeded = false;
        BlockState state = world.getBlockState(pos);

        // CAMPFIRE/FIRE
        if (AreaUtils.isLitCampfire(state)) {
            succeeded |= world.setBlockAndUpdate(pos, state.setValue(BlockStateProperties.LIT, false));
        }
        // SNOW
        if (world.isEmptyBlock(pos) && AreaUtils.isValidSnowPosition(world, pos)) {
            succeeded |= world.setBlockAndUpdate(pos, SNOW.defaultBlockState());
        }
        // FIRE
        if (state.getBlock() == FIRE) {
            succeeded |= world.setBlockAndUpdate(pos, AIR.defaultBlockState());
        }
        // WATER
        boolean isFull = state.getBlock() == WATER && state.getValue(LiquidBlock.LEVEL) == 0;
        if (state.getMaterial() == Material.WATER && isFull && state.canSurvive(world, pos) && world.isUnobstructed(state, pos, CollisionContext.empty())) {
            succeeded |= world.setBlockAndUpdate(pos, FROSTED_ICE.defaultBlockState());
            world.scheduleTick(pos, FROSTED_ICE, MathHelper.nextInt(world.random, 60, 120));
        }
        // LAVA
        isFull = state.getBlock() == LAVA && state.getValue(LiquidBlock.LEVEL) == 0;
        if (state.getMaterial() == Material.LAVA && isFull && state.canSurvive(world, pos) && world.isUnobstructed(state, pos, CollisionContext.empty())) {
            succeeded |= world.setBlockAndUpdate(pos, GLOSSED_MAGMA.get().defaultBlockState());
            world.scheduleTick(pos, GLOSSED_MAGMA.get(), MathHelper.nextInt(world.random, 60, 120));
        }
        return succeeded;
    };

    public static final IBlockTransformer ICE_TRANSFORM = (world, pos, face, entity) -> {

        boolean succeeded = false;
        BlockState state = world.getBlockState(pos);

        // CAMPFIRE/FIRE
        if (AreaUtils.isLitCampfire(state)) {
            succeeded |= world.setBlockAndUpdate(pos, state.setValue(BlockStateProperties.LIT, false));
        }
        // SNOW
        if (world.isEmptyBlock(pos) && AreaUtils.isValidSnowPosition(world, pos)) {
            succeeded |= world.setBlockAndUpdate(pos, SNOW.defaultBlockState());
        }
        // FIRE
        if (state.getBlock() == FIRE) {
            succeeded |= world.setBlockAndUpdate(pos, AIR.defaultBlockState());
        }
        // WATER
        boolean isFull = state.getBlock() == WATER && state.getValue(LiquidBlock.LEVEL) == 0;
        if (state.getMaterial() == Material.WATER && isFull && state.canSurvive(world, pos) && world.isUnobstructed(state, pos, CollisionContext.empty())) {
            succeeded |= world.setBlockAndUpdate(pos, ICE.defaultBlockState());
        }
        // LAVA
        isFull = state.getBlock() == LAVA && state.getValue(LiquidBlock.LEVEL) == 0;
        if (state.getMaterial() == Material.LAVA && isFull && state.canSurvive(world, pos) && world.isUnobstructed(state, pos, CollisionContext.empty())) {
            succeeded |= world.setBlockAndUpdate(pos, OBSIDIAN.defaultBlockState());
        }
        return succeeded;
    };

    public static final IBlockTransformer ICE_TRANSFORM_SURFACE = (world, pos, face, entity) -> {

        boolean succeeded = false;
        BlockState state = world.getBlockState(pos);
        // TODO separate config values pain
        boolean permanentWater = true;
        boolean permanentLava = true;

        BlockState frozenWater = permanentWater ? ICE.defaultBlockState() : FROSTED_ICE.defaultBlockState();
        BlockState frozenLava = permanentLava ? OBSIDIAN.defaultBlockState() : GLOSSED_MAGMA.get().defaultBlockState();

        // SNOW
        if (world.isEmptyBlock(pos) && AreaUtils.isValidSnowPosition(world, pos)) {
            succeeded |= world.setBlockAndUpdate(pos, SNOW.defaultBlockState());
        }
        BlockPos above = pos.relative(Direction.UP);
        if (world.getBlockState(above).isAir()) {
            boolean isFull = state.getBlock() == WATER && state.getValue(LiquidBlock.LEVEL) == 0;
            if (state.getMaterial() == Material.WATER && isFull && state.canSurvive(world, pos) && world.isUnobstructed(state, pos, CollisionContext.empty())) {
                succeeded |= world.setBlockAndUpdate(pos, frozenWater);
                if (!permanentWater) {
                    world.scheduleTick(pos, FROSTED_ICE, MathHelper.nextInt(world.random, 60, 120));
                }
            }
            // LAVA
            isFull = state.getBlock() == LAVA && state.getValue(LiquidBlock.LEVEL) == 0;
            if (state.getMaterial() == Material.LAVA && isFull && state.canSurvive(world, pos) && world.isUnobstructed(state, pos, CollisionContext.empty())) {
                succeeded |= world.setBlockAndUpdate(pos, frozenLava);
                if (!permanentLava) {
                    world.scheduleTick(pos, GLOSSED_MAGMA.get(), MathHelper.nextInt(world.random, 60, 120));
                }
            }
        }
        return succeeded;
    };

    public static final IBlockTransformer EARTH_TRANSFORM = (world, pos, face, entity) -> {
        boolean succeeded = false;
        BlockState state = world.getBlockState(pos);
        Material material = state.getMaterial();
        if (material == Material.STONE || material == Material.DIRT || state.getBlock() instanceof SnowyDirtBlock) {
            succeeded |= Utils.destroyBlock(world, pos, true, entity);
        }
        return succeeded;
    };

    public static final IBlockTransformer LIGHTNING_TRANSFORM = (world, pos, face, entity) -> {
        boolean succeeded = false;
        BlockState state = world.getBlockState(pos);
        if (state.isAir()) {
            if (isValidLightningBoltPosition(world, pos, 1.0F)) {
                succeeded |= world.setBlockAndUpdate(pos, LIGHTNING_AIR.get().defaultBlockState());
            }
        }
        return succeeded;
    };
    // endregion ELEMENTAL

    // region CONVERSION
    public static final IBlockTransformer SIGNAL_AIR_TRANSFORM = getConversionTransform(REPLACEABLE_AIR, SIGNAL_AIR.get().defaultBlockState(), false);
    public static final IBlockTransformer GLOW_AIR_TRANSFORM = getConversionTransform(REPLACEABLE_AIR, GLOW_AIR.get().defaultBlockState(), false);
    public static final IEffectApplier GLOW_ENTITIES = (target, duration, power, source) -> {

        if (target instanceof LivingEntity) {
            LivingEntity living = (LivingEntity) target;
            living.addEffect(new MobEffectInstance(MobEffects.GLOWING, duration, power));
            if (living.getMobType() == MobType.UNDEAD) {
                living.hurt(DamageSource.explosion(source instanceof LivingEntity ? (LivingEntity) source : null), 4.0F);
                living.setSecondsOnFire(duration / 20);
            }
        }
    };
    public static final IBlockTransformer ENDER_AIR_TRANSFORM = getConversionTransform(REPLACEABLE_AIR, ENDER_AIR.get().defaultBlockState(), false);
    public static final IEffectApplier ENDERFERE_ENTITIES = (target, duration, power, source) -> {

        if (target instanceof EnderMan || target instanceof Endermite) {
            LivingEntity living = (LivingEntity) target;
            living.addEffect(new MobEffectInstance(ENDERFERENCE.get(), duration, power));
            living.hurt(DamageSource.explosion(source instanceof LivingEntity ? (LivingEntity) source : null), 4.0F);
        }
    };

    public static final IBlockTransformer MYCELIUM_TRANSFORM = getConversionTransform(new ObjectOpenHashSet<>(new BlockState[]{DIRT.defaultBlockState(), GRASS_BLOCK.defaultBlockState()}), MYCELIUM.defaultBlockState(), true);
    public static final IBlockTransformer GRASS_TRANSFORM = getConversionTransform(DIRT.defaultBlockState(), GRASS_BLOCK.defaultBlockState(), true);
    // endregion CONVERSION

    // region GROWTH
    public static final IBlockTransformer GROW_MUSHROOMS = new IBlockTransformer() {

        @Override
        public boolean transformBlock(Level level, BlockPos pos, Direction face, @Nullable Entity entity) {

            Block below = level.getBlockState(pos.relative(Direction.DOWN)).getBlock();
            if (level.getBlockState(pos).isAir() && (below.equals(MYCELIUM) || below.equals(PODZOL))) {
                return level.setBlockAndUpdate(pos, level.random.nextBoolean() ? BROWN_MUSHROOM.defaultBlockState() : RED_MUSHROOM.defaultBlockState());
            }
            return false;
        }

        @Override
        public void transformSphere(Level levelIn, Vec3 pos, float radius, float chance, int max, @Nullable Entity entity) {

            float f = Math.min(HORZ_MAX, radius);
            float v = Math.min(VERT_MAX, radius);
            float f2 = f * f;
            BlockPos origin = new BlockPos(pos);

            if (transformBlock(levelIn, origin.relative(Direction.UP), Direction.DOWN, entity)) {
                --max;
            }
            for (BlockPos iterPos : BlockPos.betweenClosed(origin.offset(-f, -v, -f), origin.offset(f + 1, v + 1, f + 1))) {
                if (max <= 0) {
                    return;
                }
                double distance = iterPos.distSqr(origin);
                if (distance < f2) {
                    if (levelIn.random.nextDouble() < 0.5 - (distance / f2) && transformBlock(levelIn, iterPos, Direction.DOWN, entity)) {
                        --max;
                    }
                }
            }
        }
    };

    public static final IBlockTransformer GROW_PLANTS = (world, pos, face, entity) -> {

        BlockState state = world.getBlockState(pos);
        if (state.getBlock() instanceof BonemealableBlock) {
            BonemealableBlock growable = (BonemealableBlock) state.getBlock();
            if (!world.isClientSide && growable.isValidBonemealTarget(world, pos, state, world.isClientSide) && growable.isBonemealSuccess(world, world.random, pos, state)) {
                // TODO: Remove try/catch when Mojang fixes base issue.
                try {
                    growable.performBonemeal((ServerLevel) world, world.random, pos, state);
                } catch (Exception e) {
                    // Vanilla issue causes bamboo to crash if grown close to world height
                    if (!(growable instanceof BambooBlock)) {
                        throw e;
                    }
                }
                return true;
            }
        }
        return false;
    };
    // endregion GROWTH

    // region HELPER
    private static IBlockTransformer getConversionTransform(Set<BlockState> replaceable, BlockState replacement, boolean requireAir) {

        if (requireAir) {
            return (levelIn, pos, face, entity) -> {
                BlockPos above = pos.relative(Direction.UP);
                if (levelIn.getBlockState(above).isAir() && replaceable.contains(levelIn.getBlockState(pos))) {
                    return levelIn.setBlockAndUpdate(pos, replacement);
                }
                return false;
            };
        } else {
            return (levelIn, pos, face, entity) -> {
                if (replaceable.contains(levelIn.getBlockState(pos))) {
                    return levelIn.setBlockAndUpdate(pos, replacement);
                }
                return false;
            };
        }
    }

    private static IBlockTransformer getConversionTransform(BlockState replaceable, BlockState replacement, boolean requireAir) {

        if (requireAir) {
            return (levelIn, pos, face, entity) -> {
                BlockPos above = pos.relative(Direction.UP);
                if (levelIn.getBlockState(above).isAir() && replaceable.equals(levelIn.getBlockState(pos))) {
                    return levelIn.setBlockAndUpdate(pos, replacement);
                }
                return false;
            };
        } else {
            return (levelIn, pos, face, entity) -> {
                if (replaceable.equals(levelIn.getBlockState(pos))) {
                    return levelIn.setBlockAndUpdate(pos, replacement);
                }
                return false;
            };
        }
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
    // endregion HELPERS

    // region INTERFACES
    public interface IEffectApplier {

        void applyEffect(Entity target, int duration, int power, @Nullable Entity source);

        default void applyEffectNearby(Level levelIn, Vec3 pos, Predicate<? super Entity> filter, float radius, int duration, int amplifier, @Nullable Entity source) {

            levelIn.getEntities(source, (new AABB(pos.subtract(radius, radius, radius), pos.add(radius, radius, radius))).inflate(1), filter)
                    .forEach(livingEntity -> this.applyEffect(livingEntity, duration, amplifier, source));
        }

        default void applyEffectNearby(Level levelIn, Vec3 pos, float radius, int duration, int amplifier, @Nullable Entity source) {

            applyEffectNearby(levelIn, pos, EntitySelector.ENTITY_STILL_ALIVE, radius, duration, amplifier, source);
        }

        default void applyEffectNearby(Level levelIn, Vec3 pos, float radius, int duration, int amplifier) {

            applyEffectNearby(levelIn, pos, radius, duration, amplifier, null);
        }

    }

    public interface IBlockTransformer {

        boolean transformBlock(Level level, BlockPos pos, Direction face, @Nullable Entity entity);

        default void transformSphere(Level level, Vec3 pos, float radius, @Nullable Entity entity) {

            float f = Math.min(HORZ_MAX, radius);
            float v = Math.min(VERT_MAX, radius);
            float f2 = f * f;
            BlockPos origin = new BlockPos(pos);

            for (BlockPos iterPos : BlockPos.betweenClosed(origin.offset(-f, -v, -f), origin.offset(f + 1, v + 1, f + 1))) {
                if (iterPos.distSqr(origin) < f2) {
                    transformBlock(level, iterPos, Direction.DOWN, entity);
                }
            }
        }

        default void transformSphere(Level levelIn, Vec3 pos, float radius, float chance, @Nullable Entity entity) {

            float f = Math.min(HORZ_MAX, radius);
            float v = Math.min(VERT_MAX, radius);
            float f2 = f * f;
            BlockPos origin = new BlockPos(pos);

            for (BlockPos iterPos : BlockPos.betweenClosed(origin.offset(-f, -v, -f), origin.offset(f, v, f))) {
                double distSqr = iterPos.distSqr(origin);
                if (distSqr < f2 && (chance > 0.99999F || levelIn.random.nextDouble() < chance)) {
                    transformBlock(levelIn, iterPos, Direction.DOWN, entity);
                }
            }
        }

        default void transformSphere(Level levelIn, Vec3 pos, float radius, float chance, int max, @Nullable Entity entity) {

            float f = Math.min(HORZ_MAX, radius);
            float v = Math.min(VERT_MAX, radius);
            float f2 = f * f;
            BlockPos origin = new BlockPos(pos);

            if (transformBlock(levelIn, origin, Direction.DOWN, entity)) {
                --max;
            }
            for (BlockPos iterPos : BlockPos.betweenClosed(origin.offset(-f, -v, -f), origin.offset(f, v, f))) {
                if (max <= 0) {
                    return;
                }
                double distSqr = iterPos.distSqr(origin);
                if (distSqr < f2 && (chance > 0.99999F || levelIn.random.nextDouble() < chance) && transformBlock(levelIn, iterPos, Direction.DOWN, entity)) {
                    --max;
                }
            }
        }

    }
    // endregion INTERFACES

    // TODO: Refactor Archer's Paradox to not use these.
    // region BURNING
    public static void igniteNearbyEntities(Entity entity, Level levelIn, BlockPos pos, int radius, int duration) {

        AABB area = new AABB(pos.offset(-radius, -radius, -radius), pos.offset(1 + radius, 1 + radius, 1 + radius));
        List<LivingEntity> mobs = levelIn.getEntitiesOfClass(LivingEntity.class, area, EntitySelector.ENTITY_STILL_ALIVE);
        mobs.removeIf(Entity::isInWater);
        mobs.removeIf(Entity::fireImmune);
        mobs.removeIf(mob -> mob instanceof EnderMan);
        for (LivingEntity mob : mobs) {
            mob.setSecondsOnFire(duration);
        }
    }

    public static void igniteNearbyGround(Entity entity, Level levelIn, BlockPos pos, int radius, double chance) {

        float f = (float) Math.min(HORZ_MAX, radius);
        float v = (float) Math.min(VERT_MAX, radius);
        float f2 = f * f;
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

        for (BlockPos blockpos : BlockPos.betweenClosed(pos.offset(-f, -v, -f), pos.offset(f, v, f))) {
            double distance = blockpos.distToCenterSqr(entity.position());
            if (distance < f2) {
                mutable.set(blockpos.getX(), blockpos.getY() + 1, blockpos.getZ());
                BlockState blockstate1 = levelIn.getBlockState(mutable);
                if (blockstate1.isAir()) {
                    if (isValidFirePosition(levelIn, mutable, chance)) {
                        levelIn.setBlockAndUpdate(mutable, ((FireBlock) FIRE).getStateForPlacement(levelIn, mutable));
                    }
                }
            }
        }
    }

    public static void igniteSpecial(Entity entity, Level levelIn, BlockPos pos, int radius, boolean campfire, boolean tnt, @Nullable Entity igniter) {

        float f = (float) Math.min(HORZ_MAX, radius);
        float v = (float) Math.min(VERT_MAX, radius);
        float f2 = f * f;

        for (BlockPos blockpos : BlockPos.betweenClosed(pos.offset(-f, -v, -f), pos.offset(f, v, f))) {
            double distance = blockpos.distToCenterSqr(entity.position());
            if (distance < f2) {
                BlockState state = levelIn.getBlockState(blockpos);
                if (campfire && isUnlitCampfire(state)) {
                    levelIn.setBlockAndUpdate(blockpos, state.setValue(BlockStateProperties.LIT, true));
                } else if (tnt && isUnlitTNT(state)) {
                    state.getBlock().onCaughtFire(state, levelIn, blockpos, Direction.UP, igniter instanceof LivingEntity ? (LivingEntity) igniter : null);
                    levelIn.setBlockAndUpdate(blockpos, AIR.defaultBlockState());
                }
            }
        }
    }

    public static boolean isValidFirePosition(Level levelIn, BlockPos pos, double chance) {

        BlockPos below = pos.below();
        BlockState state = levelIn.getBlockState(below);
        if (Block.isFaceFull(state.getCollisionShape(levelIn, below), Direction.UP)) {
            return state.getMaterial().isFlammable() || levelIn.random.nextDouble() < chance; // Random chance.
        }
        return false;
    }
    // endregion

    // region FREEZING
    public static void freezeNearbyGround(Entity entity, Level levelIn, BlockPos pos, int radius) {

        BlockState state = SNOW.defaultBlockState();
        float f = (float) Math.min(HORZ_MAX, radius);
        float v = (float) Math.min(VERT_MAX, radius);
        float f2 = f * f;
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

        for (BlockPos blockpos : BlockPos.betweenClosed(pos.offset(-f, -v, -f), pos.offset(f, v, f))) {
            double distance = blockpos.distToCenterSqr(entity.position());
            if (distance < f2) {
                mutable.set(blockpos.getX(), blockpos.getY() + 1, blockpos.getZ());
                BlockState blockstate1 = levelIn.getBlockState(mutable);
                if (blockstate1.isAir()) {
                    if (levelIn.getBiome(mutable).value().getTemperature(blockpos) < 0.8F && isValidSnowPosition(levelIn, mutable)) {
                        levelIn.setBlockAndUpdate(mutable, state);
                    }
                }
            }
        }
    }

    public static void freezeSpecial(Entity entity, Level levelIn, BlockPos pos, int radius, boolean campfire, boolean fire) {

        float f = (float) Math.min(HORZ_MAX, radius);
        float v = (float) Math.min(VERT_MAX, radius);
        float f2 = f * f;

        for (BlockPos blockpos : BlockPos.betweenClosed(pos.offset(-f, -v, -f), pos.offset(f, v, f))) {
            double distance = blockpos.distToCenterSqr(entity.position());
            if (distance < f2) {
                BlockState state = levelIn.getBlockState(blockpos);
                if (campfire && isLitCampfire(state)) {
                    levelIn.setBlockAndUpdate(blockpos, state.setValue(BlockStateProperties.LIT, false));
                } else if (fire && state.getBlock() == FIRE) {
                    levelIn.setBlockAndUpdate(blockpos, AIR.defaultBlockState());
                }
            }
        }
    }

    public static void freezeSurfaceWater(Entity entity, Level level, BlockPos pos, int radius, boolean permanent) {

        BlockState state = permanent ? ICE.defaultBlockState() : FROSTED_ICE.defaultBlockState();
        float f = (float) Math.min(HORZ_MAX, radius);
        float v = (float) Math.min(VERT_MAX, radius);
        float f2 = f * f;
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

        for (BlockPos blockpos : BlockPos.betweenClosed(pos.offset(-f, -v, -f), pos.offset(f, v, f))) {
            double distance = blockpos.distToCenterSqr(entity.position());
            if (distance < f2) {
                mutable.set(blockpos.getX(), blockpos.getY() + 1, blockpos.getZ());
                BlockState blockstate1 = level.getBlockState(mutable);
                if (blockstate1.isAir()) {
                    BlockState blockstate2 = level.getBlockState(blockpos);
                    boolean isFull = blockstate2.getBlock() == WATER && blockstate2.getValue(LiquidBlock.LEVEL) == 0;
                    if (blockstate2.getMaterial() == Material.WATER && isFull && state.canSurvive(level, blockpos) && level.isUnobstructed(state, blockpos, CollisionContext.empty())) {
                        level.setBlockAndUpdate(blockpos, state);
                        if (!permanent) {
                            level.scheduleTick(blockpos, FROSTED_ICE, MathHelper.nextInt(level.random, 60, 120));
                        }
                    }
                }
            }
        }
    }

    public static void freezeAllWater(Entity entity, Level levelIn, BlockPos pos, int radius, boolean permanent) {

        BlockState state = permanent ? ICE.defaultBlockState() : FROSTED_ICE.defaultBlockState();
        float f = (float) Math.min(HORZ_MAX, radius);
        float v = (float) Math.min(VERT_MAX, radius);
        float f2 = f * f;

        for (BlockPos blockpos : BlockPos.betweenClosed(pos.offset(-f, -v, -f), pos.offset(f, v, f))) {
            double distance = blockpos.distToCenterSqr(entity.position());
            if (distance < f2) {
                BlockState blockstate2 = levelIn.getBlockState(blockpos);
                boolean isFull = blockstate2.getBlock() == WATER && blockstate2.getValue(LiquidBlock.LEVEL) == 0;
                if (blockstate2.getMaterial() == Material.WATER && isFull && state.canSurvive(levelIn, blockpos) && levelIn.isUnobstructed(state, blockpos, CollisionContext.empty())) {
                    levelIn.setBlockAndUpdate(blockpos, state);
                    if (!permanent) {
                        levelIn.scheduleTick(blockpos, FROSTED_ICE, MathHelper.nextInt(levelIn.random, 60, 120));
                    }
                }
            }
        }
    }

    public static void freezeSurfaceLava(Entity entity, Level levelIn, BlockPos pos, int radius, boolean permanent) {

        if (GLOSSED_MAGMA == null && !permanent) {
            return;
        }
        BlockState state = permanent ? OBSIDIAN.defaultBlockState() : GLOSSED_MAGMA.get().defaultBlockState();
        float f = (float) Math.min(HORZ_MAX, radius);
        float v = (float) Math.min(VERT_MAX, radius);
        float f2 = f * f;
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

        for (BlockPos blockpos : BlockPos.betweenClosed(pos.offset(-f, -v, -f), pos.offset(f, v, f))) {
            double distance = blockpos.distToCenterSqr(entity.position());
            if (distance < f2) {
                mutable.set(blockpos.getX(), blockpos.getY() + 1, blockpos.getZ());
                BlockState blockstate1 = levelIn.getBlockState(mutable);
                if (blockstate1.isAir()) {
                    BlockState blockstate2 = levelIn.getBlockState(blockpos);
                    boolean isFull = blockstate2.getBlock() == LAVA && blockstate2.getValue(LiquidBlock.LEVEL) == 0;
                    if (blockstate2.getMaterial() == Material.LAVA && isFull && state.canSurvive(levelIn, blockpos) && levelIn.isUnobstructed(state, blockpos, CollisionContext.empty())) {
                        levelIn.setBlockAndUpdate(blockpos, state);
                        if (!permanent) {
                            levelIn.scheduleTick(blockpos, GLOSSED_MAGMA.get(), MathHelper.nextInt(levelIn.random, 60, 120));
                        }
                    }
                }
            }
        }
    }

    public static void freezeAllLava(Entity entity, Level levelIn, BlockPos pos, int radius, boolean permanent) {

        if (GLOSSED_MAGMA == null && !permanent) {
            return;
        }
        BlockState state = permanent ? OBSIDIAN.defaultBlockState() : GLOSSED_MAGMA.get().defaultBlockState();
        float f = (float) Math.min(HORZ_MAX, radius);
        float v = (float) Math.min(VERT_MAX, radius);
        float f2 = f * f;

        for (BlockPos blockpos : BlockPos.betweenClosed(pos.offset(-f, -v, -f), pos.offset(f, v, f))) {
            double distance = blockpos.distToCenterSqr(entity.position());
            if (distance < f2) {
                BlockState blockstate2 = levelIn.getBlockState(blockpos);
                boolean isFull = blockstate2.getBlock() == LAVA && blockstate2.getValue(LiquidBlock.LEVEL) == 0;
                if (blockstate2.getMaterial() == Material.LAVA && isFull && state.canSurvive(levelIn, blockpos) && levelIn.isUnobstructed(state, blockpos, CollisionContext.empty())) {
                    levelIn.setBlockAndUpdate(blockpos, state);
                    if (!permanent) {
                        levelIn.scheduleTick(blockpos, GLOSSED_MAGMA.get(), MathHelper.nextInt(levelIn.random, 60, 120));
                    }
                }
            }
        }
    }

    public static boolean isValidSnowPosition(Level levelIn, BlockPos pos) {

        BlockState state = levelIn.getBlockState(pos.below());
        Block block = state.getBlock();
        if (block == ICE || block == PACKED_ICE || block == BARRIER || block == FROSTED_ICE || block == GLOSSED_MAGMA.get()) {
            return false;
        }
        return Block.isFaceFull(state.getCollisionShape(levelIn, pos.below()), Direction.UP) || block == SNOW && state.getValue(SnowLayerBlock.LAYERS) == 8;
    }
    // endregion

    // region AREA TRANSFORMS / MISC
    private static boolean isValidLightningBoltPosition(Level levelIn, BlockPos pos, double chance) {

        BlockPos below = pos.below();
        BlockState state = levelIn.getBlockState(below);
        if (levelIn.canSeeSky(pos) && Block.isFaceFull(state.getCollisionShape(levelIn, below), Direction.UP)) {
            return levelIn.random.nextDouble() < chance; // Random chance.
        }
        return false;
    }

    public static void transformArea(Entity entity, Level levelIn, BlockPos pos, BlockState replaceable, BlockState replacement, int radius, boolean requireAir) {

        float f = (float) Math.min(HORZ_MAX, radius);
        float v = (float) Math.min(VERT_MAX, radius);
        float f2 = f * f;
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

        if (requireAir) {
            for (BlockPos iterPos : BlockPos.betweenClosed(pos.offset(-f, -v, -f), pos.offset(f, v, f))) {
                double distance = iterPos.distToCenterSqr(entity.position());
                if (distance < f2) {
                    mutable.set(iterPos.getX(), iterPos.getY() + 1, iterPos.getZ());
                    BlockState blockstate1 = levelIn.getBlockState(mutable);
                    if (blockstate1.isAir()) {
                        if (levelIn.getBlockState(iterPos) == replaceable) {
                            levelIn.setBlockAndUpdate(iterPos, replacement);
                        }
                    }
                }
            }
        } else {
            for (BlockPos iterPos : BlockPos.betweenClosed(pos.offset(-f, -v, -f), pos.offset(f, v, f))) {
                double distance = iterPos.distToCenterSqr(entity.position());
                if (distance < f2) {
                    if (levelIn.getBlockState(iterPos) == replaceable) {
                        levelIn.setBlockAndUpdate(iterPos, replacement);
                    }
                }
            }
        }
    }

    public static void transformArea(Entity entity, Level levelIn, BlockPos pos, Set<BlockState> replaceable, BlockState replacement, int radius, boolean requireAir) {

        float f = (float) Math.min(HORZ_MAX, radius);
        float v = (float) Math.min(VERT_MAX, radius);
        float f2 = f * f;
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

        if (requireAir) {
            for (BlockPos blockpos : BlockPos.betweenClosed(pos.offset(-f, -v, -f), pos.offset(f, v, f))) {
                double distance = blockpos.distToCenterSqr(entity.position());
                if (distance < f2) {
                    mutable.set(blockpos.getX(), blockpos.getY() + 1, blockpos.getZ());
                    BlockState blockstate1 = levelIn.getBlockState(mutable);
                    if (blockstate1.isAir()) {
                        if (replaceable.contains(levelIn.getBlockState(blockpos))) {
                            levelIn.setBlockAndUpdate(blockpos, replacement);
                        }
                    }
                }
            }
        } else {
            for (BlockPos iterPos : BlockPos.betweenClosed(pos.offset(-f, -v, -f), pos.offset(f, v, f))) {
                if (iterPos.closerThan(pos, f)) {
                    if (replaceable.contains(levelIn.getBlockState(iterPos))) {
                        levelIn.setBlockAndUpdate(iterPos, replacement);
                    }
                }
            }
        }
    }

    public static void transformGrass(Entity entity, Level levelIn, BlockPos pos, int radius) {

        transformArea(entity, levelIn, pos, DIRT.defaultBlockState(), GRASS_BLOCK.defaultBlockState(), radius, true);
    }

    public static void transformMycelium(Entity entity, Level levelIn, BlockPos pos, int radius) {

        Set<BlockState> replaceable = new ObjectOpenHashSet<>();
        Collections.addAll(replaceable, DIRT.defaultBlockState(), GRASS_BLOCK.defaultBlockState());
        transformArea(entity, levelIn, pos, replaceable, MYCELIUM.defaultBlockState(), radius, true);
    }

    public static void transformSignalAir(Entity entity, Level levelIn, BlockPos pos, int radius) {

        Set<BlockState> replaceable = new ObjectOpenHashSet<>();
        Collections.addAll(replaceable, AIR.defaultBlockState(), CAVE_AIR.defaultBlockState());
        transformArea(entity, levelIn, pos, replaceable, SIGNAL_AIR.get().defaultBlockState(), radius, false);
    }

    public static void transformGlowAir(Entity entity, Level levelIn, BlockPos pos, int radius) {

        Set<BlockState> replaceable = new ObjectOpenHashSet<>();
        Collections.addAll(replaceable, AIR.defaultBlockState(), CAVE_AIR.defaultBlockState());
        transformArea(entity, levelIn, pos, replaceable, GLOW_AIR.get().defaultBlockState(), radius, false);
    }

    public static void transformEnderAir(Entity entity, Level levelIn, BlockPos pos, int radius) {

        Set<BlockState> replaceable = new ObjectOpenHashSet<>();
        Collections.addAll(replaceable, AIR.defaultBlockState(), CAVE_AIR.defaultBlockState());
        transformArea(entity, levelIn, pos, replaceable, ENDER_AIR.get().defaultBlockState(), radius, false);
    }

    public static void zapNearbyGround(Entity entity, Level levelIn, BlockPos pos, int radius, double chance, int max) {

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
                BlockState blockstate1 = levelIn.getBlockState(mutable);
                if (blockstate1.isAir()) {
                    if (isValidLightningBoltPosition(levelIn, mutable, chance)) {
                        levelIn.setBlockAndUpdate(mutable, LIGHTNING_AIR.get().defaultBlockState());
                        ++count;
                    }
                }
            }
        }
    }

    public static void growMushrooms(Entity entity, Level levelIn, BlockPos pos, int radius, int count) {

        float f = (float) Math.min(HORZ_MAX, radius);
        float v = (float) Math.min(VERT_MAX, radius);
        float f2 = f * f;
        int grow = 0;
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

        mutable.set(entity.blockPosition().above());
        BlockState blockstate1 = levelIn.getBlockState(mutable);
        if (blockstate1.isAir()) {
            if (isValidMushroomPosition(levelIn, entity.blockPosition(), 1.0)) {
                levelIn.setBlockAndUpdate(mutable, levelIn.random.nextBoolean() ? BROWN_MUSHROOM.defaultBlockState() : RED_MUSHROOM.defaultBlockState());
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
                blockstate1 = levelIn.getBlockState(mutable);
                if (blockstate1.isAir()) {
                    if (isValidMushroomPosition(levelIn, iterPos, 0.5 - (distance / f2))) {
                        levelIn.setBlockAndUpdate(mutable, levelIn.random.nextBoolean() ? BROWN_MUSHROOM.defaultBlockState() : RED_MUSHROOM.defaultBlockState());
                        ++grow;
                    }
                }
            }
        }
    }

    private static boolean isValidMushroomPosition(Level levelIn, BlockPos pos, double chance) {

        Block block = levelIn.getBlockState(pos).getBlock();
        return levelIn.random.nextDouble() < chance && (block == MYCELIUM || block == PODZOL);
    }

    public static void growPlants(Entity entity, Level levelIn, BlockPos pos, int radius) {

        float f = (float) Math.min(HORZ_MAX, radius);
        float v = (float) Math.min(VERT_MAX, radius);
        float f2 = f * f;

        BlockState state;
        for (BlockPos iterPos : BlockPos.betweenClosed(pos.offset(-f, -v, -f), pos.offset(f, v, f))) {
            double distance = iterPos.distToCenterSqr(entity.position());
            if (distance < f2) {
                state = levelIn.getBlockState(iterPos);
                if (state.getBlock() instanceof BonemealableBlock) {
                    BonemealableBlock growable = (BonemealableBlock) state.getBlock();
                    if (growable.isValidBonemealTarget(levelIn, iterPos, state, levelIn.isClientSide)) {
                        if (!levelIn.isClientSide) {
                            if (growable.isBonemealSuccess(levelIn, levelIn.random, iterPos, state)) {
                                // TODO: Remove try/catch when Mojang fixes base issue.
                                try {
                                    growable.performBonemeal((ServerLevel) levelIn, levelIn.random, iterPos, state);
                                } catch (Exception e) {
                                    // Vanilla issue causes bamboo to crash if grown close to world height
                                    if (!(growable instanceof BambooBlock)) {
                                        throw e;
                                    }
                                }
                                // growable.performBonemeal((ServerLevel) levelIn, levelIn.rand, pos, state);
                                // ++grow;
                            }
                        }
                    }
                }
            }
        }
    }

    public static void growPlants(Entity entity, Level levelIn, BlockPos pos, int radius, int count) {

        float f = (float) Math.min(HORZ_MAX, radius);
        float v = (float) Math.min(VERT_MAX, radius);
        float f2 = f * f;
        int grow = 0;

        BlockState state = levelIn.getBlockState(entity.blockPosition());
        if (state.getBlock() instanceof BonemealableBlock) {
            BonemealableBlock growable = (BonemealableBlock) state.getBlock();
            if (growable.isValidBonemealTarget(levelIn, pos, state, levelIn.isClientSide)) {
                if (!levelIn.isClientSide) {
                    if (growable.isBonemealSuccess(levelIn, levelIn.random, pos, state)) {
                        // TODO: Remove try/catch when Mojang fixes base issue.
                        try {
                            growable.performBonemeal((ServerLevel) levelIn, levelIn.random, pos, state);
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
                state = levelIn.getBlockState(iterPos);
                if (state.getBlock() instanceof BonemealableBlock) {
                    BonemealableBlock growable = (BonemealableBlock) state.getBlock();
                    if (growable.isValidBonemealTarget(levelIn, iterPos, state, levelIn.isClientSide)) {
                        if (!levelIn.isClientSide) {
                            if (growable.isBonemealSuccess(levelIn, levelIn.random, iterPos, state)) {
                                // TODO: Remove try/catch when Mojang fixes base issue.
                                try {
                                    growable.performBonemeal((ServerLevel) levelIn, levelIn.random, iterPos, state);
                                    ++grow;
                                } catch (Exception e) {
                                    // Vanilla issue causes bamboo to crash if grown close to world height
                                    if (!(growable instanceof BambooBlock)) {
                                        throw e;
                                    }
                                }
                                // growable.performBonemeal((ServerLevel) levelIn, levelIn.rand, pos, state);
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
