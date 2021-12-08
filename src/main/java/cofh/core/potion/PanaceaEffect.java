package cofh.core.potion;

import cofh.lib.potion.EffectCoFH;
import cofh.lib.util.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.PotionEvent;

import javax.annotation.Nullable;
import java.util.Iterator;

public class PanaceaEffect extends EffectCoFH {

    public PanaceaEffect(EffectType typeIn, int liquidColorIn) {

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
        Iterator<EffectInstance> iterator = entity.getActiveEffectsMap().values().iterator();

        while (iterator.hasNext()) {
            EffectInstance effect = iterator.next();
            if (!effect.isAmbient() && effect.getEffect().getCategory() == EffectType.HARMFUL && !MinecraftForge.EVENT_BUS.post(new PotionEvent.PotionRemoveEvent(entity, effect))) {
                entity.onEffectRemoved(effect);
                iterator.remove();
            }
        }
    }
    // endregion
}
