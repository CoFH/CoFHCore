package cofh.core.effect;

import cofh.lib.effect.MobEffectCoFH;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class WrenchedMobEffect extends MobEffectCoFH {

    public WrenchedMobEffect(MobEffectCategory typeIn, int liquidColorIn) {

        super(typeIn, liquidColorIn);
    }

    @Override
    public void applyEffectTick(LivingEntity entityLivingBaseIn, int amplifier) {

        entityLivingBaseIn.setYRot(entityLivingBaseIn.getYRot() + 2 * (1 + amplifier));
    }

}
