package cofh.lib.entity;

import cofh.lib.util.helpers.MathHelper;
import cofh.lib.util.references.CoreReferences;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import static cofh.lib.util.references.CoreReferences.BLACK_HOLE_ENTITY;

public class BlackHoleEntity extends AbstractAoESpellEntity {

    public BlackHoleEntity(EntityType<? extends BlackHoleEntity> type, Level level) {

        super(type, level);
        duration = 100;
    }

    public BlackHoleEntity(Level level, Vec3 pos, float radius) {

        this(BLACK_HOLE_ENTITY, level);
        this.radius = radius;
        moveTo(pos);
    }

    @Override
    public void tick() {

        //TODO particle
        if (level.isClientSide) {
            this.level.addParticle(CoreReferences.FROST_PARTICLE, this.getRandomX(0.5D), this.getRandomY(), this.getRandomZ(0.5D), 0.0D, 0.0D, 0.0D);
        } else {
            float rangeSqr = radius * radius;
            double inv = 1 / rangeSqr;
            for (Entity entity : level.getEntities(this, this.getBoundingBox().inflate(radius))) {
                Vec3 diff = this.position().subtract(entity.position());
                double distSqr = diff.lengthSqr();
                if (distSqr < rangeSqr) {
                    Vec3 velocity = entity.getDeltaMovement().scale(0.9F + distSqr * inv * 0.1F);
                    entity.setDeltaMovement(velocity.add(diff.scale(0.05 * Math.min(MathHelper.invSqrt((float) distSqr), 1.0))));
                }
            }
        }
        super.tick();
    }

}
