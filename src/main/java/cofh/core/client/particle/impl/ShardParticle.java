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
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import javax.annotation.Nonnull;

public class ShardParticle extends PointToPointParticle {

    //The displacement, i.e. the start point subtracted from the end point.
    protected Vector3f disp;

    public ShardParticle(BiColorParticleOptions data, ClientLevel level, double sx, double sy, double sz, double ex, double ey, double ez) {

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

        float progress = 1.0F - MathHelper.cos(time / duration * MathHelper.F_HALF_PI);
        float dx = disp.x() * progress;
        float dy = disp.y() * progress;
        float dz = disp.z() * progress;
        stack.translate(dx, dy, dz);
        stack.scale(size, size, size);
        //if (progress > 1.0F) {
        //    this.alpha = Math.max(1 - MathHelper.easeOutCubic(progress - 1.0F) * 5, 0);
        //}

        VFXHelper.alignVertical(stack, disp);
        Matrix4f pose = stack.last().pose();
        Vector3f norm = VFXHelper.normal(stack);

        // Trail
        Vector4f start = new Vector4f(0, 0, 0, 1).mul(pose);
        float length = Math.min(MathHelper.dist(dx, dy, dz) / size, 3.0F);
        Vector4f end = new Vector4f(0, -length, 0, 1).mul(pose);
        float w = 0.12F * size;
        Vector2f perp = VFXHelper.axialPerp(start, end, w);
        consumer = buffer.getBuffer(RenderTypes.FLAT_TRANSLUCENT);
        new VFXHelper.VFXNode(start, perp).renderStart(norm, consumer, packedLight, c1);
        new VFXHelper.VFXNode(end, perp.mul(0)).renderEnd(norm, consumer, packedLight, c1);

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

}
