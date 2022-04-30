package cofh.lib.block.impl.rails;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.RailShape;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class DetectorRailBlockWL extends DetectorRailBlockCoFH implements IWaterLoggable {

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public DetectorRailBlockWL(Properties builder) {

        super(builder);
        this.registerDefaultState(this.stateDefinition.any().setValue(getShapeProperty(), RailShape.NORTH_SOUTH).setValue(POWERED, false).setValue(WATERLOGGED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {

        super.createBlockStateDefinition(builder);
        builder.add(WATERLOGGED);
    }

    @Override
    public float getRailMaxSpeed(BlockState state, World world, BlockPos pos, AbstractMinecartEntity cart) {

        return state.getValue(WATERLOGGED) ? maxSpeed * 1.5F : maxSpeed;
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

}
