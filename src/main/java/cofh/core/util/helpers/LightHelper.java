package cofh.core.util.helpers;

import cofh.core.common.light.LightSavedData;
import cofh.core.common.network.packet.client.LightAddPacket;
import cofh.core.common.network.packet.client.LightRemovePacket;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class LightHelper {

    public static void addLight(Level level, BlockPos center, double radius) {

        LightSavedData.get(level).add(center.asLong(), radius);
        if (!level.isClientSide) {
            LightAddPacket.sendToClient(level, center, radius);
        } else {
            LightSavedData.dirtySections(level, center, radius);
        }
    }

    public static void removeLight(Level level, BlockPos center) {

        LightSavedData.Light removed = LightSavedData.get(level).remove(center.asLong());
        if (removed == null) {
            return;
        }
        if (!level.isClientSide) {
            LightRemovePacket.sendToClient(level, center);
        } else {
            LightSavedData.dirtySections(level, center, removed.radius());
        }
    }

}
