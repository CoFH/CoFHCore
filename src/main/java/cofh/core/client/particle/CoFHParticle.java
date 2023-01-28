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

/**
 * The base class for CoFH particles.
 */
public abstract class CoFHParticle extends Particle {

    //The total time a particle is rendered, in ticks.
    protected float duration = 1.0F;
    //The amount of delay before a particle is rendered, in ticks.
    protected float delay = 0.0F;
    //The size of the particle. May represent different things for different particles. Refer to the implementation for exact details.
    protected float size = 1.0F;
    protected final int seed;

    public CoFHParticle(CoFHParticleOptions data, ClientLevel level, double x, double y, double z, double dx, double dy, double dz) {

        super(level, x + dx, y + dy, z + dz);
        this.seed = random.nextInt();
        this.xo = x;
        this.yo = y;
        this.zo = z;
        this.xd = dx;
        this.yd = dy;
        this.zd = dz;
        setLifetime(data.duration, data.delay);
        setSize(data.size);
    }

    //Pre-mixin rendering method
    //@Override
    //public void render(VertexConsumer consumer, Camera cam, float partialTicks) {
    //
    //    float time = this.age + partialTicks - this.delay;
    //    if (time < 0 || time > this.duration) {
    //        return;
    //    }
    //    Vec3 camPos = cam.getPosition();
    //    MultiBufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
    //
    //    PoseStack sysStack = RenderSystem.getModelViewStack();
    //    Matrix4f pose = sysStack.last().pose();
    //    sysStack.popPose();
    //    RenderSystem.applyModelViewMatrix();
    //
    //    PoseStack stack = new PoseStack();
    //    stack.mulPoseMatrix(pose);
    //    stack.pushPose();
    //
    //    double x = MathHelper.interpolate(this.xo, this.x, partialTicks);
    //    double y = MathHelper.interpolate(this.yo, this.y, partialTicks);
    //    double z = MathHelper.interpolate(this.zo, this.z, partialTicks);
    //    stack.translate(x - camPos.x, y - camPos.y, z - camPos.z);
    //
    //    stack.pushPose();
    //    stack.mulPose(cam.rotation());
    //    render(stack, buffer, consumer, getLightColor(partialTicks, x, y, z), time, partialTicks, cam);
    //    stack.popPose();
    //
    //    stack.popPose();
    //
    //    sysStack.pushPose();
    //    sysStack.mulPoseMatrix(pose);
    //    RenderSystem.applyModelViewMatrix();
    //}

    @Override
    public void render(VertexConsumer consumer, Camera cam, float partialTicks) {

        float time = this.age + partialTicks - this.delay;
        if (time < 0 || time > this.duration) {
            return;
        }
        Vec3 camPos = cam.getPosition();
        MultiBufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();

        PoseStack stack = RenderHelper.particleStack;
        stack.pushPose();

        double x = MathHelper.interpolate(this.xo, this.x, partialTicks);
        double y = MathHelper.interpolate(this.yo, this.y, partialTicks);
        double z = MathHelper.interpolate(this.zo, this.z, partialTicks);
        stack.translate(x - camPos.x, y - camPos.y, z - camPos.z);

        render(stack, buffer, consumer, getLightColor(partialTicks, x, y, z), time, partialTicks);

        stack.popPose();
    }

    /**
     * Method for rendering impl.
     *
     * @param buffer   Minecraft's main buffer source.
     * @param consumer {@link VertexConsumer} from the {@link ParticleRenderType} given by {@link #getRenderType()}. Useless for most particles with custom rendering.
     * @param time     Number of ticks since the particle started rendering.
     * @param pTicks   Partial ticks. The {@code time} parameter should usually be used instead.
     */
    public abstract void render(PoseStack stack, MultiBufferSource buffer, VertexConsumer consumer, int packedLight, float time, float pTicks);

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

    protected void setLifetime(float duration, float delay) {

        this.delay = delay;
        this.duration = duration;
        lifetime = MathHelper.ceil(this.duration + delay);
    }

    protected void setSize(float size) {

        this.size = size;
    }

}
