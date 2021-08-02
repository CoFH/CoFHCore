package cofh.lib.energy;

import net.minecraftforge.energy.IEnergyStorage;

import java.util.function.IntSupplier;

/**
 * Implementation of an Energy Storage object. See {@link IEnergyStorage}.
 * Additional options (receive/extract limits) are provided.
 *
 * @author King Lemming
 */
public class EnergyStorageAdjustable extends EnergyStorageCoFH {

    protected IntSupplier curReceive = this::getMaxReceive;
    protected IntSupplier curExtract = this::getMaxExtract;

    public EnergyStorageAdjustable(int capacity) {

        this(capacity, capacity, capacity, 0);
    }

    public EnergyStorageAdjustable(int capacity, int maxTransfer) {

        this(capacity, maxTransfer, maxTransfer, 0);
    }

    public EnergyStorageAdjustable(int capacity, int maxReceive, int maxExtract) {

        this(capacity, maxReceive, maxExtract, 0);
    }

    public EnergyStorageAdjustable(int capacity, int maxReceive, int maxExtract, int energy) {

        super(capacity, maxReceive, maxExtract, energy);
    }

    public EnergyStorageAdjustable setTransferLimits(IntSupplier curReceive, IntSupplier curExtract) {

        this.curReceive = curReceive;
        this.curExtract = curExtract;
        return this;
    }

    // region IEnergyStorage
    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {

        if (maxReceive > this.curReceive.getAsInt()) {
            return super.receiveEnergy(this.curReceive.getAsInt(), simulate);
        }
        return super.receiveEnergy(maxReceive, simulate);
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {

        if (maxExtract > this.curExtract.getAsInt()) {
            return super.extractEnergy(this.curExtract.getAsInt(), simulate);
        }
        return super.extractEnergy(maxExtract, simulate);
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
