package cofh.lib.util.helpers;

import cofh.lib.item.IMultiModeItem;
import com.google.common.base.Strings;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

public class ItemHelper {

    private ItemHelper() {

    }

    public static ItemStack consumeItem(ItemStack stack, int amount) {

        if (amount <= 0) {
            return stack;
        }
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        Item item = stack.getItem();
        boolean largerStack = stack.getItem().getItemStackLimit(stack) > 1;
        // vanilla only alters the stack passed to hasContainerItem/etc. when the size is > 1

        if (largerStack) {
            stack.shrink(amount);
            if (stack.isEmpty()) {
                stack = ItemStack.EMPTY;
            }
        } else if (item.hasContainerItem(stack)) {
            ItemStack ret = item.getContainerItem(stack);
            if (ret.isEmpty()) {
                return ItemStack.EMPTY;
            }
            if (ret.isDamageable() && ret.getDamage() > ret.getMaxDamage()) {
                ret = ItemStack.EMPTY;
            }
            return ret;
        }
        return largerStack ? stack : ItemStack.EMPTY;
    }

    // region CLONESTACK
    public static ItemStack cloneStack(Item item) {

        return cloneStack(item, 1);
    }

    public static ItemStack cloneStack(Block block) {

        return cloneStack(block, 1);
    }

    public static ItemStack cloneStack(Item item, int stackSize) {

        if (item == null) {
            return ItemStack.EMPTY;
        }
        return new ItemStack(item, stackSize);
    }

    public static ItemStack cloneStack(Block block, int stackSize) {

        if (block == null) {
            return ItemStack.EMPTY;
        }
        return new ItemStack(block, stackSize);
    }

    public static ItemStack cloneStack(ItemStack stack, int stackSize) {

        if (stack.isEmpty() || stackSize <= 0) {
            return ItemStack.EMPTY;
        }
        ItemStack retStack = stack.copy();
        retStack.setCount(stackSize);

        return retStack;
    }

    public static ItemStack cloneStack(ItemStack stack) {

        return stack.isEmpty() ? ItemStack.EMPTY : stack.copy();
    }
    // endregion

    // region NBT TAGS
    public static ItemStack copyTag(ItemStack container, ItemStack other) {

        if (!other.isEmpty() && other.hasTag()) {
            container.setTag(other.getTag().copy());
        }
        return container;
    }

    public static CompoundNBT setItemStackTagName(CompoundNBT tag, String name) {

        if (Strings.isNullOrEmpty(name)) {
            return null;
        }
        if (tag == null) {
            tag = new CompoundNBT();
        }
        if (!tag.contains("display")) {
            tag.put("display", new CompoundNBT());
        }
        tag.getCompound("display").putString("Name", name);
        return tag;
    }
    // endregion

    // region COMPARISON
    public static boolean itemsEqualWithTags(ItemStack stackA, ItemStack stackB) {

        return itemsEqual(stackA, stackB) && ItemStack.areItemStackTagsEqual(stackA, stackB);
    }

    public static boolean itemsEqual(ItemStack stackA, ItemStack stackB) {

        return ItemStack.areItemsEqual(stackA, stackB);
    }

