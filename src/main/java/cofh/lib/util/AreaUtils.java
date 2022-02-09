package cofh.lib.util;

import cofh.lib.util.helpers.MathHelper;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.EndermanEntity;
import net.minecraft.entity.monster.EndermiteEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import static cofh.lib.util.references.CoreReferences.*;
import static net.minecraft.block.Blocks.*;

public class AreaUtils {

    private AreaUtils() {

    }

    public static final int HORZ_MAX = 32;
    public static final int VERT_MAX = 16;
    public static final Set<BlockState> REPLACEABLE_AIR = new ObjectOpenHashSet<>(new BlockState[]{AIR.defaultBlockState(), CAVE_AIR.defaultBlockState()});
    
    public static final IEffectApplier igniteEntities = (target, duration, power, source) -> {

        if (!target.fireImmune() && !target.isInWater() && target.getRemainingFireTicks() <= 0) {
            target.setSecondsOnFire(duration / 20);
        }
        if (target instanceof LivingEntity) {
            LivingEntity living = (LivingEntity) target;
            living.removeEffect(CHILLED);
        }
    };

    public static final IEffectApplier chillEntities = (target, duration, power, source) -> {
        
        if (target.getRemainingFireTicks() > 0) {
            target.setRemainingFireTicks(0);
        }
        if (target instanceof LivingEntity) {
            LivingEntity living = (LivingEntity) target;
            living.addEffect(new EffectInstance(CHILLED, duration, power));
        }
    };

    public static final IEffectApplier sunderEntities = (target, duration, power, source) -> {
        
        if (target instanceof LivingEntity) {
            LivingEntity living = (LivingEntity) target;
            living.addEffect(new EffectInstance(SUNDERED, duration, power));
        }
    };

    public static final IEffectApplier shockEntities = (target, duration, power, source) -> {

        if (target instanceof LivingEntity) {
            LivingEntity living = (LivingEntity) target;
            if (!living.hasEffect(LIGHTNING_RESISTANCE)) {
                living.addEffect(new EffectInstance(SHOCKED, duration, power));
            }
        }
    };

    public static final IBlockTransformer fireTransform = (world, pos, face, entity) -> {

        boolean succeeded = false;
        BlockState state = world.getBlockState(pos);

        if (AreaUtils.isUnlitCampfire(state)) {
            succeeded |= world.setBlockAndUpdate(pos, state.setValue(BlockStateProperties.LIT, true));
        }
        if (state.isAir(world, pos)) {
            if (AbstractFireBlock.canBePlacedAt(world, pos, face)) {
                succeeded |= world.setBlock(pos, AbstractFireBlock.getState(world, pos), 11);
            }
        }
        if (state.getBlock() == ICE || state.getBlock() == FROSTED_ICE) {
            succeeded |= world.setBlockAndUpdate(pos, WATER.defaultBlockState());
        }
        return succeeded;
    };

    public static final IBlockTransformer fireTransformSpecial = (world, pos, face, entity) -> {

        boolean succeeded = false;
        BlockState state = world.getBlockState(pos);
        if (isUnlitCampfire(state)) {
            world.setBlockAndUpdate(pos, state.setValue(BlockStateProperties.LIT, true));
        } else if (isUnlitTNT(state)) {
            state.getBlock().catchFire(state, world, pos, Direction.UP, entity instanceof LivingEntity ? (LivingEntity) entity : null);
            world.setBlockAndUpdate(pos, AIR.defaultBlockState());
        }
        return succeeded;
    };

    public static final IBlockTransformer iceTransform = (world, pos, face, entity) -> {

        boolean succeeded = false;
        BlockState state = world.getBlockState(pos);
        //TODO separate config values pain
        boolean permanentWater = true;
        boolean permanentLava = true;

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
        boolean isFull = state.getBlock() == WATER && state.getValue(FlowingFluidBlock.LEVEL) == 0;
        if (state.getMaterial() == Material.WATER && isFull && state.canSurvive(world, pos) && world.isUnobstructed(state, pos, ISelectionContext.empty())) {
            succeeded |= world.setBlockAndUpdate(pos, permanentWater ? ICE.defaultBlockState() : FROSTED_ICE.defaultBlockState());
            if (!permanentWater) {
                world.getBlockTicks().scheduleTick(pos, FROSTED_ICE, net.minecraft.util.math.MathHelper.nextInt(world.random, 60, 120));
            }
        }
        // LAVA
        isFull = state.getBlock() == LAVA && state.getValue(FlowingFluidBlock.LEVEL) == 0;
        if (state.getMaterial() == Material.LAVA && isFull && state.canSurvive(world, pos) && world.isUnobstructed(state, pos, ISelectionContext.empty())) {
            succeeded |= world.setBlockAndUpdate(pos, permanentLava ? OBSIDIAN.defaultBlockState() : GLOSSED_MAGMA.defaultBlockState());
            if (!permanentLava) {
                world.getBlockTicks().scheduleTick(pos, GLOSSED_MAGMA, net.minecraft.util.math.MathHelper.nextInt(world.random, 60, 120));
            }
        }
        return succeeded;
    };

