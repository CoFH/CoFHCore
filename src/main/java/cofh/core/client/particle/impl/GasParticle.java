package cofh.core.client.particle.impl;

import cofh.core.client.particle.SpriteParticle;
import cofh.core.client.particle.options.ColorParticleOptions;
import cofh.core.util.helpers.vfx.Color;
import cofh.core.util.helpers.vfx.RenderTypes;
import cofh.lib.util.helpers.MathHelper;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.Direction;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public abstract class GasParticle extends SpriteParticle {

    protected Color baseColor;
    protected float groundFriction = 0.5F;

    protected GasParticle(ColorParticleOptions data, ClientLevel level, SpriteSet sprites, double x, double y, double z, double dx, double dy, double dz) {

        super(data, level, sprites, x, y, z, dx, dy, dz);
        oRoll = roll = random.nextFloat() * MathHelper.F_TAU;
        baseColor = Color.fromRGBA(data.rgba0);
    }

    @Override
    public void tick() {

        if (this.age > this.delay) {
            int max = MathHelper.ceil(this.duration * 128);
            int time = MathHelper.clamp(MathHelper.floor((this.age - this.delay) * 128), 0, max);
            this.sprite = sprites.get(time, max);
            super.tick();
        } else {
            ++age;
        }
    }

    @Override
    public void move(double dx, double dy, double dz) {

        if (dx == 0.0D && dy == 0.0D && dz == 0.0D) {
            return;
        }
        Vec3 start = new Vec3(x, y, z);
        Vec3 velocity = new Vec3(dx, dy, dz);
        Vec3 end = start.add(dx, dy, dz);
        if (hasPhysics) {
            BlockHitResult result = level.clip(new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY, null));
            if (!level.getFluidState(result.getBlockPos()).isEmpty()) {
                this.remove();
                return;
            }
            if (result.getType() != HitResult.Type.MISS) {
                Vec3 loc = result.getLocation();
                Vec3 remaining = end.subtract(loc).scale(groundFriction);
                Direction face = result.getDirection();
                Direction.Axis axis = face.getAxis();
                velocity = velocity.with(axis, 0).scale(0.3F);
                end = loc.add(remaining.with(axis, face.getAxisDirection().getStep() * 0.05F));
            }
        }
        end = end.subtract(start);
        if (!end.equals(Vec3.ZERO)) {
            this.setBoundingBox(this.getBoundingBox().move(end));
            this.setLocationFromBoundingbox();
        }
        this.xd = velocity.x;
        this.yd = velocity.y;
        this.zd = velocity.z;
    }

    @Override
    public ParticleRenderType getRenderType() {

        return RenderTypes.PARTICLE_SHEET_OVER;
    }

}
