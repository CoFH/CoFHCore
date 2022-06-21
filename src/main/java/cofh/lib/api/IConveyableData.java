package cofh.lib.api;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

/**
 * Implement this interface on Objects which can write some amount of data about themselves.
 * <p>
 * This is typically for the purposes of being transferred to a similar object (Tile Entity/Entity).
 *
 * @author King Lemming
 */
public interface IConveyableData {

    /**
     * Read the data from a tag. The player object exists because this should always be called via player interaction!
     */
    default void readConveyableData(Player player, CompoundTag tag) {

    }

    /**
     * Write the data to a tag. The player object exists because this should always be called via player interaction!
     */
    default void writeConveyableData(Player player, CompoundTag tag) {

    }

}
