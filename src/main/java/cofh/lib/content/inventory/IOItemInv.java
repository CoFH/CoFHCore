package cofh.core.content.inventory;

import cofh.lib.api.IStorageCallback;
import cofh.lib.api.StorageGroup;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.EmptyHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;

import static cofh.lib.util.Constants.FALSE;

public class IOItemInv extends SimpleItemInv {

    protected List<ItemStorageCoFH> accessibleSlots = new ArrayList<>();
    protected List<ItemStorageCoFH> internalSlots = new ArrayList<>();

    protected IOItemHandler outputHandler;
    protected IOItemHandler inputHandler;
    protected IItemHandler internalHandler;

    public IOItemInv(@Nullable IStorageCallback callback) {

        super(callback);
    }

    public IOItemInv(IStorageCallback callback, String tag) {

        super(callback, tag);
    }

    public void addSlot(ItemStorageCoFH slot, StorageGroup group) {

        if (allHandler != null) {
            return;
        }
        slots.add(slot);
        switch (group) {
            case INTERNAL:
                internalSlots.add(slot);
                break;
            case ACCESSIBLE:
                accessibleSlots.add(slot);
                break;
            default:
        }
    }

    public void setConditions(BooleanSupplier allowInsert, BooleanSupplier allowExtract) {

        outputHandler.setConditions(FALSE, allowExtract);
        inputHandler.setConditions(allowInsert, FALSE);
    }

    public void initHandlers() {

        ((ArrayList<ItemStorageCoFH>) slots).trimToSize();
        ((ArrayList<ItemStorageCoFH>) accessibleSlots).trimToSize();
        ((ArrayList<ItemStorageCoFH>) internalSlots).trimToSize();

        outputHandler = new IOItemHandler(callback, accessibleSlots);
        inputHandler = new IOItemHandler(callback, accessibleSlots);
        internalHandler = new SimpleItemHandler(callback, internalSlots);
        allHandler = new SimpleItemHandler(callback, slots);
    }

    public boolean isBufferEmpty() {

        for (ItemStorageCoFH slot : accessibleSlots) {
            if (!slot.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public boolean isBufferFull() {

        for (ItemStorageCoFH slot : accessibleSlots) {
            if (!slot.isFull()) {
                return false;
            }
        }
        return true;
    }

    public boolean isConfigEmpty() {

        for (ItemStorageCoFH slot : internalSlots) {
            if (!slot.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public boolean isConfigFull() {

        for (ItemStorageCoFH slot : internalSlots) {
            if (!slot.isFull()) {
                return false;
            }
        }
        return true;
    }

    public List<ItemStorageCoFH> getAccessibleSlots() {

        return accessibleSlots;
    }

    public List<ItemStorageCoFH> getInternalSlots() {

        return internalSlots;
    }

    @Override
    public IItemHandler getHandler(StorageGroup group) {

        if (allHandler == null) {
            initHandlers();
        }
        switch (group) {
            case INPUT:
                return inputHandler;
            case OUTPUT:
                return outputHandler;
            case INTERNAL:
                return internalHandler;
            case ALL:
                return allHandler;
            default:
        }
        return EmptyHandler.INSTANCE;
    }

}
