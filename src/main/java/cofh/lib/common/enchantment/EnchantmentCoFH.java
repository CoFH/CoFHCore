package cofh.lib.common.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public abstract class EnchantmentCoFH extends Enchantment {

    protected boolean enable = true;

    protected boolean allowGenerateInLoot = true;
    protected boolean allowOnBooks = true;
    protected boolean allowVillagerTrade = true;
    protected boolean treasureEnchantment = false;

    protected int maxLevel = 1;

    protected EnchantmentCoFH(Rarity rarityIn, EnchantmentCategory typeIn, EquipmentSlot[] slots) {

        super(rarityIn, typeIn, slots);
    }

    public EnchantmentCoFH setEnable(boolean enable) {

        this.enable = enable;
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

    @Override
    public String getDescriptionId() {

        return isEnabled() ? this.getOrCreateDescriptionId() : "enchantment.cofh_core.disabled";
    }

    public boolean isEnabled() {

        return enable;
    }

    @Override
    public int getMaxCost(int level) {

        return enable ? maxDelegate(level) : -1;
    }

    protected int maxDelegate(int level) {

        return getMinCost(level) + 5;
    }

    @Override
    public int getMaxLevel() {

        return maxLevel;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {

        return enable && super.canApplyAtEnchantingTable(stack);
    }

    @Override
    public boolean isAllowedOnBooks() {

        return enable && allowOnBooks;
    }

    @Override
    public boolean isDiscoverable() {

        return enable && allowGenerateInLoot;
    }

    @Override
    public boolean isTradeable() {

        return enable && allowVillagerTrade;
    }

    @Override
    public boolean isTreasureOnly() {

        return treasureEnchantment;
    }

}
