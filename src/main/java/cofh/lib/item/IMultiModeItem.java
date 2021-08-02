package cofh.lib.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

import static cofh.lib.util.constants.NBTTags.TAG_MODE;

/**
 * Implement this interface on Item classes which have multiple modes - what that means is completely up to you.
 * This just provides a uniform way of dealing with them.
 *
 * @author King Lemming
 */
public interface IMultiModeItem {

    /**
     * Get the current mode of an item.
     */
    default int getMode(ItemStack stack) {

        return !stack.hasTag() ? 0 : stack.getTag().getInt(TAG_MODE);
    }

    /**
     * Attempt to set the empowered state of the item.
     *
     * @param stack ItemStack to set the mode on.
     * @param mode  Desired mode.
     * @return TRUE if the operation was successful, FALSE if it was not.
     */
    default boolean setMode(ItemStack stack, int mode) {

        if (getNumModes(stack) <= 1) {
            mode = 0;
        }
        if (!stack.hasTag()) {
            stack.setTag(new CompoundNBT());
        }
        if (mode < getNumModes(stack)) {
            stack.getTag().putInt("Mode", mode);
            return true;
        }
        return false;
    }

    /**
     * Increment the current mode of an item.
     */
    default boolean incrMode(ItemStack stack) {

        if (getNumModes(stack) <= 1) {
            return false;
        }
        if (!stack.hasTag()) {
            stack.setTag(new CompoundNBT());
        }
        int curMode = getMode(stack);
        ++curMode;
        if (curMode >= getNumModes(stack)) {
            curMode = 0;
        }
        stack.getTag().putInt("Mode", curMode);
        return true;
    }

    /**
     * Decrement the current mode of an item.
     */
    default boolean decrMode(ItemStack stack) {

        if (getNumModes(stack) <= 1) {
            return false;
        }
        if (!stack.hasTag()) {
            stack.setTag(new CompoundNBT());
        }
        int curMode = getMode(stack);
        --curMode;
        if (curMode <= 0) {
            curMode = getNumModes(stack) - 1;
        }
        stack.getTag().putInt("Mode", curMode);
        return true;
    }

    /**
     * Returns the number of possible modes.
     */
    default int getNumModes(ItemStack stack) {

        return 2;
    }

    /**
     * Callback method for reacting to a state change. Useful in KeyBinding handlers.
     *
     * @param player Player holding the item, if applicable.
     * @param stack  The item being held.
     */
    default void onModeChange(PlayerEntity player, ItemStack stack) {

    }

}
