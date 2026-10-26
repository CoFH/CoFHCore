package cofh.core.common.network.packet.client;

import cofh.core.CoFHCore;
import cofh.core.util.helpers.LightHelper;
import cofh.lib.common.network.packet.IPacketClient;
import cofh.lib.common.network.packet.PacketBase;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;

import static cofh.core.common.network.packet.PacketIDs.PACKET_LIGHT_REMOVE;

public class LightRemovePacket extends PacketBase implements IPacketClient {

    protected long pos;

    public LightRemovePacket() {

        super(PACKET_LIGHT_REMOVE, CoFHCore.PACKET_HANDLER);
    }

    @Override
    public void handleClient() {

        Level level = Minecraft.getInstance().level;
        if (level != null) {
            LightHelper.removeLight(level, BlockPos.of(pos));
        }
    }

    @Override
    public void write(FriendlyByteBuf buf) {

        buf.writeLong(pos);
    }

    @Override
    public void read(FriendlyByteBuf buf) {

        this.pos = buf.readLong();
    }

    public static void sendToClient(Level level, BlockPos pos) {

        if (!level.isClientSide) {
            LightRemovePacket packet = new LightRemovePacket();
            packet.pos = pos.asLong();
            packet.sendToDimension(level.dimension());
        }
    }

}
