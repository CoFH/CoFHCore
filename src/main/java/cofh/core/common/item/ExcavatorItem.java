package cofh.core.common.item;

import cofh.lib.common.item.ShovelItemCoFH;
import net.minecraft.world.item.Tier;

public class ExcavatorItem extends ShovelItemCoFH {

    private static final float DEFAULT_ATTACK_DAMAGE = 2.0F;
    private static final float DEFAULT_ATTACK_SPEED = -3.2F;
    private static final int DEFAULT_BASE_AREA = 1;

    private final int radius;

    public ExcavatorItem(Tier tier, float attackDamageIn, float attackSpeedIn, int radius, Properties builder) {

        super(tier, attackDamageIn, attackSpeedIn, builder.durability(tier.getUses() * 4));
        this.radius = radius;
    }

    public ExcavatorItem(Tier tier, float attackDamageIn, float attackSpeedIn, Properties builder) {

        this(tier, attackDamageIn, attackSpeedIn, DEFAULT_BASE_AREA, builder);
    }

    public ExcavatorItem(Tier tier, float attackDamageIn, Properties builder) {

        this(tier, attackDamageIn, DEFAULT_ATTACK_SPEED, DEFAULT_BASE_AREA, builder);
    }

    public ExcavatorItem(Tier tier, Properties builder) {

        this(tier, DEFAULT_ATTACK_DAMAGE, DEFAULT_ATTACK_SPEED, DEFAULT_BASE_AREA, builder);
    }

}
