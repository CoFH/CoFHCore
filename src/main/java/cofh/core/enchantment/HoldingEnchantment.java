package cofh.core.enchantment;

import cofh.lib.enchantment.EnchantmentCoFH;
import cofh.lib.item.IContainerItem;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class HoldingEnchantment extends EnchantmentCoFH {

    public HoldingEnchantment() {

        super(Rarity.COMMON, EnchantmentType.VANISHABLE, EquipmentSlotType.values());
        maxLevel = 4;
    }

    @Override
    public int getMinCost(int level) {

        return 1 + (level - 1) * 5;
    }

    @Override
    protected int maxDelegate(int level) {

        return getMinCost(level) + 50;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {

        Item item = stack.getItem();
        return enable && (item instanceof IContainerItem || supportsEnchantment(stack));
    }

}
