package cofh.core.common.light;

import cofh.lib.util.constants.NBTTags;
import cofh.lib.util.helpers.MathHelper;
import it.unimi.dsi.fastutil.longs.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.chunk.LightChunkGetter;
import net.minecraft.world.level.saveddata.SavedData;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.LongConsumer;

import static cofh.lib.util.constants.ModIds.ID_COFH_CORE;

public class LightSavedData extends SavedData {

    protected static final String NAME = new ResourceLocation(ID_COFH_CORE, "lights").toString();
    protected static LightSavedData CLIENT = new LightSavedData();

    protected final List<Light> lights = new ArrayList<>();
    protected final Long2ObjectMap<List<Light>> sections = new Long2ObjectOpenHashMap<>();
    protected volatile Long2ObjectMap<DataLayer> baked = Long2ObjectMaps.unmodifiable(new Long2ObjectOpenHashMap<>());

    public static LightSavedData get(Level level) {

        if (level instanceof ServerLevel serverLevel) {
            return serverLevel.getDataStorage().computeIfAbsent(LightSavedData::new, LightSavedData::new, NAME);
        }
        return CLIENT;
    }

    public static LightSavedData get(LightChunkGetter chunkSource) {

        if (chunkSource instanceof ServerChunkCache cache) {
            return cache.getDataStorage().computeIfAbsent(LightSavedData::new, LightSavedData::new, NAME);
        }
        return CLIENT;
    }

    @Nullable
    public DataLayer layer(long section) {

        return baked.get(section);
    }

    public static void dirtySections(Level level, BlockPos center, double radius) {

        if (level instanceof ClientLevel clientLevel) {
            forEachSection(center.asLong(), radius, section ->  dirtySection(clientLevel, section));
        }
    }

    protected static void dirtySection(ClientLevel level, long section) {

        int sx = SectionPos.x(section);
        int sz = SectionPos.z(section);
        if (level.getChunkSource().getChunk(sx, sz, ChunkStatus.FULL, false) != null) {
            level.setSectionDirtyWithNeighbors(sx, SectionPos.y(section), sz);
        }
    }

    public void toNetwork(FriendlyByteBuf buf) {

        buf.writeVarInt(lights.size());
        for (Light light : lights) {
            buf.writeLong(light.center);
            buf.writeDouble(light.radius);
        }
    }

    public void fromNetwork(ClientLevel level, FriendlyByteBuf buf) {

        lights.clear();
        sections.clear();
        for (int i = buf.readVarInt(); i > 0; --i) {
            long center = buf.readLong();
            double radius = buf.readDouble();
            Light light = new Light(center, radius);
            lights.add(light);
            forEachSection(center, radius, section ->  sections.computeIfAbsent(section, k -> new ArrayList<>(1)).add(light));
        }
        Long2ObjectOpenHashMap<DataLayer> next = new Long2ObjectOpenHashMap<>();
        for (long section : sections.keySet()) {
            bake(next, section);
        }
        baked = Long2ObjectMaps.unmodifiable(next);
        LongIterator it = baked.keySet().iterator();
        while (it.hasNext()) {
            dirtySection(level, it.nextLong());
        }
    }

    public void add(long center, double radius) {

        remove(center);
        Light light = new Light(center, radius);
        lights.add(light);
        forEachSection(center, radius, section ->  sections.computeIfAbsent(section, k -> new ArrayList<>(1)).add(light));
        rebake(center, radius);
        setDirty(true);
    }

    public Light remove(long center) {

        Light removed = null;
        Iterator<Light> it = lights.iterator();
        while (it.hasNext()) {
            Light light = it.next();
            if (light.center == center) {
                it.remove();
                forEachSection(center, light.radius, section ->  {
                    List<Light> list = sections.get(section);
                    if (list != null) {
                        list.remove(light);
                        if (list.isEmpty()) {
                            sections.remove(section);
                        }
                    }
                });
                removed = light;
            }
        }
        if (removed != null) {
            rebake(removed.center, removed.radius());
            setDirty(true);
        }
        return removed;
    }

