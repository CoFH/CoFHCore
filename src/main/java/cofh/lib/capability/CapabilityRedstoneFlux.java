package cofh.lib.capability;

import cofh.lib.energy.EnergyStorageCoFH;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

import javax.annotation.Nullable;

public class CapabilityRedstoneFlux {

    @CapabilityInject(IRedstoneFluxStorage.class)
    public static Capability<IRedstoneFluxStorage> RF_ENERGY = null;

    public static void register() {

        CapabilityManager.INSTANCE.register(IRedstoneFluxStorage.class, new DefaultRedstoneFluxStorage<>(), () -> new EnergyStorageCoFH(1000));
    }

    private static class DefaultRedstoneFluxStorage<T extends IRedstoneFluxStorage> implements Capability.IStorage<T> {

        @Nullable
        @Override
        public INBT writeNBT(Capability<T> capability, T instance, Direction side) {

            return null;
        }

        @Override
        public void readNBT(Capability<T> capability, T instance, Direction side, INBT nbt) {

        }

    }

}
