package cofh.lib.item.impl;

import net.minecraft.item.IItemTier;

import static cofh.lib.util.constants.ToolTypes.KNIFE;

public class KnifeItem extends SwordItemCoFH {

    private static final int DEFAULT_ATTACK_DAMAGE = 1;
    private static final float DEFAULT_ATTACK_SPEED = -1.0F;

    public KnifeItem(IItemTier tier, int attackDamageIn, float attackSpeedIn, Properties builder) {

        super(tier, attackDamageIn, attackSpeedIn, builder);
    }

    public KnifeItem(IItemTier tier, Properties builder) {

        this(tier, DEFAULT_ATTACK_DAMAGE, DEFAULT_ATTACK_SPEED, builder.addToolType(KNIFE, tier.getLevel()));
    }

}
