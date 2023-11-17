package cofh.core.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import static cofh.core.init.CoreMobEffects.CHILLED;

/**
 * Hooks into mouse sensitivity.
 *
 * @author Hekera
 */
@Mixin (MouseHandler.class)
public abstract class MouseHandlerMixin {

    @ModifyVariable (
            method = "turnPlayer",
            ordinal = 3,
            at = @At (
                    value = "STORE"
            )
    )
    private double adjustSensitivity(double sensitivity) {

        Player player = Minecraft.getInstance().player;
        if (player == null) {
            return sensitivity;
        }
        MobEffectInstance effect = player.getEffect(CHILLED.get());
        if (effect == null) {
            return sensitivity;
        }
        int amplifier = effect.getAmplifier();
        if (amplifier < 0) {
            return sensitivity;
        }
        float reduction = 15F / (16F + amplifier);
        reduction *= reduction;
        reduction *= reduction;
        return sensitivity * Math.max(reduction * reduction, 0.1F);
    }

}