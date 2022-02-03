package cofh.core.client.particle;

import cofh.core.util.helpers.RenderHelper;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class ShockwaveParticle extends Particle {

    protected float heightScale;
    protected float speed;

    private ShockwaveParticle(ClientWorld worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double speed, double radius, double heightScale) {

        super(worldIn, xCoordIn, yCoordIn, zCoordIn, radius, speed, heightScale);
        this.lifetime = MathHelper.ceil((radius + 6) / speed);
        this.speed = (float) speed;
        this.setSize((float) radius * 2, (float) heightScale); //TODO: bbheight?
        this.heightScale = (float) heightScale;

        hasPhysics = false;
        xd = yd = zd = 0;
    }

    @Override
    public void render(IVertexBuilder builder, ActiveRenderInfo info, float partialTicks) {

        BlockPos origin = new BlockPos(x, y, z);
        Vector3d camPos = info.getPosition();

        MatrixStack matrixStack = new MatrixStack();
        matrixStack.translate(origin.getX() - camPos.x, origin.getY() - camPos.y, origin.getZ() - camPos.z);
        IRenderTypeBuffer.Impl buffer = Minecraft.getInstance().renderBuffers().bufferSource();

        RenderHelper.renderShockwave(matrixStack, buffer, level, origin, (age + partialTicks) * speed, bbWidth * 0.5F, heightScale);

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
        public Particle createParticle(BasicParticleType data, ClientWorld world, double x, double y, double z, double speed, double radius, double heightScale) {

            return new ShockwaveParticle(world, x, y, z, speed, radius, heightScale);
        }
    }

}
