package cofh.lib.util.helpers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;

/**
 * Contains various helper functions to assist with Sound manipulation.
 *
 * @author King Lemming
 */
public class SoundHelper {

    private SoundHelper() {

    }

    private static SoundManager manager() {

        return Minecraft.getInstance().getSoundManager();
    }

    /**
     * This allows you to have some tricky functionality with Tile Entities. Just be sure you aren't dumb.
     */
    public static void playSound(Object sound) {

        if (sound instanceof SoundInstance) {
            manager().play((SoundInstance) sound);
        }
    }

    public static void playSound(SoundInstance sound) {

        manager().play(sound);
    }

    public static void playClickSound(float pitch) {

        playClickSound(0.3F, pitch);
    }

    public static void playClickSound(float volume, float pitch) {

        playSimpleSound(SoundEvents.UI_BUTTON_CLICK, volume, pitch);
    }

    public static void playSimpleSound(SoundEvent sound, float volume, float pitch) {

        manager().play(SimpleSoundInstance.forUI(sound, pitch, volume));
    }

}
