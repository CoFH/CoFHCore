package cofh.lib.block.impl;

import cofh.lib.block.IDismantleable;
import cofh.lib.util.Utils;
import net.minecraft.block.BlockState;
import net.minecraft.block.GlassBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class HardenedGlassBlock extends GlassBlock implements IDismantleable {

    public HardenedGlassBlock(Properties properties) {

        super(properties);
    }

    @Override
    public boolean canEntityDestroy(BlockState state, IBlockReader world, BlockPos pos, Entity entity) {

        return false;
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {

        if (Utils.isWrench(player.getHeldItem(handIn).getItem())) {
            if (player.isSecondaryUseActive()) {
                if (canDismantle(worldIn, pos, state, player)) {
                    dismantleBlock(worldIn, pos, state, hit, player, false);
                    return ActionResultType.SUCCESS;
                }
            }
        }
        return ActionResultType.PASS;
    }

}
