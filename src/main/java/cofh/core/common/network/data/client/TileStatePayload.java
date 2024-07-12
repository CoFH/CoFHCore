package cofh.core.common.network.data.client;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import static cofh.lib.util.constants.ModIds.ID_COFH_CORE;

public record TileStatePayload(BlockPos pos, FriendlyByteBuf buf) implements CustomPacketPayload {

    public static final ResourceLocation ID = new ResourceLocation(ID_COFH_CORE, "tile_state_packet");

    public TileStatePayload(final FriendlyByteBuf buf) {

        this(buf.readBlockPos(), buf);
    }

    @Override
    public void write(FriendlyByteBuf buf) {

        buf.writeBlockPos(pos);
        buf.writeBytes(this.buf);
    }

    @Override
    public ResourceLocation id() {

        return ID;
    }

}
