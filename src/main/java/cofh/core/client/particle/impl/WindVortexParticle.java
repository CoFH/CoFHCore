package cofh.core.client.particle.impl;

import cofh.core.client.particle.CylindricalParticle;
import cofh.core.client.particle.options.CylindricalParticleOptions;
import cofh.core.util.helpers.vfx.Color;
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

public class WindVortexParticle extends CylindricalParticle {

    private WindVortexParticle(CylindricalParticleOptions data, ClientLevel level, double x, double y, double z, double xDir, double yDir, double zDir) {

        super(data, level, x, y, z, xDir, yDir, zDir);
        oRoll = roll = random.nextFloat() * MathHelper.F_TAU;
    }

    @Override
    public void tick() {

        if (this.age++ >= this.lifetime) {
            this.remove();
        }
    }

    @Override
    public void render(PoseStack stack, MultiBufferSource buffer, VertexConsumer consumer, int packedLight, float time, float pTicks) {

        SplittableRandom rand = new SplittableRandom(this.seed);
        if (!rotation.equals(Quaternion.ONE)) {
            stack.mulPose(rotation);
        }
        float progress = time / duration;
        float easePlat = MathHelper.easePlateau(progress);

        stack.scale(size * 0.17F, height * rand.nextFloat(-0.5F, 0.5F), size * 0.17F);

        float incr = VFXHelper.WIND_INCR;
        Vector4f[] posns = new Vector4f[rand.nextInt(10, 20)];
        float angle = 3.125F * (1.0F - progress);
        int outer = MathHelper.clamp(MathHelper.ceil(angle / incr), 0, posns.length);
        for (int i = 0; i < outer; ++i) {
            float rot = angle - i * incr;
            float r = 0.6F + 0.4F * rot * (1.0F - MathHelper.cos(rot));
            float y = 0.25F + 0.25F * (1.0F - MathHelper.cos(rot * 0.32F * MathHelper.F_PI * 0.5F));
            rot += roll;
            posns[i] = new Vector4f(r * MathHelper.cos(rot), y, r * MathHelper.sin(rot), 1.0F);
        }
        for (int i = outer; i < posns.length; ++i) {
            float rot = angle - i * incr;
            float r = 0.6F;
            rot += roll;
            posns[i] = new Vector4f(r * MathHelper.cos(rot), 0.25F, r * MathHelper.sin(rot), 1.0F);
        }
        Color color = c0.scaleAlpha(easePlat);
        VFXHelper.renderStreamLine(stack, buffer.getBuffer(RenderTypes.FLAT_TRANSLUCENT), packedLight, posns, color, VFXHelper.getWidthFunc((float) rand.nextDouble(0.05F, 0.07F)));
        VFXHelper.renderCyclone(stack, buffer.getBuffer(RenderTypes.FLAT_TRANSLUCENT), packedLight, 1, (float) rand.nextDouble(0.05F, 0.07F), progress * 0.5F + (float) rand.nextDouble(420F), color.a * 0.00392157F);
    }

    @Nonnull
    public static ParticleProvider<CylindricalParticleOptions> factory(SpriteSet spriteSet) {

        return WindVortexParticle::new;
    }

}
