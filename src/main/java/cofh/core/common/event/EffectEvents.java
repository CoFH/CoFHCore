package cofh.core.common.event;

import cofh.lib.common.effect.CustomParticleMobEffect;
import cofh.lib.common.effect.MobEffectCoFH;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.EntityStruckByLightningEvent;
import net.neoforged.neoforge.event.entity.EntityTeleportEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.entity.living.PotionColorCalculationEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerXpEvent;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

import static cofh.core.init.CoreMobEffects.*;
import static cofh.lib.util.constants.ModIds.ID_COFH_CORE;

@Mod.EventBusSubscriber (modid = ID_COFH_CORE)
public class EffectEvents {

    private EffectEvents() {

    }

    @SubscribeEvent (priority = EventPriority.HIGH)
    public static void handleChorusFruitTeleportEvent(EntityTeleportEvent.ChorusFruit event) {

        if (event.isCanceled()) {
            return;
        }
        LivingEntity entity = event.getEntityLiving();
        if (entity.hasEffect(ENDERFERENCE.get())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent (priority = EventPriority.HIGH)
    public static void handleEnderEntityTeleportEvent(EntityTeleportEvent.EnderEntity event) {

        if (event.isCanceled()) {
            return;
        }
        LivingEntity entity = event.getEntityLiving();
        if (entity.hasEffect(ENDERFERENCE.get())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent (priority = EventPriority.HIGH)
    public static void handleEnderPearlTeleportEvent(EntityTeleportEvent.EnderPearl event) {

        if (event.isCanceled()) {
            return;
        }
        LivingEntity entity = event.getPlayer();
        if (entity.hasEffect(ENDERFERENCE.get())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent (priority = EventPriority.HIGH)
    public static void handleEntityStruckByLightningEvent(EntityStruckByLightningEvent event) {

        if (event.isCanceled()) {
            return;
        }
        Entity entity = event.getEntity();
        if (entity instanceof LivingEntity living) {
            if (living.hasEffect(LIGHTNING_RESISTANCE.get())) {
                event.setCanceled(true);
            } else {
                living.addEffect(new MobEffectInstance(SHOCKED.get(), 100, 0));
            }
        }
    }

    @SubscribeEvent (priority = EventPriority.HIGH)
    public static void handlePotionColorEvent(PotionColorCalculationEvent event) {

        Collection<MobEffectInstance> effects = event.getEffects();
        if (effects.isEmpty()) {
            return;
        }
        Predicate<MobEffectInstance> hasCustomParticle = effect -> effect.getEffect() instanceof CustomParticleMobEffect;
        if (effects.stream().anyMatch(hasCustomParticle)) {
            List<MobEffectInstance> nonCustom = effects.stream().filter(hasCustomParticle.negate()).toList();
            if (nonCustom.isEmpty()) {
                event.shouldHideParticles(true);
            } else {
                event.setColor(PotionUtils.getColor(nonCustom));
            }
        }
    }

    @SubscribeEvent (priority = EventPriority.LOWEST)
    public static void handlePotionAddEvent(MobEffectEvent.Added event) {

        if (event.getResult() == Event.Result.DENY) {
            return;
        }
        MobEffectInstance instance = event.getEffectInstance();
        if (instance.getEffect() instanceof MobEffectCoFH effect) {
            effect.onApply(event.getEntity(), instance);
        }
    }

    @SubscribeEvent (priority = EventPriority.LOWEST)
    public static void handlePotionTrackEvent(PlayerEvent.StartTracking event) {

        if (event.getResult() == Event.Result.DENY) {
            return;
        }
        if (event.getTarget() instanceof LivingEntity entity) {
            for (MobEffectInstance instance : entity.getActiveEffects()) {
                if (instance.getEffect() instanceof MobEffectCoFH effect) {
                    effect.onTrack(entity, instance, event.getEntity());
                }
            }
        }
    }

    @SubscribeEvent (priority = EventPriority.LOWEST)
    public static void handlePotionRemoveEvent(MobEffectEvent.Remove event) {

        if (event.isCanceled()) {
            return;
        }
        MobEffectInstance instance = event.getEffectInstance();
        if (instance != null && instance.getEffect() instanceof MobEffectCoFH effect) {
            effect.onRemove(event.getEntity(), instance);
        }
    }

    @SubscribeEvent (priority = EventPriority.LOWEST)
    public static void handlePotionExpiredEvent(MobEffectEvent.Expired event) {

        if (event.isCanceled()) {
            return;
        }
        MobEffectInstance instance = event.getEffectInstance();
        if (instance != null && instance.getEffect() instanceof MobEffectCoFH effect) {
            effect.onExpire(event.getEntity(), instance);
        }
    }

    @SubscribeEvent (priority = EventPriority.HIGH)
    public static void handleTargetChangeEvent(LivingEvent.LivingVisibilityEvent event) {

        LivingEntity entity = event.getEntity();
        if (entity.hasEffect(TRUE_INVISIBILITY.get())) {
            float armor = Math.max(entity.getArmorCoverPercentage(), 0.1F) * 0.7F;
            event.modifyVisibility(0.07F / armor);
        }
    }

    @SubscribeEvent (priority = EventPriority.HIGH)
    public static void handleXpChangeEvent(PlayerXpEvent.XpChange event) {

        if (event.isCanceled() || event.getAmount() <= 0) {
            return;
        }
        Player player = event.getEntity();

        MobEffectInstance clarityEffect = player.getEffect(CLARITY.get());
        if (clarityEffect == null) {
            return;
        }
        event.setAmount(Math.max(event.getAmount(), getXPValue(event.getAmount(), clarityEffect.getAmplifier())));
    }

    // region HELPERS
    private static int getXPValue(int baseExp, int amplifier) {

        return baseExp * (100 + CLARITY_MOD * (1 + amplifier)) / 100;
    }
    // endregion

    private static final int CLARITY_MOD = 20;

}
