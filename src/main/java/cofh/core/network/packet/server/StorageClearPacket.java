package cofh.core.network.packet.server;

import cofh.core.CoFHCore;
import cofh.core.network.packet.IPacketServer;
import cofh.core.network.packet.PacketBase;
import cofh.lib.content.block.entity.ITileCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import static cofh.lib.util.Constants.PACKET_STORAGE_CLEAR;

public class StorageClearPacket extends PacketBase implements IPacketServer {

    protected BlockPos pos;
    protected int storageType;
    protected int storageIndex;

    public StorageClearPacket() {

        super(PACKET_STORAGE_CLEAR, CoFHCore.PACKET_HANDLER);
    }

    @Override
    public void handleServer(ServerPlayer player) {

        Level world = player.level;
        if (!world.isLoaded(pos)) {
            return;
        }
        BlockEntity tile = world.getBlockEntity(pos);
        if (tile instanceof ITileCallback) {
            switch (StorageType.values()[storageType]) {
                case ENERGY:
                    ((ITileCallback) tile).clearEnergy(storageIndex);
                    break;
                case FLUID:
                    ((ITileCallback) tile).clearTank(storageIndex);
                    break;
                case ITEM:
                    ((ITileCallback) tile).clearSlot(storageIndex);
                    break;
            }
        }
        // TODO: Debug logging?
    }

    @Override
    public void write(FriendlyByteBuf buf) {

        buf.writeBlockPos(pos);
        buf.writeInt(storageType);
        buf.writeInt(storageIndex);
    }

    @Override
    public void read(FriendlyByteBuf buf) {

        pos = buf.readBlockPos();
        storageType = buf.readInt();
        storageIndex = buf.readInt();
    }

    public static boolean sendToServer(ITileCallback tile, StorageType storageType, int storageIndex) {

        if (tile == null) {
            return false;
        }
        StorageClearPacket packet = new StorageClearPacket();
        packet.pos = tile.pos();
        packet.storageType = storageType.ordinal();
        packet.storageIndex = storageIndex;
        packet.sendToServer();
        return true;
    }

    // CLEAR TYPE ENUM
    public enum StorageType {
        ENERGY, FLUID, ITEM;

        public static final StorageType[] VALUES = values();
    }
    // endregion
}
