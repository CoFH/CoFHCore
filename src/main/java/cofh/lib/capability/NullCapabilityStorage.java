package cofh.lib.capability;

import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;

/**
 * A {@link Capability.IStorage} implementation, that does nothing.
 * Useful in cases where you don't need the capability to be saved.
 * <p>
 * Yoinked from CCL with permission :)
 * <p>
 * Created by covers1624 on 5/5/20.
 */
public class NullCapabilityStorage<T> implements Capability.IStorage<T> {

    private static final NullCapabilityStorage<?> INSTANCE = new NullCapabilityStorage<>();

    private NullCapabilityStorage() {

    }

    @SuppressWarnings ("unchecked")
    public static <R> NullCapabilityStorage<R> instance() {

        return (NullCapabilityStorage<R>) INSTANCE;
    }

    //@formatter:off
    @Override public INBT writeNBT(Capability<T> capability, T instance, Direction side) { return null; }
    @Override public void readNBT(Capability<T> capability, T instance, Direction side, INBT nbt) { }
    //@formatter:on
}
