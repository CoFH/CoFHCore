package cofh.core.client.particle;

import cofh.lib.util.helpers.MathHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.phys.Vec3;

public abstract class CustomRenderParticle extends Particle {

    public CustomRenderParticle(net.minecraft.client.multiplayer.ClientLevel level, double xPos, double yPos, double zPos, double xVel, double yVel, double zVel) {

        super(level, xPos, yPos, zPos, xVel, yVel, zVel);
    }

    @Override
    public void render(VertexConsumer builder, Camera info, float partialTicks) {

        Vec3 camPos = info.getPosition();
        MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
        PoseStack stack = new PoseStack();
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
    }

    public abstract void render(PoseStack stack, MultiBufferSource buffer, int packedLightIn, float partialTicks);

    @Override
    public ParticleRenderType getRenderType() {

        return ParticleRenderType.CUSTOM;
    }

}
