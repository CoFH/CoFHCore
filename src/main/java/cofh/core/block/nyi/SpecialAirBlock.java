package cofh.core.block.nyi;

import cofh.lib.util.helpers.MathHelper;
import net.minecraft.block.AirBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.Random;

public class SpecialAirBlock extends AirBlock {

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final IntegerProperty AGE = IntegerProperty.create("age", 0, 3);

    public SpecialAirBlock(Properties builder) {

        super(builder);
        this.setDefaultState(this.stateContainer.getBaseState().with(WATERLOGGED, false).with(AGE, 0));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {

        builder.add(WATERLOGGED);
        builder.add(AGE);
    }

    @Override
    public FluidState getFluidState(BlockState state) {

        return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockItemUseContext context) {

        boolean flag = context.getWorld().getFluidState(context.getPos()).getFluid() == Fluids.WATER;
        return this.getDefaultState().with(WATERLOGGED, flag);
    }

    @Override
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {

        if (stateIn.get(WATERLOGGED)) {
            worldIn.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
        }
        return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    @Override
    public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {

        if (blockIn == this && this.shouldDisperse(worldIn, pos, 3)) {
            this.turnIntoAir(state, worldIn, pos);
        }
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos, isMoving);
    }

    @Override
    public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random) {

        this.tick(state, worldIn, pos, random);
    }

    @Override
    public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {

        if ((rand.nextInt(4) == 0 || this.shouldDisperse(worldIn, pos, 4)) && this.slightlyDisperse(state, worldIn, pos)) {
            BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
            for (Direction direction : Direction.values()) {
                blockpos$mutable.setAndMove(pos, direction);
                BlockState blockstate = worldIn.getBlockState(blockpos$mutable);
                if (blockstate.isIn(this) && !this.slightlyDisperse(blockstate, worldIn, blockpos$mutable)) {
                    worldIn.getPendingBlockTicks().scheduleTick(blockpos$mutable, this, net.minecraft.util.math.MathHelper.nextInt(rand, 20, 40));
                }
            }
        } else {
            worldIn.getPendingBlockTicks().scheduleTick(pos, this, MathHelper.nextInt(rand, 20, 40));
        }
    }

    // region HELPERS
    protected void turnIntoAir(BlockState state, World worldIn, BlockPos pos) {

        BlockState newState = worldIn.getFluidState(pos).getBlockState();
        worldIn.setBlockState(pos, newState);
        worldIn.neighborChanged(pos, newState.getBlock(), pos);
    }

    protected boolean shouldDisperse(IBlockReader worldIn, BlockPos pos, int neighborsRequired) {

        int i = 0;
        BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
        for (Direction direction : Direction.values()) {
            blockpos$mutable.setAndMove(pos, direction);
            if (worldIn.getBlockState(blockpos$mutable).isIn(this)) {
                ++i;
                if (i >= neighborsRequired) {
                    return false;
                }
            }
        }
        return true;
    }

    protected boolean slightlyDisperse(BlockState state, World worldIn, BlockPos pos) {

        int i = state.get(AGE);
        if (i < 1) {
            worldIn.setBlockState(pos, state.with(AGE, i + 1), 2);
            return false;
        } else {
            this.turnIntoAir(state, worldIn, pos);
            return true;
        }
    }
    // endregion
}
