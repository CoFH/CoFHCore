package cofh.core.util.helpers;

import cofh.core.item.IBlockRayTraceItem;
import cofh.core.item.IEntityRayTraceItem;
import cofh.core.item.ILeftClickHandlerItem;
import cofh.core.item.IMultiModeItem;
import com.google.common.base.Strings;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;

import java.util.function.Predicate;

public final class ItemHelper {

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
        boolean largerStack = stack.getItem().getMaxStackSize(stack) > 1;
        // vanilla only alters the stack passed to hasContainerItem/etc. when the size is > 1

        if (largerStack) {
            stack.shrink(amount);
            if (stack.isEmpty()) {
                stack = ItemStack.EMPTY;
            }
        } else if (item.hasCraftingRemainingItem(stack)) {
            ItemStack ret = item.getCraftingRemainingItem(stack);
            if (ret.isEmpty()) {
                return ItemStack.EMPTY;
            }
            if (ret.isDamageableItem() && ret.getDamageValue() > ret.getMaxDamage()) {
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

    public static CompoundTag setItemStackTagName(CompoundTag tag, String name) {

        if (Strings.isNullOrEmpty(name)) {
            return null;
        }
        if (tag == null) {
            tag = new CompoundTag();
        }
        if (!tag.contains("display")) {
            tag.put("display", new CompoundTag());
        }
        tag.getCompound("display").putString("Name", name);
        return tag;
    }
    // endregion

    // region COMPARISON
    public static boolean itemsEqualWithTags(ItemStack stackA, ItemStack stackB) {

        return itemsEqual(stackA, stackB) && ItemStack.tagMatches(stackA, stackB);
    }

    public static boolean itemsEqual(ItemStack stackA, ItemStack stackB) {

        return ItemStack.isSame(stackA, stackB);
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
        if (stackA.getDamageValue() != stackB.getDamageValue()) {
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
        int numberOfKeys = stackA.getTag().getAllKeys().size();
        if (numberOfKeys != stackB.getTag().getAllKeys().size()) {
            return false;
        }

        CompoundTag tagA = stackA.getTag();
        CompoundTag tagB = stackB.getTag();

        String[] keys = new String[numberOfKeys];
        keys = tagA.getAllKeys().toArray(keys);

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
    public static boolean isPlayerHoldingSomething(Player player) {

        return !getHeldStack(player).isEmpty();
    }

    public static ItemStack getMainhandStack(Player player) {

        return player.getMainHandItem();
    }

    public static ItemStack getOffhandStack(Player player) {

        return player.getOffhandItem();
    }

    public static ItemStack getHeldStack(Player player) {

        ItemStack stack = player.getMainHandItem();
        if (stack.isEmpty()) {
            stack = player.getOffhandItem();
        }
        return stack;
    }

    public static InteractionHand getMatchingHand(Player player, Predicate<ItemStack> filter) {

        ItemStack stack = player.getMainHandItem();
        if (!stack.isEmpty() && filter.test(stack)) {
            return InteractionHand.MAIN_HAND;
        }
        stack = player.getOffhandItem();
        if (!stack.isEmpty() && filter.test(stack)) {
            return InteractionHand.OFF_HAND;
        }
        return InteractionHand.MAIN_HAND;
    }

    public static ItemStack getMatchingHeldStack(Player player, Predicate<ItemStack> filter) {

        ItemStack stack = player.getMainHandItem();
        if (!stack.isEmpty() && filter.test(stack)) {
            return stack;
        }
        stack = player.getOffhandItem();
        if (!stack.isEmpty() && filter.test(stack)) {
            return stack;
        }
        return ItemStack.EMPTY;
    }
    // endregion

    // region MODE CHANGE
    public static ItemStack getHeldMultiModeStack(Player player) {

        ItemStack stack = player.getMainHandItem();
        if (stack.isEmpty() || !(stack.getItem() instanceof IMultiModeItem)) {
            stack = player.getOffhandItem();
        }
        return stack;
    }

    public static boolean isPlayerHoldingMultiModeItem(Player player) {

        if (!isPlayerHoldingSomething(player)) {
            return false;
        }
        ItemStack heldItem = player.getMainHandItem();
        if (heldItem.getItem() instanceof IMultiModeItem) {
            return true;
        } else {
            heldItem = player.getOffhandItem();
            return heldItem.getItem() instanceof IMultiModeItem;
        }
    }

    public static boolean incrHeldMultiModeItemState(Player player) {

        if (!isPlayerHoldingSomething(player)) {
            return false;
        }
        ItemStack mainHand = player.getMainHandItem();
        ItemStack offHand = player.getOffhandItem();
        return mainHand.getItem() instanceof IMultiModeItem
                ? ((IMultiModeItem) mainHand.getItem()).incrMode(mainHand)
                : offHand.getItem() instanceof IMultiModeItem && ((IMultiModeItem) offHand.getItem()).incrMode(offHand);
    }

    public static boolean decrHeldMultiModeItemState(Player player) {

        if (!isPlayerHoldingSomething(player)) {
            return false;
        }
        ItemStack mainHand = player.getMainHandItem();
        ItemStack offHand = player.getOffhandItem();
        return mainHand.getItem() instanceof IMultiModeItem
                ? ((IMultiModeItem) mainHand.getItem()).decrMode(mainHand)
                : offHand.getItem() instanceof IMultiModeItem && ((IMultiModeItem) offHand.getItem()).decrMode(offHand);
    }

    public static void onHeldMultiModeItemChange(Player player) {

        ItemStack heldItem = getHeldMultiModeStack(player);
        ((IMultiModeItem) heldItem.getItem()).onModeChange(player, heldItem);
    }
    // endregion

    // region LEFT CLICK
    public static boolean isPlayerHoldingLeftClickItem(Player player) {

        if (!isPlayerHoldingSomething(player)) {
            return false;
        }
        return player.getMainHandItem().getItem() instanceof ILeftClickHandlerItem;
    }

    public static void onHeldLeftClickItem(Player player) {

        ItemStack heldItem = player.getMainHandItem();
        ((ILeftClickHandlerItem) heldItem.getItem()).onLeftClick(player, heldItem);
    }
    // endregion

    // region RAY TRACE
    public static boolean isPlayerHoldingEntityRayTraceItem(Player player) {

        if (!isPlayerHoldingSomething(player)) {
            return false;
        }
        return player.getMainHandItem().getItem() instanceof IEntityRayTraceItem;
    }

    public static void onRayTraceEntity(Player player, int targetId, Vec3 origin, Vec3 hit) {

        ItemStack stack = player.getMainHandItem();
        Level level = player.level;
        Entity target = level.getEntity(targetId);
        if (target != null) {
            ((IEntityRayTraceItem) stack.getItem()).handleEntityRayTrace(level, stack, player, target, origin, hit);
        }
    }

    public static boolean isPlayerHoldingBlockRayTraceItem(Player player) {

        if (!isPlayerHoldingSomething(player)) {
            return false;
        }
        return player.getMainHandItem().getItem() instanceof IBlockRayTraceItem;
    }

    public static void onRayTraceBlock(Player player, BlockPos pos, Vec3 origin, Vec3 hit) {

        ItemStack stack = player.getMainHandItem();
        ((IBlockRayTraceItem) stack.getItem()).handleBlockRayTrace(player.level, stack, player, pos, origin, hit);
    }
    // endregion
}
