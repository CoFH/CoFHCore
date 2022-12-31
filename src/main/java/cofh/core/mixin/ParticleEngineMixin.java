package cofh.core.mixin;

import cofh.core.util.helpers.RenderHelper;
import cofh.core.util.helpers.vfx.RenderTypes;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;

/**
 * pain
 * @author Hekera
 */
@Mixin (ParticleEngine.class)
public abstract class ParticleEngineMixin {

    @Inject (
            method = "render*",
            locals = LocalCapture.CAPTURE_FAILSOFT,
            at = @At (
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/vertex/Tesselator;getInstance()Lcom/mojang/blaze3d/vertex/Tesselator;"
            )
    )
    private void poseStackFix(PoseStack stack, MultiBufferSource.BufferSource source, LightTexture light, Camera cam, float pTick, Frustum frustum,
                              CallbackInfo info, PoseStack renderStack, Iterator<ParticleRenderType> iter, ParticleRenderType type) {

        if (type instanceof RenderTypes.ParticleRenderTypeCoFH) {
            RenderHelper.particleStack = stack;
            renderStack.popPose();
            RenderSystem.applyModelViewMatrix();
        }
    }

    @Inject (
            method = "render*",
            locals = LocalCapture.CAPTURE_FAILSOFT,
            at = @At (
                    value = "INVOKE",
                    shift = At.Shift.AFTER,
                    target = "Lnet/minecraft/client/particle/ParticleRenderType;end(Lcom/mojang/blaze3d/vertex/Tesselator;)V"
            )
    )
    private void poseStackRevert(PoseStack stack, MultiBufferSource.BufferSource source, LightTexture light, Camera cam, float pTick, Frustum frustum,
                                 CallbackInfo info, PoseStack renderStack, Iterator<ParticleRenderType> iter, ParticleRenderType type) {

        if (type instanceof RenderTypes.ParticleRenderTypeCoFH) {
            source.endBatch();
            renderStack.pushPose();
            renderStack.mulPoseMatrix(stack.last().pose());
            RenderSystem.applyModelViewMatrix();
        }
    }

}