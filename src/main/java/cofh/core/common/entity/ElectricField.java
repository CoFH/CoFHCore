package cofh.core.common.entity;

import cofh.core.client.particle.options.BiColorParticleOptions;
import cofh.core.init.CoreMobEffects;
import cofh.core.init.CoreParticles;
import cofh.core.util.AreaUtils;
import cofh.core.util.helpers.ArcheryHelper;
import cofh.lib.common.entity.AbstractFieldSpell;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.function.Predicate;

import static cofh.core.init.CoreEntities.ELECTRIC_FIELD;

public class ElectricField extends AbstractFieldSpell {

    protected int lastArc = 0;

    public ElectricField(EntityType<? extends ElectricField> type, Level level) {

        super(type, level);
    }

    public ElectricField(Level level, Entity owner, Vec3 pos, float power, int duration, float radius) {

        super(ELECTRIC_FIELD.get(), level, pos, owner, power, duration, radius);
    }

    @Override
    protected void setRadius(float radius) {

        this.radius = radius;
        setBoundingBox(getType().getAABB(getX(), getY(), getZ()).inflate(radius));
    }

    @Override
    public void activeTick() {

        if (!level.isClientSide()) {
            int max = 16;
            if (tickCount < duration + 10 && (lastArc >= max || random.nextInt(max - lastArc) == 0)) {
                lastArc = 0;
                summonArc();
            } else {
                ++lastArc;
            }
        }
    }

    protected void summonArc() {

        Vec3 pos = position();
        Predicate<Entity> filter = EntitySelector.LIVING_ENTITY_STILL_ALIVE;
        Entity owner = getOwner();
        if (owner != null) {
            filter = filter.and(entity -> !entity.isPassengerOfSameVehicle(owner));
        }
        float radius = getRadius();
        List<Entity> entities = AreaUtils.getEntitiesInSphere(level, pos, radius, this, filter);

        Vec3 end;
        if (rand.nextInt(5) < entities.size()) {
            end = entities.get(rand.nextInt(entities.size())).getBoundingBox().getCenter();
        } else {
            end = new Vec3(rand.nextGaussian(), -Math.abs(rand.nextGaussian()), rand.nextGaussian()).normalize().scale(radius).add(pos);
        }
        pos = pos.add(0, radius - 0.5F, 0);

        BlockHitResult raytrace = level.clip(new ClipContext(pos, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
        end = raytrace.getLocation();
        ((ServerLevel) level).sendParticles(new BiColorParticleOptions(CoreParticles.STRAIGHT_ARC.get(), 0.2F, 4, 0, 0xFFFFFFFF, 0xFFFC52A4), pos.x, pos.y, pos.z, 0, end.x, end.y, end.z, 1.0F);

        float padding = 0.1F;
        ArcheryHelper.findHitEntities(entities.stream(), pos, end, new Vec3(padding, padding, padding)).forEach(result -> {
            Entity target = result.getEntity();
            target.hurt(level.damageSources().source(getDamageType(), this, owner == null ? this : owner), power * 4.0F); // TODO directionality
            if (target instanceof LivingEntity living && rand.nextFloat() < power * 0.4F) {
                living.addEffect(new MobEffectInstance(CoreMobEffects.SHOCKED.get(), 80, 0, true, false, true));
            }
        });
    }

    public int getTextureIndex(int max) {

        if (tickCount < 3 || duration - tickCount < 3) {
            return 0;
        } else {
            return this.random.nextInt(max - 1) + 1;
        }
    }

    protected ResourceKey<DamageType> getDamageType() {

        return DamageTypes.LIGHTNING_BOLT;
    }

}
