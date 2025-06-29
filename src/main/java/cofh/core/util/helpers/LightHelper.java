package cofh.core.util.helpers;

import cofh.core.common.network.packet.client.LightAddPacket;
import cofh.core.common.network.packet.client.LightRemovePacket;
import cofh.lib.util.helpers.MathHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.lighting.LightEngine;

import static cofh.core.common.capability.CapabilityPersistentLight.LIGHT_CAPABILITY;

public class LightHelper {

    protected static final BlockPos.MutableBlockPos CURSOR = new BlockPos.MutableBlockPos();

    public static void addLight(Level level, BlockPos center, double radius) {

        int r = MathHelper.floor(radius);
        int minZ = SectionPos.blockToSectionCoord(center.getZ() - r);
        int maxX = SectionPos.blockToSectionCoord(center.getX() + r);
        int maxZ = SectionPos.blockToSectionCoord(center.getZ() + r);
        for (int x = SectionPos.blockToSectionCoord(center.getX() - r); x <= maxX; ++x) {
            for (int z = minZ; z <= maxZ; ++z) {
                level.getChunk(x, z).getCapability(LIGHT_CAPABILITY).ifPresent(cap -> cap.add(center, radius));
            }
        }
        forEach(level, center, radius, (e, b, x, y, z) -> {
            long pos = BlockPos.asLong(x, y, z);
            e.storage.setStoredLevel(pos, Math.max(e.storage.getStoredLevel(pos), b));
        });
        LightAddPacket.sendToClient(level, center, radius);
    }

    public static void removeLight(Level level, BlockPos center) {

        level.getChunkAt(center).getCapability(LIGHT_CAPABILITY).map(cap -> cap.remove(center)).ifPresent(removed -> removeLight(level, center, removed.radius()));
    }

    public static void removeLight(Level level, BlockPos center, double radius) {

        int r = MathHelper.floor(radius);
        int minZ = SectionPos.blockToSectionCoord(center.getZ() - r);
        int maxX = SectionPos.blockToSectionCoord(center.getX() + r);
        int maxZ = SectionPos.blockToSectionCoord(center.getZ() + r);
        for (int x = SectionPos.blockToSectionCoord(center.getX() - r); x <= maxX; ++x) {
            for (int z = minZ; z <= maxZ; ++z) {
                level.getChunk(x, z).getCapability(LIGHT_CAPABILITY).ifPresent(cap -> cap.remove(center));
            }
        }
        LightRemovePacket.sendToClient(level, center, radius);
        forEach(level, center, radius, (e, b, x, y, z) -> e.checkBlock(CURSOR.set(x, y, z)));
    }

    public static int getBrightness(BlockPos center, double r2, int x, int y, int z) {

        return MathHelper.ceil((r2 - center.distToLowCornerSqr(x, y, z)) * 15 / r2);
    }

    protected static void forEach(Level level, BlockPos center, double radius, PosConsumer consumer) {

        LightEngine<?, ?> engine = level.getLightEngine().blockEngine;
        if (engine == null) {
            return;
        }
        int r = MathHelper.floor(radius);
        double r2 = radius * radius;
        int xn = center.getX() - r;
        int xp = center.getX() + r;
        int yn = center.getY() - r;
        int yp = center.getY() + r;
        int zn = center.getZ() - r;
        int zp = center.getZ() + r;
        for (int sx = SectionPos.blockToSectionCoord(xn); sx <= SectionPos.blockToSectionCoord(xp); ++sx) {
            for (int sy = SectionPos.blockToSectionCoord(yn); sy <= SectionPos.blockToSectionCoord(yp); ++sy) {
                for (int sz = SectionPos.blockToSectionCoord(zn); sz <= SectionPos.blockToSectionCoord(zp); ++sz) {
                    if (!engine.storage.storingLightForSection(SectionPos.asLong(sx, sy, sz))) {
                        continue;
                    }
                    for (int x = Math.max(xn, SectionPos.sectionToBlockCoord(sx)); x <= Math.min(xp, SectionPos.sectionToBlockCoord(sx, SectionPos.SECTION_MAX_INDEX)); ++x) {
                        for (int y = Math.max(yn, SectionPos.sectionToBlockCoord(sy)); y <= Math.min(yp, SectionPos.sectionToBlockCoord(sy, SectionPos.SECTION_MAX_INDEX)); ++y) {
                            for (int z = Math.max(zn, SectionPos.sectionToBlockCoord(sz)); z <= Math.min(zp, SectionPos.sectionToBlockCoord(sz, SectionPos.SECTION_MAX_INDEX)); ++z) {
                                int brightness = getBrightness(center, r2, x, y, z);
                                if (brightness > 0) {
                                    consumer.consume(engine, brightness, x, y, z);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    protected interface PosConsumer {

        void consume(LightEngine<?, ?> engine, int brightness, int x, int y, int z);
    }

}
