package cofh.core.common.capability.templates;

import cofh.core.util.helpers.ArcheryHelper;
import cofh.lib.api.capability.IArcheryBowItem;
import cofh.lib.util.helpers.MathHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ArcheryBowItemWrapper implements IArcheryBowItem {

    private final float accuracyModifier;
    private final float damageModifier;
    private final float velocityModifier;

    final ItemStack bowItem;

    public ArcheryBowItemWrapper(ItemStack bowItem, float accuracyModifier, float damageModifier, float velocityModifier) {

        this.bowItem = bowItem;

        this.accuracyModifier = MathHelper.clamp(accuracyModifier, 0.1F, 10.0F);
        this.damageModifier = MathHelper.clamp(damageModifier, 0.1F, 10.0F);
        this.velocityModifier = MathHelper.clamp(velocityModifier, 0.1F, 10.0F);
    }

    public ArcheryBowItemWrapper(ItemStack bowItem) {

        this(bowItem, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public float getAccuracyModifier(Player shooter) {

        return accuracyModifier;
    }

    @Override
    public float getDamageModifier(Player shooter) {

        return damageModifier;
    }

    @Override
    public float getVelocityModifier(Player shooter) {

        return velocityModifier;
    }

    @Override
    public void onArrowLoosed(Player shooter) {

        bowItem.hurtAndBreak(1, shooter, (entity) -> entity.broadcastBreakEvent(shooter.getUsedItemHand()));
    }

    @Override
    public boolean fireArrow(ItemStack arrow, Player shooter, int charge, Level world) {

        return ArcheryHelper.fireArrow(bowItem, arrow, shooter, charge, world);
    }

}
