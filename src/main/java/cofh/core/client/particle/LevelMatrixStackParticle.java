package cofh.core.client.particle;

import cofh.core.event.CoreClientEvents;
import cofh.lib.util.helpers.MathHelper;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.vector.Vector3d;

public abstract class LevelMatrixStackParticle extends CustomRenderParticle {

    public LevelMatrixStackParticle(ClientWorld level, double xPos, double yPos, double zPos, double xVel, double yVel, double zVel) {

        super(level, xPos, yPos, zPos, xVel, yVel, zVel);
    }

    @Override
    public void render(IVertexBuilder builder, ActiveRenderInfo info, float partialTicks) {

        Vector3d camPos = info.getPosition();
        IRenderTypeBuffer.Impl buffer = Minecraft.getInstance().renderBuffers().bufferSource();
        MatrixStack stack = CoreClientEvents.levelStack;
        RenderSystem.popMatrix();
        stack.pushPose();

        double x = MathHelper.interpolate(this.xo, this.x, partialTicks) - camPos.x;
        double y = MathHelper.interpolate(this.yo, this.y, partialTicks) - camPos.y;
        double z = MathHelper.interpolate(this.zo, this.z, partialTicks) - camPos.z;
        stack.translate(x, y, z);
        stack.pushPose();
        render(stack, buffer, getLightColor(partialTicks), partialTicks);
        stack.popPose();

        stack.popPose();
        buffer.endBatch();
        RenderSystem.pushMatrix();
        RenderSystem.multMatrix(stack.last().pose());
    }

}
