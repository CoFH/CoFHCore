package cofh.core.block;

import cofh.core.tileentity.SignalAirTile;
import cofh.lib.util.Utils;
import net.minecraft.block.AirBlock;
import net.minecraft.block.BlockState;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;

public class SignalAirBlock extends AirBlock {

    public SignalAirBlock(Properties builder) {

        super(builder);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {

        return true;
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {

        return new SignalAirTile();
    }

    @Override
    public boolean canProvidePower(BlockState state) {

        return true;
    }

    @Override
    public int getStrongPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {

        // return side == Direction.DOWN ? blockState.getWeakPower(blockAccess, pos, side) : 0;
        return blockState.getWeakPower(blockAccess, pos, side);
    }

    @Override
    public int getWeakPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {

        TileEntity tile = blockAccess.getTileEntity(pos);
        return tile instanceof SignalAirTile ? ((SignalAirTile) tile).getPower() : 0;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {

        if (rand.nextInt(8) == 0) {
            Utils.spawnBlockParticlesClient(worldIn, RedstoneParticleData.REDSTONE_DUST, pos, rand, 2);
        }
    }

}
