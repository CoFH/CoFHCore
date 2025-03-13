package cofh.core.mixin;

import cofh.lib.util.ModelTracker;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelPart;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin (ModelPart.class)
public class ModelPartMixin {

    @Shadow @Final public List<ModelPart.Cube> cubes;

    @Inject (
            method = "compile",
            at = @At (
                    value = "HEAD"
            )
    )
    private void recordModelCube(PoseStack.Pose pPose, VertexConsumer pVertexConsumer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha, CallbackInfo ci) {

        if (ModelTracker.record) {
            ModelTracker.parts.add(new ModelTracker.PartRecord(cubes, new Matrix4f(pPose.pose())));
        }
    }

}
