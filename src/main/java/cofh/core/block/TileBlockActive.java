package cofh.core.block;

import cofh.core.tileentity.TileCoFH;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

import java.util.function.Supplier;

import static cofh.lib.util.constants.Constants.ACTIVE;

public class TileBlockActive extends TileBlockCoFH {

    public TileBlockActive(Properties builder, Class<? extends TileCoFH> tileClass, Supplier<BlockEntityType<? extends TileCoFH>> blockEntityType) {

        super(builder, tileClass, blockEntityType);
        this.registerDefaultState(this.stateDefinition.any().setValue(ACTIVE, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {

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
