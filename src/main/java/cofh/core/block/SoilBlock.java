package cofh.core.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.AttachedStemBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.PlantType;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;

import java.util.function.Supplier;

import static cofh.lib.util.Constants.FUNGUS;
import static net.minecraftforge.common.PlantType.*;

public class SoilBlock extends Block {

    protected static final VoxelShape SHAPE_TILLED = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 15.0D, 16.0D);

    protected Supplier<Block> otherBlock = () -> Blocks.DIRT;

    public SoilBlock(Properties properties) {

        super(properties);
    }

    public SoilBlock otherBlock(Supplier<Block> dirt) {

        this.otherBlock = dirt;
        return this;
    }

    @Override
    public boolean canSustainPlant(BlockState state, BlockGetter world, BlockPos pos, Direction facing, IPlantable plantable) {

        return canSustainPlant(state, world, pos, facing, plantable, false);
    }

    protected boolean canSustainPlant(BlockState state, BlockGetter world, BlockPos pos, Direction facing, IPlantable plantable, boolean tilled) {

        if (plantable.getPlant(world, pos.relative(facing)).getBlock() instanceof AttachedStemBlock) {
            return true;
        }
        PlantType type = plantable.getPlantType(world, pos.above());

        if (type == CROP) {
            return tilled;
        }
        if (type == CAVE || type == DESERT || type == PLAINS || type == FUNGUS) {
            return !tilled;
        }
        if (type == BEACH) {
            for (Direction direction : Direction.Plane.HORIZONTAL) {
                BlockPos qPos = pos.relative(direction);
                if (world.getFluidState(qPos).is(FluidTags.WATER) || world.getBlockState(qPos).getBlock() == Blocks.FROSTED_ICE) {
                    return true;
                }
            }
        }
        //        if (plantable instanceof BushBlock && ((BushBlock) plantable).isValidGround(state, world, pos)) {
        //            return true;
        //        }
        return false;
    }

    @Override
    public boolean isFertile(BlockState state, BlockGetter world, BlockPos pos) {

        return true;
    }

    @Override
    public BlockState getToolModifiedState(BlockState state, UseOnContext context, ToolAction toolAction, boolean simulate) {

        if (ToolActions.HOE_TILL == toolAction && context.getItemInHand().canPerformAction(ToolActions.HOE_TILL)) {
            if (context.getLevel().getBlockState(context.getClickedPos().above()).isAir()) {
                return otherBlock.get().defaultBlockState();
            }
        }
        return state;
    }

}
