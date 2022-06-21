package cofh.core.capability.templates;

import cofh.core.capability.CapabilityArchery;
import cofh.core.util.helpers.ArcheryHelper;
import cofh.lib.api.capability.IArcheryBowItem;
import cofh.lib.util.helpers.MathHelper;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ArcheryBowItemWrapper implements IArcheryBowItem, ICapabilityProvider {

    private final LazyOptional<IArcheryBowItem> holder = LazyOptional.of(() -> this);

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

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull final Capability<T> cap, final @Nullable Direction side) {

        return CapabilityArchery.BOW_ITEM_CAPABILITY.orEmpty(cap, this.holder);
    }

}
