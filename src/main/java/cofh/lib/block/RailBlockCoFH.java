package cofh.lib.block;

import cofh.lib.api.block.IDismantleable;
import cofh.lib.api.block.IWrenchable;
import cofh.lib.util.Utils;
import cofh.lib.util.helpers.MathHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RailBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

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
    public float getRailMaxSpeed(BlockState state, Level world, BlockPos pos, AbstractMinecart cart) {

        return maxSpeed;
    }

    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {

        if (Utils.isWrench(player.getItemInHand(handIn))) {
            if (player.isSecondaryUseActive()) {
                if (canDismantle(worldIn, pos, state, player)) {
                    dismantleBlock(worldIn, pos, state, hit, player, false);
                    return InteractionResult.SUCCESS;
                }
            } else {
                BlockState rotState = rotate(state, worldIn, pos, Rotation.CLOCKWISE_90);
                if (rotState != state) {
                    worldIn.setBlockAndUpdate(pos, rotState);
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return InteractionResult.PASS;
    }

}
