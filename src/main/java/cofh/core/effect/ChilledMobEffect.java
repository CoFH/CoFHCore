package cofh.core.effect;

import cofh.lib.effect.CustomParticleMobEffect;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import static cofh.core.init.CoreParticles.FROST;

public class ChilledMobEffect extends CustomParticleMobEffect {

    public ChilledMobEffect(MobEffectCategory typeIn, int liquidColorIn) {

        super(typeIn, liquidColorIn);
    }

    @Override
    public void applyEffectTick(LivingEntity living, int amplifier) {

        super.applyEffectTick(living, amplifier);

        if (living.canFreeze()) {
            living.isInPowderSnow = true;
            living.setTicksFrozen(Math.min(living.getTicksRequiredToFreeze(), living.getTicksFrozen() + 2 + amplifier * 2));
        }
    }

    @Override
    public double getAttributeModifierValue(int amplifier, AttributeModifier modifier) {

        return Math.max(-0.90D, super.getAttributeModifierValue(amplifier, modifier));
    }

    @Override
    public ParticleOptions getParticle() {

        return (SimpleParticleType) FROST.get();
    }

}
