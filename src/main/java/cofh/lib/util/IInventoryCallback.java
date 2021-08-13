package cofh.lib.util;

public interface IInventoryCallback {

    default void onInventoryChanged(int slot) {

    }

    default void onTankChanged(int tank) {

    }

    default boolean clearSlot(int slot) {

        return false;
    }

    default boolean clearTank(int tank) {

        return false;
    }

    default boolean clearEnergy(int coil) {

        return false;
    }

}
