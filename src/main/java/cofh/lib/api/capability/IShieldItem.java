package cofh.lib.api.capability;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.phys.Vec3;

import static net.minecraft.tags.DamageTypeTags.BYPASSES_SHIELD;

public interface IShieldItem {

    /**
     * Whether the shield can block the given damage source. This is called in {@link LivingEntity#isDamageSourceBlocked}.
     *
     * @param entity Entity holding the shield.
     * @param source Immediate source of the damage. True source may be an entity.
     * @return Whether the damage should be blocked.
     */
    default boolean canBlock(LivingEntity entity, DamageSource source) {

        return canBlockDamageSource(entity, source);
    }

    /**
     * Callback for when a block is performed with the shield. Used for example, if energy should be drained or an effect should happen.
     *
     * @param entity Entity holding the shield.
     * @param source Immediate source of the damage. True source may be an entity.
     * @param amount The amount of damage.
     * @return How much damage was blocked. Return {@param amount} if the damage should be fully blocked.
     */
    default float onBlock(LivingEntity entity, DamageSource source, float amount) {

        return amount;
    }

    /**
     * Vanilla implementation.
     */
    static boolean canBlockDamageSource(LivingEntity living, DamageSource source) {

        Entity entity = source.getDirectEntity();
        if (entity instanceof AbstractArrow arrow) {
            if (arrow.getPierceLevel() > 0) {
                return false;
            }
        }
        if (!source.is(BYPASSES_SHIELD) && living.isBlocking()) {
            return canBlockDamagePosition(living, source.getSourcePosition());
        }
        return false;
    }

    static boolean canBlockDamagePosition(LivingEntity living, Vec3 sourcePos) {

        if (sourcePos != null) {
            return new Vec3(living.getX() - sourcePos.x(), 0.0D, living.getZ() - sourcePos.z()).dot(living.getViewVector(1.0F)) < 0.0D;
        }
        return false;
    }

}
