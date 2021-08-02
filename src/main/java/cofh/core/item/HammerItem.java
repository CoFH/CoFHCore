package cofh.core.item;

import cofh.lib.capability.templates.AreaEffectMiningItemWrapper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nullable;

import static cofh.lib.util.constants.ToolTypes.HAMMER;

public class HammerItem extends PickaxeItem {

    private static final float DEFAULT_ATTACK_DAMAGE = 4.0F;
    private static final float DEFAULT_ATTACK_SPEED = -3.4F;
    private static final int DEFAULT_BASE_AREA = 1;

    private final int radius;

    public HammerItem(IItemTier tier, float attackDamageIn, float attackSpeedIn, int radius, Properties builder) {

        super(tier, (int) attackDamageIn, attackSpeedIn, builder.addToolType(HAMMER, tier.getHarvestLevel()));
        this.radius = radius;
    }

    public HammerItem(IItemTier tier, float attackDamageIn, float attackSpeedIn, Properties builder) {

        this(tier, attackDamageIn, attackSpeedIn, DEFAULT_BASE_AREA, builder.addToolType(HAMMER, tier.getHarvestLevel()));
    }

    public HammerItem(IItemTier tier, float attackDamageIn, Properties builder) {

        this(tier, attackDamageIn, DEFAULT_ATTACK_SPEED, DEFAULT_BASE_AREA, builder.addToolType(HAMMER, tier.getHarvestLevel()));
    }

    public HammerItem(IItemTier tier, Properties builder) {

        this(tier, DEFAULT_ATTACK_DAMAGE, DEFAULT_ATTACK_SPEED, DEFAULT_BASE_AREA, builder.addToolType(HAMMER, tier.getHarvestLevel()));
    }

    //    @Override
    //    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
    //
    //        return super.canApplyAtEnchantingTable(stack, enchantment) || enchantment.canApply(new ItemStack(Items.IRON_PICKAXE));
    //    }

    @Override
    public boolean canDisableShield(ItemStack stack, ItemStack shield, LivingEntity entity, LivingEntity attacker) {

        return true;
    }

    @Nullable
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {

        return new AreaEffectMiningItemWrapper(stack, radius, AreaEffectMiningItemWrapper.Type.HAMMER);
    }

}
