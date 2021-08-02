package cofh.lib.block.impl;

import net.minecraft.block.AttachedStemBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.state.StateContainer;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.PlantType;

import java.util.Random;

import static cofh.lib.util.constants.Constants.CHARGED;
import static cofh.lib.util.constants.Constants.FUNGUS;
import static net.minecraftforge.common.PlantType.*;

public class SoilBlock extends Block {

    protected static final VoxelShape SHAPE_TILLED = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 15.0D, 16.0D);

    public SoilBlock(Properties properties) {

        super(properties);
        this.setDefaultState(this.stateContainer.getBaseState().with(CHARGED, 0));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {

        super.fillStateContainer(builder);
        builder.add(CHARGED);
    }

    @Override
    public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {

        BlockPos abovePos = pos.up();
        BlockState aboveState = worldIn.getBlockState(abovePos);

        if (aboveState.getBlock() instanceof IPlantable && aboveState.ticksRandomly()) {
            int charge = state.get(CHARGED);
            int boost = 1 + charge;
            for (int i = 0; i < boost; ++i) {
                aboveState.randomTick(worldIn, abovePos, rand);
            }
            if (rand.nextInt(boost) > 0) {
                worldIn.setBlockState(pos, state.with(CHARGED, Math.max(0, charge - 1)), 2);
            }
        }
    }

    public static void charge(BlockState state, World worldIn, BlockPos pos) {

        int charge = state.get(CHARGED);
        if (charge < 4) {
            worldIn.setBlockState(pos, state.with(CHARGED, charge + 1), 2);
        } else if (worldIn instanceof ServerWorld) {
            state.getBlock().tick(state, (ServerWorld) worldIn, pos, worldIn.rand);
        }
    }

    @Override
    public boolean canSustainPlant(BlockState state, IBlockReader world, BlockPos pos, Direction facing, IPlantable plantable) {

        return canSustainPlant(state, world, pos, facing, plantable, false);
    }

    protected boolean canSustainPlant(BlockState state, IBlockReader world, BlockPos pos, Direction facing, IPlantable plantable, boolean tilled) {

        if (plantable.getPlant(world, pos.offset(facing)).getBlock() instanceof AttachedStemBlock) {
            return true;
        }
        PlantType type = plantable.getPlantType(world, pos.up());

        if (type == CROP) {
            return tilled;
        }
        if (type == CAVE || type == DESERT || type == PLAINS || type == FUNGUS) {
            return !tilled;
        }
        if (type == BEACH) {
            for (Direction direction : Direction.Plane.HORIZONTAL) {
                BlockPos qPos = pos.offset(direction);
                if (world.getFluidState(qPos).isTagged(FluidTags.WATER) || world.getBlockState(qPos).getBlock() == Blocks.FROSTED_ICE) {
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
    public boolean isFertile(BlockState state, IBlockReader world, BlockPos pos) {

        return true;
    }

    @OnlyIn(Dist.CLIENT)
    public boolean isViewBlocking(BlockState state, IBlockReader worldIn, BlockPos pos) {

        return true;
    }

}
