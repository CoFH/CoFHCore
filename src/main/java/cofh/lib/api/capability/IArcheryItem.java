package cofh.lib.api.capability;

import net.minecraft.world.entity.player.Player;

public interface IArcheryItem {

    /**
     * Callback for when an arrow is loosed. Used for example, if energy should be drained or an effect should happen.
     *
     * @param shooter Player holding the weapon.
     */
    void onArrowLoosed(Player shooter);

}
