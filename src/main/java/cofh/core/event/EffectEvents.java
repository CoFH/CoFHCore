package cofh.core.event;

import cofh.core.network.packet.client.EffectAddedPacket;
import cofh.core.network.packet.client.EffectRemovedPacket;
import cofh.lib.effect.CustomParticleEffect;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraftforge.event.entity.EntityStruckByLightningEvent;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.PotionColorCalculationEvent;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.event.entity.player.PlayerXpEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static cofh.core.init.CoreMobEffects.*;
import static cofh.lib.util.constants.Constants.ID_COFH_CORE;

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
    public static void handleLivingAttackEvent(LivingAttackEvent event) {

        if (event.isCanceled()) {
            return;
        }
        LivingEntity entity = event.getEntityLiving();
        DamageSource source = event.getSource();
        if (source.isBypassMagic()) {
            return;
        }
        if (source.isExplosion() && entity.hasEffect(EXPLOSION_RESISTANCE.get())) {
            event.setCanceled(true);
        } else if (source.isMagic() && entity.hasEffect(MAGIC_RESISTANCE.get())) {
            event.setCanceled(true);
        } else if (source == DamageSource.LIGHTNING_BOLT && entity.hasEffect(LIGHTNING_RESISTANCE.get())) {
            event.setCanceled(true);
        } else if (source.isFire()) {
            entity.removeEffect(CHILLED.get());
        }
    }

    @SubscribeEvent (priority = EventPriority.HIGH)
    public static void handlePotionColorEvent(PotionColorCalculationEvent event) {

        Collection<MobEffectInstance> effects = event.getEffects();
        if (effects.isEmpty()) {
            return;
        }
        Predicate<MobEffectInstance> hasCustomParticle = effect -> effect.getEffect() instanceof CustomParticleEffect;
        if (effects.stream().anyMatch(hasCustomParticle)) {
            List<MobEffectInstance> nonCustom = effects.stream().filter(hasCustomParticle.negate()).collect(Collectors.toList());
            if (nonCustom.isEmpty()) {
                event.shouldHideParticles(true);
            } else {
                event.setColor(PotionUtils.getColor(nonCustom));
            }
        }
    }

    @SubscribeEvent (priority = EventPriority.HIGH)
    public static void handlePotionAddEvent(PotionEvent.PotionAddedEvent event) {

        if (event.getPotionEffect().getEffect() instanceof CustomParticleEffect) {
            EffectAddedPacket.sendToClient(event.getEntityLiving(), event.getPotionEffect());
        }
    }

    @SubscribeEvent (priority = EventPriority.HIGH)
    public static void handlePotionRemoveEvent(PotionEvent.PotionRemoveEvent event) {

        MobEffectInstance effect = event.getPotionEffect();
        if (effect != null && effect.getEffect() instanceof CustomParticleEffect) {
            EffectRemovedPacket.sendToClient(event.getEntityLiving(), event.getPotionEffect());
        }
    }

    @SubscribeEvent (priority = EventPriority.HIGH)
    public static void handleXpChangeEvent(PlayerXpEvent.XpChange event) {

        if (event.isCanceled() || event.getAmount() <= 0) {
            return;
        }
        Player player = event.getPlayer();

        MobEffectInstance clarityEffect = player.getEffect(CLARITY.get());
        if (clarityEffect == null) {
            return;
        }
        event.setAmount(getXPValue(event.getAmount(), clarityEffect.getAmplifier()));
    }

    // region HELPERS
    private static int getXPValue(int baseExp, int amplifier) {

        return baseExp * (100 + CLARITY_MOD * (1 + amplifier)) / 100;
    }
    // endregion

    private static final int CLARITY_MOD = 20;

}
