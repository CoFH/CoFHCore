package cofh.core.client;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

public abstract class PostTarget extends PostEffect {

    protected final String targetName;
    protected final String outputName;
    protected final RenderStateShard.OutputStateShard fallback;
    protected RenderStateShard.OutputStateShard output;
    protected RenderTarget target;
    protected boolean active;

    public PostTarget(ResourceLocation shader, String targetName, RenderStateShard.OutputStateShard fallback) {

        super(shader);
        this.targetName = targetName;
        this.outputName = shader.toString();
        this.fallback = fallback;
        this.output = fallback;
    }

    public PostTarget(ResourceLocation shader) {

        this(shader, "final", RenderType.MAIN_TARGET);
    }

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
                () -> Minecraft.getInstance().getMainRenderTarget().bindWrite(true));
    }

}
