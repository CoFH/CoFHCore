package cofh.lib.block;

import cofh.lib.api.block.IDismantleable;
import cofh.lib.util.Utils;
import cofh.lib.util.helpers.MathHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;

import static cofh.lib.util.constants.BlockStatePropertiesCoFH.RAIL_STRAIGHT_FLAT;

public class CrossoverRailBlock extends BaseRailBlock implements IDismantleable {

    protected float maxSpeed = 0.4F;

    public CrossoverRailBlock(Properties builder) {

        super(true, builder);
        this.registerDefaultState(this.stateDefinition.any().setValue(getShapeProperty(), RailShape.NORTH_SOUTH).setValue(WATERLOGGED, Boolean.valueOf(false)));
    }

    public CrossoverRailBlock speed(float maxSpeed) {

        this.maxSpeed = MathHelper.clamp(maxSpeed, 0F, 1F);
        return this;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {

        builder.add(getShapeProperty(), WATERLOGGED);
    }

    @Override
    public float getRailMaxSpeed(BlockState state, Level world, BlockPos pos, AbstractMinecart cart) {

        return maxSpeed;
    }

    @Override
    public Property<RailShape> getShapeProperty() {

        return RAIL_STRAIGHT_FLAT;
    }

    @Override
    public boolean canMakeSlopes(BlockState state, BlockGetter world, BlockPos pos) {

        return false;
    }

    @Override
    public RailShape getRailDirection(BlockState state, BlockGetter world, BlockPos pos, @Nullable AbstractMinecart cart) {

        if (cart != null) {
            double absX = Math.abs(cart.getDeltaMovement().x);
            double absZ = Math.abs(cart.getDeltaMovement().z);
            if (absX > absZ) {
                return RailShape.EAST_WEST;
            } else if (absZ > absX) {
                return RailShape.NORTH_SOUTH;
            }
        }
        return state.getValue(getShapeProperty());
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
