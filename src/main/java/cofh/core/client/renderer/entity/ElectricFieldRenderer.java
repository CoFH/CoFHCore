package cofh.core.client.renderer.entity;

import cofh.core.common.entity.ElectricField;
import cofh.lib.client.renderer.entity.ITranslucentRenderer;
import cofh.lib.util.constants.ModIds;
import cofh.lib.util.helpers.MathHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.SplittableRandom;
import java.util.stream.IntStream;

public class ElectricFieldRenderer extends EntityRenderer<ElectricField> implements ITranslucentRenderer {

    public static final ResourceLocation[] TEXTURES = IntStream.range(0, 5).mapToObj(i -> new ResourceLocation(ModIds.ID_COFH_CORE, "textures/particle/plasma_ball_" + i + ".png")).toArray(ResourceLocation[]::new);

    public ElectricFieldRenderer(EntityRendererProvider.Context ctx) {

        super(ctx);
    }

    @Override
    public void render(ElectricField entity, float entityYaw, float partialTicks, PoseStack stack, MultiBufferSource buffer, int packedLight) {

        stack.pushPose();

        int time = MathHelper.floor((entity.tickCount + partialTicks) * 0.75F);
        SplittableRandom rand = new SplittableRandom(time * 69420L);
        float rot = rand.nextFloat(MathHelper.F_TAU);

        packedLight = 0x00F000F0;

        Vector4f center = new Vector4f(0, entity.getEyeHeight(), 0, 1).mul(stack.last().pose());
        VertexConsumer consumer = buffer.getBuffer(RenderType.entityTranslucent(getTextureLocation(entity)));

        float x = center.x();
        float y = center.y();
        float z = center.z() + 0.1F;

        float sin = MathHelper.sin(rot);
        float cos = MathHelper.cos(rot);
        float w = 0.5F;
        float a = w * (cos - sin);
        float b = w * (sin + cos);

        Vector3f normal = new Vector3f(0, 1, 0).mul(stack.last().normal());
        float nx = normal.x();
        float ny = normal.y();
        float nz = normal.z();

        consumer.vertex(x + a, y + b, z).color(0xFF, 0xFF, 0xFF, 0xFF).uv(1, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(nx, ny, nz).endVertex();
        consumer.vertex(x - b, y + a, z).color(0xFF, 0xFF, 0xFF, 0xFF).uv(0, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(nx, ny, nz).endVertex();
        consumer.vertex(x - a, y - b, z).color(0xFF, 0xFF, 0xFF, 0xFF).uv(0, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(nx, ny, nz).endVertex();
        consumer.vertex(x + b, y - a, z).color(0xFF, 0xFF, 0xFF, 0xFF).uv(1, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(nx, ny, nz).endVertex();

        stack.popPose();

        super.render(entity, entityYaw, partialTicks, stack, buffer, packedLight);
    }

    @Override
    public boolean shouldRender(ElectricField entity, Frustum clip, double x, double y, double z) {

        return super.shouldRender(entity, clip, x, y, z);
    }

    @Override
    public ResourceLocation getTextureLocation(ElectricField entity) {

        return TEXTURES[entity.getTextureIndex(TEXTURES.length)];
    }

}
