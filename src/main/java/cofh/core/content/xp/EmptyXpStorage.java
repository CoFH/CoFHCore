package cofh.core.content.xp;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

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
    public void readFromBuffer(FriendlyByteBuf buffer) {

    }

    @Override
    public void writeToBuffer(FriendlyByteBuf buffer) {

    }
    // endregion

    // region NBT
    @Override
    public XpStorage read(CompoundTag nbt) {

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