    public static final IBlockTransformer iceSurfaceTransform = (world, pos, face, entity) -> {

        boolean succeeded = false;
        BlockState state = world.getBlockState(pos);
        //TODO separate config values pain
        boolean permanentWater = true;
        boolean permanentLava = true;
        BlockState frozenWater = permanentWater ? ICE.defaultBlockState() : FROSTED_ICE.defaultBlockState();
        BlockState frozenLava = permanentLava ? OBSIDIAN.defaultBlockState() : GLOSSED_MAGMA.defaultBlockState();

        // SNOW
        if (world.isEmptyBlock(pos) && AreaUtils.isValidSnowPosition(world, pos)) {
            succeeded |= world.setBlockAndUpdate(pos, SNOW.defaultBlockState());
        }
        BlockPos above = pos.relative(Direction.UP);
        if (world.getBlockState(above).isAir(world, above)) {
            boolean isFull = state.getBlock() == WATER && state.getValue(FlowingFluidBlock.LEVEL) == 0;
            if (state.getMaterial() == Material.WATER && isFull && state.canSurvive(world, pos) && world.isUnobstructed(state, pos, ISelectionContext.empty())) {
                succeeded |= world.setBlockAndUpdate(pos, frozenWater);
                if (!permanentWater) {
                    world.getBlockTicks().scheduleTick(pos, FROSTED_ICE, net.minecraft.util.math.MathHelper.nextInt(world.random, 60, 120));
                }
            }
            // LAVA
            isFull = state.getBlock() == LAVA && state.getValue(FlowingFluidBlock.LEVEL) == 0;
            if (state.getMaterial() == Material.LAVA && isFull && state.canSurvive(world, pos) && world.isUnobstructed(state, pos, ISelectionContext.empty())) {
                succeeded |= world.setBlockAndUpdate(pos, frozenLava);
                if (!permanentLava) {
                    world.getBlockTicks().scheduleTick(pos, GLOSSED_MAGMA, net.minecraft.util.math.MathHelper.nextInt(world.random, 60, 120));
                }
            }
        }
        return succeeded;
    };

    public static final IBlockTransformer earthTransform = (world, pos, face, entity) -> {
        boolean succeeded = false;
        BlockState state = world.getBlockState(pos);
        Material material = state.getMaterial();
        if (material == Material.STONE || material == Material.DIRT || state.getBlock() instanceof SnowyDirtBlock) {
            succeeded |= Utils.destroyBlock(world, pos, true, entity);
        }
        return succeeded;
    };

    public static final IBlockTransformer lightningTransform = (world, pos, face, entity) -> {
        boolean succeeded = false;
        BlockState state = world.getBlockState(pos);
        if (state.isAir(world, pos)) {
            if (isValidLightningBoltPosition(world, pos, 1.0F)) {
                succeeded |= world.setBlockAndUpdate(pos, LIGHTNING_AIR.defaultBlockState());
            }
        }
        return succeeded;
    };
    // endregion ELEMENTAL

    // region CONVERSION
    public static final IBlockTransformer signalAirTransform = getConversionTransform(REPLACEABLE_AIR, SIGNAL_AIR.defaultBlockState(), false);
    public static final IBlockTransformer glowAirTransform = getConversionTransform(REPLACEABLE_AIR, GLOW_AIR.defaultBlockState(), false);
    public static final IEffectApplier glowEntities = (target, duration, power, source) -> {
        
        if (target instanceof LivingEntity) {
            LivingEntity living = (LivingEntity) target;
            living.addEffect(new EffectInstance(Effects.GLOWING, duration, power));
            if (living.getMobType() == CreatureAttribute.UNDEAD) {
                living.hurt(DamageSource.explosion(source instanceof LivingEntity ? (LivingEntity) source : null), 4.0F);
                living.setSecondsOnFire(duration / 20);
            }
        }
    };
    public static final IBlockTransformer enderAirTransform = getConversionTransform(REPLACEABLE_AIR, ENDER_AIR.defaultBlockState(), false);
    public static final IEffectApplier enderfereEntities = (target, duration, power, source) -> {

        if (target instanceof EndermanEntity || target instanceof EndermiteEntity) {
            LivingEntity living = (LivingEntity) target;
            living.addEffect(new EffectInstance(ENDERFERENCE, duration, power));
            living.hurt(DamageSource.explosion(source instanceof LivingEntity ? (LivingEntity) source : null), 4.0F);
        }
    };

