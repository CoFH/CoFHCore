package cofh.lib.block.impl;

import net.minecraft.block.*;
import net.minecraft.pathfinding.PathType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;

import java.util.function.Supplier;

public class TilledSoilBlock extends SoilBlock {

    protected Supplier<Block> dirt = () -> Blocks.DIRT;

    public TilledSoilBlock(Properties properties) {

        super(properties);
    }

    public TilledSoilBlock dirt(Supplier<Block> dirt) {

        this.dirt = dirt;
        return this;
    }

    @Override
    public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {

        if (!state.isValidPosition(worldIn, pos)) {
            turnToDirt(state, worldIn, pos);
        }
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos, isMoving);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {

        return SHAPE_TILLED;
    }

    @Override
    public boolean allowsMovement(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {

        return false;
    }

    @Override
    public boolean canSustainPlant(BlockState state, IBlockReader world, BlockPos pos, Direction facing, IPlantable plantable) {

        return canSustainPlant(state, world, pos, facing, plantable, true);
    }

    @Override
    public boolean isTransparent(BlockState state) {

        return true;
    }

    @Override
    public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {

        BlockState blockstate = worldIn.getBlockState(pos.up());
        return !blockstate.getMaterial().isSolid() || blockstate.getBlock() instanceof FenceGateBlock || blockstate.getBlock() instanceof MovingPistonBlock;
    }

    public void turnToDirt(BlockState state, World worldIn, BlockPos pos) {

        worldIn.setBlockState(pos, nudgeEntitiesWithNewState(state, dirt.get().getDefaultState(), worldIn, pos));
    }

}
