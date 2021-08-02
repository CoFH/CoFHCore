package cofh.core.network.packet.server;

import cofh.core.CoFHCore;
import cofh.core.tileentity.TileCoFH;
import cofh.lib.network.packet.IPacketServer;
import cofh.lib.network.packet.PacketBase;
import cofh.lib.util.IInventoryCallback;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import static cofh.lib.util.constants.Constants.PACKET_STORAGE_CLEAR;

public class StorageClearPacket extends PacketBase implements IPacketServer {

    protected BlockPos pos;
    protected int storageType;
    protected int storageIndex;

    public StorageClearPacket() {

        super(PACKET_STORAGE_CLEAR, CoFHCore.PACKET_HANDLER);
    }

    @Override
    public void handleServer(ServerPlayerEntity player) {

        World world = player.world;
        if (!world.isBlockPresent(pos)) {
            return;
        }
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof IInventoryCallback) {
            switch (StorageType.values()[storageType]) {
                case ENERGY:
                    ((IInventoryCallback) tile).clearEnergy(storageIndex);
                    break;
                case FLUID:
                    ((IInventoryCallback) tile).clearTank(storageIndex);
                    break;
                case ITEM:
                    ((IInventoryCallback) tile).clearSlot(storageIndex);
                    break;
            }
        }
        // TODO: Debug logging?
    }

    @Override
    public void write(PacketBuffer buf) {

        buf.writeBlockPos(pos);
        buf.writeInt(storageType);
        buf.writeInt(storageIndex);
    }

    @Override
    public void read(PacketBuffer buf) {

        pos = buf.readBlockPos();
        storageType = buf.readInt();
        storageIndex = buf.readInt();
    }

    public static boolean sendToServer(TileCoFH tile, StorageType storageType, int storageIndex) {

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
