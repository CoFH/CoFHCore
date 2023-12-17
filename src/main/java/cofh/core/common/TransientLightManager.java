package cofh.core.common;

import cofh.core.util.ProxyUtils;
import cofh.lib.util.constants.ModIds;
import it.unimi.dsi.fastutil.longs.Long2ByteMap;
import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.BlockLightEngine;
import net.minecraft.world.level.lighting.LightEngine;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Class that allows for the placement of temporary light sources.
 * You should only call the methods in this class from the client side.
 */
@Mod.EventBusSubscriber (modid = ModIds.ID_COFH_CORE)
public class TransientLightManager {

    protected static final BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
    protected static Long2ByteMap CURRENT = new Long2ByteOpenHashMap();
    protected static Long2ByteMap PREVIOUS = new Long2ByteOpenHashMap();

    // region TRANSIENT
    public static void addLight(BlockPos pos, int level) {

        addLight(pos.asLong(), level);
    }

    /**
     * Places a light source lasting one tick in the client level.
     *
     * @param pos   The position to place the light source at.
     * @param level The light level of the light source.
     */
    public static void addLight(long pos, int level) {

        if (0 < level && level < 16 && level > CURRENT.get(pos)) {
            CURRENT.put(pos, (byte) level);
        }
    }

    @SubscribeEvent
    protected static void tick(TickEvent.ClientTickEvent event) {

        if (event.phase != TickEvent.Phase.END || CURRENT.isEmpty() && PREVIOUS.isEmpty()) {
            return;
        }
        Level level = ProxyUtils.getClientWorld();
        if (level == null || !(level.getLightEngine().blockEngine instanceof BlockLightEngine engine)) {
            CURRENT.clear();
            return;
        }
        for (Long2ByteMap.Entry entry : CURRENT.long2ByteEntrySet()) {
            long pos = entry.getLongKey();
            if (!engine.storage.storingLightForSection(SectionPos.blockToSection(pos))) {
                return;
            }
            int light = entry.getByteValue();
            int previous = PREVIOUS.remove(pos);
            if (previous == light) {
                continue;
            }
            int stored = engine.storage.getStoredLevel(pos);
            if (stored == light) {
                continue;
            }
            boolean empty = true;
            if (stored == previous) {
                BlockState state = engine.getState(cursor.set(pos));
                int emitted = engine.getEmission(pos, state);
                if (emitted > light) {
                    if (emitted == stored) {
                        continue;
                    }
                    light = emitted;
                    empty = LightEngine.isEmptyShape(state);
                }
            } else if (stored > light) {
                continue;
            }
            if (stored > 0) {
                engine.storage.setStoredLevel(pos, 0);
                engine.enqueueDecrease(pos, LightEngine.QueueEntry.decreaseAllDirections(stored));
            }
            if (light > 0) {
                engine.enqueueIncrease(pos, LightEngine.QueueEntry.increaseLightFromEmission(light, empty));
            }
        }
        for (Long2ByteMap.Entry entry : PREVIOUS.long2ByteEntrySet()) {
            long pos = entry.getLongKey();
            if (engine.storage.getStoredLevel(pos) == entry.getByteValue()) {
                engine.checkBlock(cursor.set(pos));
            }
        }
        PREVIOUS = CURRENT;
        CURRENT = new Long2ByteOpenHashMap(CURRENT.size() + 10);
    }

}