    public static final IBlockTransformer myceliumTransform = getConversionTransform(new ObjectOpenHashSet<>(new BlockState[]{DIRT.defaultBlockState(), GRASS_BLOCK.defaultBlockState()}), MYCELIUM.defaultBlockState(), true);
    public static final IBlockTransformer grassTransform = getConversionTransform(DIRT.defaultBlockState(), GRASS_BLOCK.defaultBlockState(), true);
    // endregion CONVERSION

    // region GROWTH
    public static final IBlockTransformer growMushrooms = new IBlockTransformer() {

        @Override
        public boolean transformBlock(World world, BlockPos pos, Direction face, @Nullable Entity entity) {

            Block below = world.getBlockState(pos.relative(Direction.DOWN)).getBlock();
            if (world.getBlockState(pos).isAir(world, pos) && (below.equals(MYCELIUM) || below.equals(PODZOL))) {
                return world.setBlockAndUpdate(pos, world.random.nextBoolean() ? BROWN_MUSHROOM.defaultBlockState() : RED_MUSHROOM.defaultBlockState());
            }
            return false;
        }

        @Override
        public void transformSphere(World world, Vector3d pos, float radius, float chance, int max, @Nullable Entity entity) {

            float f = Math.min(HORZ_MAX, radius);
            float v = Math.min(VERT_MAX, radius);
            float f2 = f * f;
            BlockPos origin = new BlockPos(pos);

            if (transformBlock(world, origin.relative(Direction.UP), Direction.DOWN, entity)) {
                --max;
            }
            for (BlockPos iterPos : BlockPos.betweenClosed(origin.offset(-f, -v, -f), origin.offset(f + 1, v + 1, f + 1))) {
                if (max <= 0) {
                    return;
                }
                double distance = iterPos.distSqr(entity.position(), true);
                if (distance < f2) {
                    if (world.random.nextDouble() < 0.5 - (distance / f2) && transformBlock(world, iterPos, Direction.DOWN, entity)) {
                        --max;
                    }
                }
            }
        }
    };

