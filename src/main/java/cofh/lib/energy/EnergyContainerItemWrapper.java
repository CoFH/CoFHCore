package cofh.lib.energy;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This class provides a simple way to wrap an IEnergyContainerItem to allow for capability support.
 *
 * @author King Lemming
 */
public class EnergyContainerItemWrapper implements IEnergyStorage, ICapabilityProvider {

    private final LazyOptional<IEnergyStorage> holder = LazyOptional.of(() -> this);

    protected final ItemStack container;
    protected final IEnergyContainerItem item;

    public EnergyContainerItemWrapper(ItemStack containerIn, IEnergyContainerItem itemIn) {

        this.container = containerIn;
        this.item = itemIn;
    }

    // region IEnergyStorage
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
    // endregion

    // region ICapabilityProvider
    @Nonnull
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {

        return CapabilityEnergy.ENERGY.orEmpty(cap, holder);
    }
    // endregion
}
