package cofh.lib.common.energy;

import cofh.lib.api.item.IEnergyContainerItem;
import net.minecraft.world.item.ItemStack;

/**
 * This class provides a simple way to wrap an IEnergyContainerItem to allow for capability support.
 *
 * @author King Lemming
 */
public class EnergyContainerItemWrapper implements IRedstoneFluxStorage {

    protected final ItemStack container;
    protected final IEnergyContainerItem item;

    public EnergyContainerItemWrapper(ItemStack containerIn, IEnergyContainerItem itemIn) {

        this.container = containerIn;
        this.item = itemIn;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {

        if (!canReceive()) {
            return 0;
        }
        return item.receiveEnergy(container, maxReceive, simulate);
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {

        if (!canExtract()) {
            return 0;
        }
        return item.extractEnergy(container, maxExtract, simulate);
    }

    @Override
    public int getEnergyStored() {

        return item.getEnergyStored(container);
    }

    @Override
    public int getMaxEnergyStored() {

        return item.getMaxEnergyStored(container);
    }

    @Override
    public boolean canExtract() {

        return item.getExtract(container) > 0;
    }

    @Override
    public boolean canReceive() {

        return item.getReceive(container) > 0;
    }

}
