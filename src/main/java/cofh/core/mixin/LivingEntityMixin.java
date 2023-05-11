package cofh.core.mixin;

import cofh.lib.api.capability.IShieldItem;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

import static cofh.core.capability.CapabilityShieldItem.SHIELD_ITEM_CAPABILITY;
import static cofh.core.init.CoreMobEffects.*;

/**
 * @author King Lemming and Hek
 */
@Mixin (LivingEntity.class)
public abstract class LivingEntityMixin {

    @Shadow protected abstract int increaseAirSupply(int p_21307_);

    @Shadow protected abstract void detectEquipmentUpdates();

    @Shadow public abstract ItemStack getUseItem();

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

        if (source.isFire()) {
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
        if (!source.isBypassMagic()) {
            if (source.isExplosion() && living.hasEffect(EXPLOSION_RESISTANCE.get())) {
                callback.setReturnValue(false);
            }
            if (source.isMagic() && living.hasEffect(MAGIC_RESISTANCE.get())) {
                callback.setReturnValue(false);
            }
            if (source == DamageSource.FREEZE && living.hasEffect(COLD_RESISTANCE.get())) {
                callback.setReturnValue(false);
            }
            if (source == DamageSource.LIGHTNING_BOLT && living.hasEffect(LIGHTNING_RESISTANCE.get())) {
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
