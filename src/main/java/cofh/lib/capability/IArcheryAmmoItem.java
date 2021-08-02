package cofh.lib.capability;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

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
    AbstractArrowEntity createArrowEntity(World world, PlayerEntity shooter);

    boolean isEmpty(PlayerEntity shooter);

    boolean isInfinite(ItemStack bow, PlayerEntity shooter);

}
