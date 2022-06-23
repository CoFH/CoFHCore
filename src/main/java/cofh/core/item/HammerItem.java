package cofh.core.item;

import cofh.core.capability.templates.AreaEffectMiningItemWrapper;
import cofh.lib.item.PickaxeItemCoFH;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nullable;

// TODO Hammer Tool Type.
public class HammerItem extends PickaxeItemCoFH {

    private static final float DEFAULT_ATTACK_DAMAGE = 4.0F;
    private static final float DEFAULT_ATTACK_SPEED = -3.4F;
    private static final int DEFAULT_BASE_AREA = 1;

    private final int radius;

    public HammerItem(Tier tier, float attackDamageIn, float attackSpeedIn, int radius, Properties builder) {

        super(tier, (int) attackDamageIn, attackSpeedIn, builder.durability(tier.getUses() * 4));
        this.radius = radius;
    }

    public HammerItem(Tier tier, float attackDamageIn, float attackSpeedIn, Properties builder) {

        this(tier, attackDamageIn, attackSpeedIn, DEFAULT_BASE_AREA, builder);
    }

    public HammerItem(Tier tier, float attackDamageIn, Properties builder) {

        this(tier, attackDamageIn, DEFAULT_ATTACK_SPEED, DEFAULT_BASE_AREA, builder);
    }

    public HammerItem(Tier tier, Properties builder) {

        this(tier, DEFAULT_ATTACK_DAMAGE, DEFAULT_ATTACK_SPEED, DEFAULT_BASE_AREA, builder);
    }

    @Override
    public boolean canDisableShield(ItemStack stack, ItemStack shield, LivingEntity entity, LivingEntity attacker) {

        return true;
    }

    @Nullable
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {

        return new AreaEffectMiningItemWrapper(stack, radius, AreaEffectMiningItemWrapper.Type.HAMMER);
    }

}
