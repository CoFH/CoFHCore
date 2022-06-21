package cofh.core.content.block;

import cofh.core.content.block.entity.TileCoFH;
import cofh.lib.api.block.IWrenchable;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

import javax.annotation.Nullable;
import java.util.function.Supplier;

import static cofh.lib.util.constants.BlockStatePropertiesCoFH.FACING_HORIZONTAL;

public class TileBlock4Way extends TileBlockCoFH implements IWrenchable {

    public TileBlock4Way(Properties builder, Class<? extends TileCoFH> tileClass, Supplier<BlockEntityType<? extends TileCoFH>> blockEntityType) {

        super(builder, tileClass, blockEntityType);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {

        super.createBlockStateDefinition(builder);
        builder.add(FACING_HORIZONTAL);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {

        return this.defaultBlockState().setValue(FACING_HORIZONTAL, context.getPlayer() != null ? context.getPlayer().getMotionDirection().getOpposite() : Direction.NORTH);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {

        return state.setValue(FACING_HORIZONTAL, rot.rotate(state.getValue(FACING_HORIZONTAL)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirrorIn) {

        return state.rotate(mirrorIn.getRotation(state.getValue(FACING_HORIZONTAL)));
    }

}
