package cofh.core.util.helpers;

import cofh.lib.capability.CapabilityRedstoneFlux;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

/**
 * This class contains helper functions related to Redstone Flux, aka the Forge Energy system.
 *
 * @author King Lemming
 */
public class EnergyHelper {

    public static boolean standaloneRedstoneFlux;

    private EnergyHelper() {

    }

    public static boolean hasEnergyHandlerCap(ItemStack item) {

        return !item.isEmpty() && item.getCapability(getEnergySystem()).isPresent();
    }

    public static Capability<? extends IEnergyStorage> getEnergySystem() {

        return standaloneRedstoneFlux ? CapabilityRedstoneFlux.RF_ENERGY : CapabilityEnergy.ENERGY;
    }

}
