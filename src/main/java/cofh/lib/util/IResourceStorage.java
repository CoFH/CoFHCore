package cofh.lib.util;

public interface IResourceStorage {

    default boolean isFull() {

        return getSpace() <= 0;
    }

    default int getSpace() {

        return getCapacity() - getStored();
    }

    default double getRatio() {

        return (double) getStored() / getCapacity();
    }

    boolean clear();

    void modify(int amount);

    boolean isCreative();

    boolean isEmpty();

    int getCapacity();

    int getStored();

    String getUnit();

}
