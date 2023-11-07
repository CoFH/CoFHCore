package cofh.core.common.item;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface ILeftClickHandlerItem {

    default void onLeftClick(Player player, ItemStack stack) {

    }

}
