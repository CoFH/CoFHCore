package cofh.lib.client.audio;

import cofh.lib.util.helpers.MathHelper;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.function.BooleanSupplier;

import static cofh.lib.util.constants.Constants.FALSE;

public class ConditionalSound extends AbstractTickableSoundInstance {

    boolean beginFadeOut;
    boolean donePlaying;
    int ticks = 0;
    int fadeIn = 50;
    int fadeOut = 50;
    float baseVolume = 1.0F;

    BooleanSupplier condition = FALSE;

    public ConditionalSound(SoundEvent soundIn, SoundSource categoryIn, BlockEntity tile, BooleanSupplier condition) {

        super(soundIn, categoryIn, MathHelper.RANDOM);

        this.x = tile.getBlockPos().getX() + 0.5D;
        this.y = tile.getBlockPos().getY() + 0.5D;
        this.z = tile.getBlockPos().getZ() + 0.5D;

        this.looping = true;
        this.condition = condition;
    }

    public ConditionalSound(SoundEvent soundIn, SoundSource categoryIn, Entity entity, BooleanSupplier condition) {

        super(soundIn, categoryIn, MathHelper.RANDOM);

        this.x = entity.getX();
        this.y = entity.getY();
        this.z = entity.getZ();

        this.looping = true;
        this.condition = condition;
    }

    public ConditionalSound setFadeIn(int fadeIn) {

        this.fadeIn = Math.min(0, fadeIn);
        return this;
    }

    public ConditionalSound setFadeOut(int fadeOut) {

        this.fadeOut = Math.min(0, fadeOut);
        return this;
    }

    public float getFadeInMultiplier() {

        return ticks >= fadeIn ? 1 : ticks / (float) fadeIn;
    }

    public float getFadeOutMultiplier() {

        return ticks >= fadeOut ? 0 : (fadeOut - ticks) / (float) fadeOut;
    }

    // region ITickableSound
    @Override
    public void tick() {

        if (!beginFadeOut) {
            if (ticks < fadeIn) {
                ++ticks;
            }
            if (!condition.getAsBoolean()) {
                beginFadeOut = true;
                ticks = 0;
            }
        } else {
            ++ticks;
        }
        float multiplier = beginFadeOut ? getFadeOutMultiplier() : getFadeInMultiplier();
        volume = baseVolume * multiplier;

        if (multiplier <= 0) {
            donePlaying = true;
        }
    }

    @Override
    public boolean isStopped() {

        return donePlaying;
    }
    // endregion
}
