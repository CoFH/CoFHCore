package cofh.lib.capability;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * Implement this interface as a capability for a Bow Item which should be compatible with CoFH's improved archery handling.
 * You can get really clever and add your own custom bow behavior by overriding the "fireArrow" method. By default, this method is a passthrough for CoFH's vanilla handling.
 *
 * @author King Lemming
 */
public interface IArcheryBowItem extends IArcheryItem {

    /**
     * Return the total accuracy multiplier for this item - LOWER IS BETTER - 1.0F is default.
     * Note that weapons and ammo can BOTH have this!
     *
     * @param shooter Player holding the bow.
     * @return Total accuracy modifier - 0.5F means arrows will shoot with half as much variance.
     */
    default float getAccuracyModifier(PlayerEntity shooter) {

        return 1.0F;
    }

    /**
     * Return the total damage multiplier for this item - 1.0F is default.
     * Note that weapons and ammo can BOTH have this!
     *
     * @param shooter Player holding the bow.
     * @return Total damage modifier - 1.1F would be 10% more.
     */
    default float getDamageModifier(PlayerEntity shooter) {

        return 1.0F;
    }

    /**
     * Return the total arrow speed multiplier for this item - 1.0F is default.
     * Note that weapons and ammo can BOTH have this!
     *
     * @param shooter Player holding the bow.
     * @return Total speed modifier - as an example, 1.1F would be 10% more.
     */
    default float getVelocityModifier(PlayerEntity shooter) {

        return 1.0F;
    }

    /**
     * This method handles the entire shooting process - these parameters are essentially passed in from the onArrowLoosed event.
     *
     * @param arrow   ItemStack representing the ammo.
     * @param shooter Player holding the bow.
     * @param charge  Amount of time the bow has been drawn.
     * @param world   World object, used to spawn the arrow.
     * @return TRUE if an arrow was actually fired.
     */
    boolean fireArrow(ItemStack arrow, PlayerEntity shooter, int charge, World world);

}
