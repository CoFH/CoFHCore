package cofh.core.effect;

import cofh.lib.effect.EffectCoFH;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class WrenchedEffect extends EffectCoFH {

    public WrenchedEffect(MobEffectCategory typeIn, int liquidColorIn) {

        super(typeIn, liquidColorIn);
    }

    @Override
    public void applyEffectTick(LivingEntity entityLivingBaseIn, int amplifier) {

        entityLivingBaseIn.setYRot(entityLivingBaseIn.getYRot() + 2 * (1 + amplifier));
    }

}
