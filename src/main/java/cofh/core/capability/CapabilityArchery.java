package cofh.core.capability;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;

public class CapabilityArchery {

    public static final Capability<IArcheryBowItem> BOW_ITEM_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});

    public static final Capability<IArcheryAmmoItem> AMMO_ITEM_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});

    public static void register(RegisterCapabilitiesEvent event) {

        event.register(IArcheryBowItem.class);
        event.register(IArcheryAmmoItem.class);
    }

}
