package cofh.lib.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.util.List;

import static cofh.lib.util.references.CoreReferences.ELECTRIC_FIELD_ENTITY;

public class ElectricFieldEntity extends AbstractAoESpellEntity {

    public ElectricFieldEntity(EntityType<? extends ElectricFieldEntity> type, World world) {

        super(type, world);
    }

    public ElectricFieldEntity(World world, Vector3d pos, float radius, int duration) {

        this(ELECTRIC_FIELD_ENTITY, world);
        this.moveTo(pos);
        this.radius = radius;
        this.duration = duration;
    }

    @Override
    public void tick() {

        if (!level.isClientSide && random.nextInt(20) == 0) {
            summonArc();
        }
        super.tick();
    }

    protected void summonArc() {

        double radiusSqr = radius * radius;
        List<Entity> entities = level.getEntities(this, this.getBoundingBox().inflate(radius), EntityPredicates.ATTACK_ALLOWED.and((entity) -> entity.distanceToSqr(this) < radiusSqr));
        if (random.nextInt(5) < entities.size()) {
            level.addFreshEntity((new ElectricArcEntity(level, entities.get(random.nextInt(entities.size())))).setOwner(this.getOwner()));
        } else {
            Vector3d pos = this.position().add(random.nextGaussian() * radius * 0.5F, radius, random.nextGaussian() * radius * 0.5F);
            BlockRayTraceResult raytrace = level.clip(new RayTraceContext(pos, pos.add(0, -2 * radius, 0), RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.ANY, this));
            if (raytrace.getType().equals(RayTraceResult.Type.MISS)) {
                return;
            }
            level.addFreshEntity((new ElectricArcEntity(level, raytrace.getLocation())).setOwner(this.getOwner()));
        }
    }

}
