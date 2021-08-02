package cofh.lib.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileItemEntity;
import net.minecraft.network.IPacket;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public abstract class AbstractGrenadeEntity extends ProjectileItemEntity {

    protected static final int CLOUD_DURATION = 20;
    public int radius = 5;

    public AbstractGrenadeEntity(EntityType<? extends ProjectileItemEntity> type, World worldIn) {

        super(type, worldIn);
    }

    public AbstractGrenadeEntity(EntityType<? extends ProjectileItemEntity> type, double x, double y, double z, World worldIn) {

        super(type, x, y, z, worldIn);
    }

    public AbstractGrenadeEntity(EntityType<? extends ProjectileItemEntity> type, LivingEntity livingEntityIn, World worldIn) {

        super(type, livingEntityIn, worldIn);
    }

    public AbstractGrenadeEntity setRadius(int radius) {

        this.radius = radius;
        return this;
    }

    @Override
    public IPacket<?> createSpawnPacket() {

        return NetworkHooks.getEntitySpawningPacket(this);
    }

}
