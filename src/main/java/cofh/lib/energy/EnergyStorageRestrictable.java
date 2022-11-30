package cofh.lib.energy;

import net.minecraftforge.energy.IEnergyStorage;

import java.util.function.IntSupplier;

/**
 * Implementation of an Energy Storage object. See {@link IEnergyStorage}.
 * Additional options (receive/extract limits) are provided.
 *
 * @author King Lemming
 */
public class EnergyStorageRestrictable extends EnergyStorageCoFH {

    protected IntSupplier curReceive = this::getMaxReceive;
    protected IntSupplier curExtract = this::getMaxExtract;

    public EnergyStorageRestrictable(int capacity) {

        this(capacity, capacity, capacity, 0);
    }

    public EnergyStorageRestrictable(int capacity, int maxTransfer) {

        this(capacity, maxTransfer, maxTransfer, 0);
    }

    public EnergyStorageRestrictable(int capacity, int maxReceive, int maxExtract) {

        this(capacity, maxReceive, maxExtract, 0);
    }

    public EnergyStorageRestrictable(int capacity, int maxReceive, int maxExtract, int energy) {

        super(capacity, maxReceive, maxExtract, energy);
    }

    public EnergyStorageRestrictable setTransferLimits(IntSupplier curReceive, IntSupplier curExtract) {

        this.curReceive = curReceive;
        this.curExtract = curExtract;
        return this;
    }

    // region IEnergyStorage
    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {

        return super.receiveEnergy(Math.min(maxReceive, curReceive.getAsInt()), simulate);
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {

        return super.extractEnergy(Math.min(maxExtract, curExtract.getAsInt()), simulate);
    }

    @Override
    public boolean canExtract() {

        return curExtract.getAsInt() > 0;
    }

    @Override
    public boolean canReceive() {

        return curReceive.getAsInt() > 0;
    }
    // endregion
}
