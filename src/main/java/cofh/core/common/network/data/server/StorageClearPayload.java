package cofh.core.common.network.data.server;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import static cofh.lib.util.constants.ModIds.ID_COFH_CORE;

public record StorageClearPayload(BlockPos pos, int type, int index) implements CustomPacketPayload {

    public static final ResourceLocation ID = new ResourceLocation(ID_COFH_CORE, "storage_clear_packet");

    public StorageClearPayload(final FriendlyByteBuf buf) {

        this(buf.readBlockPos(), buf.readInt(), buf.readInt());
    }

    @Override
    public void write(FriendlyByteBuf buf) {

        buf.writeBlockPos(pos);
        buf.writeInt(type);
        buf.writeInt(index);
    }

    @Override
    public ResourceLocation id() {

        return ID;
    }

}
