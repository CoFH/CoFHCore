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
public class WindSpiralParticle extends LevelMatrixStackParticle {

    protected static final float defaultLifetime = 10.0F;
    protected int seed;
    protected float yScale;
    protected float pLifetime;

    private WindSpiralParticle(ClientWorld level, double xPos, double yPos, double zPos, double speed, double yRot, double xRot) {

        super(level, xPos, yPos, zPos, speed, yRot, xRot);
        this.pLifetime = defaultLifetime / (float) speed;
        this.lifetime = MathHelper.ceil(pLifetime);
        this.seed = random.nextInt();
        this.yScale = random.nextFloat();

        this.setSize(1.0F, 1.0F);

        hasPhysics = false;
        xd = yd = zd = 0;
        alpha = 0.20F * (1.0F + random.nextFloat());
        rCol = gCol = bCol = 1.0F - 0.1F * random.nextFloat();
        oRoll = roll = random.nextFloat() * MathHelper.F_TAU;
    }

    @Override
    public void render(MatrixStack stack, IRenderTypeBuffer buffer, int packedLightIn, float partialTicks) {

        float time = age + partialTicks;
        if (time > pLifetime) {
            return;
        }
        SplittableRandom rand = new SplittableRandom(this.seed);
        float progress = time / pLifetime;
        float easeIn = MathHelper.easeInCubic(progress);
        float easePlat = MathHelper.bevel(progress * 2 + 0.333334F);

        float offset = (float) rand.nextDouble() + 0.1F;
        float expand = (1.0F + progress * (1.0F + (float) rand.nextDouble()) * (1.0F - easeIn) * 0.5F) * 0.5F * offset;
        stack.translate(0, offset + progress * (rand.nextDouble() + 2.0F) * 0.2F, 0);
        stack.scale(expand, (float) rand.nextDouble() * 0.5F, expand);
        stack.mulPose(Vector3f.YP.rotation(roll + progress * 4.0F * (1.5F + (float) rand.nextDouble()))); //TODO rotations
        int argb = VFXHelper.packARGB(alpha * (1.0F - easeIn), rCol, gCol, bCol);
        int length = (int) (3 + easePlat * 20);
        Vector4f[] poss = new Vector4f[length];
        for (int i = 0; i < length; ++i) {
            float rot = i * 0.1309F;
            float r = 1.0F + i * 0.02F;
            poss[i] = new Vector4f(r * MathHelper.cos(rot), i * 0.03F, r * MathHelper.sin(rot), 1.0F);
        }
        VFXHelper.renderStreamLine(stack, buffer.getBuffer(RenderTypes.FLAT_TRANSLUCENT), packedLightIn, poss, argb, VFXHelper.getWidthFunc((easePlat * 0.05F + 0.02F) * 0.5F * (1.0F + (float) rand.nextDouble())));
    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements IParticleFactory<BasicParticleType> {

        public Factory(IAnimatedSprite sprite) {

        }

        @Nullable
        @Override
        public Particle createParticle(BasicParticleType data, ClientWorld world, double x, double y, double z, double speed, double scale, double height) {

            return new WindSpiralParticle(world, x, y, z, speed, scale, height);
        }
    }

}
