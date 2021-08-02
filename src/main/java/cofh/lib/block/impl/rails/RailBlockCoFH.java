package cofh.lib.block.impl.rails;

import cofh.lib.block.IDismantleable;
import cofh.lib.block.IWrenchable;
import cofh.lib.util.Utils;
import cofh.lib.util.helpers.MathHelper;
import net.minecraft.block.BlockState;
import net.minecraft.block.RailBlock;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;

public class RailBlockCoFH extends RailBlock implements IDismantleable, IWrenchable {

    protected float maxSpeed = 0.4F;

    public RailBlockCoFH(Properties builder) {

        super(builder);
    }

    public RailBlockCoFH speed(float maxSpeed) {

        this.maxSpeed = MathHelper.clamp(maxSpeed, 0F, 1F);
        return this;
    }

    @Override
    public float getRailMaxSpeed(BlockState state, World world, BlockPos pos, AbstractMinecartEntity cart) {

        return maxSpeed;
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {

        if (Utils.isWrench(player.getHeldItem(handIn).getItem())) {
            if (player.isSecondaryUseActive()) {
                if (canDismantle(worldIn, pos, state, player)) {
                    dismantleBlock(worldIn, pos, state, hit, player, false);
                    return ActionResultType.SUCCESS;
                }
            } else {
                BlockState rotState = rotate(state, worldIn, pos, Rotation.CLOCKWISE_90);
                if (rotState != state) {
                    worldIn.setBlockState(pos, rotState);
                    return ActionResultType.SUCCESS;
                }
            }
        }
        return ActionResultType.PASS;
    }

}
