package cofh.core.capability;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * Implement this interface as a capability for a Quiver item which should be compatible with CoFH's improved archery handling.
 *
 * @author King Lemming
 */
public interface IArcheryAmmoItem extends IArcheryItem {

    /**
     * Handle creation of a custom arrow entity - this method actually spawns the arrow!
     *
     * @param world   World where the arrow will spawn.
     * @param shooter Player holding the bow.
     * @return Custom arrow entity which was spawned in the world.
     */
    AbstractArrow createArrowEntity(Level world, Player shooter);

    boolean isEmpty(Player shooter);

    boolean isInfinite(ItemStack bow, Player shooter);

}
