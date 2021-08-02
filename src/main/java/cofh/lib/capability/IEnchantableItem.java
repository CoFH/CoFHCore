package cofh.lib.capability;

import net.minecraft.enchantment.Enchantment;

/**
 * Implement this interface as a capability for Items which have explicit enchantment support.
 * The reason for this interface is to prevent certain enchantments (example: Holding) from applying to
 * EVERYTHING due to the generic logic which would check the Enchantment's type (ALL).
 *
 * @author King Lemming
 */
public interface IEnchantableItem {

    /**
     * Simple boolean to determine if an enchantment applies to an ItemStack.
     *
     * @param ench Enchantment to be applied.
     * @return TRUE if the enchantment can be applied to the ItemStack (in a meaningful way).
     */
    boolean supportsEnchantment(Enchantment ench);

}
