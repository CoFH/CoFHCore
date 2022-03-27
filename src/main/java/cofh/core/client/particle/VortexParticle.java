package cofh.core.client.particle;

import cofh.core.util.helpers.vfx.VFXHelper;
import cofh.lib.util.helpers.MathHelper;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.math.vector.Vector4f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class VortexParticle extends Particle {

    protected int length;
    protected float scale;
    protected float height;

    private VortexParticle(ClientWorld worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double speed, double scale, double height) {

        super(worldIn, xCoordIn, yCoordIn, zCoordIn, speed, scale, height);
        this.lifetime = MathHelper.ceil(10 / speed);
        this.length = random.nextInt(16) + 16;
        this.height = (float) height * random.nextFloat();
        this.scale = (float) (scale * (1.0F + random.nextGaussian() * 0.1F));
        this.setSize(this.scale * 4, this.height);

        hasPhysics = false;
        xd = yd = zd = 0;
        alpha = 0.125F * (1.0F + random.nextFloat());
        rCol = gCol = bCol = 1.0F - 0.1F * random.nextFloat();
        oRoll = roll = random.nextFloat() * 2 * (float) Math.PI;
    }

    @Override
    public void render(IVertexBuilder builder, ActiveRenderInfo info, float partialTicks) {

        float time = age + partialTicks;
        float progress = time / lifetime;
        Vector3d camPos = info.getPosition();

        MatrixStack matrixStack = new MatrixStack();
        matrixStack.translate(x - camPos.x, y - camPos.y, z - camPos.z);
        matrixStack.scale(scale, height, scale);
        matrixStack.mulPose(Vector3f.YP.rotation(roll));
        IRenderTypeBuffer.Impl buffer = Minecraft.getInstance().renderBuffers().bufferSource();

        int a = (int) (255.0F * alpha * MathHelper.clamp(3.0F * (0.5F - Math.abs(progress - 0.5F)), 0.0F, 1.0F));
        int argb = (a << 24) | ((int) (255.0F * rCol) << 16) | ((int) (255.0F * gCol) << 8) | ((int) (255.0F * bCol));
        List<Vector4f> nodes = new ArrayList<>();
        int num = (int) (3.0F * Math.max(progress - 0.66F, 0.0F) * length);
        float increment = 1.0F / 48.0F;
        float i = Math.min(0.66F, progress);
        for (; i >= 0.33F && num < length; i -= increment, ++num) {
            nodes.add(new Vector4f((float) MathHelper.cos(9.4248F * (i - 0.33F)) * 0.5F, (1.0F - i * 1.5F) * height, (float) MathHelper.sin(9.4248F * (i - 0.33F)) * 0.75F, 1.0F));
        }
        for (; i >= 0 && num < length; i -= increment, ++num) {
            nodes.add(new Vector4f((float) MathHelper.cos(4.7124F * (i - 0.33F)) * 2.0F - 1.5F, (1.0F - i * 1.5F) * height, (float) MathHelper.sin(4.7124F * (i - 0.33F)) * 2.0F, 1.0F));
        }
        if (i < 0 && num < length) {
            nodes.add(new Vector4f(-1.25F, height, -2.0F, 1.0F));
        }
        VFXHelper.renderCyclone(matrixStack, buffer, getLightColor(partialTicks), 0.5F, 0.5F, 1, 0.03F, -time, 0.5F);
        //RenderHelper.renderStreamLine(matrixStack, buffer, getLightColor(partialTicks), 0.03F, nodes.toArray(new Vector4f[nodes.size()]), new Vector2f(0, 1), argb);

        buffer.endBatch();
    }

    @Override
    public IParticleRenderType getRenderType() {

        return IParticleRenderType.CUSTOM;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements IParticleFactory<BasicParticleType> {

        public Factory(IAnimatedSprite sprite) {

        }

        @Nullable
        @Override
        public Particle createParticle(BasicParticleType data, ClientWorld world, double x, double y, double z, double speed, double scale, double height) {

            return new VortexParticle(world, x, y, z, speed, scale, height);
        }
    }

}
