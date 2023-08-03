package cofh.core.entity;

import cofh.core.client.particle.options.CylindricalParticleOptions;
import cofh.core.init.CoreEntities;
import cofh.core.util.AreaUtils;
import cofh.core.util.helpers.vfx.VFXHelper;
import cofh.lib.entity.AbstractAoESpell;
import cofh.lib.util.helpers.MathHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import static cofh.core.init.CoreMobEffects.SUNDERED;
import static cofh.core.init.CoreParticles.SHOCKWAVE;

public class Shockwave extends AbstractAoESpell {

    public static final float speed = 1.0F;

    public float power;
    public int debuffDuration = 100;

    public Shockwave(EntityType<? extends Shockwave> type, Level world) {

        super(type, world);
        radius = 8.0F;
        duration = MathHelper.ceil(radius / speed);
    }

    public Shockwave(Level world, LivingEntity attacker, Vec3 pos, float power) {

        this(CoreEntities.SHOCKWAVE.get(), world);
        this.owner = attacker;
        setPos(pos.x(), pos.y(), pos.z());
        this.power = power;
    }

    @Override
    public void onCast() {

        if (level.isClientSide) {
            BlockPos pos = this.blockPosition();
            level.addParticle(new CylindricalParticleOptions(SHOCKWAVE.get(), radius * 2, duration + 5, 0.6F), pos.getX(), pos.getY(), pos.getZ(), 0, 0, 0);
        }
    }

    @Override
    public void activeTick() {

        if (level.isClientSide()) {
            BlockPos center = this.blockPosition();
            VFXHelper.SHOCKWAVE_OFFSETS
                    .subMap(Math.min(tickCount * speed, radius), Math.min((tickCount + 1) * speed, radius))
                    .values().forEach(offs -> offs.forEach(off -> {
                        if (level.getRandom().nextBoolean()) {
                            for (int y = 1; y >= -1; --y) {
                                BlockPos pos = center.offset(off.apply(y));
                                BlockState state = level.getBlockState(pos);
                                if (!state.isAir() && state.isRedstoneConductor(level, pos) && state.isCollisionShapeFullBlock(level, pos) &&
                                        !state.hasBlockEntity() && !level.getBlockState(pos.above()).isCollisionShapeFullBlock(level, pos.above())) {
                                    level.addDestroyBlockEffect(pos, state);
                                    return;
                                }
                            }
                        }
                    }));
        } else {
            attack();
        }
    }

    public int getDuration() {

        return duration;
    }

    public float getRadius() {

        return radius;
    }

    public boolean attack() {

        boolean hitSomething = false;
        float dist = tickCount * speed;
        float r = speed * 0.75F;
        float r2 = r * r;
        float strength = power * (duration - (tickCount * 0.5F)) / duration;
        float damage = strength * 8.0F;

        Vec3 pos = this.position();
        for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(dist + r, 2, dist + r), EntitySelector.NO_CREATIVE_OR_SPECTATOR)) {
            if (entity.equals(this.owner)) {
                continue;
            }
            Vec3 relPos = entity.position().subtract(pos);
            Vec3 center = new Vec3(relPos.x, 0, relPos.z).normalize().scale(dist).add(pos);
            if (AreaUtils.closestPointOnAABB(center, entity.getBoundingBox()).subtract(center).horizontalDistanceSqr() > r2) {
                continue;
            }
            // TODO damage source
            DamageSource source = this.owner instanceof Player player ? DamageSource.playerAttack(player) : DamageSource.mobAttack(this.owner);
            if (entity.hurt(source, damage)) {
                hitSomething = true;
                entity.addEffect(new MobEffectInstance(SUNDERED.get(), debuffDuration, MathHelper.weightedRound(strength, this.random) - 1, false, false));
                entity.knockback(0.8F, -relPos.x(), -relPos.z());
            }
        }
        return hitSomething;
    }

}
