package cofh.core.capability.templates;

import cofh.core.capability.CapabilityAreaEffect;
import cofh.core.util.helpers.AreaEffectHelper;
import cofh.lib.api.capability.IAreaEffectItem;
import com.google.common.collect.ImmutableList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AreaEffectItemWrapper implements IAreaEffectItem, ICapabilityProvider {

    private final LazyOptional<IAreaEffectItem> holder = LazyOptional.of(() -> this);

    final ItemStack areaEffectItem;

    public AreaEffectItemWrapper(ItemStack areaEffectItem) {

        this.areaEffectItem = areaEffectItem;
    }

    @Override
    public ImmutableList<BlockPos> getAreaEffectBlocks(BlockPos pos, Player player) {

        return AreaEffectHelper.getAreaEffectBlocks(areaEffectItem, pos, player);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull final Capability<T> cap, final @Nullable Direction side) {

        return CapabilityAreaEffect.AREA_EFFECT_ITEM_CAPABILITY.orEmpty(cap, this.holder);
    }

}
