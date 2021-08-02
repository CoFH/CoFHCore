package cofh.lib.capability;

import net.minecraft.entity.player.PlayerEntity;

public interface IArcheryItem {

    /**
     * Callback for when an arrow is loosed. Used for example, if energy should be drained or an effect should happen.
     *
     * @param shooter Player holding the weapon.
     */
    void onArrowLoosed(PlayerEntity shooter);

}
