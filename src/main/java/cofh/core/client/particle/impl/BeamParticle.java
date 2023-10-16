package cofh.core.client.particle.impl;

import cofh.core.client.particle.PointToPointParticle;
import cofh.core.client.particle.options.BiColorParticleOptions;
import cofh.core.util.helpers.vfx.VFXHelper;
import cofh.lib.util.helpers.MathHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.MultiBufferSource;
import org.joml.Vector3f;

import javax.annotation.Nonnull;

public class BeamParticle extends PointToPointParticle {

    //The displacement, i.e. the start point subtracted from the end point.
    protected Vector3f disp;

    private BeamParticle(BiColorParticleOptions data, ClientLevel level, double sx, double sy, double sz, double ex, double ey, double ez) {

        super(data, level, sx, sy, sz, ex, ey, ez);
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

        float progress = time / duration;
        float rad = progress * MathHelper.F_PI;
        float easeCos = 0.5F * MathHelper.cos(rad * 0.5F) + 0.5F;
        float easeCub = 1.0F - MathHelper.easeInCubic(progress);
        Vector3f end = disp.copy();
        end.mul(Math.min(time * 12.0F / VFXHelper.length(end), 1.0F));
        VFXHelper.alignVertical(stack, Vector3f.ZERO, end);
        VFXHelper.renderBeam(stack, buffer, packedLight, this.size * easeCos,
                c0.scaleAlpha(easeCub), c1.scaleAlpha(easeCub));
    }

    @Override
    public int getLightColor(float pTicks) {

        return 0x00F000F0;
    }

    @Override
    public int getLightColor(float pTicks, double x, double y, double z) {

        return 0x00F000F0;
    }

    @Nonnull
    public static ParticleProvider<BiColorParticleOptions> factory(SpriteSet spriteSet) {

        return BeamParticle::new;
    }

}
