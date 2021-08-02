package cofh.lib.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;

import static cofh.lib.capability.CapabilityEnchantableItem.ENCHANTABLE_ITEM_CAPABILITY;

public abstract class EnchantmentCoFH extends Enchantment {

    protected boolean enable = true;

    protected boolean allowGenerateInLoot = true;
    protected boolean allowOnBooks = true;
    protected boolean allowVillagerTrade = true;
    protected boolean treasureEnchantment = false;

    protected int maxLevel = 1;

    protected EnchantmentCoFH(Rarity rarityIn, EnchantmentType typeIn, EquipmentSlotType[] slots) {

        super(rarityIn, typeIn, slots);
    }

    public EnchantmentCoFH setEnable(boolean enable) {

        this.enable = enable;
        name = enable ? getName() : "enchantment.cofh_core.disabled";
        return this;
    }

    public EnchantmentCoFH setTreasureEnchantment(boolean treasureEnchantment) {

        this.treasureEnchantment = treasureEnchantment;
        return this;
    }

    public EnchantmentCoFH setAllowOnBooks(boolean allowOnBooks) {

        this.allowOnBooks = allowOnBooks;
        return this;
    }

    public EnchantmentCoFH setMaxLevel(int maxLevel) {

        this.maxLevel = maxLevel;
        return this;
    }

    public boolean isEnabled() {

        return enable;
    }

    protected boolean supportsEnchantment(ItemStack stack) {

        return stack.getCapability(ENCHANTABLE_ITEM_CAPABILITY).filter(cap -> cap.supportsEnchantment(this)).isPresent();
    }

    @Override
    public int getMaxEnchantability(int level) {

        return enable ? maxDelegate(level) : -1;
    }

    protected int maxDelegate(int level) {

        return getMinEnchantability(level) + 5;
    }

    @Override
    public int getMaxLevel() {

        return maxLevel;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {

        return enable && (super.canApplyAtEnchantingTable(stack) || supportsEnchantment(stack));
    }

    @Override
    public boolean isAllowedOnBooks() {

        return enable && allowOnBooks;
    }

    @Override
    public boolean canGenerateInLoot() {

        return enable && allowGenerateInLoot;
    }

    @Override
    public boolean canVillagerTrade() {

        return enable && allowVillagerTrade;
    }

    @Override
    public boolean isTreasureEnchantment() {

        return treasureEnchantment;
    }

}
