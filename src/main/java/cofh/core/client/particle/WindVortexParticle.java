package cofh.core.client.particle;

import cofh.core.util.helpers.vfx.RenderTypes;
import cofh.core.util.helpers.vfx.VFXHelper;
import cofh.lib.util.helpers.MathHelper;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.math.vector.Vector4f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.SplittableRandom;

@OnlyIn(Dist.CLIENT)
public class WindVortexParticle extends LevelMatrixStackParticle {

    protected static final float defaultLifetime = 8.0F;
    protected float fLifetime;
    protected int seed;
    protected float scale;
    protected float height;

    private WindVortexParticle(ClientWorld level, double xPos, double yPos, double zPos, double speed, double width, double height) {

        super(level, xPos, yPos, zPos, speed, width, height);
        this.fLifetime = defaultLifetime / (float) speed;
        this.lifetime = MathHelper.ceil(fLifetime * 1.2F);
        this.seed = random.nextInt();
        this.setSize((float) (width * (1.0F + random.nextGaussian() * 0.1F)), (float) height * random.nextFloat()); // TODO

        hasPhysics = false;
        xd = yd = zd = 0;
        alpha = 0.20F * (1.0F + random.nextFloat());
        rCol = gCol = bCol = 1.0F - 0.1F * random.nextFloat();
        oRoll = roll = random.nextFloat() * MathHelper.F_TAU;
    }

    @Override
    public void render(MatrixStack stack, IRenderTypeBuffer buffer, int packedLight, float partialTicks) {

        SplittableRandom rand = new SplittableRandom(this.seed);
        float time = age + partialTicks - (float) rand.nextDouble(this.fLifetime * 0.2F);
        if (time < 0 || time > fLifetime) {
            return;
        }
        float progress = time / fLifetime;
        float easeSin = 1.0F - MathHelper.sin(progress * MathHelper.F_PI * 0.5F);
        float easePlat = MathHelper.easePlateau(progress);

        float a = alpha * easePlat;
        float halfHeight = 0.5F * bbHeight;
        stack.scale(bbWidth, halfHeight, bbWidth);
        stack.pushPose();

        float hScale = easeSin + 0.5F;
        if (halfHeight > 0) {
            stack.translate(0, rand.nextDouble(-halfHeight, halfHeight), 0);
        }
        stack.scale(hScale, 2.0F, hScale);
        stack.mulPose(Vector3f.YP.rotation(roll + progress * (float) rand.nextDouble(2.0F, 3.0F)));
        Vector4f[] poss = new Vector4f[rand.nextInt(10, 20)];
        for (int i = 0; i < poss.length; ++i) {
            float rot = i * 0.1309F;
            float r = 0.95F + i * 0.08F;
            poss[i] = new Vector4f(r * MathHelper.cos(rot), easeSin + i * 0.05F, r * MathHelper.sin(rot), 1.0F);
        }
        VFXHelper.renderStreamLine(stack, buffer.getBuffer(RenderTypes.FLAT_TRANSLUCENT), packedLight, poss, VFXHelper.packARGB(a, rCol, gCol, bCol), VFXHelper.getWidthFunc((float) rand.nextDouble(0.02F, 0.04F)));

        stack.popPose();
        VFXHelper.renderCyclone(stack, buffer.getBuffer(RenderTypes.FLAT_TRANSLUCENT), packedLight, 1, (float) rand.nextDouble(0.02F, 0.04F), progress * 0.5F + (float) rand.nextDouble(420F), a);
    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements IParticleFactory<BasicParticleType> {

        public Factory(IAnimatedSprite sprite) {

        }

        @Nullable
        @Override
        public Particle createParticle(BasicParticleType data, ClientWorld world, double x, double y, double z, double speed, double width, double height) {

            return new WindVortexParticle(world, x, y, z, speed, width, height);
        }
    }

}
