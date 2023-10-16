package cofh.core.client.particle.impl;

import cofh.core.client.particle.PointToPointParticle;
import cofh.core.client.particle.options.BiColorParticleOptions;
import cofh.core.util.helpers.RenderHelper;
import cofh.core.util.helpers.vfx.Color;
import cofh.core.util.helpers.vfx.RenderTypes;
import cofh.core.util.helpers.vfx.VFXHelper;
import cofh.lib.util.helpers.MathHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.phys.Vec2;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import javax.annotation.Nonnull;

public class ShardParticle extends PointToPointParticle {

    //The displacement, i.e. the start point subtracted from the end point.
    protected Vector3f disp;

    private ShardParticle(BiColorParticleOptions data, ClientLevel level, double sx, double sy, double sz, double ex, double ey, double ez) {

        super(data, level, sx, sy, sz, ex, ey, ez);
        this.friction = 1.0F;
        this.disp = new Vector3f((float) (ex - sx), (float) (ey - sy), (float) (ez - sz));
    }

    @Override
    public void tick() {

        if (this.age++ >= this.lifetime) {
            this.remove();
        }
    }

    @Override
    public void render(PoseStack stack, MultiBufferSource buffer, VertexConsumer consumer, int packedLight, float time, float pTicks) {

        float progress = 1.0F - MathHelper.cos(time / duration * MathHelper.F_PI * 0.5F);
        float dx = disp.x() * progress;
        float dy = disp.y() * progress;
        float dz = disp.z() * progress;
        stack.translate(dx, dy, dz);
        float dist = MathHelper.dist(dx, dy, dz);
        stack.scale(size, size, size);
        //if (progress > 1.0F) {
        //    this.alpha = Math.max(1 - MathHelper.easeOutCubic(progress - 1.0F) * 5, 0);
        //}

        VFXHelper.alignVertical(stack, disp);
        PoseStack.Pose last = stack.last();
        Matrix4f pose = last.pose();
        Matrix3f norm = last.normal();

        // Trail
        Vector4f start = new Vector4f(0, 0, 0, 1);
        start.transform(pose);
        Vector4f end = new Vector4f(0, -Math.min(dist / size, 3.0F), 0, 1);
        end.transform(pose);
        Vec2 perp = VFXHelper.axialPerp(start, end, 1.0F);
        float w = 0.12F * size;
        float xs = perp.x * w;
        float ys = perp.y * w;
        consumer = buffer.getBuffer(RenderTypes.FLAT_TRANSLUCENT);
        new VFXHelper.VFXNode(start.x() + xs, start.x() - xs, start.y() + ys, start.y() - ys, start.z(), w).renderStart(norm, consumer, packedLight, c1);
        new VFXHelper.VFXNode(end.x(), end.x(), end.y(), end.y(), end.z(), w * 0.1F).renderEnd(norm, consumer, packedLight, c1);

        // If different colors, end batch so the body always renders on top of the trail.
        if (!c0.sameRGB(c1)) {
            buffer.getBuffer(RenderTypes.LINEAR_GLOW);
            buffer.getBuffer(RenderTypes.FLAT_TRANSLUCENT);
        }
        // Body
        RenderHelper.renderBipyramid(stack, consumer, packedLight, c0, 4, 0.6F, 0.1F);
        buffer.getBuffer(RenderTypes.LINEAR_GLOW);
        buffer.getBuffer(RenderTypes.FLAT_TRANSLUCENT);
        RenderHelper.renderBipyramid(stack, consumer, packedLight, c0.mix(Color.WHITE, 0.5F), 4, 0.4F, 0.066F);
    }

    @Nonnull
    public static ParticleProvider<BiColorParticleOptions> factory(SpriteSet spriteSet) {

        return ShardParticle::new;
    }

}
