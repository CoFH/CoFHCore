package cofh.core.content.entity;

import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import static cofh.core.init.CoreEntities.ELECTRIC_ARC;
import static cofh.core.init.CoreMobEffects.SHOCKED;
import static cofh.core.init.CoreParticles.PLASMA;
import static cofh.core.init.CoreParticles.SPARK;

public class ElectricArc extends AbstractAoESpell {

    public static final int defaultDuration = 10;
    protected boolean cosmetic = false;
    public float damage = 5.0F;
    public long seed = random.nextLong();

    public ElectricArc(EntityType<? extends ElectricArc> type, Level level) {

        super(type, level);
        radius = 1.5F;
        duration = defaultDuration;
        noCulling = true;
    }

    public ElectricArc(Level level, Vec3 pos) {

        this(ELECTRIC_ARC.get(), level);
        this.moveTo(pos);
    }

    public ElectricArc(Level level, Entity target) {

        this(level, target.position());
    }

    @Override
    public void activeTick() {

        if (level.isClientSide) {
            level.addParticle((SimpleParticleType) SPARK.get(), this.getX() + random.nextGaussian() * radius, this.getY() + random.nextFloat() * 0.25F, this.getZ() + random.nextGaussian() * radius, 0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    public void onCast() {

        if (level.isClientSide) {
            level.addParticle((SimpleParticleType) PLASMA.get(), this.getX(), this.getY() + 6, this.getZ(), 0.0D, 0.0D, 0.0D);
        } else {
            strike();
        }
    }

    public boolean strike() {

        // TODO: sounds
        //this.level.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.LIGHTNING_BOLT_THUNDER, getSoundSource(), 2.0F, 0.8F + this.random.nextFloat() * 0.2F);
        //this.level.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.LIGHTNING_BOLT_IMPACT, getSoundSource(), 2.0F, 0.5F + this.random.nextFloat() * 0.2F);
        if (cosmetic) {
            return false;
        }
        boolean hitSomething = false;
        for (Entity entity : level.getEntities(this, this.getBoundingBox().inflate(radius), Entity::isAlive)) {
            hitSomething |= attack(entity);
        }
        return hitSomething;
    }

    public boolean attack(Entity entity) {

        if (entity.hurt(DamageSource.LIGHTNING_BOLT, this.damage)) {
            if (entity instanceof LivingEntity) {
                ((LivingEntity) entity).addEffect(new MobEffectInstance(SHOCKED.get(), 100, 0));
            }
            return true;
        }
        return false;
    }

    public ElectricArc setDamage(float damage) {

        this.damage = damage;
        return this;
    }

    public ElectricArc setRadius(float radius) {

        this.radius = radius;
        return this;
    }

    public ElectricArc setCosmetic(boolean cosmetic) {

        this.cosmetic = cosmetic;
        return this;
    }

    @OnlyIn (Dist.CLIENT)
    public boolean shouldRenderAtSqrDistance(double distSqr) {

        double d0 = 64.0D * getViewScale();
        return distSqr < d0 * d0;
    }

}
