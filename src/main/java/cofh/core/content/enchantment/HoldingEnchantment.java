package cofh.core.content.enchantment;

import cofh.core.content.item.IContainerItem;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class HoldingEnchantment extends EnchantmentCoFH {

    public HoldingEnchantment() {

        super(Rarity.COMMON, EnchantmentCategory.VANISHABLE, EquipmentSlot.values());
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