    public static final IBlockTransformer growPlants = (world, pos, face, entity) -> {

        BlockState state = world.getBlockState(pos);
        if (state.getBlock() instanceof IGrowable) {
            IGrowable growable = (IGrowable) state.getBlock();
            if (!world.isClientSide && growable.isValidBonemealTarget(world, pos, state, world.isClientSide) && growable.isBonemealSuccess(world, world.random, pos, state)) {
                // TODO: Remove try/catch when Mojang fixes base issue.
                try {
                    growable.performBonemeal((ServerWorld) world, world.random, pos, state);
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
            return (worldIn, pos, face, entity) -> {
                BlockPos above = pos.relative(Direction.UP);
                if (worldIn.getBlockState(above).isAir(worldIn, above) && replaceable.contains(worldIn.getBlockState(pos))) {
                    return worldIn.setBlockAndUpdate(pos, replacement);
                }
                return false;
            };
        } else {
            return (worldIn, pos, face, entity) -> {
                if (replaceable.contains(worldIn.getBlockState(pos))) {
                    return worldIn.setBlockAndUpdate(pos, replacement);
                }
                return false;
            };
        }
    }

    private static IBlockTransformer getConversionTransform(BlockState replaceable, BlockState replacement, boolean requireAir) {

        if (requireAir) {
            return (worldIn, pos, face, entity) -> {
                BlockPos above = pos.relative(Direction.UP);
                if (worldIn.getBlockState(above).isAir(worldIn, above) && replaceable.equals(worldIn.getBlockState(pos))) {
                    return worldIn.setBlockAndUpdate(pos, replacement);
                }
                return false;
            };
        } else {
            return (worldIn, pos, face, entity) -> {
                if (replaceable.equals(worldIn.getBlockState(pos))) {
                    return worldIn.setBlockAndUpdate(pos, replacement);
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

        return state.getBlock() instanceof TNTBlock;
    }
    // endregion HELPERS

    // region INTERFACES
    public interface IEffectApplier {

        void applyEffect(Entity target, int duration, int power, @Nullable Entity source);

        default void applyEffectNearby(World worldIn, Vector3d pos, Predicate<? super Entity> filter, float radius, int duration, int amplifier, @Nullable Entity source) {

            worldIn.getEntities(source, (new AxisAlignedBB(pos.subtract(radius, radius, radius), pos.add(radius, radius, radius))).inflate(1), filter)
                    .forEach(livingEntity -> this.applyEffect(livingEntity, duration, amplifier, source));
        }

        default void applyEffectNearby(World worldIn, Vector3d pos, float radius, int duration, int amplifier, @Nullable Entity source) {

            applyEffectNearby(worldIn, pos, EntityPredicates.ENTITY_STILL_ALIVE, radius, duration, amplifier, source);
        }

        default void applyEffectNearby(World worldIn, Vector3d pos, float radius, int duration, int amplifier) {

            applyEffectNearby(worldIn, pos, radius, duration, amplifier, null);
        }
    }

    public interface IBlockTransformer {

        boolean transformBlock(World world, BlockPos pos, Direction face, @Nullable Entity entity);

        default void transformSphere(World world, Vector3d pos, float radius, @Nullable Entity entity) {

            float f = Math.min(HORZ_MAX, radius);
            float v = Math.min(VERT_MAX, radius);
            float f2 = f * f;
            BlockPos origin = new BlockPos(pos);

            for (BlockPos iterPos : BlockPos.betweenClosed(origin.offset(-f, -v, -f), origin.offset(f + 1, v + 1, f + 1))) {
                if (iterPos.distSqr(pos, true) < f2) {
                    transformBlock(world, iterPos, Direction.DOWN, entity);
                }
            }
        }

        default void transformSphere(World world, Vector3d pos, float radius, float chance, @Nullable Entity entity) {

            float f = Math.min(HORZ_MAX, radius);
            float v = Math.min(VERT_MAX, radius);
            float f2 = f * f;
            BlockPos origin = new BlockPos(pos);

            for (BlockPos iterPos : BlockPos.betweenClosed(origin.offset(-f, -v, -f), origin.offset(f, v, f))) {
                double distSqr = iterPos.distSqr(pos, true);
                if (distSqr < f2 && (chance > 0.99999F || world.random.nextDouble() < chance)) {
                    transformBlock(world, iterPos, Direction.DOWN, entity);
                }
            }
        }

        default void transformSphere(World world, Vector3d pos, float radius, float chance, int max, @Nullable Entity entity) {

            float f = Math.min(HORZ_MAX, radius);
            float v = Math.min(VERT_MAX, radius);
            float f2 = f * f;
            BlockPos origin = new BlockPos(pos);

            if (transformBlock(world, origin, Direction.DOWN, entity)) {
                --max;
            }
            for (BlockPos iterPos : BlockPos.betweenClosed(origin.offset(-f, -v, -f), origin.offset(f, v, f))) {
                if (max <= 0) {
                    return;
                }
                double distSqr = iterPos.distSqr(pos, true);
                if (distSqr < f2 && (chance > 0.99999F || world.random.nextDouble() < chance) && transformBlock(world, iterPos, Direction.DOWN, entity)) {
                    --max;
                }
            }
        }
    }
    // endregion INTERFACES











    // region BURNING
    public static void igniteNearbyEntities(Entity entity, World worldIn, BlockPos pos, int radius, int duration) {

        AxisAlignedBB area = new AxisAlignedBB(pos.offset(-radius, -radius, -radius), pos.offset(1 + radius, 1 + radius, 1 + radius));
        List<LivingEntity> mobs = worldIn.getEntitiesOfClass(LivingEntity.class, area, EntityPredicates.ENTITY_STILL_ALIVE);
        mobs.removeIf(Entity::isInWater);
        mobs.removeIf(Entity::fireImmune);
        mobs.removeIf(mob -> mob instanceof EndermanEntity);
        for (LivingEntity mob : mobs) {
            mob.setSecondsOnFire(duration);
        }
    }

    public static void igniteNearbyGround(Entity entity, World worldIn, BlockPos pos, int radius, double chance) {

        float f = (float) Math.min(HORZ_MAX, radius);
        float v = (float) Math.min(VERT_MAX, radius);
        float f2 = f * f;
        BlockPos.Mutable mutable = new BlockPos.Mutable();

        for (BlockPos blockpos : BlockPos.betweenClosed(pos.offset(-f, -v, -f), pos.offset(f, v, f))) {
            double distance = blockpos.distSqr(entity.position(), true);
            if (distance < f2) {
                mutable.set(blockpos.getX(), blockpos.getY() + 1, blockpos.getZ());
                BlockState blockstate1 = worldIn.getBlockState(mutable);
                if (blockstate1.isAir(worldIn, mutable)) {
                    if (isValidFirePosition(worldIn, mutable, chance)) {
                        worldIn.setBlockAndUpdate(mutable, ((FireBlock) FIRE).getStateForPlacement(worldIn, mutable));
                    }
                }
            }
        }
    }

    public static void igniteSpecial(Entity entity, World worldIn, BlockPos pos, int radius, boolean campfire, boolean tnt, @Nullable Entity igniter) {

        float f = (float) Math.min(HORZ_MAX, radius);
        float v = (float) Math.min(VERT_MAX, radius);
        float f2 = f * f;

        for (BlockPos blockpos : BlockPos.betweenClosed(pos.offset(-f, -v, -f), pos.offset(f, v, f))) {
            double distance = blockpos.distSqr(entity.position(), true);
            if (distance < f2) {
                BlockState state = worldIn.getBlockState(blockpos);
                if (campfire && isUnlitCampfire(state)) {
                    worldIn.setBlockAndUpdate(blockpos, state.setValue(BlockStateProperties.LIT, true));
                } else if (tnt && isUnlitTNT(state)) {
                    state.getBlock().catchFire(state, worldIn, blockpos, Direction.UP, igniter instanceof LivingEntity ? (LivingEntity) igniter : null);
                    worldIn.setBlockAndUpdate(blockpos, AIR.defaultBlockState());
                }
            }
        }
    }

    public static boolean isValidFirePosition(World worldIn, BlockPos pos, double chance) {

        BlockPos below = pos.below();
        BlockState state = worldIn.getBlockState(below);
        if (Block.isFaceFull(state.getCollisionShape(worldIn, below), Direction.UP)) {
            return state.getMaterial().isFlammable() || worldIn.random.nextDouble() < chance; // Random chance.
        }
        return false;
    }
    // endregion

    // region FREEZING
    public static void freezeNearbyGround(Entity entity, World worldIn, BlockPos pos, int radius) {

        BlockState state = SNOW.defaultBlockState();
        float f = (float) Math.min(HORZ_MAX, radius);
        float v = (float) Math.min(VERT_MAX, radius);
        float f2 = f * f;
        BlockPos.Mutable mutable = new BlockPos.Mutable();

        for (BlockPos blockpos : BlockPos.betweenClosed(pos.offset(-f, -v, -f), pos.offset(f, v, f))) {
            double distance = blockpos.distSqr(entity.position(), true);
            if (distance < f2) {
                mutable.set(blockpos.getX(), blockpos.getY() + 1, blockpos.getZ());
                BlockState blockstate1 = worldIn.getBlockState(mutable);
                if (blockstate1.isAir(worldIn, mutable)) {
                    if (worldIn.getBiome(mutable).getTemperature(blockpos) < 0.8F && isValidSnowPosition(worldIn, mutable)) {
                        worldIn.setBlockAndUpdate(mutable, state);
                    }
                }
            }
        }
    }

    public static void freezeSpecial(Entity entity, World worldIn, BlockPos pos, int radius, boolean campfire, boolean fire) {

        float f = (float) Math.min(HORZ_MAX, radius);
        float v = (float) Math.min(VERT_MAX, radius);
        float f2 = f * f;

        for (BlockPos blockpos : BlockPos.betweenClosed(pos.offset(-f, -v, -f), pos.offset(f, v, f))) {
            double distance = blockpos.distSqr(entity.position(), true);
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

    public static void freezeSurfaceWater(Entity entity, World worldIn, BlockPos pos, int radius, boolean permanent) {

        BlockState state = permanent ? ICE.defaultBlockState() : FROSTED_ICE.defaultBlockState();
        float f = (float) Math.min(HORZ_MAX, radius);
        float v = (float) Math.min(VERT_MAX, radius);
        float f2 = f * f;
        BlockPos.Mutable mutable = new BlockPos.Mutable();

        for (BlockPos blockpos : BlockPos.betweenClosed(pos.offset(-f, -v, -f), pos.offset(f, v, f))) {
            double distance = blockpos.distSqr(entity.position(), true);
            if (distance < f2) {
                mutable.set(blockpos.getX(), blockpos.getY() + 1, blockpos.getZ());
                BlockState blockstate1 = worldIn.getBlockState(mutable);
                if (blockstate1.isAir(worldIn, mutable)) {
                    BlockState blockstate2 = worldIn.getBlockState(blockpos);
                    boolean isFull = blockstate2.getBlock() == WATER && blockstate2.getValue(FlowingFluidBlock.LEVEL) == 0;
                    if (blockstate2.getMaterial() == Material.WATER && isFull && state.canSurvive(worldIn, blockpos) && worldIn.isUnobstructed(state, blockpos, ISelectionContext.empty())) {
                        worldIn.setBlockAndUpdate(blockpos, state);
                        if (!permanent) {
                            worldIn.getBlockTicks().scheduleTick(blockpos, FROSTED_ICE, MathHelper.nextInt(worldIn.random, 60, 120));
                        }
                    }
                }
            }
        }
    }

    public static void freezeAllWater(Entity entity, World worldIn, BlockPos pos, int radius, boolean permanent) {

        BlockState state = permanent ? ICE.defaultBlockState() : FROSTED_ICE.defaultBlockState();
        float f = (float) Math.min(HORZ_MAX, radius);
        float v = (float) Math.min(VERT_MAX, radius);
        float f2 = f * f;

        for (BlockPos blockpos : BlockPos.betweenClosed(pos.offset(-f, -v, -f), pos.offset(f, v, f))) {
            double distance = blockpos.distSqr(entity.position(), true);
            if (distance < f2) {
                BlockState blockstate2 = worldIn.getBlockState(blockpos);
                boolean isFull = blockstate2.getBlock() == WATER && blockstate2.getValue(FlowingFluidBlock.LEVEL) == 0;
                if (blockstate2.getMaterial() == Material.WATER && isFull && state.canSurvive(worldIn, blockpos) && worldIn.isUnobstructed(state, blockpos, ISelectionContext.empty())) {
                    worldIn.setBlockAndUpdate(blockpos, state);
                    if (!permanent) {
                        worldIn.getBlockTicks().scheduleTick(blockpos, FROSTED_ICE, MathHelper.nextInt(worldIn.random, 60, 120));
                    }
                }
            }
        }
    }

    public static void freezeSurfaceLava(Entity entity, World worldIn, BlockPos pos, int radius, boolean permanent) {

        if (GLOSSED_MAGMA == null && !permanent) {
            return;
        }
        BlockState state = permanent ? OBSIDIAN.defaultBlockState() : GLOSSED_MAGMA.defaultBlockState();
        float f = (float) Math.min(HORZ_MAX, radius);
        float v = (float) Math.min(VERT_MAX, radius);
        float f2 = f * f;
        BlockPos.Mutable mutable = new BlockPos.Mutable();

        for (BlockPos blockpos : BlockPos.betweenClosed(pos.offset(-f, -v, -f), pos.offset(f, v, f))) {
            double distance = blockpos.distSqr(entity.position(), true);
            if (distance < f2) {
                mutable.set(blockpos.getX(), blockpos.getY() + 1, blockpos.getZ());
                BlockState blockstate1 = worldIn.getBlockState(mutable);
                if (blockstate1.isAir(worldIn, mutable)) {
                    BlockState blockstate2 = worldIn.getBlockState(blockpos);
                    boolean isFull = blockstate2.getBlock() == LAVA && blockstate2.getValue(FlowingFluidBlock.LEVEL) == 0;
                    if (blockstate2.getMaterial() == Material.LAVA && isFull && state.canSurvive(worldIn, blockpos) && worldIn.isUnobstructed(state, blockpos, ISelectionContext.empty())) {
                        worldIn.setBlockAndUpdate(blockpos, state);
                        if (!permanent) {
                            worldIn.getBlockTicks().scheduleTick(blockpos, GLOSSED_MAGMA, MathHelper.nextInt(worldIn.random, 60, 120));
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
        BlockState state = permanent ? OBSIDIAN.defaultBlockState() : GLOSSED_MAGMA.defaultBlockState();
        float f = (float) Math.min(HORZ_MAX, radius);
        float v = (float) Math.min(VERT_MAX, radius);
        float f2 = f * f;

        for (BlockPos blockpos : BlockPos.betweenClosed(pos.offset(-f, -v, -f), pos.offset(f, v, f))) {
            double distance = blockpos.distSqr(entity.position(), true);
            if (distance < f2) {
                BlockState blockstate2 = worldIn.getBlockState(blockpos);
                boolean isFull = blockstate2.getBlock() == LAVA && blockstate2.getValue(FlowingFluidBlock.LEVEL) == 0;
                if (blockstate2.getMaterial() == Material.LAVA && isFull && state.canSurvive(worldIn, blockpos) && worldIn.isUnobstructed(state, blockpos, ISelectionContext.empty())) {
                    worldIn.setBlockAndUpdate(blockpos, state);
                    if (!permanent) {
                        worldIn.getBlockTicks().scheduleTick(blockpos, GLOSSED_MAGMA, MathHelper.nextInt(worldIn.random, 60, 120));
                    }
                }
            }
        }
    }

    public static boolean isValidSnowPosition(World worldIn, BlockPos pos) {

        BlockState state = worldIn.getBlockState(pos.below());
        Block block = state.getBlock();
        if (block == ICE || block == PACKED_ICE || block == BARRIER || block == FROSTED_ICE || block == GLOSSED_MAGMA) {
            return false;
        }
        return Block.isFaceFull(state.getCollisionShape(worldIn, pos.below()), Direction.UP) || block == SNOW && state.getValue(SnowBlock.LAYERS) == 8;
    }
    // endregion

    // region AREA TRANSFORMS / MISC
    private static boolean isValidLightningBoltPosition(World worldIn, BlockPos pos, double chance) {

        BlockPos below = pos.below();
        BlockState state = worldIn.getBlockState(below);
        if (worldIn.canSeeSky(pos) && Block.isFaceFull(state.getCollisionShape(worldIn, below), Direction.UP)) {
            return worldIn.random.nextDouble() < chance; // Random chance.
        }
        return false;
    }

    public static void transformArea(Entity entity, World worldIn, BlockPos pos, BlockState replaceable, BlockState replacement, int radius, boolean requireAir) {

        float f = (float) Math.min(HORZ_MAX, radius);
        float v = (float) Math.min(VERT_MAX, radius);
        float f2 = f * f;
        BlockPos.Mutable mutable = new BlockPos.Mutable();

        if (requireAir) {
            for (BlockPos iterPos : BlockPos.betweenClosed(pos.offset(-f, -v, -f), pos.offset(f, v, f))) {
                double distance = iterPos.distSqr(entity.position(), true);
                if (distance < f2) {
                    mutable.set(iterPos.getX(), iterPos.getY() + 1, iterPos.getZ());
                    BlockState blockstate1 = worldIn.getBlockState(mutable);
                    if (blockstate1.isAir(worldIn, mutable)) {
                        if (worldIn.getBlockState(iterPos) == replaceable) {
                            worldIn.setBlockAndUpdate(iterPos, replacement);
                        }
                    }
                }
            }
        } else {
            for (BlockPos iterPos : BlockPos.betweenClosed(pos.offset(-f, -v, -f), pos.offset(f, v, f))) {
                double distance = iterPos.distSqr(entity.position(), true);
                if (distance < f2) {
                    if (worldIn.getBlockState(iterPos) == replaceable) {
                        worldIn.setBlockAndUpdate(iterPos, replacement);
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
            for (BlockPos blockpos : BlockPos.betweenClosed(pos.offset(-f, -v, -f), pos.offset(f, v, f))) {
                double distance = blockpos.distSqr(entity.position(), true);
                if (distance < f2) {
                    mutable.set(blockpos.getX(), blockpos.getY() + 1, blockpos.getZ());
                    BlockState blockstate1 = worldIn.getBlockState(mutable);
                    if (blockstate1.isAir(worldIn, mutable)) {
                        if (replaceable.contains(worldIn.getBlockState(blockpos))) {
                            worldIn.setBlockAndUpdate(blockpos, replacement);
                        }
                    }
                }
            }
        } else {
            for (BlockPos iterPos : BlockPos.betweenClosed(pos.offset(-f, -v, -f), pos.offset(f, v, f))) {
                if (iterPos.closerThan(entity.position(), f)) {
                    if (replaceable.contains(worldIn.getBlockState(iterPos))) {
                        worldIn.setBlockAndUpdate(iterPos, replacement);
                    }
                }
            }
        }
    }

    public static void transformGrass(Entity entity, World worldIn, BlockPos pos, int radius) {

        transformArea(entity, worldIn, pos, DIRT.defaultBlockState(), GRASS_BLOCK.defaultBlockState(), radius, true);
    }

    public static void transformMycelium(Entity entity, World worldIn, BlockPos pos, int radius) {

        Set<BlockState> replaceable = new ObjectOpenHashSet<>();
        Collections.addAll(replaceable, DIRT.defaultBlockState(), GRASS_BLOCK.defaultBlockState());
        transformArea(entity, worldIn, pos, replaceable, MYCELIUM.defaultBlockState(), radius, true);
    }

    public static void transformSignalAir(Entity entity, World worldIn, BlockPos pos, int radius) {

        Set<BlockState> replaceable = new ObjectOpenHashSet<>();
        Collections.addAll(replaceable, AIR.defaultBlockState(), CAVE_AIR.defaultBlockState());
        transformArea(entity, worldIn, pos, replaceable, SIGNAL_AIR.defaultBlockState(), radius, false);
    }

    public static void transformGlowAir(Entity entity, World worldIn, BlockPos pos, int radius) {

        Set<BlockState> replaceable = new ObjectOpenHashSet<>();
        Collections.addAll(replaceable, AIR.defaultBlockState(), CAVE_AIR.defaultBlockState());
        transformArea(entity, worldIn, pos, replaceable, GLOW_AIR.defaultBlockState(), radius, false);
    }

    public static void transformEnderAir(Entity entity, World worldIn, BlockPos pos, int radius) {

        Set<BlockState> replaceable = new ObjectOpenHashSet<>();
        Collections.addAll(replaceable, AIR.defaultBlockState(), CAVE_AIR.defaultBlockState());
        transformArea(entity, worldIn, pos, replaceable, ENDER_AIR.defaultBlockState(), radius, false);
    }

    public static void zapNearbyGround(Entity entity, World worldIn, BlockPos pos, int radius, double chance, int max) {

        float f = (float) Math.min(HORZ_MAX, radius);
        float v = (float) Math.min(VERT_MAX, radius);
        float f2 = f * f;
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        int count = 0;

        for (BlockPos blockpos : BlockPos.betweenClosed(pos.offset(-f, -v, -f), pos.offset(f, v, f))) {
            if (count >= max) {
                return;
            }
            double distance = blockpos.distSqr(entity.position(), true);
            if (distance < f2) {
                mutable.set(blockpos.getX(), blockpos.getY() + 1, blockpos.getZ());
                BlockState blockstate1 = worldIn.getBlockState(mutable);
                if (blockstate1.isAir(worldIn, mutable)) {
                    if (isValidLightningBoltPosition(worldIn, mutable, chance)) {
                        worldIn.setBlockAndUpdate(mutable, LIGHTNING_AIR.defaultBlockState());
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

        mutable.set(entity.blockPosition().above());
        BlockState blockstate1 = worldIn.getBlockState(mutable);
        if (blockstate1.isAir(worldIn, mutable)) {
            if (isValidMushroomPosition(worldIn, entity.blockPosition(), 1.0)) {
                worldIn.setBlockAndUpdate(mutable, worldIn.random.nextBoolean() ? BROWN_MUSHROOM.defaultBlockState() : RED_MUSHROOM.defaultBlockState());
                ++grow;
            }
        }
        for (BlockPos iterPos : BlockPos.betweenClosed(pos.offset(-f, -v, -f), pos.offset(f, v, f))) {
            if (grow >= count) {
                return;
            }
            double distance = iterPos.distSqr(entity.position(), true);
            if (distance < f2) {
                mutable.set(iterPos.getX(), iterPos.getY() + 1, iterPos.getZ());
                blockstate1 = worldIn.getBlockState(mutable);
                if (blockstate1.isAir(worldIn, mutable)) {
                    if (isValidMushroomPosition(worldIn, iterPos, 0.5 - (distance / f2))) {
                        worldIn.setBlockAndUpdate(mutable, worldIn.random.nextBoolean() ? BROWN_MUSHROOM.defaultBlockState() : RED_MUSHROOM.defaultBlockState());
                        ++grow;
                    }
                }
            }
        }
    }

    private static boolean isValidMushroomPosition(World worldIn, BlockPos pos, double chance) {

        Block block = worldIn.getBlockState(pos).getBlock();
        return worldIn.random.nextDouble() < chance && (block == MYCELIUM || block == PODZOL);
    }

    public static void growPlants(Entity entity, World worldIn, BlockPos pos, int radius) {

        float f = (float) Math.min(HORZ_MAX, radius);
        float v = (float) Math.min(VERT_MAX, radius);
        float f2 = f * f;

        BlockState state;
        for (BlockPos iterPos : BlockPos.betweenClosed(pos.offset(-f, -v, -f), pos.offset(f, v, f))) {
            double distance = iterPos.distSqr(entity.position(), true);
            if (distance < f2) {
                state = worldIn.getBlockState(iterPos);
                if (state.getBlock() instanceof IGrowable) {
                    IGrowable growable = (IGrowable) state.getBlock();
                    if (growable.isValidBonemealTarget(worldIn, iterPos, state, worldIn.isClientSide)) {
                        if (!worldIn.isClientSide) {
                            if (growable.isBonemealSuccess(worldIn, worldIn.random, iterPos, state)) {
                                // TODO: Remove try/catch when Mojang fixes base issue.
                                try {
                                    growable.performBonemeal((ServerWorld) worldIn, worldIn.random, iterPos, state);
                                } catch (Exception e) {
                                    // Vanilla issue causes bamboo to crash if grown close to world height
                                    if (!(growable instanceof BambooBlock)) {
                                        throw e;
                                    }
                                }
                                // growable.performBonemeal((ServerWorld) worldIn, worldIn.rand, pos, state);
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

        BlockState state = worldIn.getBlockState(entity.blockPosition());
        if (state.getBlock() instanceof IGrowable) {
            IGrowable growable = (IGrowable) state.getBlock();
            if (growable.isValidBonemealTarget(worldIn, pos, state, worldIn.isClientSide)) {
                if (!worldIn.isClientSide) {
                    if (growable.isBonemealSuccess(worldIn, worldIn.random, pos, state)) {
                        // TODO: Remove try/catch when Mojang fixes base issue.
                        try {
                            growable.performBonemeal((ServerWorld) worldIn, worldIn.random, pos, state);
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
            double distance = iterPos.distSqr(entity.position(), true);
            if (distance < f2) {
                state = worldIn.getBlockState(iterPos);
                if (state.getBlock() instanceof IGrowable) {
                    IGrowable growable = (IGrowable) state.getBlock();
                    if (growable.isValidBonemealTarget(worldIn, iterPos, state, worldIn.isClientSide)) {
                        if (!worldIn.isClientSide) {
                            if (growable.isBonemealSuccess(worldIn, worldIn.random, iterPos, state)) {
                                // TODO: Remove try/catch when Mojang fixes base issue.
                                try {
                                    growable.performBonemeal((ServerWorld) worldIn, worldIn.random, iterPos, state);
                                    ++grow;
                                } catch (Exception e) {
                                    // Vanilla issue causes bamboo to crash if grown close to world height
                                    if (!(growable instanceof BambooBlock)) {
                                        throw e;
                                    }
                                }
                                // growable.performBonemeal((ServerWorld) worldIn, worldIn.rand, pos, state);
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
