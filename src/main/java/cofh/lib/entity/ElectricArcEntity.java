package cofh.lib.entity;

import cofh.lib.util.references.CoreReferences;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

import static cofh.lib.util.references.CoreReferences.ELECTRIC_ARC_ENTITY;
import static cofh.lib.util.references.CoreReferences.SHOCKED;

public class ElectricArcEntity extends Entity {

    @Nullable
    protected LivingEntity owner = null;
    @Nullable
    protected Entity target = null;
    protected boolean cosmetic = false;
    public static final int duration = 1000;
    public float damage = 5.0F;
    public float radius = 1.0F;
    public long seed = random.nextLong();

    public ElectricArcEntity(EntityType<? extends ElectricArcEntity> type, World world) {

        super(type, world);
        noCulling = true;
        this.yRotO = this.yRot = -360 * random.nextFloat();
    }

    public ElectricArcEntity(World world, Vector3d pos) {

        this(ELECTRIC_ARC_ENTITY, world);
        this.moveTo(pos);
    }

    public ElectricArcEntity(World world, Entity target) {

        this(world, target.position());
        this.target = target;
    }

    @Override
    public SoundCategory getSoundSource() {

        return owner == null ? SoundCategory.NEUTRAL : owner.getSoundSource();
    }

    @Override
    public void tick() {

        if (level.isClientSide) {
            level.addParticle(CoreReferences.SPARK_PARTICLE, this.getX() + random.nextGaussian() * radius, this.getY() + random.nextFloat() * 0.25F, this.getZ() + random.nextGaussian() * radius, 0.0D, 0.0D, 0.0D);
            if (this.firstTick) {
                level.addParticle(CoreReferences.PLASMA_PARTICLE, this.getX(), this.getY() + 10, this.getZ(), 0.0D, 0.0D, 0.0D);
            }
        } else {
            if (firstTick) {
                strike();
            }
            if (this.tickCount >= duration) {
                this.remove();
            }
        }

        this.firstTick = false;
    }

    public boolean strike() {

        //TODO: sounds
        //this.level.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.LIGHTNING_BOLT_THUNDER, getSoundSource(), 2.0F, 0.8F + this.random.nextFloat() * 0.2F);
        //this.level.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.LIGHTNING_BOLT_IMPACT, getSoundSource(), 2.0F, 0.5F + this.random.nextFloat() * 0.2F);
        if (cosmetic) {
            return false;
        }
        if (target != null) {
            return attack(target);
        }
        boolean hitSomething = false;
        float r2 = radius * radius;
        for (Entity entity : level.getEntities(this, this.getBoundingBox().inflate(radius), Entity::isAlive)) {
            if (this.distanceToSqr(entity) < r2) {
                hitSomething |= attack(entity);
            }
        }
        return hitSomething;
    }

    public boolean attack(Entity entity) {

        if (entity.hurt(DamageSource.LIGHTNING_BOLT, this.damage)) {
            if (entity instanceof LivingEntity) {
                ((LivingEntity) entity).addEffect(new EffectInstance(SHOCKED, 100, 0));
            }
            return true;
        }
        return false;
    }

    public ElectricArcEntity setDamage(float damage) {

        this.damage = damage;
        return this;
    }

    public ElectricArcEntity setRadius(float radius) {

        this.radius = radius;
        return this;
    }

    public ElectricArcEntity setOwner(LivingEntity owner) {

        this.owner = owner;
        return this;
    }

    public ElectricArcEntity setCosmetic(boolean cosmetic) {

        this.cosmetic = cosmetic;
        return this;
    }

    @Override
    protected void defineSynchedData() {

    }

    @Override
    protected void readAdditionalSaveData(CompoundNBT nbt) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundNBT nbt) {

    }

    @Override
    public IPacket<?> getAddEntityPacket() {

        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @OnlyIn(Dist.CLIENT)
    public boolean shouldRenderAtSqrDistance(double distSqr) {

        double d0 = 64.0D * getViewScale();
        return distSqr < d0 * d0;
    }

}
