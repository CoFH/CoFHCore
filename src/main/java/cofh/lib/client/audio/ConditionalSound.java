package cofh.lib.client.audio;

import net.minecraft.client.audio.TickableSound;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;

import java.util.function.BooleanSupplier;

import static cofh.lib.util.constants.Constants.FALSE;

public class ConditionalSound extends TickableSound {

    boolean beginFadeOut;
    boolean donePlaying;
    int ticks = 0;
    int fadeIn = 50;
    int fadeOut = 50;
    float baseVolume = 1.0F;

    BooleanSupplier condition = FALSE;

    public ConditionalSound(SoundEvent soundIn, SoundCategory categoryIn, TileEntity tile, BooleanSupplier condition) {

        super(soundIn, categoryIn);

        this.x = tile.getPos().getX() + 0.5D;
        this.y = tile.getPos().getY() + 0.5D;
        this.z = tile.getPos().getZ() + 0.5D;

        this.repeat = true;
        this.condition = condition;
    }

    public ConditionalSound(SoundEvent soundIn, SoundCategory categoryIn, Entity entity, BooleanSupplier condition) {

        super(soundIn, categoryIn);

        this.x = entity.getPosX();
        this.y = entity.getPosY();
        this.z = entity.getPosZ();

        this.repeat = true;
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
    public boolean isDonePlaying() {

        return donePlaying;
    }
    // endregion
}
