package cofh.core.client.particle.impl;

import cofh.core.client.particle.options.BiColorParticleOptions;
import cofh.core.util.helpers.RenderHelper;
import cofh.core.util.helpers.vfx.Color;
import cofh.core.util.helpers.vfx.RenderTypes;
import cofh.core.util.helpers.vfx.VFXHelper;
import cofh.core.util.helpers.vfx.TrailNode;
import cofh.lib.util.helpers.MathHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class CurvedShardParticle extends CurvedPointToPointParticle {

    public CurvedShardParticle(BiColorParticleOptions data, ClientLevel level, double sx, double sy, double sz, double ex, double ey, double ez) {

        super(data, level, sx, sy, sz, ex, ey, ez);
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
            posns[i] = new Vector4f(pos(disp, perp, eccentricity, progress - segment * i), 1).mul(pose);
        }
        TrailNode[] nodes = new TrailNode[posns.length];
        for (int i = 0; i < nodes.length; ++i) {
            int j = Math.max(i, 1);
            nodes[i] = new TrailNode(posns[i], VFXHelper.axialPerp(posns[j - 1], posns[j], 0.35F * size * (length - segment * i)));
        }
        VFXHelper.renderNodes(norm, buffer.getBuffer(RenderTypes.FLAT_TRANSLUCENT), packedLight, c1, nodes);
        // Body
        Vector3f pos = pos(disp, perp, eccentricity, progress);
        stack.translate(pos.x, pos.y, pos.z);
        stack.scale(size, size, size);
        consumer = buffer.getBuffer(RenderTypes.translucent(RenderTypes.BLANK_TEXTURE));
        VFXHelper.alignVertical(stack, tangent(disp, perp, eccentricity, progress));
        RenderHelper.renderBipyramid(stack, consumer, packedLight, c0, 4, 0.6F, 0.1F);
        RenderHelper.renderBipyramid(stack, consumer, packedLight, c0.mix(Color.WHITE, 0.5F), 4, 0.4F, 0.066F);
    }

}
