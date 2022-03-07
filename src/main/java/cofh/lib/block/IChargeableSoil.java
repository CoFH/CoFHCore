package cofh.lib.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.extensions.IForgeBlock;

import static cofh.lib.util.constants.Constants.CHARGED;

public interface IChargeableSoil extends IForgeBlock {

    static void charge(BlockState state, Level worldIn, BlockPos pos) {

        int charge = state.getValue(CHARGED);
        if (charge < 4) {
            worldIn.setBlock(pos, state.setValue(CHARGED, charge + 1), 2);
        } else if (worldIn instanceof ServerLevel serverLevel) {
            state.getBlock().tick(state, serverLevel, pos, worldIn.random);
        }
    }

    // TODO: Generic Logic for reference; remove if necessary.
    //    static boolean canSustainPlant(BlockState state, IBlockReader world, BlockPos pos, Direction facing, IPlantable plantable, boolean tilled) {
    //
    //        if (plantable.getPlant(world, pos.offset(facing)).getBlock() instanceof AttachedStemBlock) {
    //            return true;
    //        }
    //        PlantType type = plantable.getPlantType(world, pos.up());
    //
    //        if (type == CROP) {
    //            return tilled;
    //        }
    //        if (type == CAVE || type == DESERT || type == PLAINS || type == FUNGUS) {
    //            return !tilled;
    //        }
    //        if (type == BEACH) {
    //            for (Direction direction : Direction.Plane.HORIZONTAL) {
    //                BlockPos qPos = pos.offset(direction);
    //                if (world.getFluidState(qPos).isTagged(FluidTags.WATER) || world.getBlockState(qPos).getBlock() == Blocks.FROSTED_ICE) {
    //                    return true;
    //                }
    //            }
    //        }
    //        //        if (plantable instanceof BushBlock && ((BushBlock) plantable).isValidGround(state, world, pos)) {
    //        //            return true;
    //        //        }
    //        return false;
    //    }

}
