package cofh.core.init;

import cofh.core.client.settings.KeyBindingModeChange;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class CoreKeys {

    private CoreKeys() {

    }

    public static void register() {

        ClientRegistry.registerKeyBinding(MULTIMODE_INCREMENT);
        // ClientRegistry.registerKeyBinding(MULTIMODE_DECREMENT);
    }

    public static final KeyBinding MULTIMODE_INCREMENT = new KeyBindingModeChange.Increment("key.cofh.mode_change_increment", 86, "CoFH");
    // public static final KeyBinding MULTIMODE_DECREMENT = new KeyBindingModeChange.Decrement("key.cofh.mode_change_decrement", 66, "CoFH");

}
