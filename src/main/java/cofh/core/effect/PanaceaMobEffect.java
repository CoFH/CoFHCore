package cofh.core.effect;

import cofh.lib.effect.MobEffectCoFH;
import cofh.lib.util.Utils;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.MobEffectEvent;

import javax.annotation.Nullable;
import java.util.Iterator;

public class PanaceaMobEffect extends MobEffectCoFH {

    public PanaceaMobEffect(MobEffectCategory typeIn, int liquidColorIn) {

        super(typeIn, liquidColorIn);
    }

    @Override
    public void applyEffectTick(LivingEntity entityLivingBaseIn, int amplifier) {

        clearHarmfulEffects(entityLivingBaseIn);
    }

    @Override
    public void applyInstantenousEffect(@Nullable Entity source, @Nullable Entity indirectSource, LivingEntity entityLivingBaseIn, int amplifier, double health) {

        clearHarmfulEffects(entityLivingBaseIn);
    }

    // region HELPERS
    public static void clearHarmfulEffects(LivingEntity entity) {

        if (Utils.isClientWorld(entity.level)) {
            return;
        }
        Iterator<MobEffectInstance> iterator = entity.getActiveEffectsMap().values().iterator();

        while (iterator.hasNext()) {
            MobEffectInstance effect = iterator.next();
            if (!effect.isAmbient() && effect.getEffect().getCategory() == MobEffectCategory.HARMFUL && !MinecraftForge.EVENT_BUS.post(new MobEffectEvent.Remove(entity, effect))) {
                entity.onEffectRemoved(effect);
                iterator.remove();
            }
        }
    }
    // endregion
}
