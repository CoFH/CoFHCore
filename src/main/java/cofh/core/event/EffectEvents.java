package cofh.core.event;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.EntityStruckByLightningEvent;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.player.PlayerXpEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static cofh.lib.util.constants.Constants.ID_COFH_CORE;
import static cofh.lib.util.references.CoreReferences.*;

@Mod.EventBusSubscriber(modid = ID_COFH_CORE)
public class EffectEvents {

    private EffectEvents() {

    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void handleEnderTeleportEvent(EnderTeleportEvent event) {

        if (event.isCanceled()) {
            return;
        }
        LivingEntity entity = event.getEntityLiving();
        if (entity.isPotionActive(ENDERFERENCE)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void handleEntityStruckByLightningEvent(EntityStruckByLightningEvent event) {

        if (event.isCanceled()) {
            return;
        }
        Entity entity = event.getEntity();
        if (entity instanceof LivingEntity && ((LivingEntity) entity).isPotionActive(LIGHTNING_RESISTANCE)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void handleLivingAttackEvent(LivingAttackEvent event) {

        if (event.isCanceled()) {
            return;
        }
        LivingEntity entity = event.getEntityLiving();
        DamageSource source = event.getSource();
        if (source.isDamageAbsolute()) {
            return;
        }
        if (source.isExplosion() && entity.isPotionActive(EXPLOSION_RESISTANCE)) {
            event.setCanceled(true);
        } else if (source.isMagicDamage() && entity.isPotionActive(MAGIC_RESISTANCE)) {
            event.setCanceled(true);
        } else if (source == DamageSource.LIGHTNING_BOLT && entity.isPotionActive(LIGHTNING_RESISTANCE)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void handleXpChangeEvent(PlayerXpEvent.XpChange event) {

        if (event.isCanceled() || event.getAmount() <= 0) {
            return;
        }
        PlayerEntity player = event.getPlayer();

        EffectInstance clarityEffect = player.getActivePotionEffect(CLARITY);
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

    private static final int CLARITY_MOD = 40;

}
