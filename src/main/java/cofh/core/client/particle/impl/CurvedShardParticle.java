package cofh.core.client.particle.impl;

import cofh.core.client.particle.options.BiColorParticleOptions;
import cofh.core.util.helpers.RenderHelper;
import cofh.core.util.helpers.vfx.Color;
import cofh.core.util.helpers.vfx.RenderTypes;
import cofh.core.util.helpers.vfx.VFXHelper;
import cofh.lib.util.helpers.MathHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import static cofh.core.util.helpers.vfx.RenderTypes.LINEAR_GLOW;
import static cofh.core.util.helpers.vfx.RenderTypes.ROUND_GLOW;

public class CurvedShardParticle extends ShardParticle {

    public static final Vector3f[] UNITS = {new Vector3f(1, 0, 0), new Vector3f(0, 1, 0), new Vector3f(1, 0, 0)};

    protected Vector3f dir;
    protected float offset;

    public CurvedShardParticle(BiColorParticleOptions data, ClientLevel level, double sx, double sy, double sz, double ex, double ey, double ez) {

        super(data, level, sx, sy, sz, ex, ey, ez);
        this.roll = random.nextFloat() * MathHelper.F_TAU;
        this.dir = dir(this.disp, this.roll);
        this.offset = (1 - Math.abs(random.nextFloat() - random.nextFloat())) * MathHelper.sqrt(disp.length()) * 0.3F;
    }

    @Override
    public int getLightColor(float partialTicks) {

        return RenderHelper.FULL_BRIGHT;
    }

    @Override
    public int getLightColor(float pTicks, double x, double y, double z) {

        return RenderHelper.FULL_BRIGHT;
    }

    @Override
    public void render(PoseStack stack, MultiBufferSource buffer, VertexConsumer consumer, int packedLight, float time, float pTicks) {

        float progress = 1.0F - MathHelper.cos(time / duration * MathHelper.F_HALF_PI);
        Matrix4f pose = stack.last().pose();
        Vector3f norm = VFXHelper.normal(stack);

        // Trail
        float segment = 0.1F;
        Vector4f[] posns = new Vector4f[Math.max(5, MathHelper.ceil(progress / segment) + 1)];
        float length = Math.min(4 * segment, progress);
        segment = length / (posns.length - 1);
        for (int i = 0; i < posns.length; ++i) {
            posns[i] = pos(progress - segment * i).mul(pose);
        }
        VFXHelper.VFXNode[] nodes = new VFXHelper.VFXNode[posns.length];
        for (int i = 0; i < nodes.length; ++i) {
            int j = Math.max(i, 1);
            nodes[i] = new VFXHelper.VFXNode(posns[i], VFXHelper.axialPerp(posns[j - 1], posns[j], 0.35F * size * (length - segment * i)));
        }
        consumer = buffer.getBuffer(RenderTypes.FLAT_TRANSLUCENT);
        VFXHelper.renderNodes(norm, consumer, packedLight, c1, nodes);

        // If different colors, end batch so the body always renders on top of the trail.
        if (!c0.sameRGB(c1)) {
            buffer.getBuffer(RenderTypes.LINEAR_GLOW);
            buffer.getBuffer(RenderTypes.FLAT_TRANSLUCENT);
        }
        // Body
        Vector4f pos = pos(progress);
        stack.translate(pos.x, pos.y, pos.z);
        stack.scale(size, size, size);
        VFXHelper.alignVertical(stack, tangent(progress));
        RenderHelper.renderBipyramid(stack, consumer, packedLight, c0, 4, 0.6F, 0.1F);
        buffer.getBuffer(RenderTypes.LINEAR_GLOW);
        buffer.getBuffer(RenderTypes.FLAT_TRANSLUCENT);
        RenderHelper.renderBipyramid(stack, consumer, packedLight, c0.mix(Color.WHITE, 0.5F), 4, 0.4F, 0.066F);
    }

    protected Vector3f perpendicular(Vector3f v) {

        float min = Float.MAX_VALUE;
        Vector3f out = UNITS[0];
        for (Vector3f unit : UNITS) {
            float dot = Math.abs(v.dot(unit));
            if (dot < min) {
                out = unit;
                min = dot;
            }
        }
        return new Vector3f(v).cross(out);
    }

    protected Vector3f dir(Vector3f disp, float roll) {

        float sin = MathHelper.sin(roll);
        float cos = MathHelper.cos(roll);
        Vector3f perp = perpendicular(disp).normalize();
        return perp.mul(sin).add(new Vector3f(perp).cross(disp).normalize().mul(cos));
    }

    protected Vector4f pos(float progress) {

        return new Vector4f(new Vector3f(dir).mul(offset * MathHelper.sin(progress * MathHelper.F_PI)).add(new Vector3f(disp).mul(progress)), 1);
    }

    protected Vector3f tangent(float progress) {

        return new Vector3f(dir).mul(offset * MathHelper.cos(progress * MathHelper.F_PI) * MathHelper.F_PI).add(disp);
    }

}
