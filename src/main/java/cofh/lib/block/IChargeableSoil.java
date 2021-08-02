package cofh.lib.block;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.extensions.IForgeBlock;

import static cofh.lib.util.constants.Constants.CHARGED;

public interface IChargeableSoil extends IForgeBlock {

    static void charge(BlockState state, World worldIn, BlockPos pos) {

        int charge = state.get(CHARGED);
        if (charge < 4) {
            worldIn.setBlockState(pos, state.with(CHARGED, charge + 1), 2);
        } else if (worldIn instanceof ServerWorld) {
            state.getBlock().tick(state, (ServerWorld) worldIn, pos, worldIn.rand);
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