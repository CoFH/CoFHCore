package cofh.core.init;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

import static cofh.core.CoFHCore.SOUND_EVENTS;
import static cofh.lib.util.references.CoreIDs.ID_SOUND_ELECTRICITY;

public class CoreSounds {

    private CoreSounds() {

    }

    public static void register() {

        //registerSound(ID_SOUND_ELECTRICITY);
    }

    public static void registerSound(String soundID) {

        //SOUND_EVENTS.register(soundID, () -> new SoundEvent(new ResourceLocation(soundID)));
    }
}
