package cofh.lib.capability;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;

public class CapabilityAreaEffect {

    private static boolean registered = false;

    public static Capability<IAreaEffect> AREA_EFFECT_ITEM_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() { });

    public static void register(RegisterCapabilitiesEvent event) {

        if (registered) {
            return;
        }
        registered = true;

        event.register(IAreaEffect.class);
    }

}
