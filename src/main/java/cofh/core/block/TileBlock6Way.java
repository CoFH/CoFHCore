package cofh.core.block;

import cofh.core.tileentity.TileCoFH;
import cofh.lib.block.IWrenchable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.function.Supplier;

import static cofh.lib.util.constants.Constants.FACING_ALL;

public class TileBlock6Way extends TileBlockCoFH implements IWrenchable {

    public TileBlock6Way(Properties builder, Supplier<? extends TileCoFH> supplier) {

        super(builder, supplier);
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {

        super.fillStateContainer(builder);
        builder.add(FACING_ALL);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {

        return this.getDefaultState().with(FACING_ALL, context.getNearestLookingDirection().getOpposite());
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {

        return state.with(FACING_ALL, rot.rotate(state.get(FACING_ALL)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirrorIn) {

        return state.rotate(mirrorIn.toRotation(state.get(FACING_ALL)));
    }

    // region IWrenchable
    @Override
    public void wrenchBlock(World world, BlockPos pos, BlockState state, RayTraceResult target, PlayerEntity player) {

        BlockState rotState = state.with(FACING_ALL, Direction.byIndex(state.get(FACING_ALL).getIndex() + 1));
        if (rotState != state) {
            world.setBlockState(pos, rotState);
        }
    }
    // endregion
}
