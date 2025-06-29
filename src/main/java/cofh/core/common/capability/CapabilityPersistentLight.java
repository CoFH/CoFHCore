package cofh.core.common.capability;

import cofh.core.common.capability.templates.PersistentLights;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;

public class CapabilityPersistentLight {

    public static final Capability<PersistentLights> LIGHT_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
    });

    public static void register(RegisterCapabilitiesEvent event) {

        event.register(PersistentLights.class);
    }

}
