package cofh.core.common.capability.templates;

import cofh.core.util.helpers.AreaEffectHelper;
import cofh.lib.api.capability.IAreaEffectHandler;
import com.google.common.collect.ImmutableList;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class AreaEffectItemWrapper implements IAreaEffectHandler {

    final ItemStack areaEffectItem;

    public AreaEffectItemWrapper(ItemStack areaEffectItem) {

        this.areaEffectItem = areaEffectItem;
    }

    @Override
    public ImmutableList<BlockPos> getAreaEffectBlocks(BlockPos pos, Player player) {

        return AreaEffectHelper.getAreaEffectBlocks(areaEffectItem, pos, player);
    }

}
