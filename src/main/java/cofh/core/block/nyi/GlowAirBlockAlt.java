package cofh.core.block.nyi;

import cofh.lib.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Random;

public class GlowAirBlockAlt extends SpecialAirBlock {

    public GlowAirBlockAlt(Properties builder) {

        super(builder);
    }

    @Override
    public void animateTick(BlockState stateIn, Level worldIn, BlockPos pos, Random rand) {

        if (rand.nextInt(16) == 0) {
            Utils.spawnBlockParticlesClient(worldIn, ParticleTypes.INSTANT_EFFECT, pos, rand, 2);
        }
    }

}
