package cofh.core.common.entity;

import cofh.core.client.particle.options.ColorParticleOptions;
import cofh.core.common.effect.ChilledMobEffect;
import cofh.core.util.AreaUtils;
import cofh.core.util.helpers.vfx.Color;
import cofh.lib.common.entity.AbstractAoESpell;
import cofh.lib.util.helpers.MathHelper;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;

import java.util.Random;
import java.util.function.Predicate;
import java.util.random.RandomGenerator;

import static cofh.core.init.CoreEntities.FROST_FIELD;
import static cofh.core.init.CoreParticles.FROST;
import static cofh.core.init.CoreParticles.MIST;

public class FrostField extends AbstractAoESpell implements IEntityAdditionalSpawnData {

    protected RandomGenerator rand = new Random();
    protected float power;

    public FrostField(EntityType<? extends FrostField> type, Level level) {

        super(type, level);
    }

    public FrostField(Level level, LivingEntity owner, Vec3 pos, float radius, int duration, float power) {

        this(FROST_FIELD.get(), level);
        this.moveTo(pos);
        this.owner = owner;
        this.radius = radius;
        this.duration = duration;
        this.power = power;
        setBoundingBox(getBoundingBox().inflate(radius, 0.75F, radius));
    }

    @Override
    public void activeTick() {

        Vec3 center = position();
        float r = Math.min(tickCount * 12.0F / this.duration, 1.0F);
        float n = MathHelper.ceil(r * 4);
        r *= this.radius;
        if (level.isClientSide()) {
            for (int i = 0; i < n; ++i) {
                addParticle(new ColorParticleOptions(MIST.get(), rand.nextFloat(1.0F, 3.0F), rand.nextFloat(50, 70), 0, Color.fromRGBA(rand.nextInt(153, 180), rand.nextInt(192, 216), 255, 48).toRGBA()), center, r);
                addParticle(FROST.get(), center, r);
            }
        } else if (tickCount % 6 == 0) {
            Predicate<Entity> filter = EntitySelector.LIVING_ENTITY_STILL_ALIVE;
            if (owner != null) {
                filter = filter.and(entity -> !entity.isPassengerOfSameVehicle(owner));
            }
            for (Entity target : AreaUtils.getEntitiesInCylinder(level, center, r, 1.5F, this, filter)) {
                ChilledMobEffect.applyChilled(target, this.power, this.rand);
            }
        }
    }

    protected void addParticle(ParticleOptions options, Vec3 center, float radius) {

        float var = 0.01F;
        Vec3 pos = randomOffsetRadial(center, radius, 0.5F);
        level.addParticle(options, pos.x, pos.y, pos.z, rand.nextFloat(-var, var), rand.nextFloat(-var, var), rand.nextFloat(-var, var));
    }

    protected Vec3 randomOffsetRadial(Vec3 center, float radius, float height) {

        double r = radius * Math.sqrt(rand.nextFloat());
        float angle = rand.nextFloat(MathHelper.F_TAU);
        return center.add(r * MathHelper.cos(angle), rand.nextFloat(height), r * MathHelper.sin(angle));
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
        setBoundingBox(getBoundingBox().inflate(radius, 0.75F, radius));
    }

}
