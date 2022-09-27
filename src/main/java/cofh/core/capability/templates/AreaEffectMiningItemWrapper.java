package cofh.core.capability.templates;

import cofh.core.util.helpers.AreaEffectHelper;
import com.google.common.collect.ImmutableList;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import static cofh.core.util.references.EnsorcIDs.ID_EXCAVATING;
import static cofh.lib.util.Utils.getEnchantment;
import static cofh.lib.util.Utils.getItemEnchantmentLevel;
import static cofh.lib.util.constants.ModIds.ID_ENSORCELLATION;

public class AreaEffectMiningItemWrapper extends AreaEffectItemWrapper {

    private final int radius;
    private final int depth;
    private final Type type;

    public enum Type {
        EXCAVATOR, HAMMER, SICKLE
    }

    public AreaEffectMiningItemWrapper(ItemStack containerIn, int radius, int depth, Type type) {

        super(containerIn);

        this.radius = radius;
        this.depth = depth;
        this.type = type;
    }

    public AreaEffectMiningItemWrapper(ItemStack containerIn, int radius, Type type) {

        this(containerIn, radius, 1, type);
    }

    @Override
    public ImmutableList<BlockPos> getAreaEffectBlocks(BlockPos pos, Player player) {

        if (type == Type.SICKLE) {
            return AreaEffectHelper.getBlocksCentered(areaEffectItem, pos, player, radius, depth);
        }
        return AreaEffectHelper.getBreakableBlocksRadius(areaEffectItem, pos, player, radius + getItemEnchantmentLevel(getEnchantment(ID_ENSORCELLATION, ID_EXCAVATING), areaEffectItem));
    }

}
