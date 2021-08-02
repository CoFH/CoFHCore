package cofh.lib.capability.templates;

import cofh.lib.util.helpers.AreaEffectHelper;
import com.google.common.collect.ImmutableList;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

import static cofh.lib.util.Utils.getItemEnchantmentLevel;
import static cofh.lib.util.references.EnsorcReferences.EXCAVATING;

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
    public ImmutableList<BlockPos> getAreaEffectBlocks(BlockPos pos, PlayerEntity player) {

        if (type == Type.SICKLE) {
            return AreaEffectHelper.getBlocksCentered(areaEffectItem, pos, player, radius, depth);
        }
        return AreaEffectHelper.getBreakableBlocksRadius(areaEffectItem, pos, player, radius + getItemEnchantmentLevel(EXCAVATING, areaEffectItem));
    }

}
