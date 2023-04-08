package cofh.core.entity;

import cofh.core.client.particle.options.BiColorParticleOptions;
import cofh.core.init.CoreMobEffects;
import cofh.core.init.CoreParticles;
import cofh.core.util.AreaUtils;
import cofh.core.util.helpers.ArcheryHelper;
import cofh.lib.entity.AbstractAoESpell;
import cofh.lib.util.helpers.MathHelper;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;

import java.util.List;
import java.util.function.Predicate;

import static cofh.core.init.CoreEntities.ELECTRIC_FIELD;

public class ElectricField extends AbstractAoESpell implements IEntityAdditionalSpawnData {

    protected float power;
    protected int lastArc = 0;

    public ElectricField(EntityType<? extends ElectricField> type, Level level) {

        super(type, level);
    }

    public ElectricField(Level level, LivingEntity owner, Vec3 pos, float radius, int duration, float power) {

        this(ELECTRIC_FIELD.get(), level);
        this.moveTo(pos);
        this.owner = owner;
        this.radius = radius;
        this.duration = duration;
        this.power = power;
        setBoundingBox(getBoundingBox().inflate(radius));
    }

    public ElectricField(Level level, Vec3 pos, float radius, int duration) {

        this(level, null, pos, radius, duration, 1.0F);
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
        if (owner != null) {
            filter = filter.and(entity -> !entity.isPassengerOfSameVehicle(owner));
        }
        List<Entity> entities = AreaUtils.getEntitiesInSphere(level, pos, radius, this, filter);

        Vec3 end;
        if (random.nextInt(5) < entities.size()) {
            end = entities.get(random.nextInt(entities.size())).getBoundingBox().getCenter();
        } else {
            end = new Vec3(random.nextGaussian(), -Math.abs(random.nextGaussian()), random.nextGaussian()).normalize().scale(radius).add(pos);
        }
        pos = pos.add(0, radius - 0.5F, 0);

        BlockHitResult raytrace = level.clip(new ClipContext(pos, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
        end = raytrace.getLocation();
        ((ServerLevel) level).sendParticles(new BiColorParticleOptions(CoreParticles.STRAIGHT_ARC.get(), 0.2F, 7, 0, 0xFFFFFFFF, 0xFFFC52A4), pos.x, pos.y, pos.z, 0, end.x, end.y, end.z, 1.0F);

        float padding = 0.1F;
        ArcheryHelper.findHitEntities(entities.stream(), pos, end, new Vec3(padding, padding, padding)).forEach(result -> {
            Entity target = result.getEntity();
            target.hurt(DamageSource.LIGHTNING_BOLT, power * 4.0F); //TODO damage source, directionality
            if (target instanceof LivingEntity living && random.nextFloat() < power * 0.4F) {
                living.addEffect(new MobEffectInstance(CoreMobEffects.SHOCKED.get(), 80, 0, true, false, true));
            }
        });
    }

    public float getRadius() {

        return radius;
    }

    @OnlyIn(Dist.CLIENT)
    public int getTextureIndex(int max) {

        if (tickCount < 3 || duration - tickCount < 3) {
            return 0;
        } else {
            return this.random.nextInt(max - 1) + 1;
        }
    }

    @Override
    public void writeSpawnData(FriendlyByteBuf buffer) {

        buffer.writeInt(duration);
        buffer.writeFloat(radius);
    }

    @Override
    public void readSpawnData(FriendlyByteBuf additionalData) {

        duration = additionalData.readInt();
        radius = additionalData.readFloat();
        setBoundingBox(getBoundingBox().inflate(radius));
    }

}
