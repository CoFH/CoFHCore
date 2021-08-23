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
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {

        super.createBlockStateDefinition(builder);
        builder.add(FACING_ALL);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {

        return this.defaultBlockState().setValue(FACING_ALL, context.getNearestLookingDirection().getOpposite());
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {

        return state.setValue(FACING_ALL, Direction.from3DDataValue(state.getValue(FACING_ALL).get3DDataValue() + 1));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirrorIn) {

        return state.rotate(mirrorIn.getRotation(state.getValue(FACING_ALL)));
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
