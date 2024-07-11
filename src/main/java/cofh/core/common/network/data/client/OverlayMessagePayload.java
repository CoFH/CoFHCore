package cofh.core.common.network.data.client;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import static cofh.lib.util.constants.ModIds.ID_COFH_CORE;

public record OverlayMessagePayload(String message) implements CustomPacketPayload {

    public static final ResourceLocation ID = new ResourceLocation(ID_COFH_CORE, "overlay_message_packet");

    public OverlayMessagePayload(final FriendlyByteBuf buf) {

        this(buf.readUtf());
    }

    @Override
    public void write(FriendlyByteBuf buf) {

        buf.writeUtf(message);
    }

    @Override
    public ResourceLocation id() {

        return ID;
    }

}
