package cofh.core.block.nyi;

import cofh.lib.util.helpers.MathHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

import javax.annotation.Nullable;
import java.util.Random;

import static cofh.lib.util.Constants.DIRECTIONS;

public class SpecialAirBlock extends AirBlock {

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final IntegerProperty AGE = IntegerProperty.create("age", 0, 3);

    public SpecialAirBlock(Properties builder) {

        super(builder);
        this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, false).setValue(AGE, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {

        builder.add(WATERLOGGED);
        builder.add(AGE);
    }

    @Override
    public FluidState getFluidState(BlockState state) {

        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {

        boolean flag = context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER;
        return this.defaultBlockState().setValue(WATERLOGGED, flag);
    }

    @Override
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos) {

        if (stateIn.getValue(WATERLOGGED)) {
            worldIn.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(worldIn));
        }
        return super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    @Override
    public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {

        if (blockIn == this && this.shouldDisperse(worldIn, pos, 3)) {
            this.turnIntoAir(state, worldIn, pos);
        }
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos, isMoving);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel worldIn, BlockPos pos, Random random) {

        this.tick(state, worldIn, pos, random);
    }

    @Override
    public void tick(BlockState state, ServerLevel worldIn, BlockPos pos, Random rand) {

        if ((rand.nextInt(4) == 0 || this.shouldDisperse(worldIn, pos, 4)) && this.slightlyDisperse(state, worldIn, pos)) {
            BlockPos.MutableBlockPos blockpos$mutable = new BlockPos.MutableBlockPos();
            for (Direction direction : DIRECTIONS) {
                blockpos$mutable.setWithOffset(pos, direction);
                BlockState blockstate = worldIn.getBlockState(blockpos$mutable);
                if (blockstate.is(this) && !this.slightlyDisperse(blockstate, worldIn, blockpos$mutable)) {
                    worldIn.scheduleTick(blockpos$mutable, this, Mth.nextInt(rand, 20, 40));
                }
            }
        } else {
            worldIn.scheduleTick(pos, this, MathHelper.nextInt(rand, 20, 40));
        }
    }

    // region HELPERS
    protected void turnIntoAir(BlockState state, Level worldIn, BlockPos pos) {

        BlockState newState = worldIn.getFluidState(pos).createLegacyBlock();
        worldIn.setBlockAndUpdate(pos, newState);
        worldIn.neighborChanged(pos, newState.getBlock(), pos);
    }

    protected boolean shouldDisperse(LevelReader worldIn, BlockPos pos, int neighborsRequired) {

        int i = 0;
        BlockPos.MutableBlockPos blockpos$mutable = new BlockPos.MutableBlockPos();
        for (Direction direction : DIRECTIONS) {
            blockpos$mutable.setWithOffset(pos, direction);
            if (worldIn.getBlockState(blockpos$mutable).is(this)) {
                ++i;
                if (i >= neighborsRequired) {
                    return false;
                }
            }
        }
        return true;
    }

    protected boolean slightlyDisperse(BlockState state, Level worldIn, BlockPos pos) {

        int i = state.getValue(AGE);
        if (i < 1) {
            worldIn.setBlock(pos, state.setValue(AGE, i + 1), 2);
            return false;
        } else {
            this.turnIntoAir(state, worldIn, pos);
            return true;
        }
    }
    // endregion
}
