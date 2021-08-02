package cofh.lib.capability;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.DamageSource;

public interface IShieldItem {

    /**
     * Callback for when a block is performed with the shield. Used for example, if energy should be drained or an effect should happen.
     *
     * @param entity Entity holding the shield.
     * @param source Immediate source of the damage. True source may be an entity.
     */
    void onBlock(LivingEntity entity, DamageSource source);

}
