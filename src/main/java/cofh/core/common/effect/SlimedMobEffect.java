package cofh.core.common.effect;

import cofh.lib.common.effect.CustomParticleMobEffect;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffectCategory;

public class SlimedMobEffect extends CustomParticleMobEffect {

    public SlimedMobEffect(MobEffectCategory typeIn, int liquidColorIn) {

        super(typeIn, liquidColorIn);
    }

    @Override
    public ParticleOptions getParticle() {

        return ParticleTypes.ITEM_SLIME;
    }

}
