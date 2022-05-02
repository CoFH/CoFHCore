package cofh.lib.capability;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;

public class CapabilityArchery {

    private static boolean registered = false;

    public static Capability<IArcheryBowItem> BOW_ITEM_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});

    public static Capability<IArcheryAmmoItem> AMMO_ITEM_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});

    public static void register(RegisterCapabilitiesEvent event) {

        if (registered) {
            return;
        }
        registered = true;
        event.register(IArcheryBowItem.class);
        event.register(IArcheryAmmoItem.class);
    }

}
