package cofh.lib.item;

import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;

public interface IPlacementItem {

    boolean onBlockPlacement(ItemStack stack, ItemUseContext context);

}
