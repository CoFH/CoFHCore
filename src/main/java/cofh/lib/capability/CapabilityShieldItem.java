package cofh.lib.capability;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;

public class CapabilityShieldItem {

    private static boolean registered = false;

    public static Capability<IShieldItem> SHIELD_ITEM_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});

    public static void register(RegisterCapabilitiesEvent event) {

        if (registered) {
            return;
        }
        registered = true;

        event.register(IShieldItem.class);
    }

}
