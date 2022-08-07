package cofh.core.client.particle;

import cofh.core.client.particle.options.CoFHParticleOptions;
import cofh.core.util.helpers.RenderHelper;
import cofh.core.util.helpers.vfx.RenderTypes;
import cofh.lib.util.helpers.MathHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

public abstract class CustomRenderParticle extends Particle {

    protected float fLifetime = 1.0F;
    protected float size = 1.0F;
    protected final int seed;

    public CustomRenderParticle(CoFHParticleOptions data, ClientLevel level, double x, double y, double z, double dx, double dy, double dz) {

        super(level, x, y, z);
        this.seed = random.nextInt();
        this.xd = dx;
        this.yd = dy;
        this.zd = dz;
        setDuration(data.duration);
        setSize(data.size);
    }

    @Override
    public void render(VertexConsumer builder, Camera info, float partialTicks) {

        Vec3 camPos = info.getPosition();
        MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
        PoseStack stack = RenderHelper.particleStack;
        stack.pushPose();

        double x = MathHelper.interpolate(this.xo, this.x, partialTicks);
        double y = MathHelper.interpolate(this.yo, this.y, partialTicks);
        double z = MathHelper.interpolate(this.zo, this.z, partialTicks);
        stack.translate(x - camPos.x, y - camPos.y, z - camPos.z);

        stack.pushPose();
        render(stack, buffer, getLightColor(partialTicks, x, y, z), partialTicks);
        stack.popPose();

        stack.popPose();
    }

    public abstract void render(PoseStack stack, MultiBufferSource buffer, int packedLight, float pTicks);

    @Override
    public ParticleRenderType getRenderType() {

        return RenderTypes.CUSTOM;
    }

    @Override
    public int getLightColor(float pTicks) {

        double x = MathHelper.interpolate(this.xo, this.x, pTicks);
        double y = MathHelper.interpolate(this.yo, this.y, pTicks);
        double z = MathHelper.interpolate(this.zo, this.z, pTicks);
        return getLightColor(pTicks, x, y, z);
    }

    protected int getLightColor(float pTicks, double x, double y, double z) {

        BlockPos blockpos = new BlockPos(x, y, z);
        return this.level.hasChunkAt(blockpos) ? LevelRenderer.getLightColor(this.level, blockpos) : 0;
    }

    protected void setDuration(float duration) {

        fLifetime = duration;
        lifetime = MathHelper.ceil(fLifetime);
    }

    protected void setSize(float size) {

        this.size = size;
    }

}
