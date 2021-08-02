package cofh.lib.capability.templates;

import cofh.lib.capability.IArcheryAmmoItem;
import cofh.lib.util.helpers.ArcheryHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static cofh.lib.capability.CapabilityArchery.AMMO_ITEM_CAPABILITY;
import static cofh.lib.util.Utils.getItemEnchantmentLevel;

public class ArcheryAmmoItemWrapper implements IArcheryAmmoItem, ICapabilityProvider {

    private final LazyOptional<IArcheryAmmoItem> holder = LazyOptional.of(() -> this);

    final ItemStack ammoItem;

    public ArcheryAmmoItemWrapper(ItemStack ammoItem) {

        this.ammoItem = ammoItem;
    }

    @Override
    public void onArrowLoosed(PlayerEntity shooter) {

        ammoItem.shrink(1);
    }

    @Override
    public AbstractArrowEntity createArrowEntity(World world, PlayerEntity shooter) {

        return ArcheryHelper.createDefaultArrow(world, ammoItem, shooter);
    }

    @Override
    public boolean isEmpty(PlayerEntity shooter) {

        return ammoItem.isEmpty();
    }

    @Override
    public boolean isInfinite(ItemStack bow, PlayerEntity shooter) {

        return shooter != null && shooter.abilities.isCreativeMode || getItemEnchantmentLevel(Enchantments.INFINITY, bow) > 0 && ammoItem.getItem().getClass() == ArrowItem.class;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull final Capability<T> cap, final @Nullable Direction side) {

        return AMMO_ITEM_CAPABILITY.orEmpty(cap, this.holder);
    }

}
