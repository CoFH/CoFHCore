package cofh.lib.capability.templates;

import cofh.lib.capability.IArcheryBowItem;
import cofh.lib.util.helpers.ArcheryHelper;
import cofh.lib.util.helpers.MathHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static cofh.lib.capability.CapabilityArchery.BOW_ITEM_CAPABILITY;

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
    public float getAccuracyModifier(PlayerEntity shooter) {

        return accuracyModifier;
    }

    @Override
    public float getDamageModifier(PlayerEntity shooter) {

        return damageModifier;
    }

    @Override
    public float getVelocityModifier(PlayerEntity shooter) {

        return velocityModifier;
    }

    @Override
    public void onArrowLoosed(PlayerEntity shooter) {

        bowItem.damageItem(1, shooter, (entity) -> entity.sendBreakAnimation(shooter.getActiveHand()));
    }

    @Override
    public boolean fireArrow(ItemStack arrow, PlayerEntity shooter, int charge, World world) {

        return ArcheryHelper.fireArrow(bowItem, arrow, shooter, charge, world);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull final Capability<T> cap, final @Nullable Direction side) {

        return BOW_ITEM_CAPABILITY.orEmpty(cap, this.holder);
    }

}
