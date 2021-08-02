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

import static cofh.lib.util.constants.Constants.FACING_ALL;

public class DirectionalBlock6Way extends Block {

    public DirectionalBlock6Way(Properties properties) {

        super(properties);
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

        return state.with(FACING_ALL, Direction.byIndex(state.get(FACING_ALL).getIndex() + 1));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirrorIn) {

        return state.rotate(mirrorIn.toRotation(state.get(FACING_ALL)));
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {

        if (Utils.isWrench(player.getHeldItem(handIn).getItem())) {

            BlockState rotState = rotate(state, worldIn, pos, Rotation.CLOCKWISE_90);
            if (rotState != state) {
                worldIn.setBlockState(pos, rotState);
                return ActionResultType.SUCCESS;
            }
        }
        return ActionResultType.PASS;
    }

}
