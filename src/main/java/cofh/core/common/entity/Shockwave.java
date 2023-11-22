package cofh.core.common.entity;

import cofh.core.util.AreaUtils;
import cofh.core.util.helpers.vfx.VFXHelper;
import cofh.lib.common.entity.AbstractAoESpell;
import cofh.lib.util.helpers.MathHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector2i;

import java.util.SplittableRandom;

import static cofh.core.init.CoreMobEffects.SUNDERED;

public abstract class Shockwave extends AbstractAoESpell {

    protected final SplittableRandom rand = new SplittableRandom();

    public Shockwave(EntityType<? extends Shockwave> type, Level level) {

        super(type, level);
    }

    public Shockwave(EntityType<? extends Shockwave> type, Level level, Vec3 pos, Entity owner, float power) {

        super(type, level, pos, owner, power);
    }

    @Override
    public void activeTick() {

        if (level.isClientSide()) {
            BlockPos center = this.blockPosition();
            float speed = getSpeed();
            float radius = getRadius();
            BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
            BlockPos.MutableBlockPos above = new BlockPos.MutableBlockPos();
            VFXHelper.SHOCKWAVE_OFFSETS
                    .subMap(Math.min(tickCount * speed, radius), Math.min((tickCount + 1) * speed, radius))
                    .values().forEach(offs -> {
                        for (Vector2i offset : offs) {
                            if (rand.nextBoolean()) {
                                for (int y = 1; y >= -1; --y) {
                                    cursor.set(center.getX() + offset.x, center.getY() + y, center.getZ() + offset.y);
                                    BlockState state = level.getBlockState(cursor);
                                    if (!state.isAir() && state.isRedstoneConductor(level, cursor) && state.isCollisionShapeFullBlock(level, cursor) && !state.hasBlockEntity()) {
                                        above.set(cursor.getX(), cursor.getY() + 1, cursor.getZ());
                                        if (!level.getBlockState(above).isCollisionShapeFullBlock(level, above)) {
                                            level.addDestroyBlockEffect(cursor, state);
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    });
        } else if (tickCount < getBaseDuration() && attack()) {
            onHit();
        }
    }

    public float getSpeed() {

        return 1.0F;
    }

    protected int getBaseDuration() {

        return MathHelper.ceil(getRadius() / getSpeed());
    }

    @Override
    public int getDuration() {

        return getBaseDuration() + 5;
    }

    @Override
    public float getRadius() {

        return getBbWidth() * 0.5F;
    }

    public int getDebuffDuration() {

        return 100;
    }

    public boolean attack() {

        boolean hitSomething = false;
        float speed = getSpeed();
        float dist = tickCount * speed;
        float r = speed * 0.75F;
        float r2 = r * r;
        float strength = getPower() / Math.max(r * 0.25F, 1.0F);
        float damage = getDamageMultiplier() * strength;
        Vec3 pos = this.position();
        Entity owner = getOwner();
        for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, getBoundingBox(), EntitySelector.NO_CREATIVE_OR_SPECTATOR)) {
            if (entity.equals(owner)) {
                continue;
            }
            Vec3 relPos = entity.position().subtract(pos);
            Vec3 center = new Vec3(relPos.x, 0, relPos.z).normalize().scale(dist).add(pos);
            if (AreaUtils.closestPointOnAABB(center, entity.getBoundingBox()).subtract(center).horizontalDistanceSqr() > r2) {
                continue;
            }
            DamageSource source = getDamageSource();
            if (entity.hurt(source, damage)) {
                hitSomething = true;
                entity.addEffect(new MobEffectInstance(SUNDERED.get(), getDebuffDuration(), MathHelper.weightedRound(strength, this.random) - 1, false, false));
                entity.knockback(0.8F, -relPos.x(), -relPos.z());
            }
        }
        return hitSomething;
    }

    protected float getDamageMultiplier() {

        return 8.0F;
    }

    protected void onHit() {

    }

    protected abstract DamageSource getDamageSource();

}
