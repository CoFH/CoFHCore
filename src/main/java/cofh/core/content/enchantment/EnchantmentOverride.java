package cofh.core.content.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public abstract class EnchantmentOverride extends EnchantmentCoFH {

    protected EnchantmentOverride(Rarity rarityIn, EnchantmentCategory typeIn, EquipmentSlot[] slots) {

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
    public boolean isDiscoverable() {

        return allowGenerateInLoot;
    }

    @Override
    public boolean isTradeable() {

        return allowVillagerTrade;
    }

}
