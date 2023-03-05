package cofh.core.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface ITrackedItem {

    /**
     * Called when the player swaps to this item.
     * This is only evaluated server side.
     */
    default void onSwapTo(Player player, InteractionHand hand, ItemStack stack) {

    }

    /**
     * Called when the player swaps off this item or changes which hand it's in.
     * This is only evaluated server side.
     *
     * @param hand     The hand that the item formerly was in.
     * @param duration If the item was being used at the time of swapping, this is the number of ticks it was being used for. Otherwise, -1.
     */
    default void onSwapFrom(Player player, InteractionHand hand, ItemStack stack, int duration) {

    }

    default long getLastUsedTime(Player player, InteractionHand hand) {

        ItemTracker.TrackedItemData data = ItemTracker.getData(player, hand);
        return data == null ? -1 : data.lastUse;
    }

    /**
     * @param from The previously held item stack.
     * @param to   The currently held item stack.
     * @return Whether two item stacks should be considered the same for tracking purposes.
     */
    default boolean matches(ItemStack from, ItemStack to) {

        return ItemStack.matches(from, to);
    }

}
