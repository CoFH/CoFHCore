package cofh.lib.common.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;

/**
 * Why does this exist? I'm glad you asked.
 * <p>
 * One word: Protected. As in the constructor. Because of course it is.
 *
 * @author King Lemming
 */
public class MobEffectCoFH extends MobEffect {

    public MobEffectCoFH(MobEffectCategory type, int liquidColor) {

        super(type, liquidColor);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {

        return duration > 0;
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {

    }

    @Override
    public void applyInstantenousEffect(@Nullable Entity source, @Nullable Entity indirectSource, LivingEntity target, int amplifier, double multiplier) {

    }

    public void onApply(LivingEntity entity, MobEffectInstance instance) {

    }

    public void onTrack(LivingEntity entity, MobEffectInstance instance, Player tracker) {

    }

    public void onRemove(LivingEntity entity, MobEffectInstance instance) {

    }

    public void onExpire(LivingEntity entity, MobEffectInstance instance) {

    }

}
