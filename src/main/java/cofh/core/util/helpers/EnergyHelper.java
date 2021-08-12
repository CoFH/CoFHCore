package cofh.core.util.helpers;

import cofh.lib.capability.CapabilityRedstoneFlux;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import static cofh.lib.util.constants.Constants.RF_PER_FURNACE_UNIT;

/**
 * This class contains helper functions related to Redstone Flux, aka the Forge Energy system.
 *
 * @author King Lemming
 */
public class EnergyHelper {

    public static boolean standaloneRedstoneFlux;

    private EnergyHelper() {

    }

    public static boolean validFurnaceFuel(ItemStack input) {

        return getEnergyFurnaceFuel(input) > 0;
    }

    public static int getEnergyFurnaceFuel(ItemStack stack) {

        return ForgeHooks.getBurnTime(stack) * RF_PER_FURNACE_UNIT;
    }

    public static boolean hasEnergyHandlerCap(ItemStack item) {

        return !item.isEmpty() && item.getCapability(getEnergySystem()).isPresent();
    }

    public static Capability<? extends IEnergyStorage> getEnergySystem() {

        return standaloneRedstoneFlux ? CapabilityRedstoneFlux.RF_ENERGY : CapabilityEnergy.ENERGY;
    }

}
