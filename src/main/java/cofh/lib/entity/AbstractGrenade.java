package cofh.lib.entity;

import cofh.core.client.particle.options.BiColorParticleOptions;
import cofh.core.client.particle.options.CylindricalParticleOptions;
import cofh.lib.block.IDetonatable;
import cofh.lib.util.helpers.MathHelper;
import cofh.lib.util.references.CoreReferences;
import net.minecraft.network.protocol.Packet;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

import java.util.Arrays;

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
            //this.detonate(result.getLocation());
            this.level.broadcastEntityEvent(this, (byte) 3);
            this.discard();
        }
    }

    @Override
    public void handleEntityEvent(byte event) {

        if (event == 3) {
            level.addParticle(new CylindricalParticleOptions(CoreReferences.BLAST_WAVE_PARTICLE, radius, radius * 1.5F, 2.5F), this.getX(), this.getY(), this.getZ(), 0, 0, 0);
            //this.level.addParticle(ParticleTypes.EXPLOSION, this.getX(), this.getY(), this.getZ(), 1.0, 0, 0);
            //ParticleHelper.radial(level, CoreReferences.EXPLOSION_PARTICLE, this.getX(), this.getY(), this.getZ(), 1.0F, radius, 1.5F, 0xFF0000FF);
            Vec3 pos = this.position().add(0, 2, 0);
            float speed = 2.0F;
            for (Vec3 vel : icoPoints(pos, speed)) {
                //Vec3 vel = new Vec3(random.nextFloat(-1, 1), random.nextFloat(-1, 1), random.nextFloat(-1, 1)).normalize().scale(speed).add(pos);
                //ParticleHelper.p2p(level, CoreReferences.BULLET_PARTICLE, pos.x, pos.y, pos.z, vel.x, vel.y, vel.z, 0.5F, 1.0F, 0xd0d9f5FF, 0xd0d9f5FF);
                level.addParticle(new BiColorParticleOptions(CoreReferences.SHARD_PARTICLE, 1.0F, 3.0F, 0xd0d9f5FF, 0x0000FFFF), pos.x, pos.y, pos.z, vel.x, vel.y, vel.z);
            }
            this.level.playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 0.5F, (1.0F + (this.level.random.nextFloat() - this.level.random.nextFloat()) * 0.2F) * 0.7F, false);
        } else {
            super.handleEntityEvent(event);
        }
    }

    public static Vec3[] icoPoints(Vec3 center, float rad) {

        float phi = (float) MathHelper.PHI * rad;
        float inv = rad / (float) MathHelper.PHI;
        Vec3[] pts = {new Vec3(phi, inv, 0), new Vec3(-phi, inv, 0), new Vec3(phi, -inv, 0), new Vec3(-phi, -inv, 0),
                new Vec3(0, phi, inv), new Vec3(0, -phi, inv), new Vec3(0, phi, -inv), new Vec3(0, -phi, -inv),
                new Vec3(inv, 0, phi), new Vec3(-inv, 0, phi), new Vec3(inv, 0, -phi), new Vec3(-inv, 0, -phi),
                new Vec3(rad, rad, rad), new Vec3(rad, rad, -rad), new Vec3(rad, -rad, rad), new Vec3(rad, -rad, -rad),
                new Vec3(-rad, rad, rad), new Vec3(-rad, rad, -rad), new Vec3(-rad, -rad, rad), new Vec3(-rad, -rad, -rad)};
        return Arrays.stream(pts).map(center::add).toArray(Vec3[]::new);
    }

    @Override
    public Packet<?> getAddEntityPacket() {

        return NetworkHooks.getEntitySpawningPacket(this);
    }

}
