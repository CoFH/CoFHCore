package cofh.lib.api.item;

import cofh.lib.api.ContainerType;
import cofh.lib.util.Utils;
import net.minecraft.world.item.ItemStack;

import static cofh.core.util.references.CoreIDs.ID_HOLDING;
import static cofh.lib.util.Utils.getEnchantment;
import static cofh.lib.util.Utils.getItemEnchantmentLevel;
import static cofh.lib.util.constants.ModIds.ID_COFH_CORE;

/**
 * Marker interface for anything that supports the "Holding" enchantment.
 */
public interface IContainerItem {

    default int getMaxStored(ItemStack container, int amount) {

        int holding = getItemEnchantmentLevel(getEnchantment(ID_COFH_CORE, ID_HOLDING), container);
        return Utils.getEnchantedCapacity(amount, holding);
    }

    default boolean isCreative(ItemStack stack, ContainerType type) {

        return false;
    }

}
