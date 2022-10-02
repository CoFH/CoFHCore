package cofh.core.client.particle.impl;

import cofh.core.client.particle.CylindricalParticle;
import cofh.core.client.particle.options.CylindricalParticleOptions;
import cofh.core.util.helpers.vfx.RenderTypes;
import cofh.core.util.helpers.vfx.VFXHelper;
import cofh.lib.util.helpers.MathHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector4f;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.SplittableRandom;

@OnlyIn (Dist.CLIENT)
public class WindVortexParticle extends CylindricalParticle { //TODO

    //protected static final float defaultLifetime = 8.0F;

    private WindVortexParticle(CylindricalParticleOptions data, ClientLevel level, double x, double y, double z, double xDir, double yDir, double zDir) {

        super(data, level, x, y, z, xDir, yDir, zDir);
        //this.setSize((float) (width * (1.0F + random.nextGaussian() * 0.1F)), (float) height * random.nextFloat());
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
    public void render(PoseStack stack, MultiBufferSource buffer, int packedLight, float partialTicks) {

        SplittableRandom rand = new SplittableRandom(this.seed);
        float time = age + partialTicks - (float) rand.nextDouble(this.fLifetime * 0.25F);
        if (time < 0 || time > fLifetime) {
            return;
        }
        if (!rotation.equals(Quaternion.ONE)) {
            stack.mulPose(rotation);
        }
        float progress = time / fLifetime;
        float easePlat = MathHelper.easePlateau(progress);

        int a = MathHelper.clamp((int) ((rgba0 & 0xFF) * easePlat), 0, 255);
        stack.scale(size * 0.2F, height * rand.nextFloat(-0.5F, 0.5F), size * 0.2F);

        float incr = VFXHelper.WIND_INCR;
        Vector4f[] poss = new Vector4f[rand.nextInt(10, 20)];
        float angle = 3.125F * (1.0F - progress);
        int outer = MathHelper.clamp(MathHelper.ceil(angle / incr), 0, poss.length);
        for (int i = 0; i < outer; ++i) {
            float rot = angle - i * incr;
            float r = 0.5F + 0.3F * rot * (1.0F - MathHelper.cos(rot));
            float y = 0.25F + 0.25F * (1.0F - MathHelper.cos(rot * 0.32F * MathHelper.F_PI * 0.5F));
            rot += roll;
            poss[i] = new Vector4f(r * MathHelper.cos(rot), y, r * MathHelper.sin(rot), 1.0F);
        }
        for (int i = outer; i < poss.length; ++i) {
            float rot = angle - i * incr;
            float r = 0.5F;
            rot += roll;
            poss[i] = new Vector4f(r * MathHelper.cos(rot), 0.25F, r * MathHelper.sin(rot), 1.0F);
        }
        VFXHelper.renderStreamLine(stack, buffer.getBuffer(RenderTypes.FLAT_TRANSLUCENT), packedLight, poss, (rgba0 & 0xFFFFFF00) | a, VFXHelper.getWidthFunc((float) rand.nextDouble(0.04F, 0.05F)));
        VFXHelper.renderCyclone(stack, buffer.getBuffer(RenderTypes.FLAT_TRANSLUCENT), packedLight, 1, (float) rand.nextDouble(0.04F, 0.05F), progress * 0.5F + (float) rand.nextDouble(420F), a * 0.00392157F);
    }

    @Override
    protected void setDuration(float duration) {

        fLifetime = duration;
        lifetime = MathHelper.ceil(fLifetime * 1.25F);
    }

    @Nonnull
    public static ParticleProvider<CylindricalParticleOptions> factory(SpriteSet spriteSet) {

        return WindVortexParticle::new;
    }

}
