package cofh.lib.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public interface ILeftClickHandlerItem {

    default void onLeftClick(PlayerEntity player, ItemStack stack) {

    }

}
