package cofh.core.item;

import cofh.core.util.Utils;
import net.minecraft.world.item.ItemStack;

import static cofh.core.util.Utils.getEnchantment;
import static cofh.core.util.Utils.getItemEnchantmentLevel;
import static cofh.lib.util.Constants.ID_COFH_CORE;
import static cofh.core.util.references.CoreIDs.ID_HOLDING;

/**
 * Marker interface for anything that supports the "Holding" enchantment. Can also be done via the Enchantable capability, but this is way less overhead.
 */
public interface IContainerItem extends ICoFHItem {

    default int getMaxStored(ItemStack container, int amount) {

        int holding = getItemEnchantmentLevel(getEnchantment(ID_COFH_CORE, ID_HOLDING), container);
        return Utils.getEnchantedCapacity(amount, holding);
    }

}
