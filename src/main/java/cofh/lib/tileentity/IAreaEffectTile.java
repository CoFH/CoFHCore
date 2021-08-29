package cofh.lib.tileentity;

import cofh.lib.util.control.ISecurable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

public interface IAreaEffectTile {

    AxisAlignedBB getArea();

    BlockPos pos();

    default boolean canPlayerAccess(PlayerEntity player) {

        return !(this instanceof ISecurable) || ((ISecurable) this).canAccess(player);
    }

    default int getColor() {

        return 0xFFFFFFFF;
    }

}
