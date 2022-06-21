package cofh.lib.api.capability;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

public interface IShieldItem {

    /**
     * Callback for when a block is performed with the shield. Used for example, if energy should be drained or an effect should happen.
     *
     * @param entity Entity holding the shield.
     * @param source Immediate source of the damage. True source may be an entity.
     */
    void onBlock(LivingEntity entity, DamageSource source, float amount);

}
