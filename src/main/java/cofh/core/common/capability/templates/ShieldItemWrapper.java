package cofh.core.common.capability.templates;

import cofh.lib.api.capability.IShieldItem;
import net.minecraft.world.item.ItemStack;

public class ShieldItemWrapper implements IShieldItem {

    final ItemStack shieldItem;

    public ShieldItemWrapper(ItemStack shieldItem) {

        this.shieldItem = shieldItem;
    }

}
