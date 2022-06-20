package cofh.lib.content.block.entity;

import net.minecraft.network.FriendlyByteBuf;

public interface IPacketHandlerTile extends ITileLocation {

    // CONFIG
    default FriendlyByteBuf getConfigPacket(FriendlyByteBuf buffer) {

        return buffer;
    }

    default void handleConfigPacket(FriendlyByteBuf buffer) {

    }

    // CONTROL
    default FriendlyByteBuf getControlPacket(FriendlyByteBuf buffer) {

        return buffer;
    }

    default void handleControlPacket(FriendlyByteBuf buffer) {

    }

    // GUI
    default FriendlyByteBuf getGuiPacket(FriendlyByteBuf buffer) {

        return buffer;
    }

    default void handleGuiPacket(FriendlyByteBuf buffer) {

    }

    // REDSTONE
    default FriendlyByteBuf getRedstonePacket(FriendlyByteBuf buffer) {

        return buffer;
    }

    default void handleRedstonePacket(FriendlyByteBuf buffer) {

    }

    // STATE
    default FriendlyByteBuf getStatePacket(FriendlyByteBuf buffer) {

        return buffer;
    }

    default void handleStatePacket(FriendlyByteBuf buffer) {

    }

    // RENDER
    default FriendlyByteBuf getRenderPacket(FriendlyByteBuf buffer) {

        return buffer;
    }

    default void handleRenderPacket(FriendlyByteBuf buffer) {

    }

}
