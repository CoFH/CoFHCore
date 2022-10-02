package cofh.core.client.particle;

import cofh.core.client.particle.options.ColorParticleOptions;
import cofh.lib.util.helpers.MathHelper;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.SplittableRandom;
import java.util.random.RandomGenerator;

public abstract class TextureParticleCoFH extends TextureSheetParticle {

    protected final SpriteSet sprites;
    protected float fLifetime;
    protected RandomGenerator rand = new SplittableRandom();

    protected TextureParticleCoFH(ColorParticleOptions data, ClientLevel level, SpriteSet sprites, double x, double y, double z) {

        super(level, x, y, z);
        this.sprites = sprites;
        setSize(data.size);
        setDuration(data.duration);
        setColor(data.rgba0);
    }

    protected TextureParticleCoFH(ColorParticleOptions data, ClientLevel level, SpriteSet sprites, double x, double y, double z, double dx, double dy, double dz) {

        super(level, x, y, z, dx, dy, dz);
        this.sprites = sprites;
        setSize(data.size);
        setDuration(data.duration);
        setColor(data.rgba0);
    }

    public void setDuration(float duration) {

        fLifetime = duration;
        lifetime = MathHelper.ceil(fLifetime);
    }

    public void setSize(float size) {

        float half = size * 0.5F;
        Vec3 pos = new Vec3(x, y + half, z);
        setBoundingBox(new AABB(pos, pos).inflate(half));
        bbWidth = bbHeight = size;
        quadSize = size;
    }

    public void setColor(int rgba) {

        this.rCol = ((rgba >> 24) & 0xFF) * 0.0039215686F;
        this.gCol = ((rgba >> 16) & 0xFF) * 0.0039215686F;
        this.bCol = ((rgba >> 8) & 0xFF) * 0.0039215686F;
        this.alpha = (rgba & 0xFF) * 0.0039215686F;
    }

    @Override
    public void render(VertexConsumer consumer, Camera cam, float pTicks) {

        if (this.age + pTicks > fLifetime) {
            return;
        }
        super.render(consumer, cam, pTicks);
        //Vec3 camPos = cam.getPosition();

        //PoseStack stack = RenderHelper.particleStack;
        //double xc = MathHelper.interpolate(this.xo, this.x, pTicks) - camPos.x;
        //double yc = MathHelper.interpolate(this.yo, this.y, pTicks) - camPos.y;
        //double zc = MathHelper.interpolate(this.zo, this.z, pTicks) - camPos.z;
        //stack.pushPose();
        //stack.translate(xc, yc, zc);
        //Vector4f center = new Vector4f(0, 0, 0, 1);
        //center.transform(stack.last().pose());
        //
        //float x = center.x();
        //float y = center.y();
        //float z = center.z();
        //
        //float size = this.getQuadSize(pTicks);
        //float rot = MathHelper.interpolate(oRoll, roll, pTicks);
        //float sqrt2 = 0.5F * (float) MathHelper.SQRT_2;
        //float rs = 0;
        //float rc = 0;
        ////float rs = MathHelper.sin(rot) - sqrt2;
        ////float rc = MathHelper.cos(rot) - sqrt2;
        //
        //float u0 = this.getU0();
        //float u1 = this.getU1();
        //float v0 = this.getV0();
        //float v1 = this.getV1();
        //int packedLight = this.getLightColor(pTicks);
        //consumer.vertex(xc + 0.5, yc + 0.5, zc).uv(u1, v1).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(packedLight).endVertex();
        //consumer.vertex(xc + 0.5, yc -0.5, zc).uv(u1, v0).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(packedLight).endVertex();
        //consumer.vertex(xc -0.5, yc -0.5, zc).uv(u0, v0).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(packedLight).endVertex();
        //consumer.vertex(xc -0.5, yc + 0.5, zc).uv(u0, v1).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(packedLight).endVertex();
        ////consumer.vertex(x - size - rc, y - size - rs, z).uv(u1, v1).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(packedLight).endVertex();
        ////consumer.vertex(x + size + rs, y - size + rc, z).uv(u1, v0).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(packedLight).endVertex();
        ////consumer.vertex(x + size + rc, y + size + rs, z).uv(u0, v0).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(packedLight).endVertex();
        ////consumer.vertex(x - size - rs, y + size - rc, z).uv(u0, v1).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(packedLight).endVertex();
        //
        ////VFXHelper.renderTest(stack);
        //stack.popPose();

        //Quaternion quat = cam.rotation().copy();
        //quat.mul(Vector3f.ZP.rotation(MathHelper.interpolate(oRoll, roll, pTicks)));
        //Vector3f unit = new Vector3f(1, 1, 0);
        //unit.transform(quat);
        //unit.mul(getQuadSize(pTicks));

    }

}
