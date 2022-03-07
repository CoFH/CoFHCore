package cofh.lib.tileentity;

import cofh.lib.util.control.ISecurable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

public interface IAreaEffectTile {

    AABB getArea();

    BlockPos pos();

    default boolean canPlayerAccess(Player player) {

        return !(this instanceof ISecurable) || ((ISecurable) this).canAccess(player);
    }

    default int getColor() {

        return 0xFFFFFFFF;
    }

}
