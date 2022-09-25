package cofh.core.capability.templates;

import cofh.core.capability.IEnchantableItem;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

import static cofh.core.capability.CapabilityEnchantableItem.ENCHANTABLE_ITEM_CAPABILITY;

public class EnchantableItemWrapper implements IEnchantableItem, ICapabilityProvider {

    private final LazyOptional<IEnchantableItem> holder = LazyOptional.of(() -> this);

    final ItemStack enchantableItem;
    final Set<Enchantment> validEnchantments = new ObjectOpenHashSet<>();

    public EnchantableItemWrapper(ItemStack enchantableItem, List<Enchantment> enchantments) {

        this.enchantableItem = enchantableItem;
        this.validEnchantments.addAll(enchantments);
    }

    @Override
    public boolean supportsEnchantment(Enchantment ench) {

        return validEnchantments.contains(ench);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull final Capability<T> cap, final @Nullable Direction side) {

        return ENCHANTABLE_ITEM_CAPABILITY.orEmpty(cap, this.holder);
    }

}
