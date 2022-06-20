package cofh.core.content.item;

import cofh.core.capability.templates.ArcheryBowItemWrapper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
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

    public BowItemCoFH(Tier tier, Properties builder) {

        super(builder);
        setParams(tier);
    }

    public BowItemCoFH setParams(Tier tier) {

        this.enchantability = tier.getEnchantmentValue();
        this.damageModifier = tier.getAttackDamageBonus() / 4;
        this.velocityModifier = tier.getSpeed() / 20;
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
    public int getEnchantmentValue() {

        return enchantability;
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {

        return new ArcheryBowItemWrapper(stack, accuracyModifier, damageModifier, velocityModifier);
    }

}
