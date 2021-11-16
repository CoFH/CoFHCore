package cofh.lib.item.impl;

import cofh.lib.capability.templates.ArcheryBowItemWrapper;
import cofh.lib.item.ICoFHItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nullable;

public class CrossbowItemCoFH extends CrossbowItem implements ICoFHItem {

    protected int enchantability = 1;
    protected float accuracyModifier = 1.0F;
    protected float damageModifier = 1.0F;
    protected float velocityModifier = 1.0F;

    public CrossbowItemCoFH(Properties builder) {

        super(builder);
    }

    public CrossbowItemCoFH(IItemTier tier, Properties builder) {

        super(builder);
        setParams(tier);
    }

    public CrossbowItemCoFH setParams(IItemTier tier) {

        this.enchantability = tier.getEnchantmentValue();
        this.damageModifier = tier.getAttackDamageBonus() / 4;
        this.velocityModifier = tier.getSpeed() / 20;
        return this;
    }

    public CrossbowItemCoFH setParams(int enchantability, float accuracyModifier, float damageModifier, float velocityModifier) {

        this.enchantability = enchantability;
        this.accuracyModifier = accuracyModifier;
        this.damageModifier = damageModifier;
        this.velocityModifier = velocityModifier;
        return this;
    }

    @Override
    public int getEnchantmentValue() {

        return enchantability;
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {

        return new ArcheryBowItemWrapper(stack, accuracyModifier, damageModifier, velocityModifier);
    }

}
