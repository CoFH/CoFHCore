package cofh.core.client.particle.impl;

import cofh.core.client.particle.CylindricalParticle;
import cofh.core.client.particle.options.CylindricalParticleOptions;
import cofh.core.util.helpers.vfx.RenderTypes;
import cofh.core.util.helpers.vfx.VFXHelper;
import cofh.lib.util.helpers.MathHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector4f;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.MultiBufferSource;

import javax.annotation.Nonnull;
import java.util.SplittableRandom;

public class WindSpiralParticle extends CylindricalParticle {

    private WindSpiralParticle(CylindricalParticleOptions data, ClientLevel level, double x, double y, double z, double xDir, double yDir, double zDir) {

        super(data, level, x, y, z, xDir, yDir, zDir);
        //alpha = 0.20F * (1.0F + random.nextFloat());
        //rCol = gCol = bCol = 1.0F - 0.1F * random.nextFloat();
        oRoll = roll = random.nextFloat() * MathHelper.F_TAU;
    }

    @Override
    public void tick() {

        if (this.age++ >= this.lifetime) {
            this.remove();
        }
    }

    @Override
    public void render(PoseStack stack, MultiBufferSource buffer, VertexConsumer consumer, int packedLightIn, float time, float pTicks) {

        SplittableRandom rand = new SplittableRandom(this.seed);
        if (!rotation.equals(Quaternion.ONE)) {
            stack.mulPose(rotation);
        }
        float progress = time / duration;
        float easeCub = 1.0F - MathHelper.easeInCubic(progress);
        float easeSin = MathHelper.sin(progress * MathHelper.F_PI);
        float easePlat = MathHelper.easePlateau(progress);
        float offset = (float) rand.nextDouble(0.0F, 0.8F);
        float expand = (progress + offset) * (float) rand.nextDouble(0.3F, 0.5F);
        float yScale = offset * (float) rand.nextDouble(0.015F, 0.03F);
        float y = (expand * (float) rand.nextDouble(1.0F, 1.5F) - 0.6F);
        float rot = roll + progress * (float) rand.nextDouble(6.0F, 9.0F);
        int length = (int) (3 + easePlat * rand.nextInt(16, 24));
        Vector4f[] poss = new Vector4f[length];
        int half = length / 2;
        for (int i = 0; i < length; ++i) {
            float angle = (i - half) * 0.1309F + rot;
            float r = size * (1.0F + i * 0.02F) * expand;
            poss[i] = new Vector4f(r * MathHelper.cos(angle), i * yScale + height * y, r * MathHelper.sin(angle), 1.0F);
        }
        VFXHelper.renderStreamLine(stack, buffer.getBuffer(RenderTypes.FLAT_TRANSLUCENT), packedLightIn, poss, c0, VFXHelper.getWidthFunc((easeSin * 0.08F + 0.01F) * (float) rand.nextDouble(0.3F, 1.0F)));
    }

    @Nonnull
    public static ParticleProvider<CylindricalParticleOptions> factory(SpriteSet spriteSet) {

        return WindSpiralParticle::new;
    }

}
