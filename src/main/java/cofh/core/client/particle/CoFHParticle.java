package cofh.core.client.particle;

import cofh.core.client.event.CoreClientEvents;
import cofh.core.client.particle.options.CoFHParticleOptions;
import cofh.lib.util.helpers.MathHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;

import java.util.ArrayDeque;

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

    @Override
    public void render(VertexConsumer consumer, Camera cam, float partialTicks) {

        CoreClientEvents.delayedRenderParticles.computeIfAbsent(getRenderType(), type -> new ArrayDeque<>()).offer(this);
    }

    public void render(PoseStack stack, MultiBufferSource buffer, VertexConsumer consumer, float pTicks) {

        float time = this.age + pTicks - this.delay;
        if (time < 0 || this.duration <= time) {
            return;
        }
        stack.pushPose();

        double x = MathHelper.interpolate(this.xo, this.x, pTicks);
        double y = MathHelper.interpolate(this.yo, this.y, pTicks);
        double z = MathHelper.interpolate(this.zo, this.z, pTicks);
        stack.translate(x, y, z);

        render(stack, buffer, consumer, getLightColor(pTicks, x, y, z), time, pTicks);

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

        return ParticleRenderType.CUSTOM;
    }

    @Override
    public int getLightColor(float pTicks) {

        double x = MathHelper.interpolate(this.xo, this.x, pTicks);
        double y = MathHelper.interpolate(this.yo, this.y, pTicks);
        double z = MathHelper.interpolate(this.zo, this.z, pTicks);
        return getLightColor(pTicks, x, y, z);
    }

    protected int getLightColor(float pTicks, double x, double y, double z) {

        BlockPos blockpos = BlockPos.containing(x, y, z);
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
