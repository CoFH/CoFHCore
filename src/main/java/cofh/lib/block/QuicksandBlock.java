package cofh.lib.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Optional;
import java.util.function.Supplier;

public class QuicksandBlock extends Block implements BucketPickup {

    protected static final float IN_BLOCK_HORIZONTAL_SPEED_MULTIPLIER = 0.9F;
    protected static final float IN_BLOCK_VERTICAL_SPEED_MULTIPLIER = 1.5F;
    protected static final float NUM_BLOCKS_TO_FALL_INTO_BLOCK = 2.5F;
    protected static final VoxelShape FALLING_COLLISION_SHAPE = Shapes.box(0.0D, 0.0D, 0.0D, 1.0D, (double) 0.9F, 1.0D);
    protected static final double MINIMUM_FALL_DISTANCE_FOR_SOUND = 4.0D;
    protected static final double MINIMUM_FALL_DISTANCE_FOR_BIG_SOUND = 7.0D;

    protected Supplier<Item> bucket = () -> Items.AIR;

    public QuicksandBlock(Properties properties) {

        super(properties);
    }

    public QuicksandBlock bucket(Supplier<Item> bucket) {

        this.bucket = bucket;
        return this;
    }

    @Override
    public boolean skipRendering(BlockState state, BlockState otherState, Direction face) {

        return otherState.is(this) || super.skipRendering(state, otherState, face);
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState state, BlockGetter blockGetter, BlockPos pos) {

        return Shapes.empty();
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {

        if (!(entity instanceof LivingEntity) || entity.getFeetBlockState().is(this)) {
            entity.makeStuckInBlock(state, new Vec3(IN_BLOCK_HORIZONTAL_SPEED_MULTIPLIER, IN_BLOCK_VERTICAL_SPEED_MULTIPLIER, IN_BLOCK_HORIZONTAL_SPEED_MULTIPLIER));
        }
        if (!level.isClientSide) {
            entity.setSharedFlagOnFire(false);
        }
    }

    @Override
    public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, float distance) {

        if (!((double) distance < MINIMUM_FALL_DISTANCE_FOR_SOUND) && entity instanceof LivingEntity livingentity) {
            LivingEntity.Fallsounds fallSounds = livingentity.getFallSounds();
            SoundEvent soundevent = (double) distance < MINIMUM_FALL_DISTANCE_FOR_BIG_SOUND ? fallSounds.small() : fallSounds.big();
            entity.playSound(soundevent, 1.0F, 1.0F);
        }
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext collisionContext) {

        if (collisionContext instanceof EntityCollisionContext entitycollisioncontext) {
            Entity entity = entitycollisioncontext.getEntity();
            if (entity != null) {
                if (entity.fallDistance > NUM_BLOCKS_TO_FALL_INTO_BLOCK) {
                    return FALLING_COLLISION_SHAPE;
                }
                if (entity instanceof FallingBlockEntity) {
                    return super.getCollisionShape(state, blockGetter, pos, collisionContext);
                }
            }
        }
        return Shapes.empty();
    }

    @Override
    public VoxelShape getVisualShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext collisionContext) {

        return Shapes.empty();
    }

    @Override
    public ItemStack pickupBlock(LevelAccessor levelAccessor, BlockPos pos, BlockState state) {

        levelAccessor.setBlock(pos, Blocks.AIR.defaultBlockState(), 11);
        if (!levelAccessor.isClientSide()) {
            levelAccessor.levelEvent(2001, pos, Block.getId(state));
        }
        return new ItemStack(bucket.get());
    }

    @Override
    public Optional<SoundEvent> getPickupSound() {

        return Optional.empty();
    }

    @Override
    public boolean isPathfindable(BlockState state, BlockGetter blockGetter, BlockPos pos, PathComputationType type) {

        return true;
    }

}
