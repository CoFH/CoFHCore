package cofh.core.effect;

import cofh.lib.effect.CustomParticleEffect;
import cofh.lib.util.references.CoreReferences;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.world.effect.MobEffectCategory;

public class ShockedEffect extends CustomParticleEffect {

    public ShockedEffect(MobEffectCategory typeIn, int liquidColorIn) {

        super(typeIn, liquidColorIn);
    }

    @Override
    public ParticleOptions getParticle() {

        return CoreReferences.SPARK_PARTICLE;
    }

}
