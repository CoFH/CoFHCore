package cofh.core.content.inventory;

import cofh.core.util.StorageGroup;
import cofh.lib.content.IStorageCallback;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.EmptyHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

public class ManagedItemInv extends SimpleItemInv {

    protected List<ItemStorageCoFH> inputSlots = new ArrayList<>();
    protected List<ItemStorageCoFH> catalystSlots = new ArrayList<>();
    protected List<ItemStorageCoFH> outputSlots = new ArrayList<>();
    protected List<ItemStorageCoFH> internalSlots = new ArrayList<>();

    protected IItemHandler inputHandler;
    protected IItemHandler outputHandler;
    protected IItemHandler ioHandler;
    protected IItemHandler accessibleHandler;
    protected IItemHandler internalHandler;

    public ManagedItemInv(IStorageCallback callback) {

        super(callback);
    }

    public ManagedItemInv(IStorageCallback callback, String tag) {

        super(callback, tag);
    }

    public void addSlots(StorageGroup group, int amount) {

        for (int i = 0; i < amount; ++i) {
            addSlot(new ItemStorageCoFH(), group);
        }
    }

    public void addSlots(StorageGroup group, int amount, Predicate<ItemStack> validator) {

        for (int i = 0; i < amount; ++i) {
            addSlot(new ItemStorageCoFH(validator), group);
        }
    }

    public void addSlot(ItemStorageCoFH slot, StorageGroup group) {

        slots.add(slot);
        switch (group) {
            case CATALYST:
                catalystSlots.add(slot);
                // DO NOT PUT A BREAK HERE
            case INPUT:
                inputSlots.add(slot);
                break;
            case OUTPUT:
                outputSlots.add(slot);
                break;
            case INTERNAL:
                internalSlots.add(slot);
                break;
            case ACCESSIBLE:
                inputSlots.add(slot);
                outputSlots.add(slot);
                break;
            default:
        }
    }

    public void initHandlers() {

        ((ArrayList<ItemStorageCoFH>) slots).trimToSize();
        ((ArrayList<ItemStorageCoFH>) inputSlots).trimToSize();
        ((ArrayList<ItemStorageCoFH>) catalystSlots).trimToSize();
        ((ArrayList<ItemStorageCoFH>) outputSlots).trimToSize();
        ((ArrayList<ItemStorageCoFH>) internalSlots).trimToSize();

        inputHandler = new ManagedItemHandler(callback, inputSlots, Collections.emptyList());
        outputHandler = new ManagedItemHandler(callback, Collections.emptyList(), outputSlots);
        ioHandler = new ManagedItemHandler(callback, inputSlots, outputSlots).restrict();
        accessibleHandler = new ManagedItemHandler(callback, inputSlots, outputSlots);
        internalHandler = new SimpleItemHandler(callback, internalSlots);
        allHandler = new SimpleItemHandler(callback, slots);
    }

    public boolean hasInputSlots() {

        return inputSlots.size() > 0;
    }

    public boolean hasOutputSlots() {

        return outputSlots.size() > 0;
    }

    public boolean hasAccessibleSlots() {

        return hasInputSlots() || hasOutputSlots();
    }

    public List<ItemStorageCoFH> getInputSlots() {

        return inputSlots;
    }

    public List<ItemStorageCoFH> getOutputSlots() {

        return outputSlots;
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
            case INPUT_OUTPUT:
                return ioHandler;
            case ACCESSIBLE:
                return accessibleHandler;
            case INTERNAL:
                return internalHandler;
            case ALL:
                return allHandler;
            default:
        }
        return EmptyHandler.INSTANCE;
    }

}
