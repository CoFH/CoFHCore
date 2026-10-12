package cofh.core.common.network.packet.client;

import cofh.core.CoFHCore;
import cofh.lib.common.network.packet.IPacketClient;
import cofh.lib.common.network.packet.PacketBase;
import cofh.lib.util.helpers.ProfileHelper;
import net.minecraft.network.FriendlyByteBuf;

import java.util.UUID;

import static cofh.core.common.network.packet.PacketIDs.PACKET_PROFILE_ADD;

public class ProfileAddPacket extends PacketBase implements IPacketClient {

    protected UUID uuid;
    protected String username;

    public ProfileAddPacket() {

        super(PACKET_PROFILE_ADD, CoFHCore.PACKET_HANDLER);
    }

    @Override
    public void handleClient() {

        ProfileHelper.add(uuid, username);
    }

    @Override
    public void write(FriendlyByteBuf buf) {

        buf.writeUUID(uuid);
        buf.writeUtf(username);
    }

    @Override
    public void read(FriendlyByteBuf buf) {

        uuid = buf.readUUID();
        username = buf.readUtf();
    }

    public static void sendToClients(UUID uuid, String username) {

        ProfileAddPacket packet = new ProfileAddPacket();
        packet.uuid = uuid;
        packet.username = username;
        packet.sendToClients();
    }

}
