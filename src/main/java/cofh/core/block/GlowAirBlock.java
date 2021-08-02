package cofh.core.block;

import cofh.core.tileentity.GlowAirTile;
import cofh.lib.util.Utils;
import net.minecraft.block.AirBlock;
import net.minecraft.block.BlockState;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;

public class GlowAirBlock extends AirBlock {

    public GlowAirBlock(Properties builder) {

        super(builder);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {

        return true;
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {

        return new GlowAirTile();
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {

        if (rand.nextInt(16) == 0) {
            Utils.spawnBlockParticlesClient(worldIn, ParticleTypes.INSTANT_EFFECT, pos, rand, 2);
        }
    }

}
