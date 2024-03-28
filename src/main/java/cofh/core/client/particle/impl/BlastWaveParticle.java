package cofh.core.client.particle.impl;

import cofh.core.client.particle.CylindricalParticle;
import cofh.core.client.particle.options.CylindricalParticleOptions;
import cofh.core.init.CoreShaders;
import cofh.core.util.helpers.vfx.RenderTypes;
import cofh.core.util.helpers.vfx.VFXHelper;
import cofh.lib.util.helpers.MathHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.MultiBufferSource;
import org.joml.Quaternionf;

import javax.annotation.Nonnull;
import java.util.SplittableRandom;

import static cofh.core.util.helpers.vfx.RenderTypes.BLANK_TEXTURE;

public class BlastWaveParticle extends CylindricalParticle {

    private BlastWaveParticle(CylindricalParticleOptions data, ClientLevel level, double x, double y, double z, double xDir, double yDir, double zDir) {

        super(data, level, x, y, z, xDir, yDir, zDir);
    }

    @Override
    public void render(PoseStack stack, MultiBufferSource buffer, VertexConsumer consumer, int packedLightIn, float time, float pTicks) {

        if (!rotation.equals(new Quaternionf())) {
            stack.mulPose(rotation);
        }
        SplittableRandom rand = new SplittableRandom(seed);
        float progress = time / duration;
        float easeSin = MathHelper.sin(progress * MathHelper.F_PI * 0.5F);
        //float easeCub = MathHelper.easeOutCubic(progress);
        VFXHelper.renderCyclone(stack, CoreShaders.PIXELATE.getBuffer(BLANK_TEXTURE), getLightColor(time), c0, easeSin * size * 0.5F, 0.018F * MathHelper.sqrt(9 + size * size), height * easeSin, rand, progress * 0.5F);
    }

    @Nonnull
    public static ParticleProvider<CylindricalParticleOptions> factory(SpriteSet spriteSet) {

        return BlastWaveParticle::new;
    }

}
