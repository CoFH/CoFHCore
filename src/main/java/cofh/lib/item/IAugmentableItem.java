package cofh.lib.item;

import cofh.lib.util.helpers.AugmentableHelper;
import net.minecraft.item.ItemStack;

import java.util.List;

/**
 * A reasonably freeform interface used for handling Augmentation for ItemStack objects.
 */
public interface IAugmentableItem {

    default List<ItemStack> getAugments(ItemStack augmentable) {

        return AugmentableHelper.readAugmentsFromItem(augmentable);
    }

    /**
     * @param augmentable Stack representing the Augmentable item.
     * @return The *total* number of slots available for augmentation.
     */
    int getAugmentSlots(ItemStack augmentable);

    /**
     * Check to see if augment *can* be installed. This is the validator check for the augment slot in a GUI.
     *
     * @param augmentable Stack representing the Augmentable item.
     * @param augment     Stack representing the Augment item to be added.
     * @param augments    List of ItemStacks representing existing augments.
     * @return TRUE if the augment is compatible.
     */
    boolean validAugment(ItemStack augmentable, ItemStack augment, List<ItemStack> augments);

    /**
     * Writes the augments TO the ItemStack's NBT and performs any extra logic which may be required.
     *
     * @param augmentable Stack representing the Augmentable item.
     * @param augments    List of ItemStacks representing the Augments.
     */
    default void setAugments(ItemStack augmentable, List<ItemStack> augments) {

        AugmentableHelper.writeAugmentsToItem(augmentable, augments);
        updateAugmentState(augmentable, augments);
    }

    /**
     * Callback for items which may require parsing of the augments into a Properties list or some such. :)
     *
     * @param augmentable Stack representing the Augmentable item.
     * @param augments    List of ItemStacks representing the Augments.
     */
    default void updateAugmentState(ItemStack augmentable, List<ItemStack> augments) {

    }

}
