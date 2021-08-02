package cofh.lib.util;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

/**
 * This class allows for OreDictionary-compatible ItemStack comparisons and Integer-based Hashes without collisions.
 * <p>
 * The intended purpose of this is for things such as Recipe Handlers or HashMaps of ItemStacks.
 *
 * @author King Lemming
 */
public class ComparableItemStack {

    private final Item item;
    private int stackSize;

    public ComparableItemStack(ItemStack stack) {

        this.item = stack.getItem();
        if (!stack.isEmpty()) {
            stackSize = stack.getCount();
        }
    }

    public ComparableItemStack(Item item, int stackSize) {

        this.item = item;
        this.stackSize = stackSize;
    }

    public ComparableItemStack(ComparableItemStack other) {

        this.item = other.item;
        this.stackSize = other.stackSize;
    }

    public boolean isEqual(ComparableItemStack other) {

        if (other == null) {
            return false;
        }
        if (item == other.item) {
            return true;
        }
        if (item != null && other.item != null) {
            return item.delegate.get() == other.item.delegate.get();
        }
        return false;
    }

    public boolean isItemEqual(ComparableItemStack other) {

        return other != null && isEqual(other);
    }

    public boolean isStackEqual(ComparableItemStack other) {

        return isItemEqual(other) && stackSize == other.stackSize;
    }

    public int getId() {

        return Item.getIdFromItem(item); // '0' is null. '-1' is an unmapped item (missing in this World)
    }

    public ItemStack toItemStack() {

        return item != Items.AIR ? new ItemStack(item, stackSize) : ItemStack.EMPTY;
    }

    @Override
    public boolean equals(Object o) {

        return o instanceof ComparableItemStack && isItemEqual((ComparableItemStack) o);
    }

    @Override
    public int hashCode() {

        return item.hashCode();
    }

    @Override
    public String toString() {

        StringBuilder builder = new StringBuilder(768);

        builder.append(getClass().getName()).append('@');
        builder.append(System.identityHashCode(this)).append('{');
        builder.append("ID:").append(getId()).append(", ");
        builder.append("ITM:");
        if (item == null) {
            builder.append("null");
        } else {
            builder.append(item.getClass().getName()).append('@');
            builder.append(System.identityHashCode(item)).append(' ');
            builder.append('[').append(item.getRegistryName()).append(']');
        }
        builder.append('}');

        return builder.toString();
    }

}
