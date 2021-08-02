package cofh.lib.energy;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;

public class EmptyEnergyStorage extends EnergyStorageCoFH {

    public static final EmptyEnergyStorage INSTANCE = new EmptyEnergyStorage();

    public EmptyEnergyStorage() {

        super(0);
    }

    @Override
    public EnergyStorageCoFH applyModifiers(float storageMod, float transferMod) {

        return this;
    }

    @Override
    public EnergyStorageCoFH setCapacity(int capacity) {

        return this;
    }

    @Override
    public EnergyStorageCoFH setMaxReceive(int maxReceive) {

        return this;
    }

    @Override
    public EnergyStorageCoFH setMaxExtract(int maxExtract) {

        return this;
    }

    // region NETWORK
    @Override
    public void readFromBuffer(PacketBuffer buffer) {

    }

    @Override
    public void writeToBuffer(PacketBuffer buffer) {

    }
    // endregion

    // region NBT
    @Override
    public EnergyStorageCoFH read(CompoundNBT nbt) {

        return this;
    }

    @Override
    public CompoundNBT write(CompoundNBT nbt) {

        return nbt;
    }

    @Override
    public CompoundNBT writeWithParams(CompoundNBT nbt) {

        return nbt;
    }
    // endregion
}
