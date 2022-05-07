package cofh.core.client.particle;

import cofh.core.util.helpers.vfx.RenderTypes;
import cofh.core.util.helpers.vfx.VFXHelper;
import cofh.lib.util.helpers.MathHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.SplittableRandom;

@OnlyIn (Dist.CLIENT)
public class WindSpiralParticle extends LevelMatrixStackParticle {

    protected static final float defaultLifetime = 10.0F;
    protected float fLifetime;
    protected int seed;
    protected float yRot;
    protected float xRot;

    private WindSpiralParticle(ClientLevel level, double xPos, double yPos, double zPos, double speed, double yRot, double xRot) {

        super(level, xPos, yPos, zPos, speed, yRot, xRot);
        this.fLifetime = defaultLifetime / (float) speed;
        this.lifetime = MathHelper.ceil(fLifetime * 1.25F);
        this.seed = random.nextInt();
        this.yRot = (float) yRot;
        this.xRot = (float) xRot;

        this.setSize(1.0F, 1.0F);

        hasPhysics = false;
        xd = yd = zd = 0;
        alpha = 0.20F * (1.0F + random.nextFloat());
        rCol = gCol = bCol = 1.0F - 0.1F * random.nextFloat();
        oRoll = roll = random.nextFloat() * MathHelper.F_TAU;
    }

    @Override
    public void render(PoseStack stack, MultiBufferSource buffer, int packedLightIn, float partialTicks) {

        SplittableRandom rand = new SplittableRandom(this.seed);
        float time = age + partialTicks - (float) rand.nextDouble(0, this.fLifetime * 0.25F + 0.001F);
        if (time < 0 || time > fLifetime) {
            return;
        }
        stack.mulPose(Vector3f.YP.rotationDegrees(-yRot + 180));
        stack.mulPose(Vector3f.XP.rotationDegrees(-xRot - 90));
        stack.pushPose();

        float progress = time / fLifetime;
        float easeCub = 1.0F - MathHelper.easeInCubic(progress);
        float easeSin = MathHelper.sin(progress * MathHelper.F_PI);
        float easePlat = MathHelper.easePlateau(progress);
        float offset = (float) rand.nextDouble(0.0F, 0.8F);
        float expand = (progress + offset) * (float) rand.nextDouble(0.3F, 0.5F);
        stack.translate(0, expand * (float) rand.nextDouble(1.5F, 2.0F) - 0.75F, 0);
        stack.scale(expand, offset * (float) rand.nextDouble(0.5F, 1.0F), expand);
        stack.mulPose(Vector3f.YP.rotation(roll + progress * (float) rand.nextDouble(6.0F, 9.0F)));
        int rgba = VFXHelper.packRGBA(rCol, gCol, bCol, alpha * easeCub);
        int length = (int) (3 + easePlat * rand.nextInt(16, 24));
        Vector4f[] poss = new Vector4f[length];
        int half = length / 2;
        for (int i = 0; i < length; ++i) {
            float rot = (i - half) * 0.1309F;
            float r = 1.0F + i * 0.02F;
            poss[i] = new Vector4f(r * MathHelper.cos(rot), i * 0.03F, r * MathHelper.sin(rot), 1.0F);
        }
        VFXHelper.renderStreamLine(stack, buffer.getBuffer(RenderTypes.FLAT_TRANSLUCENT), packedLightIn, poss, rgba, VFXHelper.getWidthFunc((easeSin * 0.08F + 0.01F) * (float) rand.nextDouble(0.3F, 1.0F)));

        stack.popPose();
    }

    @OnlyIn (Dist.CLIENT)
    public static class Factory implements ParticleProvider<SimpleParticleType> {

        public Factory(SpriteSet sprite) {

        }

        // The xRot and yRot values should come from the player that casts the wind spell.
        @Nullable
        @Override
        public Particle createParticle(SimpleParticleType data, ClientLevel level, double x, double y, double z, double speed, double yRot, double xRot) {

            return new WindSpiralParticle(level, x, y, z, speed, yRot, xRot);
        }

    }

}
