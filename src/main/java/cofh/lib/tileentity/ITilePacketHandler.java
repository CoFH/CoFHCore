package cofh.lib.tileentity;

import net.minecraft.network.PacketBuffer;

public interface ITilePacketHandler {

    // CONFIG
    default PacketBuffer getConfigPacket(PacketBuffer buffer) {

        return buffer;
    }

    default void handleConfigPacket(PacketBuffer buffer) {

    }

    // CONTROL
    default PacketBuffer getControlPacket(PacketBuffer buffer) {

        return buffer;
    }

    default void handleControlPacket(PacketBuffer buffer) {

    }

    // GUI
    default PacketBuffer getGuiPacket(PacketBuffer buffer) {

        return buffer;
    }

    default void handleGuiPacket(PacketBuffer buffer) {

    }

    // REDSTONE
    default PacketBuffer getRedstonePacket(PacketBuffer buffer) {

        return buffer;
    }

    default void handleRedstonePacket(PacketBuffer buffer) {

    }

    // STATE
    default PacketBuffer getStatePacket(PacketBuffer buffer) {

        return buffer;
    }

    default void handleStatePacket(PacketBuffer buffer) {

    }

}
