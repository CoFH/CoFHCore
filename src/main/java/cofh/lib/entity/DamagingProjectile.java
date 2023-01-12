package cofh.lib.entity;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public abstract class DamagingProjectile extends ProjectileCoFH {

    protected float power;

    public DamagingProjectile(EntityType<? extends DamagingProjectile> type, Level level) {

        super(type, level);
    }

    public DamagingProjectile(EntityType<? extends DamagingProjectile> type, Level level, Entity owner, Vec3 position, Vec3 velocity, float power) {

        super(type, level, owner, position, velocity);
        this.power = power;
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

    public float getPower() {

        return power;
    }

    public abstract float getDamage(EntityHitResult result);

    public abstract DamageSource getDamageSource(EntityHitResult result);

}
