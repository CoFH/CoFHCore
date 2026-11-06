package cofh.core.client;

import cofh.core.util.helpers.RenderHelper;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public abstract class PixelateBuffer extends PostEffect {

    protected final BasicBufferSource[] sources;
    protected final RenderTarget[] targets;
    protected final RenderStateShard.OutputStateShard[] outputs;
    protected final Map<ResourceLocation, RenderType[]> types = new HashMap<>();
    protected final BucketConsumer consumer;
    protected final String outputName;
    protected RenderTarget out;
    protected float size;
    protected boolean active;

    public PixelateBuffer(ResourceLocation shader, int levels, float size) {

        super(shader);
        this.sources = new BasicBufferSource[levels];
        for (int i = 0; i < levels; ++i) {
            sources[i] = new BasicBufferSource(256);
        }
        this.targets = new RenderTarget[levels];
        this.outputs = new RenderStateShard.OutputStateShard[levels];
        this.outputName = shader.toString();
        this.size = size;
        this.consumer = new BucketConsumer(levels);
    }

    public VertexConsumer getBuffer(ResourceLocation texture) {

        if (!isEnabled()) {
            return RenderHelper.bufferSource().getBuffer(getRenderType(texture, -1));
        }
        active = true;
        RenderType[] types = this.types.computeIfAbsent(texture, tex -> {
            RenderType[] tipes = new RenderType[sources.length];
            for (int i = 0; i < tipes.length; ++i) {
                tipes[i] = getRenderType(tex, i);
            }
            return tipes;
        });
        for (int i = 0; i < sources.length; ++i) {
            consumer.buckets[i] = sources[i].getBuffer(types[i]);
        }
        return consumer.reset(size * RenderSystem.getProjectionMatrix().m11() * Minecraft.getInstance().getMainRenderTarget().height * 0.5F);
    }

    public abstract RenderType getRenderType(ResourceLocation texture, int level);

    public RenderStateShard.OutputStateShard getOutputShard(int level) {

        return level < 0 ? RenderType.MAIN_TARGET : outputs[level];
    }

    protected void resizeTargets(int width, int height) {

        for (int i = 1; i < targets.length; ++i) {
            int round = (1 << i) - 1;
            targets[i].resize((width + round) >> i, (height + round) >> i, Minecraft.ON_OSX);
        }
    }

    @Override
    public void begin(float partialTick) {

        if (active) {
            for (RenderTarget target : targets) {
                target.clear(Minecraft.ON_OSX);
            }
            Minecraft.getInstance().getMainRenderTarget().bindWrite(true);
            active = false;
        }
    }

    @Override
    public void end(float partialTick) {

        if (active) {
            for (BasicBufferSource source : sources) {
                source.endBatch();
            }
            super.end(partialTick);
        }
    }

    @Override
    public void apply(Window window) {

        if (active) {
            out.blitToScreen(window.getWidth(), window.getHeight(), false);
        }
    }

    @Override
    public void resize(int width, int height) {

        super.resize(width, height);
        if (loaded) {
            resizeTargets(width, height);
        }
    }

    @Override
    protected void onChainLoad() {

        Minecraft mc = Minecraft.getInstance();
        for (int i = 0; i < targets.length; ++i) {
            RenderTarget target = chain.getTempTarget(i == 0 ? "final" : "pixel" + (1 << i));
            targets[i] = target;
            outputs[i] = new RenderStateShard.OutputStateShard(outputName + i,
                    () -> target.bindWrite(true),
                    () -> mc.getMainRenderTarget().bindWrite(true));
        }
        out = chain.getTempTarget("swap");
        types.clear();
        resizeTargets(mc.getWindow().getWidth(), mc.getWindow().getHeight());
    }
    
    protected static class BucketConsumer implements VertexConsumer {

        protected final VertexConsumer[] buckets;
        protected VertexConsumer out;
        protected float scale;
        protected int count;

        public BucketConsumer(int size) {

            this.buckets = new VertexConsumer[size];
        }

        public BucketConsumer reset(float scale) {

            this.scale = scale;
            this.count = 0;
            return this;
        }

        protected int bucket(double z) {

            if (z >= 0) {
                return buckets.length - 1;
            }
            int size = (int) (scale / -z);
            return size > 1 ? Math.min(31 - Integer.numberOfLeadingZeros(size), buckets.length - 1) : 0;
        }

        protected void select(double z) {

            if ((count & 3) == 0) {
                out = buckets[bucket(z)];
            }
        }

        @Override
        public VertexConsumer vertex(double x, double y, double z) {

            select(z);
            out.vertex(x, y, z);
            return this;
        }

        @Override
        public VertexConsumer color(int r, int g, int b, int a) {

            out.color(r, g, b, a);
            return this;
        }

        @Override
        public VertexConsumer uv(float u, float v) {

            out.uv(u, v);
            return this;
        }

        @Override
        public VertexConsumer overlayCoords(int u, int v) {

            out.overlayCoords(u, v);
            return this;
        }

        @Override
        public VertexConsumer uv2(int u, int v) {

            out.uv2(u, v);
            return this;
        }

        @Override
        public VertexConsumer normal(float x, float y, float z) {

            out.normal(x, y, z);
            return this;
        }

        @Override
        public void endVertex() {

            out.endVertex();
            ++count;
        }

        @Override
        public void vertex(float x, float y, float z, float r, float g, float b, float a, float u, float v, int overlay, int light, float nx, float ny, float nz) {

            select(z);
            out.vertex(x, y, z, r, g, b, a, u, v, overlay, light, nx, ny, nz);
            ++count;
        }

        @Override
        public void defaultColor(int r, int g, int b, int a) {

            for (VertexConsumer bucket : buckets) {
                bucket.defaultColor(r, g, b, a);
            }
        }

        @Override
        public void unsetDefaultColor() {

            for (VertexConsumer bucket : buckets) {
                bucket.unsetDefaultColor();
            }
        }

    }

}
