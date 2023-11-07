package cofh.core.common.block;

import cofh.lib.api.block.IWrenchable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.HitResult;

import javax.annotation.Nullable;
import java.util.function.Supplier;

import static cofh.lib.util.constants.BlockStatePropertiesCoFH.ACTIVE;
import static cofh.lib.util.constants.BlockStatePropertiesCoFH.FACING_ALL;

public class EntityBlockActive6Way extends EntityBlockActive implements IWrenchable {

    public EntityBlockActive6Way(Properties builder, Class<?> tileClass, Supplier<BlockEntityType<?>> blockEntityType) {

        super(builder, tileClass, blockEntityType);
        this.registerDefaultState(this.stateDefinition.any().setValue(ACTIVE, false).setValue(FACING_ALL, Direction.UP));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {

        super.createBlockStateDefinition(builder);
        builder.add(FACING_ALL);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {

        return this.defaultBlockState().setValue(FACING_ALL, context.getNearestLookingDirection().getOpposite());
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {

        return state.setValue(FACING_ALL, rot.rotate(state.getValue(FACING_ALL)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirrorIn) {

        return state.rotate(mirrorIn.getRotation(state.getValue(FACING_ALL)));
    }

    // region IWrenchable
    @Override
    public void wrenchBlock(Level world, BlockPos pos, BlockState state, HitResult target, Player player) {

        BlockState rotState = state.setValue(FACING_ALL, Direction.from3DDataValue(state.getValue(FACING_ALL).get3DDataValue() + 1));
        if (rotState != state) {
            world.setBlockAndUpdate(pos, rotState);
        }
    }
    // endregion
}
