package cofh.core.common.network.packet.server;

import cofh.core.common.network.data.server.StorageClearPayload;
import cofh.lib.api.block.entity.ITileCallback;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import java.util.Optional;

public class StorageClearPacket {

    public static final StorageClearPacket INSTANCE = new StorageClearPacket();

    public static StorageClearPacket get() {

        return INSTANCE;
    }

    public void handle(final StorageClearPayload payload, final PlayPayloadContext context) {

        context.workHandler().submitAsync(() -> {
            Optional<Player> senderOptional = context.player();
            if (senderOptional.isEmpty()) {
                return;
            }
            Player player = senderOptional.get();

            Level world = player.level;
            if (!world.isLoaded(payload.pos())) {
                return;
            }
            BlockEntity tile = world.getBlockEntity(payload.pos());
            if (tile instanceof ITileCallback callback) {
                switch (StorageType.values()[payload.type()]) {
                    case ENERGY -> callback.clearEnergy(payload.index());
                    case FLUID -> callback.clearTank(payload.index());
                    case ITEM -> callback.clearSlot(payload.index());
                }
            }
        });
    }

    public static boolean sendToServer(ITileCallback tile, StorageType storageType, int storageIndex) {

        if (tile == null) {
            return false;
        }
        PacketDistributor.SERVER.noArg().send(new StorageClearPayload(tile.pos(), storageType.ordinal(), storageIndex));
        return true;
    }

    // STORAGE TYPE ENUM
    public enum StorageType {
        ENERGY, FLUID, ITEM;

        public static final StorageType[] VALUES = values();
    }
    // endregion
}
