package cofh.lib.enchantment;

import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;

public abstract class EnchantmentOverride extends EnchantmentCoFH {

    protected EnchantmentOverride(Rarity rarityIn, EnchantmentType typeIn, EquipmentSlotType[] slots) {

        super(rarityIn, typeIn, slots);
    }

    public EnchantmentCoFH setEnable(boolean enable) {

        this.enable = enable;
        return this;
    }

    @Override
    public boolean isEnabled() {

        return true;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {

        return stack.canApplyAtEnchantingTable(this) || supportsEnchantment(stack);
    }

    @Override
    public boolean isAllowedOnBooks() {

        return allowOnBooks;
    }

    @Override
    public boolean canGenerateInLoot() {

        return allowGenerateInLoot;
    }

    @Override
    public boolean canVillagerTrade() {

        return allowVillagerTrade;
    }

}
