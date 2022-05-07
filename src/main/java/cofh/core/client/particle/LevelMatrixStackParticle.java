package cofh.core.client.particle;

import cofh.core.CoFHCore;
import cofh.core.event.CoreClientEvents;
import cofh.lib.util.helpers.MathHelper;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.Level;

public abstract class LevelMatrixStackParticle extends CustomRenderParticle {

    // TODO revisit whether this is necessary
    public LevelMatrixStackParticle(ClientLevel level, double xPos, double yPos, double zPos, double xVel, double yVel, double zVel) {

        super(level, xPos, yPos, zPos, xVel, yVel, zVel);
    }

    @Override
    public void render(VertexConsumer builder, Camera info, float partialTicks) {

        super.render(builder, info, partialTicks);
        //Vec3 camPos = info.getPosition();
        //MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
        ////PoseStack stack = CoreClientEvents.levelStack;
        //PoseStack stack = RenderSystem.getModelViewStack();
        ////renderStack.popPose();
        //stack.pushPose();
        //double x = MathHelper.interpolate(this.xo, this.x, partialTicks) - camPos.x;
        //double y = MathHelper.interpolate(this.yo, this.y, partialTicks) - camPos.y;
        //double z = MathHelper.interpolate(this.zo, this.z, partialTicks) - camPos.z;
        //stack.translate(x, y, z);
        //stack.pushPose();
        //RenderSystem.applyModelViewMatrix();
        //render(stack, buffer, getLightColor(partialTicks), partialTicks);
        //stack.popPose();
        //
        //stack.popPose();
        ////RenderSystem.setShader(GameRenderer::getParticleShader);
        ////RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        //buffer.endBatch();
        ////        RenderSystem.pushMatrix();
        ////        RenderSystem.multMatrix(stack.last().pose());
    }

}
