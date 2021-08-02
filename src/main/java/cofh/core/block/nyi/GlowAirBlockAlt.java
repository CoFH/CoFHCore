package cofh.core.block.nyi;

import cofh.lib.util.Utils;
import net.minecraft.block.BlockState;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;

public class GlowAirBlockAlt extends SpecialAirBlock {

    public GlowAirBlockAlt(Properties builder) {

        super(builder);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {

        if (rand.nextInt(16) == 0) {
            Utils.spawnBlockParticlesClient(worldIn, ParticleTypes.INSTANT_EFFECT, pos, rand, 2);
        }
    }

}
