package cofh.lib.entity;

import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;

public abstract class AbstractGrenadeEntity extends ThrowableItemProjectile {

    protected static final int CLOUD_DURATION = 20;
    public int radius = 5;

    public AbstractGrenadeEntity(EntityType<? extends ThrowableItemProjectile> type, Level worldIn) {

        super(type, worldIn);
    }

    public AbstractGrenadeEntity(EntityType<? extends ThrowableItemProjectile> type, double x, double y, double z, Level worldIn) {

        super(type, x, y, z, worldIn);
    }

    public AbstractGrenadeEntity(EntityType<? extends ThrowableItemProjectile> type, LivingEntity livingEntityIn, Level worldIn) {

        super(type, livingEntityIn, worldIn);
    }

    public AbstractGrenadeEntity setRadius(int radius) {

        this.radius = radius;
        return this;
    }

    @Override
    public Packet<?> getAddEntityPacket() {

        return NetworkHooks.getEntitySpawningPacket(this);
    }

}
