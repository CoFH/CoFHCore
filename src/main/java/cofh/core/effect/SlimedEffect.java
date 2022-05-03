package cofh.core.effect;

import cofh.lib.effect.CustomParticleEffect;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffectCategory;

public class SlimedEffect extends CustomParticleEffect {

    public SlimedEffect(MobEffectCategory typeIn, int liquidColorIn) {

        super(typeIn, liquidColorIn);
    }

    @Override
    public ParticleOptions getParticle() {

        return ParticleTypes.ITEM_SLIME;
    }

}
