package cofh.lib.capability.templates;

import cofh.lib.capability.IArcheryAmmoItem;
import cofh.lib.util.helpers.ArcheryHelper;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
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
    public void onArrowLoosed(Player shooter) {

        ammoItem.shrink(1);
    }

    @Override
    public AbstractArrow createArrowEntity(Level world, Player shooter) {

        return ArcheryHelper.createDefaultArrow(world, ammoItem, shooter);
    }

    @Override
    public boolean isEmpty(Player shooter) {

        return ammoItem.isEmpty();
    }

    @Override
    public boolean isInfinite(ItemStack bow, Player shooter) {

        return shooter != null && shooter.getAbilities().instabuild || getItemEnchantmentLevel(Enchantments.INFINITY_ARROWS, bow) > 0 && ammoItem.getItem().getClass() == ArrowItem.class;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull final Capability<T> cap, final @Nullable Direction side) {

        return AMMO_ITEM_CAPABILITY.orEmpty(cap, this.holder);
    }

}
