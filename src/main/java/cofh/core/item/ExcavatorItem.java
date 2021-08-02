package cofh.core.item;

import cofh.lib.capability.templates.AreaEffectMiningItemWrapper;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShovelItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nullable;

import static cofh.lib.util.constants.ToolTypes.EXCAVATOR;

public class ExcavatorItem extends ShovelItem {

    private static final float DEFAULT_ATTACK_DAMAGE = 2.0F;
    private static final float DEFAULT_ATTACK_SPEED = -3.2F;
    private static final int DEFAULT_BASE_AREA = 1;

    private final int radius;

    public ExcavatorItem(IItemTier tier, float attackDamageIn, float attackSpeedIn, int radius, Properties builder) {

        super(tier, attackDamageIn, attackSpeedIn, builder.addToolType(EXCAVATOR, tier.getHarvestLevel()));
        this.radius = radius;
    }

    public ExcavatorItem(IItemTier tier, float attackDamageIn, float attackSpeedIn, Properties builder) {

        this(tier, attackDamageIn, attackSpeedIn, DEFAULT_BASE_AREA, builder.addToolType(EXCAVATOR, tier.getHarvestLevel()));
    }

    public ExcavatorItem(IItemTier tier, float attackDamageIn, Properties builder) {

        this(tier, attackDamageIn, DEFAULT_ATTACK_SPEED, DEFAULT_BASE_AREA, builder.addToolType(EXCAVATOR, tier.getHarvestLevel()));
    }

    public ExcavatorItem(IItemTier tier, Properties builder) {

        this(tier, DEFAULT_ATTACK_DAMAGE, DEFAULT_ATTACK_SPEED, DEFAULT_BASE_AREA, builder.addToolType(EXCAVATOR, tier.getHarvestLevel()));
    }

    //    @Override
    //    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
    //
    //        return super.canApplyAtEnchantingTable(stack, enchantment) || enchantment.canApply(new ItemStack(Items.IRON_SHOVEL));
    //    }

    @Nullable
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {

        return new AreaEffectMiningItemWrapper(stack, radius, AreaEffectMiningItemWrapper.Type.EXCAVATOR);
    }

}
