package cofh.core.common.network.data.server;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import static cofh.lib.util.constants.ModIds.ID_COFH_CORE;

public record SideConfigPayload(BlockPos pos, byte[] sides) implements CustomPacketPayload {

    public static final ResourceLocation ID = new ResourceLocation(ID_COFH_CORE, "side_config_packet");

    public SideConfigPayload(final FriendlyByteBuf buf) {

        this(buf.readBlockPos(), buf.readByteArray(6));
    }

    @Override
    public void write(FriendlyByteBuf buf) {

        buf.writeBlockPos(pos);
        buf.writeByteArray(sides);
    }

    @Override
    public ResourceLocation id() {

        return ID;
    }

}
