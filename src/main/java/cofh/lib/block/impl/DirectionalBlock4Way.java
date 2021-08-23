package cofh.lib.block.impl;

import cofh.lib.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;

import javax.annotation.Nullable;

import static cofh.lib.util.constants.Constants.FACING_HORIZONTAL;

public class DirectionalBlock4Way extends Block {

    public DirectionalBlock4Way(Properties properties) {

        super(properties);
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {

        super.createBlockStateDefinition(builder);
        builder.add(FACING_HORIZONTAL);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {

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
    public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {

        if (Utils.isWrench(player.getItemInHand(handIn).getItem())) {

            BlockState rotState = rotate(state, worldIn, pos, Rotation.CLOCKWISE_90);
            if (rotState != state) {
                worldIn.setBlockAndUpdate(pos, rotState);
                return ActionResultType.SUCCESS;
            }
        }
        return ActionResultType.PASS;
    }

}
