package cofh.core.common;

import cofh.core.util.ProxyUtils;
import cofh.lib.util.constants.ModIds;
import it.unimi.dsi.fastutil.longs.Long2ByteMap;
import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
import net.minecraft.client.multiplayer.ClientLevel;
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

    protected static final BlockPos.MutableBlockPos CURSOR = new BlockPos.MutableBlockPos();
    protected static Long2ByteMap current = new Long2ByteOpenHashMap();
    protected static Long2ByteMap previous = new Long2ByteOpenHashMap();
    protected static Level last = null;

    // region TRANSIENT
    public static void addLight(Level level, BlockPos pos, int light) {

        addLight(level, pos.asLong(), light);
    }

    /**
     * Places a light source lasting one tick in the client level.
     *
     * @param level The level in which this source should be placed. Mainly used for validation.
     * @param pos   The position to place the light source at.
     * @param light The light level of the light source.
     */
    public static void addLight(Level level, long pos, int light) {

        if (level.equals(ProxyUtils.getClientWorld()) && 0 < light && light < 16 && light > current.get(pos)) {
            current.put(pos, (byte) light);
        }
    }

    @SubscribeEvent
    protected static void tick(TickEvent.ClientTickEvent event) {

        if (event.phase != TickEvent.Phase.END || current.isEmpty() && previous.isEmpty()) {
            return;
        }
        Level level = ProxyUtils.getClientWorld();
        if (level == null || !level.equals(last)) {
            current.clear();
            level = last;
            last = ProxyUtils.getClientWorld();
        }
        if (level == null || !(level.getLightEngine().blockEngine instanceof BlockLightEngine engine)) {
            current.clear();
            previous.clear();
            return;
        }
        for (Long2ByteMap.Entry entry : current.long2ByteEntrySet()) {
            long pos = entry.getLongKey();
            if (!engine.storage.storingLightForSection(SectionPos.blockToSection(pos))) {
                return;
            }
            int light = entry.getByteValue();
            int previous = TransientLightManager.previous.remove(pos);
            if (previous == light) {
                continue;
            }
            int stored = engine.storage.getStoredLevel(pos);
            if (stored == light) {
                continue;
            }
            boolean empty = true;
            if (stored == previous) {
                BlockState state = engine.getState(CURSOR.set(pos));
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
        for (Long2ByteMap.Entry entry : previous.long2ByteEntrySet()) {
            long pos = entry.getLongKey();
            if (engine.storage.getStoredLevel(pos) == entry.getByteValue()) {
                engine.checkBlock(CURSOR.set(pos));
            }
        }
        previous = current;
        current = new Long2ByteOpenHashMap(current.size() + 10);
    }

}
