package cofh.lib.xp;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;

public class EmptyXpStorage extends XpStorage {

    public static final EmptyXpStorage INSTANCE = new EmptyXpStorage();

    public EmptyXpStorage() {

        super(0);
    }

    @Override
    public XpStorage applyModifiers(float storageMod) {

        return this;
    }

    @Override
    public XpStorage setCapacity(int capacity) {

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
    public XpStorage read(CompoundNBT nbt) {

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
