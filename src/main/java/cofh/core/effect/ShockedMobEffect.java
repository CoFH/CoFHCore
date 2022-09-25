package cofh.core.effect;

import cofh.lib.effect.CustomParticleMobEffect;
import cofh.lib.util.references.CoreReferences;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.effect.MobEffectCategory;

public class ShockedMobEffect extends CustomParticleMobEffect {

    public ShockedMobEffect(MobEffectCategory typeIn, int liquidColorIn) {

        super(typeIn, liquidColorIn);
    }

    @Override
    public ParticleOptions getParticle() {

        return CoreReferences.SPARK_PARTICLE;
    }

}
