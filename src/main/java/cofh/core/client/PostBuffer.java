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

public abstract class PostBuffer extends PostTarget implements MultiBufferSource {

    protected final BasicBufferSource buffer = new BasicBufferSource(256);

    public PostBuffer(ResourceLocation shader, String targetName, RenderStateShard.OutputStateShard fallback) {

        super(shader, targetName, fallback);
    }

    public PostBuffer(ResourceLocation shader) {

        super(shader);
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


    @Override
    public void end(float partialTick) {

        if (active) {
            buffer.endBatch();
            super.end(partialTick);
        }
    }

}