    protected void rebake(long center, double radius) {

        Long2ObjectOpenHashMap<DataLayer> next = new Long2ObjectOpenHashMap<>(baked);
        forEachSection(center, radius, section -> bake(next, section));
        baked = Long2ObjectMaps.unmodifiable(next);
    }

    protected void bake(Long2ObjectOpenHashMap<DataLayer> map, long section) {

        List<Light> list = sections.get(section);
        if (list == null || list.isEmpty()) {
            map.remove(section);
            return;
        }
        int bx = SectionPos.sectionToBlockCoord(SectionPos.x(section));
        int by = SectionPos.sectionToBlockCoord(SectionPos.y(section));
        int bz = SectionPos.sectionToBlockCoord(SectionPos.z(section));
        DataLayer layer = new DataLayer();
        boolean empty = true;
        for (int lx = 0; lx < SectionPos.SECTION_SIZE; ++lx) {
            int x = bx + lx;
            for (int ly = 0; ly < SectionPos.SECTION_SIZE; ++ly) {
                int y = by + ly;
                for (int lz = 0; lz < SectionPos.SECTION_SIZE; ++lz) {
                    int z = bz + lz;
                    int brightness = 0;
                    for (Light light : list) {
                        brightness = Math.max(brightness, light.brightness(x, y, z));
                    }
                    if (brightness > 0) {
                        empty = false;
                        layer.set(lx, ly, lz, brightness);
                    }
                }
            }
        }
        if (empty) {
            map.remove(section);
        } else {
            map.put(section, layer);
        }
    }

    protected static void forEachSection(long center, double radius, LongConsumer consumer) {

        int x = BlockPos.getX(center);
        int y = BlockPos.getY(center);
        int z = BlockPos.getZ(center);
        int r = MathHelper.floor(radius);
        int xp = SectionPos.blockToSectionCoord(x + r);
        int yp = SectionPos.blockToSectionCoord(y + r);
        int zp = SectionPos.blockToSectionCoord(z + r);
        for (int sx = SectionPos.blockToSectionCoord(x - r); sx <= xp; ++sx) {
            for (int sy = SectionPos.blockToSectionCoord(y - r); sy <= yp; ++sy) {
                for (int sz = SectionPos.blockToSectionCoord(z - r); sz <= zp; ++sz) {
                    consumer.accept(SectionPos.asLong(sx, sy, sz));
                }
            }
        }
    }

    protected LightSavedData() {

    }

    protected LightSavedData(CompoundTag nbt) {

        ListTag list = nbt.getList(NBTTags.TAG_LIGHTS, ListTag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); ++i) {
            CompoundTag tag = list.getCompound(i);
            add(tag.getLong(NBTTags.TAG_POSITION), tag.getDouble(NBTTags.TAG_AUGMENT_RADIUS));
        }
    }

    @Override
    public CompoundTag save(CompoundTag nbt) {

        ListTag list = new ListTag();
        for (Light light : lights) {
            CompoundTag tag = new CompoundTag();
            tag.putLong(NBTTags.TAG_POSITION, light.center);
            tag.putDouble(NBTTags.TAG_AUGMENT_RADIUS, light.radius);
            list.add(tag);
        }
        nbt.put(NBTTags.TAG_LIGHTS, list);
        return nbt;
    }

    public static final class Light {

        private final long center;
        private final double radius;
        private final double radius2;
        private final double conversion;

        public Light(long center, double radius) {

            this.center = center;
            this.radius = radius;
            this.radius2 = radius * radius;
            this.conversion = 14.9 / radius2;
        }

        public double radius() {

            return radius;
        }

        public int brightness(int x, int y, int z) {

            x -= BlockPos.getX(center);
            y -= BlockPos.getY(center);
            z -= BlockPos.getZ(center);
            return MathHelper.clamp(MathHelper.ceil((radius2 - x * x - y * y - z * z) * conversion), 0, 15);
        }

    }

}
