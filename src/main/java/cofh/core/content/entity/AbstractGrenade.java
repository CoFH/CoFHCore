package cofh.core.content.entity;

import cofh.lib.api.IDetonatable;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.protocol.Packet;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.network.NetworkHooks;

import static cofh.core.init.CoreParticles.BLAST_WAVE;

public abstract class AbstractGrenade extends ThrowableItemProjectile implements IDetonatable {

    protected static final int CLOUD_DURATION = 20;
    public int effectAmplifier = 1;
    public int effectDuration = 300;
    public int radius = 5;

    public AbstractGrenade(EntityType<? extends ThrowableItemProjectile> type, Level worldIn) {

        super(type, worldIn);
    }

    public AbstractGrenade(EntityType<? extends ThrowableItemProjectile> type, double x, double y, double z, Level worldIn) {

        super(type, x, y, z, worldIn);
    }

    public AbstractGrenade(EntityType<? extends ThrowableItemProjectile> type, LivingEntity livingEntityIn, Level worldIn) {

        super(type, livingEntityIn, worldIn);
    }

    public AbstractGrenade setRadius(int radius) {

        this.radius = radius;
        return this;
    }

    public LivingEntity getLivingOwner() {

        return getOwner() instanceof LivingEntity ? (LivingEntity) getOwner() : null;
    }

    @Override
    protected void onHit(HitResult result) {

        if (!level.isClientSide) {
            this.detonate(result.getLocation());
            this.level.broadcastEntityEvent(this, (byte) 3);
            this.discard();
        }
    }

    @Override
    public void handleEntityEvent(byte event) {

        if (event == 3) {
            this.level.addParticle((SimpleParticleType) BLAST_WAVE.get(), this.getX(), this.getY(), this.getZ(), 1.0D, 2 * radius, 1.5F);
            this.level.addParticle(ParticleTypes.EXPLOSION, this.getX(), this.getY(), this.getZ(), 1.0D, 0.0D, 0.0D);
            this.level.playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 0.5F, (1.0F + (this.level.random.nextFloat() - this.level.random.nextFloat()) * 0.2F) * 0.7F, false);
        } else {
            super.handleEntityEvent(event);
        }
    }

    @Override
    public Packet<?> getAddEntityPacket() {

        return NetworkHooks.getEntitySpawningPacket(this);
    }

}
