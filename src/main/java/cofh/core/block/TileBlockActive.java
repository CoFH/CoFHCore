package cofh.core.block;

import cofh.core.tileentity.TileCoFH;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateContainer;

import java.util.function.Supplier;

import static cofh.lib.util.constants.Constants.ACTIVE;

public class TileBlockActive extends TileBlockCoFH {

    public TileBlockActive(Properties builder, Supplier<? extends TileCoFH> supplier) {

        super(builder, supplier);
        this.registerDefaultState(this.stateDefinition.any().setValue(ACTIVE, false));
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {

        super.createBlockStateDefinition(builder);
        builder.add(ACTIVE);
    }

    //    @OnlyIn(Dist.CLIENT)
    //    public void animateTick(BlockState state, World world, BlockPos pos, Random rand) {
    //
    //        if (state.get(ACTIVE)) {
    //            TileCoFH tile = (TileCoFH) world.getTileEntity(pos);
    //            if (tile != null) {
    //                tile.animateTick(state, world, pos, rand);
    //            }
    //        }
    //    }

}
