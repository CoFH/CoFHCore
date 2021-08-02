package cofh.core.potion;

import cofh.lib.potion.EffectCoFH;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectType;

public class WrenchedEffect extends EffectCoFH {

    public WrenchedEffect(EffectType typeIn, int liquidColorIn) {

        super(typeIn, liquidColorIn);
    }

    @Override
    public void performEffect(LivingEntity entityLivingBaseIn, int amplifier) {

        entityLivingBaseIn.rotationYaw += 2 * (1 + amplifier);
    }

}
