package cofh.core.common.capability.templates;

import cofh.core.common.network.packet.client.LightSyncPacket;
import cofh.core.util.helpers.LightHelper;
import cofh.lib.util.constants.NBTTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static cofh.core.common.capability.CapabilityPersistentLight.LIGHT_CAPABILITY;

public class PersistentLights implements ICapabilitySerializable<ListTag> {

    protected final LazyOptional<PersistentLights> holder = LazyOptional.of(() -> this);
    protected final ChunkPos pos;
    protected final List<Light> lights = new ArrayList<>(1);

    public PersistentLights(ChunkPos pos) {

        this.pos = pos;
    }

    public void syncTo(ServerPlayer player) {

        LightSyncPacket.sendToClient(player, pos,
                lights.stream().map(l -> l.center).mapToLong(BlockPos::asLong).toArray(),
                lights.stream().mapToDouble(l -> l.r).toArray());
    }

    public int getBrightness(BlockPos pos) {

        int brightness = 0;
        for (Light light : lights) {
            brightness = Math.max(brightness, LightHelper.getBrightness(light.center, light.r2, pos.getX(), pos.getY(), pos.getZ()));
        }
        return brightness;
    }

    public void add(BlockPos center, double radius) {

        lights.add(new Light(center, radius));
    }

    public Light remove(BlockPos center) {

        Light removed = null;
        Iterator<Light> it = lights.iterator();
        while (it.hasNext()) {
            Light light = it.next();
            if (light.center.equals(center)) {
                it.remove();
                removed = light;
            }
        }
        return removed;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {

        return LIGHT_CAPABILITY.orEmpty(cap, holder);
    }

    @Override
    public ListTag serializeNBT() {

        ListTag list = new ListTag();
        for (Light light : lights) {
            CompoundTag tag = new CompoundTag();
            tag.putLong(NBTTags.TAG_POSITION, light.center.asLong());
            tag.putDouble(NBTTags.TAG_AUGMENT_RADIUS, light.r);
            list.add(tag);
        }
        return list;
    }

    @Override
    public void deserializeNBT(ListTag nbt) {

        for (int i = 0; i < nbt.size(); ++i) {
            CompoundTag tag = nbt.getCompound(i);
            lights.add(new Light(BlockPos.of(tag.getLong(NBTTags.TAG_POSITION)), tag.getDouble(NBTTags.TAG_AUGMENT_RADIUS)));
        }
    }

    public static final class Light {

        private final BlockPos center;
        private final double r;
        private final double r2;

        public Light(BlockPos center, double r) {

            this.center = center;
            this.r = r;
            this.r2 = r * r;
        }

        public BlockPos center() {

            return center;
        }

        public double radius() {

            return r;
        }

        @Override
        public String toString() {

            return "Light[" +
                    "center=" + center + ", " +
                    "radius=" + r + ']';
        }

    }

}
