package cofh.core.common.entity;

import cofh.lib.common.entity.AbstractAoESpell;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

import static cofh.core.init.CoreEntities.ELECTRIC_ARC;
import static cofh.core.init.CoreMobEffects.SHOCKED;
import static cofh.core.init.CoreParticles.PLASMA;
import static cofh.core.init.CoreParticles.SPARK;

public class ElectricArc extends AbstractAoESpell {

    protected boolean cosmetic = false;
    public long seed = random.nextLong();

    public ElectricArc(EntityType<? extends ElectricArc> type, Level level) {

        super(type, level);
    }

    public ElectricArc(Level level, Vec3 pos, @Nullable Entity owner) {

        super(ELECTRIC_ARC.get(), level, pos, owner, 5.0F);
    }

    public ElectricArc(Level level, @Nullable Entity owner, Entity target) {

        this(level, target.position(), owner);
    }

    @Override
    public void activeTick() {

        if (level.isClientSide) {
            level.addParticle(SPARK.get(), this.getX() + random.nextGaussian() * getRadius(), this.getY() + random.nextFloat() * 0.25F, this.getZ() + random.nextGaussian() * getRadius(), 0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    public int getDuration() {

        return 10;
    }

    @Override
    protected float getRadius() {

        return 1.5F;
    }

    @Override
    public void onCast() {

        if (level.isClientSide) {
            level.addParticle(PLASMA.get(), this.getX(), this.getY() + 6, this.getZ(), 0.0D, 0.0D, 0.0D);
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
        Entity owner = getOwner();
        for (Entity entity : level.getEntities(this, this.getBoundingBox().inflate(getRadius()), Entity::isAlive)) {
            hitSomething |= attack(entity, owner);
        }
        return hitSomething;
    }

    public boolean attack(Entity entity, Entity owner) {

        if (entity.equals(owner)) {
            return false;
        }
        if (entity.hurt(entity.level.damageSources().source(getDamageType(), this, owner == null ? this : owner), getPower())) {
            if (entity instanceof LivingEntity living) {
                living.addEffect(new MobEffectInstance(SHOCKED.get(), 100, 0));
            }
            return true;
        }
        return false;
    }

    public ElectricArc setCosmetic(boolean cosmetic) {

        this.cosmetic = cosmetic;
        return this;
    }

    protected ResourceKey<DamageType> getDamageType() {

        return DamageTypes.LIGHTNING_BOLT;
    }

}
