package cofh.core.common.network.data.client;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import static cofh.lib.util.constants.ModIds.ID_COFH_CORE;

public record PlayerMotionPayload(double motionX, double motionY, double motionZ) implements CustomPacketPayload {

    public static final ResourceLocation ID = new ResourceLocation(ID_COFH_CORE, "player_motion_packet");

    public PlayerMotionPayload(final FriendlyByteBuf buf) {

        this(buf.readDouble(), buf.readDouble(), buf.readDouble());
    }

    @Override
    public void write(FriendlyByteBuf buf) {

        buf.writeDouble(motionX);
        buf.writeDouble(motionY);
        buf.writeDouble(motionZ);
    }

    @Override
    public ResourceLocation id() {

        return ID;
    }

}
