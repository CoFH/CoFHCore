package cofh.core.common.network.data.client;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import static cofh.lib.util.constants.ModIds.ID_COFH_CORE;

public record EffectAddedPayload(int entityId, ResourceLocation effect, int duration) implements CustomPacketPayload {

    public static final ResourceLocation ID = new ResourceLocation(ID_COFH_CORE, "effect_added_packet");

    public EffectAddedPayload(final FriendlyByteBuf buf) {

        this(buf.readVarInt(), buf.readResourceLocation(), buf.readInt());
    }

    @Override
    public void write(FriendlyByteBuf buf) {

        buf.writeVarInt(entityId);
        buf.writeResourceLocation(effect);
        buf.writeInt(duration);
    }

    @Override
    public ResourceLocation id() {

        return ID;
    }

}
