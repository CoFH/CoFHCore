package cofh.core.client;

import cofh.core.util.helpers.RenderHelper;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

public abstract class PostBuffer extends PostEffect implements MultiBufferSource {

    protected final BasicBufferSource buffer = new BasicBufferSource(256);
    protected final String targetName;
    protected final String outputName;
    protected final RenderStateShard.OutputStateShard fallback;
    protected RenderStateShard.OutputStateShard output;
    protected RenderTarget target;
    protected boolean active;

    public PostBuffer(ResourceLocation shader, String targetName, RenderStateShard.OutputStateShard fallback) {

        super(shader);
        this.targetName = targetName;
        this.outputName = shader.toString();
        this.fallback = fallback;
        this.output = fallback;
    }

    public PostBuffer(ResourceLocation shader) {

        this(shader, "final", RenderType.MAIN_TARGET);
    }

    @Override
    public VertexConsumer getBuffer(RenderType type) {

        if (isEnabled()) {
            active = true;
            return buffer.getBuffer(type);
        }
        return RenderHelper.bufferSource().getBuffer(type);
    }

    public VertexConsumer getBuffer(ResourceLocation texture) {

        return getBuffer(getRenderType(texture));
    }

    public abstract RenderType getRenderType(ResourceLocation texture);

    //public RenderTarget getRenderTarget() {
    //
    //    return target;
    //}

    public RenderStateShard.OutputStateShard getOutputShard() {

        return isEnabled() ? output : fallback;
    }

    @Override
    public void begin(float partialTick) {

        if (active) {
            target.clear(Minecraft.ON_OSX);
            active = false;
        }
    }

    @Override
    public void end(float partialTick) {

        if (active) {
            buffer.endBatch();
            super.end(partialTick);
        }
    }

    @Override
    public void apply(Window window) {

        if (active) {
            target.blitToScreen(window.getWidth(), window.getHeight(), false);
        }
    }

    @Override
    public void onResourceManagerReload(ResourceManager manager) {

        target = null;
        output = RenderType.MAIN_TARGET;
        super.onResourceManagerReload(manager);
    }

    @Override
    protected void onChainLoad() {

        target = chain.getTempTarget(targetName);
        output = new RenderStateShard.OutputStateShard(outputName,
                () -> target.bindWrite(false),
                () -> Minecraft.getInstance().getMainRenderTarget().bindWrite(false));
    }

}
