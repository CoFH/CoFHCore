package cofh.lib.common.entity;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public abstract class DamagingProjectile extends ProjectileCoFH {

    public DamagingProjectile(EntityType<? extends DamagingProjectile> type, Level level) {

        super(type, level);
    }

    public DamagingProjectile(EntityType<? extends DamagingProjectile> type, Level level, Entity owner, Vec3 position, Vec3 velocity, float power) {

        super(type, level, owner, position, velocity, power);
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {

        if (result.getEntity().hurt(getDamageSource(result), getDamage(result))) {
            onHurt(result);
        }
        setPos(result.getLocation());
    }

    protected void onHurt(EntityHitResult result) {

    }

    protected abstract float getDamage(EntityHitResult result);

    protected abstract ResourceKey<DamageType> getDamageType(EntityHitResult result);

    protected DamageSource getDamageSource(EntityHitResult result) {

        Entity owner = getOwner();
        ResourceKey<DamageType> type = getDamageType(result);
        if (owner == null) {
            return level.damageSources().source(type, this);
        }
        return level.damageSources().source(type, this, owner);
    }

}
