package cofh.lib.block.impl.rails;

import cofh.lib.block.IDismantleable;
import cofh.lib.util.Utils;
import cofh.lib.util.helpers.MathHelper;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.RailShape;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

import static cofh.lib.util.constants.Constants.RAIL_STRAIGHT_FLAT;

public class CrossoverRailBlock extends AbstractRailBlock implements IDismantleable {

    protected float maxSpeed = 0.4F;

    public CrossoverRailBlock(Properties builder) {

        super(true, builder);
        this.setDefaultState(this.stateContainer.getBaseState().with(getShapeProperty(), RailShape.NORTH_SOUTH));
    }

    public CrossoverRailBlock speed(float maxSpeed) {

        this.maxSpeed = MathHelper.clamp(maxSpeed, 0F, 1F);
        return this;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {

        builder.add(getShapeProperty());
    }

    @Override
    public float getRailMaxSpeed(BlockState state, World world, BlockPos pos, AbstractMinecartEntity cart) {

        return maxSpeed;
    }

    @Override
    public Property<RailShape> getShapeProperty() {

        return RAIL_STRAIGHT_FLAT;
    }

    @Override
    public boolean canMakeSlopes(BlockState state, IBlockReader world, BlockPos pos) {

        return false;
    }

    @Override
    public RailShape getRailDirection(BlockState state, IBlockReader world, BlockPos pos, @Nullable AbstractMinecartEntity cart) {

        if (cart != null) {
            double absX = Math.abs(cart.getMotion().x);
            double absZ = Math.abs(cart.getMotion().z);
            if (absX > absZ) {
                return RailShape.EAST_WEST;
            } else if (absZ > absX) {
                return RailShape.NORTH_SOUTH;
            }
        }
        return state.get(getShapeProperty());
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {

        return state;
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirrorIn) {

        return state;
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
