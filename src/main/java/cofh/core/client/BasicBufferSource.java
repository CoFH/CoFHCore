package cofh.core.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;

public class BasicBufferSource implements MultiBufferSource {

    protected final BufferBuilder builder;
    protected final int size;
    protected RenderType lastState = null;

    public BasicBufferSource(int size) {

        this.builder = new BufferBuilder(size);
        this.size = size;
    }

    @Override
    public VertexConsumer getBuffer(RenderType type) {

        if (!type.equals(lastState) || !type.canConsolidateConsecutiveGeometry()) {
            endBatch();
            builder.begin(type.mode(), type.format());
            lastState = type;
        }
        return builder;
    }

    public void endBatch() {

        if (lastState != null) {
            lastState.end(builder, RenderSystem.getVertexSorting());
            lastState = null;
        }
    }

    public int getSize() {

        return size;
    }

}
