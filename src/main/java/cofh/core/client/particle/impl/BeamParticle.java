package cofh.core.client.particle.impl;

import cofh.core.client.particle.PointToPointParticle;
import cofh.core.client.particle.options.BiColorParticleOptions;
import cofh.core.util.helpers.vfx.VFXHelper;
import cofh.lib.util.helpers.MathHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

@OnlyIn (Dist.CLIENT)
public class BeamParticle extends PointToPointParticle {

    protected final Vector3f dest;

    private BeamParticle(BiColorParticleOptions data, ClientLevel level, double sx, double sy, double sz, double ex, double ey, double ez) {

        super(data, level, sx, sy, sz, ex, ey, ez);
        float dx = (float) (ex - sx);
        float dy = (float) (ey - sy);
        float dz = (float) (ez - sz);
        dest = new Vector3f(dx, dy, dz);
    }

    @Override
    public void render(PoseStack stack, MultiBufferSource buffer, VertexConsumer consumer, int packedLight, float time, float pTicks) {

        float progress = time / duration;
        float easeCos = 0.5F * MathHelper.cos(progress * MathHelper.F_PI * 0.5F) + 0.5F;
        float easeCub = 1.0F - MathHelper.easeInCubic(progress);
        VFXHelper.alignVertical(stack, Vector3f.ZERO, dest);
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
