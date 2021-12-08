package cofh.lib.energy;

import net.minecraftforge.energy.IEnergyStorage;

public class EnergyHandlerRestrictionWrapper implements IEnergyStorage {

    protected IEnergyStorage wrappedHandler;
    protected boolean canReceive;
    protected boolean canExtract;

    public EnergyHandlerRestrictionWrapper(IEnergyStorage wrappedHandler, boolean canReceive, boolean canExtract) {

        this.wrappedHandler = wrappedHandler;
        this.canReceive = canReceive;
        this.canExtract = canExtract;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {

        if (!canReceive()) {
            return 0;
        }
        return wrappedHandler.receiveEnergy(maxReceive, simulate);
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {

        if (!canExtract()) {
            return 0;
        }
        return wrappedHandler.extractEnergy(maxExtract, simulate);
    }

    @Override
    public int getEnergyStored() {

        return wrappedHandler.getEnergyStored();
    }

    @Override
    public int getMaxEnergyStored() {

        return wrappedHandler.getMaxEnergyStored();
    }

    @Override
    public boolean canExtract() {

        return canExtract;
    }

    @Override
    public boolean canReceive() {

        return canReceive;
    }

}
