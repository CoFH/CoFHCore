package cofh.core.content.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

import java.util.function.Supplier;

import static cofh.lib.util.constants.BlockStatePropertiesCoFH.ACTIVE;

public class TileBlockActive extends TileBlockCoFH {

    public TileBlockActive(Properties builder, Class<?> tileClass, Supplier<BlockEntityType<?>> blockEntityType) {

        super(builder, tileClass, blockEntityType);
        this.registerDefaultState(this.stateDefinition.any().setValue(ACTIVE, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {

        super.createBlockStateDefinition(builder);
        builder.add(ACTIVE);
    }

    //    @OnlyIn(Dist.CLIENT)
    //    public void animateTick(BlockState state, Level level, BlockPos pos, Random rand) {
    //
    //        if (state.get(ACTIVE)) {
    //            TileCoFH tile = (TileCoFH) world.getTileEntity(pos);
    //            if (tile != null) {
    //                tile.animateTick(state, world, pos, rand);
    //            }
    //        }
    //    }

}
