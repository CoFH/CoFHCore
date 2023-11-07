package cofh.core.common.effect;

import cofh.lib.common.effect.CustomParticleMobEffect;
import cofh.lib.util.helpers.MathHelper;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.random.RandomGenerator;

import static cofh.core.init.CoreMobEffects.CHILLED;
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

        return FROST.get();
    }

    /**
     * Applies Chilled to a target.
     * If the target already has Chilled, there is a chance based on the current duration that the amplifier
     * will be increased by one and duration reset to 8 * power ticks.
     * Otherwise, 8 * power ticks are added to the duration, up to 250 ticks.
     */
    public static void applyChilled(LivingEntity target, float power, RandomGenerator rand) {

        int add = MathHelper.round(power * 8);
        MobEffectInstance instance = target.getEffect(CHILLED.get());
        if (instance == null) {
            target.addEffect(new MobEffectInstance(CHILLED.get(), add, 0, true, false, true));
        } else {
            int duration = instance.getDuration();
            int amplifier = instance.getAmplifier();
            if (amplifier < 5 && rand.nextFloat(250) < duration) {
                target.addEffect(new MobEffectInstance(CHILLED.get(), add, amplifier + 1, true, false, true));
            } else if (duration < 250) {
                target.addEffect(new MobEffectInstance(CHILLED.get(), duration + add, amplifier, true, false, true));
            }
        }
    }

    public static void applyChilled(Entity target, float power, RandomGenerator rand) {

        if (target instanceof LivingEntity living) {
            applyChilled(living, power, rand);
        }
    }

}
