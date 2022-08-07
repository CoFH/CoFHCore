package cofh.core.client.particle.impl;

import cofh.core.client.particle.PointToPointParticle;
import cofh.core.client.particle.options.BiColorParticleOptions;
import cofh.core.client.particle.options.ColorParticleOptions;
import cofh.core.util.helpers.RenderHelper;
import cofh.core.util.helpers.vfx.RenderTypes;
import cofh.core.util.helpers.vfx.VFXHelper;
import cofh.lib.util.references.CoreReferences;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

@OnlyIn (Dist.CLIENT)
public class ShardParticle extends PointToPointParticle {

    protected float speed;

    private ShardParticle(BiColorParticleOptions data, ClientLevel level, double sx, double sy, double sz, double ex, double ey, double ez) {

        super(data, level, sx, sy, sz, ex, ey, ez);
        Vec3 disp = new Vec3(ex - sx, ey - sy, ez - sz);
        Vec3 vel = disp.scale(1.0F / fLifetime);
        xd = vel.x;
        yd = vel.y;
        zd = vel.z;
        speed = (float) vel.length();
        this.friction = 1.0F;
        this.hasPhysics = true;
        tick();
        //rgba0 = 0xff45ff00;
        //rgba1 = 0xa525f700;
    }

    @Override
    public void tick() {

        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age >= this.lifetime) {
            this.remove();
        } else if (this.age < this.fLifetime) {
            this.move(this.xd, this.yd, this.zd);
        }
        ++this.age;
    }

    @Override
    public void move(double dx, double dy, double dz) {

        if (this.hasPhysics && dx * dx + dy * dy + dz * dz < 10000) {
            float time = Math.min(this.fLifetime - this.age, 1.0F);
            Vec3 pos = new Vec3(this.x, this.y, this.z);
            Vec3 step = new Vec3(dx * time, dy * time, dz * time);
            Vec3 next = pos.add(step);
            Vec3 collide = this.level.clip(new ClipContext(pos, next,
                    ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, null)).getLocation();
            Vec3 delta = collide.subtract(pos);
            double distSqr = delta.lengthSqr();
            if (speed * speed - distSqr > 0.001F) {
                this.fLifetime = this.age + (float) Math.sqrt(distSqr) / speed;
            }
            if (this.age + 1 >= this.fLifetime) {
                this.level.addParticle(new ColorParticleOptions(CoreReferences.BLAST_PARTICLE, this.size * 0.25F, 6 + random.nextInt(4), this.rgba0), collide.x, collide.y, collide.z, 0, 0, 0);
            }

            dx = delta.x;
            dy = delta.y;
            dz = delta.z;
            //if (dist < 0.005F * speed) {
        }
        this.x += dx;
        this.y += dy;
        this.z += dz;

        //this.setBoundingBox(this.getBoundingBox().move(dx, dy, dz));
        //this.setLocationFromBoundingbox();
    }

    @Override
    public void render(PoseStack stack, MultiBufferSource buffer, int packedLight, float partialTicks) {

        float time = this.age + partialTicks - 1;
        float progress = time / fLifetime;
        //System.out.println(this.age); //this.x + " " + this.y + " " + this.z
        if (fLifetime < time) {
            return;
        }
        //if (progress > 1.0F) {
        //    this.alpha = Math.max(1 - MathHelper.easeOutCubic(progress - 1.0F) * 5, 0);
        //}

        stack.mulPose(VFXHelper.alignVertical(new Vector3f((float) xd, (float) yd, (float) zd)));
        PoseStack.Pose last = stack.last();
        Matrix4f pose = last.pose();
        Matrix3f norm = last.normal();

        // Trail
        Vector4f start = new Vector4f(0, 0, 0, 1);
        start.transform(pose);
        Vector4f end = new Vector4f(0, -speed * Math.min(time, 1.0F), 0, 1);
        end.transform(pose);
        Vec2 perp = VFXHelper.axialPerp(start, end, 1.0F);
        float w = 0.12F;
        float xs = perp.x * w;
        float ys = perp.y * w;
        VertexConsumer consumer = buffer.getBuffer(RenderTypes.FLAT_TRANSLUCENT);
        int r = (rgba1 >> 24) & 0xFF;
        int g = (rgba1 >> 16) & 0xFF;
        int b = (rgba1 >> 8) & 0xFF;
        int a = rgba1 & 0xFF;
        new VFXHelper.VFXNode(start.x() + xs, start.x() - xs, start.y() + ys, start.y() - ys, start.z(), w).renderStart(norm, consumer, packedLight, r, g, b, a);
        new VFXHelper.VFXNode(end.x(), end.x(), end.y(), end.y(), end.z(), w * 0.1F).renderEnd(norm, consumer, packedLight, r, g, b, a);

        // If different colors, end batch so the body always renders on top of the trail.
        if ((rgba0 | 0xFF) != (rgba1 | 0xFF)) {
            buffer.getBuffer(RenderTypes.LINEAR_GLOW);
            buffer.getBuffer(RenderTypes.FLAT_TRANSLUCENT);
        }
        // Body
        RenderHelper.renderBipyramid(stack, consumer, packedLight, rgba0, 4, 0.6F, 0.1F);
    }

    @Override
    public void setDuration(float duration) {

        float speed = this.fLifetime / duration;
        this.xd *= speed;
        this.yd *= speed;
        this.zd *= speed;
        this.speed *= speed;
        super.setDuration(duration);
    }

    @Nonnull
    public static ParticleProvider<BiColorParticleOptions> factory(SpriteSet spriteSet) {

        return ShardParticle::new;
    }

}
