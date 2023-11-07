package cofh.core.common.capability;

import cofh.lib.common.energy.IRedstoneFluxStorage;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;

public class CapabilityRedstoneFlux {

    public static final Capability<IRedstoneFluxStorage> RF_ENERGY = CapabilityManager.get(new CapabilityToken<>() {});

    public static void register(RegisterCapabilitiesEvent event) {

        event.register(IRedstoneFluxStorage.class);
    }

}
