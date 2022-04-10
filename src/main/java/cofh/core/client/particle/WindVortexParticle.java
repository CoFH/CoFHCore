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
import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class WindVortexParticle extends LevelMatrixStackParticle {

    protected int length;
    protected float scale;
    protected float height;
    protected float width;

    private WindVortexParticle(ClientWorld level, double xPos, double yPos, double zPos, double speed, double scale, double height) {

        super(level, xPos, yPos, zPos, speed, scale, height);
        this.lifetime = 1000; // MathHelper.ceil(10 / speed);
        this.length = random.nextInt(16) + 16;
        this.height = (float) height * random.nextFloat();
        this.scale = (float) (scale * (1.0F + random.nextGaussian() * 0.1F));
        this.width = random.nextFloat() * 0.01F + 0.02F;
        this.setSize(this.scale * 4, this.height);

        hasPhysics = false;
        xd = yd = zd = 0;
        alpha = 0.20F * (1.0F + random.nextFloat());
        rCol = gCol = bCol = 1.0F - 0.1F * random.nextFloat();
        oRoll = roll = random.nextFloat() * MathHelper.F_TAU;
    }

    @Override
    public void render(MatrixStack stack, IRenderTypeBuffer buffer, int packedLightIn, float partialTicks) {

        float time = age + partialTicks;
        float progress = time / lifetime;

        //stack.scale(scale, height, scale);
        stack.mulPose(Vector3f.YP.rotation(roll));
        float a = 0.5F; //alpha * MathHelper.clamp(3.0F * (0.5F - Math.abs(progress - 0.5F)), 0.0F, 1.0F);
        int argb = ((int) (a * 255.0F) << 24) | ((int) (255.0F * rCol) << 16) | ((int) (255.0F * gCol) << 8) | ((int) (255.0F * bCol));
        List<Vector4f> nodes = new ArrayList<>();
        int num = (int) (3.0F * Math.max(progress - 0.66F, 0.0F) * length);
        float increment = 1.0F / 48.0F;
        float i = Math.min(0.66F, progress);
        for (; i >= 0.33F && num < length; i -= increment, ++num) {
            nodes.add(new Vector4f(MathHelper.cos(9.4248F * (i - 0.33F)) * 0.5F, (1.0F - i * 1.5F) * height, MathHelper.sin(9.4248F * (i - 0.33F)) * 0.75F, 1.0F));
        }
        for (; i >= 0 && num < length; i -= increment, ++num) {
            nodes.add(new Vector4f(MathHelper.cos(4.7124F * (i - 0.33F)) * 2.0F - 1.5F, (1.0F - i * 1.5F) * height, MathHelper.sin(4.7124F * (i - 0.33F)) * 2.0F, 1.0F));
        }
        if (i < 0 && num < length) {
            nodes.add(new Vector4f(-1.25F, height, -2.0F, 1.0F));
        }
        //for (int i = 0; i < 25; ++i) {
        //    float rot = i * 0.1309F;
        //    float r = i * 0.02F;
        //    nodes.add(new Vector4f(r * MathHelper.cos(rot), i * 0.04F, r * MathHelper.sin(rot), 1.0F));
        //}
        VFXHelper.renderStreamLine(stack, buffer.getBuffer(RenderTypes.FLAT_TRANSLUCENT), packedLightIn, nodes.toArray(new Vector4f[nodes.size()]), argb, VFXHelper.getWidthFunc(width));
    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements IParticleFactory<BasicParticleType> {

        public Factory(IAnimatedSprite sprite) {

        }

        @Nullable
        @Override
        public Particle createParticle(BasicParticleType data, ClientWorld world, double x, double y, double z, double speed, double scale, double height) {

            return new WindVortexParticle(world, x, y, z, speed, scale, height);
        }
    }

}
