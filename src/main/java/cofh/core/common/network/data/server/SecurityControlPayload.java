package cofh.core.common.network.data.server;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import static cofh.lib.util.constants.ModIds.ID_COFH_CORE;

public record SecurityControlPayload(BlockPos pos, byte mode) implements CustomPacketPayload {

    public static final ResourceLocation ID = new ResourceLocation(ID_COFH_CORE, "security_control_packet");

    public SecurityControlPayload(final FriendlyByteBuf buf) {

        this(buf.readBlockPos(), buf.readByte());
    }

    @Override
    public void write(FriendlyByteBuf buf) {

        buf.writeBlockPos(pos);
        buf.writeByte(mode);
    }

    @Override
    public ResourceLocation id() {

        return ID;
    }

}
