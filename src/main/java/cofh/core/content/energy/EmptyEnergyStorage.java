package cofh.core.content.energy;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

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
    public void readFromBuffer(FriendlyByteBuf buffer) {

    }

    @Override
    public void writeToBuffer(FriendlyByteBuf buffer) {

    }
    // endregion

    // region NBT
    @Override
    public EnergyStorageCoFH read(CompoundTag nbt) {

        return this;
    }

    @Override
    public CompoundTag write(CompoundTag nbt) {

        return nbt;
    }

    @Override
    public CompoundTag writeWithParams(CompoundTag nbt) {

        return nbt;
    }
    // endregion
}
