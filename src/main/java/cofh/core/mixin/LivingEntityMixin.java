package cofh.core.mixin;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static cofh.core.common.capability.CapabilityShieldItem.SHIELD_ITEM_CAPABILITY;
import static cofh.core.init.CoreMobEffects.*;
import static cofh.lib.init.tags.DamageTypeTagsCoFH.IS_MAGIC;
import static net.minecraft.tags.DamageTypeTags.*;

/**
 * @author King Lemming and Hek
 */
@Mixin (LivingEntity.class)
public abstract class LivingEntityMixin {

    @Shadow
    public abstract ItemStack getUseItem();

    @Inject (
            method = "canBeAffected(Lnet/minecraft/world/effect/MobEffectInstance;)Z",
            at = @At ("HEAD"),
            cancellable = true
    )
    private void cancelEffectApplication(MobEffectInstance effectInstance, CallbackInfoReturnable<Boolean> callback) {

        LivingEntity living = (LivingEntity) (Object) this;

        if (effectInstance.getEffect() == CHILLED.get() && living.hasEffect(COLD_RESISTANCE.get())) {
            callback.setReturnValue(false);
        }
        if (effectInstance.getEffect() == SHOCKED.get() && living.hasEffect(LIGHTNING_RESISTANCE.get())) {
            callback.setReturnValue(false);
        }
    }

    @Inject (
            method = "canFreeze()Z",
            at = @At ("HEAD"),
            cancellable = true
    )
    private void cancelFreeze(CallbackInfoReturnable<Boolean> callback) {

        LivingEntity living = (LivingEntity) (Object) this;

        if (living.hasEffect(COLD_RESISTANCE.get())) {
            callback.setReturnValue(false);
        }
    }

    @Inject (
            method = "hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z",
            at = @At ("HEAD"),
            cancellable = true
    )
    private void cancelHurt(DamageSource source, float amount, CallbackInfoReturnable<Boolean> callback) {

        LivingEntity living = (LivingEntity) (Object) this;

        if (source.is(IS_FIRE)) {
            living.removeEffect(CHILLED.get());
            //MobEffectInstance instance = living.getEffect(CHILLED.get());
            //if (instance != null) {
            //    living.removeEffect(CHILLED.get());
            //    int amplifier = instance.getAmplifier() - 1;
            //    if (amplifier >= 0) {
            //        MobEffectInstance hidden = instance.hiddenEffect;
            //        int duration = instance.getDuration();
            //        while (hidden != null && hidden.getAmplifier() >= amplifier) {
            //            duration = Math.max(duration, hidden.getDuration());
            //            hidden = hidden.hiddenEffect;
            //        }
            //        living.addEffect(new MobEffectInstance(CHILLED.get(), duration, amplifier, instance.isAmbient(), instance.isVisible(), instance.showIcon(), hidden, instance.getFactorData()));
            //    }
            //}
        }
        if (!source.is(BYPASSES_ENCHANTMENTS)) {
            if (source.is(IS_EXPLOSION) && living.hasEffect(EXPLOSION_RESISTANCE.get())) {
                callback.setReturnValue(false);
            }
            if (source.is(IS_MAGIC) && living.hasEffect(MAGIC_RESISTANCE.get())) {
                callback.setReturnValue(false);
            }
            if (source == living.level.damageSources().freeze() && living.hasEffect(COLD_RESISTANCE.get())) {
                callback.setReturnValue(false);
            }
            if (source == living.level.damageSources().lightningBolt() && living.hasEffect(LIGHTNING_RESISTANCE.get())) {
                callback.setReturnValue(false);
            }
        }
    }

    @Inject (
            method = "updateInvisibilityStatus()V",
            at = @At ("HEAD"),
            cancellable = true
    )
    private void trueInvisibility(CallbackInfo callback) {

        LivingEntity living = (LivingEntity) (Object) this;
        if (living.hasEffect(TRUE_INVISIBILITY.get())) {
            living.removeEffectParticles();
            living.setInvisible(true);
            callback.cancel();
        }
    }

    @Inject (
            method = "isDamageSourceBlocked",
            at = @At ("HEAD"),
            cancellable = true
    )
    public void shield(DamageSource source, CallbackInfoReturnable<Boolean> cir) {

        this.getUseItem().getCapability(SHIELD_ITEM_CAPABILITY).ifPresent(shield -> {
            LivingEntity living = (LivingEntity) (Object) this;
            cir.setReturnValue(shield.canBlock(living, source));
            cir.cancel();
        });
    }

}
