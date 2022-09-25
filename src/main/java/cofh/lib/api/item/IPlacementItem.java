package cofh.lib.api.item;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;

public interface IPlacementItem {

    boolean onBlockPlacement(ItemStack stack, UseOnContext context);

}
