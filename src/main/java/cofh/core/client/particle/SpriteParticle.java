package cofh.core.client.particle;

import cofh.core.client.particle.options.ColorParticleOptions;
import cofh.core.util.helpers.vfx.RenderTypes;
import cofh.lib.util.helpers.MathHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector4f;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * Reimplementation of {@link TextureSheetParticle} in a CoFH flavor.
 * Should theoretically be much more configurable and performant compared to vanilla.
 */
public abstract class SpriteParticle extends ColorParticle {

    protected final SpriteSet sprites;
    protected TextureAtlasSprite sprite;

    protected SpriteParticle(ColorParticleOptions data, ClientLevel level, SpriteSet sprites, double x, double y, double z, double dx, double dy, double dz) {

        super(data, level, x, y, z, dx, dy, dz);
        this.sprites = sprites;
        this.sprite = sprites.get(0, 1);
    }

    @Override
    public ParticleRenderType getRenderType() {

        return RenderTypes.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public void setSize(float size) {

        float half = size * 0.5F;
        Vec3 pos = new Vec3(x, y + half, z);
        setBoundingBox(new AABB(pos, pos).inflate(half));
        bbWidth = bbHeight = size;
        super.setSize(size);
    }

    @Override
    public void render(PoseStack stack, MultiBufferSource buffer, VertexConsumer consumer, int packedLight, float time, float pTicks) {

        Vector4f center = new Vector4f(0, 0, 0, 1);
        center.transform(stack.last().pose());

        float x = center.x();
        float y = center.y();
        float z = center.z() + 0.1F;

        float rot = MathHelper.interpolate(oRoll, roll, pTicks);
        float sin = MathHelper.sin(rot);
        float cos = MathHelper.cos(rot);
        float w = size * 0.5F;
        float a = w * (cos - sin);
        float b = w * (sin + cos);

        float u0 = sprite.getU0();
        float u1 = sprite.getU1();
        float v0 = sprite.getV0();
        float v1 = sprite.getV1();

        consumer.vertex(x + a, y + b, z).uv(u1, v0).color(c0.r, c0.g, c0.b, c0.a).uv2(packedLight).endVertex();
        consumer.vertex(x - b, y + a, z).uv(u0, v0).color(c0.r, c0.g, c0.b, c0.a).uv2(packedLight).endVertex();
        consumer.vertex(x - a, y - b, z).uv(u0, v1).color(c0.r, c0.g, c0.b, c0.a).uv2(packedLight).endVertex();
        consumer.vertex(x + b, y - a, z).uv(u1, v1).color(c0.r, c0.g, c0.b, c0.a).uv2(packedLight).endVertex();
    }

}
