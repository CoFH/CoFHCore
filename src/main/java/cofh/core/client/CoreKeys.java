package cofh.core.client;

import cofh.core.client.settings.KeyBindingModeChange;
import net.minecraft.client.KeyMapping;

public class CoreKeys {

    private CoreKeys() {

    }

    public static final KeyMapping MULTIMODE_INCREMENT = new KeyBindingModeChange.Increment("key.cofh.mode_change_increment", 86, "CoFH");
    public static final KeyMapping MULTIMODE_DECREMENT = new KeyBindingModeChange.Decrement("key.cofh.mode_change_decrement", 66, "CoFH");

}
