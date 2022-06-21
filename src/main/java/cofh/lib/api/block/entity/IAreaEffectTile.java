package cofh.lib.api.block.entity;

import cofh.lib.api.control.ISecurable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

public interface IAreaEffectTile extends ITileLocation {

    AABB getArea();

    default boolean canPlayerAccess(Player player) {

        return !(this instanceof ISecurable) || ((ISecurable) this).canAccess(player);
    }

    default int getColor() {

        return 0xFFFFFFFF;
    }

}
