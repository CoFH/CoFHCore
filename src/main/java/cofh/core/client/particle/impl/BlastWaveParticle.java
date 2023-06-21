package cofh.core.client.particle.impl;

import cofh.core.client.particle.CylindricalParticle;
import cofh.core.client.particle.options.CylindricalParticleOptions;
import cofh.core.util.helpers.vfx.VFXHelper;
import cofh.lib.util.helpers.MathHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.MultiBufferSource;

import javax.annotation.Nonnull;
import java.util.SplittableRandom;

public class BlastWaveParticle extends CylindricalParticle {

    private BlastWaveParticle(CylindricalParticleOptions data, ClientLevel level, double x, double y, double z, double xDir, double yDir, double zDir) {

        super(data, level, x, y, z, xDir, yDir, zDir);
        hasPhysics = false;
    }

    @Override
    public void tick() {

        if (this.age++ >= this.lifetime) {
            this.remove();
        }
    }

    @Override
    public void render(PoseStack stack, MultiBufferSource buffer, VertexConsumer consumer, int packedLightIn, float time, float pTicks) {

        if (!rotation.equals(Quaternion.ONE)) {
            stack.mulPose(rotation);
        }
        SplittableRandom rand = new SplittableRandom(seed);
        float progress = time / duration;
        float easeSin = MathHelper.sin(progress * MathHelper.F_PI * 0.5F);
        float easeCub = MathHelper.easeOutCubic(progress);

        VFXHelper.renderCyclone(stack, buffer, getLightColor(time), size * easeSin, height * easeSin, 2, 0.015F * MathHelper.sqrt(4 + size * size) * easeCub, time * 0.05F + (float) rand.nextDouble(69F), 0.5F * MathHelper.easePlateau(progress));
    }

    @Nonnull
    public static ParticleProvider<CylindricalParticleOptions> factory(SpriteSet spriteSet) {

        return BlastWaveParticle::new;
    }

}
