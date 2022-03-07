package cofh.lib.entity;

<<<<<<< HEAD
import cofh.lib.block.IDetonatable;
import cofh.lib.util.references.CoreReferences;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileItemEntity;
import net.minecraft.network.IPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public abstract class AbstractGrenadeEntity extends ProjectileItemEntity implements IDetonatable {
=======
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;

public abstract class AbstractGrenadeEntity extends ThrowableItemProjectile {
>>>>>>> caa1a35 (Initial 1.18.2 compile pass.)

    protected static final int CLOUD_DURATION = 20;
    public int effectAmplifier = 1;
    public int effectDuration = 300;
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

    public LivingEntity getLivingOwner() {

        return getOwner() instanceof LivingEntity ? (LivingEntity) getOwner() : null;
    }

    @Override
    protected void onHit(RayTraceResult result) {

        if (!level.isClientSide) {
            this.detonate(result.getLocation());
            this.remove();
        } else if (result.getType() != RayTraceResult.Type.ENTITY || this.tickCount >= 5) {
            this.level.addParticle(CoreReferences.BLAST_WAVE_PARTICLE, this.getX(), this.getY(), this.getZ(), 1.0D, 2 * radius, 1.5F);
            this.level.addParticle(ParticleTypes.EXPLOSION, this.getX(), this.getY(), this.getZ(), 1.0D, 0.0D, 0.0D);
            this.level.playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.GENERIC_EXPLODE, SoundCategory.BLOCKS, 0.5F, (1.0F + (this.level.random.nextFloat() - this.level.random.nextFloat()) * 0.2F) * 0.7F, false);
        }
    }

    @Override
    public Packet<?> getAddEntityPacket() {

        return NetworkHooks.getEntitySpawningPacket(this);
    }

}
