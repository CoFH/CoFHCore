package cofh.core.enchantment;

import cofh.lib.api.item.IContainerItem;
import cofh.lib.enchantment.EnchantmentCoFH;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

import java.util.HashSet;
import java.util.Set;

public class HoldingEnchantment extends EnchantmentCoFH {

    private static final Set<Item> VALID_ITEMS = new HashSet<>();

    public static boolean addValidItem(Item container) {

        return VALID_ITEMS.add(container);
    }

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
        return enable && item instanceof IContainerItem || VALID_ITEMS.contains(item);
    }

}
