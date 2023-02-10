package cofh.core.mixin;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static cofh.core.init.CoreMobEffects.*;

/**
 * @author King Lemming
 */
@Mixin (LivingEntity.class)
public class LivingEntityMixin {

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

}