    /**
     * Compares item, meta, size and nbt of two stacks while ignoring nbt tag keys provided.
     * This is useful in shouldCauseReequipAnimation overrides.
     *
     * @param stackA          first stack to compare
     * @param stackB          second stack to compare
     * @param nbtTagsToIgnore tag keys to ignore when comparing the stacks
     */
    public static boolean areItemStacksEqualIgnoreTags(ItemStack stackA, ItemStack stackB, String... nbtTagsToIgnore) {

        if (stackA.isEmpty() && stackB.isEmpty()) {
            return true;
        }
        if (stackA.isEmpty() || stackB.isEmpty()) {
            return false;
        }
        if (stackA.getItem() != stackB.getItem()) {
            return false;
        }
        if (stackA.getDamage() != stackB.getDamage()) {
            return false;
        }
        if (stackA.getCount() != stackB.getCount()) {
            return false;
        }
        if (stackA.getTag() == null && stackB.getTag() == null) {
            return true;
        }
        if (stackA.getTag() == null || stackB.getTag() == null) {
            return false;
        }
        int numberOfKeys = stackA.getTag().keySet().size();
        if (numberOfKeys != stackB.getTag().keySet().size()) {
            return false;
        }

        CompoundNBT tagA = stackA.getTag();
        CompoundNBT tagB = stackB.getTag();

        String[] keys = new String[numberOfKeys];
        keys = tagA.keySet().toArray(keys);

        a:
        for (int i = 0; i < numberOfKeys; ++i) {
            for (int j = 0; j < nbtTagsToIgnore.length; ++j) {
                if (nbtTagsToIgnore[j].equals(keys[i])) {
                    continue a;
                }
            }
            if (!tagA.getCompound(keys[i]).equals(tagB.getCompound(keys[i]))) {
                return false;
            }
        }
        return true;
    }
    // endregion

    // region HELD ITEMS
    public static boolean isPlayerHoldingSomething(PlayerEntity player) {

        return !getHeldStack(player).isEmpty();
    }

    public static ItemStack getMainhandStack(PlayerEntity player) {

        return player.getHeldItemMainhand();
    }

    public static ItemStack getOffhandStack(PlayerEntity player) {

        return player.getHeldItemOffhand();
    }

    public static ItemStack getHeldStack(PlayerEntity player) {

        ItemStack stack = player.getHeldItemMainhand();
        if (stack.isEmpty()) {
            stack = player.getHeldItemOffhand();
        }
        return stack;
    }
    // endregion

    // region MODE CHANGE
    public static ItemStack getHeldMultiModeStack(PlayerEntity player) {

        ItemStack stack = player.getHeldItemMainhand();
        if (stack.isEmpty() || !(stack.getItem() instanceof IMultiModeItem)) {
            stack = player.getHeldItemOffhand();
        }
        return stack;
    }

    public static boolean isPlayerHoldingMultiModeItem(PlayerEntity player) {

        if (!isPlayerHoldingSomething(player)) {
            return false;
        }
        ItemStack heldItem = player.getHeldItemMainhand();
        if (heldItem.getItem() instanceof IMultiModeItem) {
            return true;
        } else {
            heldItem = player.getHeldItemOffhand();
            return heldItem.getItem() instanceof IMultiModeItem;
        }
    }

    public static boolean incrHeldMultiModeItemState(PlayerEntity player) {

        if (!isPlayerHoldingSomething(player)) {
            return false;
        }
        ItemStack mainHand = player.getHeldItemMainhand();
        ItemStack offHand = player.getHeldItemOffhand();
        return mainHand.getItem() instanceof IMultiModeItem
                ? ((IMultiModeItem) mainHand.getItem()).incrMode(mainHand)
                : offHand.getItem() instanceof IMultiModeItem && ((IMultiModeItem) offHand.getItem()).incrMode(offHand);
    }

    public static boolean decrHeldMultiModeItemState(PlayerEntity player) {

        if (!isPlayerHoldingSomething(player)) {
            return false;
        }
        ItemStack mainHand = player.getHeldItemMainhand();
        ItemStack offHand = player.getHeldItemOffhand();
        return mainHand.getItem() instanceof IMultiModeItem
                ? ((IMultiModeItem) mainHand.getItem()).decrMode(mainHand)
                : offHand.getItem() instanceof IMultiModeItem && ((IMultiModeItem) offHand.getItem()).decrMode(offHand);
    }

    public static void onHeldMultiModeItemChange(PlayerEntity player) {

        ItemStack heldItem = getHeldMultiModeStack(player);
        ((IMultiModeItem) heldItem.getItem()).onModeChange(player, heldItem);
    }
    // endregion
}
