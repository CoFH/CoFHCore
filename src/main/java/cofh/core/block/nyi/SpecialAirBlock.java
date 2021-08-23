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
        this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, false).setValue(AGE, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {

        builder.add(WATERLOGGED);
        builder.add(AGE);
    }

    @Override
    public FluidState getFluidState(BlockState state) {

        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockItemUseContext context) {

        boolean flag = context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER;
        return this.defaultBlockState().setValue(WATERLOGGED, flag);
    }

    @Override
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {

        if (stateIn.getValue(WATERLOGGED)) {
            worldIn.getLiquidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(worldIn));
        }
        return super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
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
                blockpos$mutable.setWithOffset(pos, direction);
                BlockState blockstate = worldIn.getBlockState(blockpos$mutable);
                if (blockstate.is(this) && !this.slightlyDisperse(blockstate, worldIn, blockpos$mutable)) {
                    worldIn.getBlockTicks().scheduleTick(blockpos$mutable, this, net.minecraft.util.math.MathHelper.nextInt(rand, 20, 40));
                }
            }
        } else {
            worldIn.getBlockTicks().scheduleTick(pos, this, MathHelper.nextInt(rand, 20, 40));
        }
    }

    // region HELPERS
    protected void turnIntoAir(BlockState state, World worldIn, BlockPos pos) {

        BlockState newState = worldIn.getFluidState(pos).createLegacyBlock();
        worldIn.setBlockAndUpdate(pos, newState);
        worldIn.neighborChanged(pos, newState.getBlock(), pos);
    }

    protected boolean shouldDisperse(IBlockReader worldIn, BlockPos pos, int neighborsRequired) {

        int i = 0;
        BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
        for (Direction direction : Direction.values()) {
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

    protected boolean slightlyDisperse(BlockState state, World worldIn, BlockPos pos) {

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
