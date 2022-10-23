package cofh.core.util.filter;

import net.minecraft.nbt.CompoundTag;

public class EmptyFilter implements IFilter {

    public static final EmptyFilter INSTANCE = new EmptyFilter();

    @Override
    public IFilter read(CompoundTag nbt) {

        return INSTANCE;
    }

    @Override
    public CompoundTag write(CompoundTag nbt) {

        return nbt;
    }

}
