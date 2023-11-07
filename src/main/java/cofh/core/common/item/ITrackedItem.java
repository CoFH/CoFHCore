package cofh.core.common.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public interface ITrackedItem {

    /**
     * Called on both sides when the player swaps to this item.
     *
     * @param from The previously held stack, or null if its is not an ITrackedItem
     */
    default void onSwapTo(Player player, InteractionHand hand, @Nullable ItemStack from, ItemStack to) {

    }

    /**
     * Called on both sides when the player swaps off this item or changes which hand it's in.
     *
     * @param hand     The hand that the item formerly was in.
     * @param to       The stack that was swapped to
     * @param duration If the item was being used at the time of swapping, this is the number of ticks it was being used for. Otherwise, -1.
     */
    default void onSwapFrom(Player player, InteractionHand hand, ItemStack from, ItemStack to, int duration) {

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
