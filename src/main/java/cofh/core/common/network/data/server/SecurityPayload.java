package cofh.core.common.network.data.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import static cofh.lib.util.constants.ModIds.ID_COFH_CORE;

public record SecurityPayload(byte mode) implements CustomPacketPayload {

    public static final ResourceLocation ID = new ResourceLocation(ID_COFH_CORE, "security_packet");

    public SecurityPayload(final FriendlyByteBuf buf) {

        this(buf.readByte());
    }

    @Override
    public void write(FriendlyByteBuf buf) {

        buf.writeByte(mode);
    }

    @Override
    public ResourceLocation id() {

        return ID;
    }

}
