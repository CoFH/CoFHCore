package cofh.lib.common.block;

import cofh.lib.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;

import static cofh.lib.util.constants.BlockStatePropertiesCoFH.FACING_HORIZONTAL;

public class DirectionalBlock4Way extends Block {

    public DirectionalBlock4Way(Properties properties) {

        super(properties);
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

    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {

        if (Utils.isWrench(player.getItemInHand(handIn))) {

            BlockState rotState = rotate(state, worldIn, pos, Rotation.CLOCKWISE_90);
            if (rotState != state) {
                worldIn.setBlockAndUpdate(pos, rotState);
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

}
