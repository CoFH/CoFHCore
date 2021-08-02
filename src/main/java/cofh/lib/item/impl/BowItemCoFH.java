package cofh.lib.item.impl;

import cofh.lib.capability.templates.ArcheryBowItemWrapper;
import cofh.lib.item.ICoFHItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nullable;

public class BowItemCoFH extends BowItem implements ICoFHItem {

    protected int enchantability = 1;
    protected float accuracyModifier = 1.0F;
    protected float damageModifier = 1.0F;
    protected float velocityModifier = 1.0F;

    public BowItemCoFH(Properties builder) {

        super(builder);
    }

    public BowItemCoFH(IItemTier tier, Properties builder) {

        super(builder);
        setParams(tier);
    }

    public BowItemCoFH setParams(IItemTier tier) {

        this.enchantability = tier.getEnchantability();
        this.damageModifier = tier.getAttackDamage() / 4;
        this.velocityModifier = tier.getEfficiency() / 20;
        return this;
    }

    public BowItemCoFH setParams(int enchantability, float accuracyModifier, float damageModifier, float velocityModifier) {

        this.enchantability = enchantability;
        this.accuracyModifier = accuracyModifier;
        this.damageModifier = damageModifier;
        this.velocityModifier = velocityModifier;
        return this;
    }

    @Override
    public int getItemEnchantability() {

        return enchantability;
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {

        return new ArcheryBowItemWrapper(stack, accuracyModifier, damageModifier, velocityModifier);
    }

}
