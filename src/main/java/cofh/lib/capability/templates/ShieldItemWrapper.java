package cofh.lib.capability.templates;

import cofh.lib.capability.IShieldItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static cofh.lib.capability.CapabilityShieldItem.SHIELD_ITEM_CAPABILITY;

public class ShieldItemWrapper implements IShieldItem, ICapabilityProvider {

    private final LazyOptional<IShieldItem> holder = LazyOptional.of(() -> this);

    final ItemStack shieldItem;

    public ShieldItemWrapper(ItemStack shieldItem) {

        this.shieldItem = shieldItem;
    }

    @Override
    public void onBlock(LivingEntity entity, DamageSource source) {

    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull final Capability<T> cap, final @Nullable Direction side) {

        return SHIELD_ITEM_CAPABILITY.orEmpty(cap, this.holder);
    }

}
