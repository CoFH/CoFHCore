package cofh.lib.util.helpers;

import cofh.core.common.network.packet.client.ProfileAddPacket;
import cofh.core.common.network.packet.client.ProfileSyncPacket;
import cofh.lib.util.constants.NBTTags;
import com.mojang.authlib.GameProfile;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static cofh.lib.util.constants.ModIds.ID_COFH_CORE;

@Mod.EventBusSubscriber (modid = ID_COFH_CORE)
public class ProfileHelper {
    
    private static final String NAME = new ResourceLocation(ID_COFH_CORE, "players").toString();
    private static final Map<UUID, String> USERNAMES = new Object2ObjectOpenHashMap<>();

    @Nullable
    public static String getUsername(UUID uuid) {

        return USERNAMES.get(uuid);
    }

    @ApiStatus.Internal
    public static void add(Map<UUID, String> profiles) {

        USERNAMES.putAll(profiles);
    }

    @ApiStatus.Internal
    public static void add(UUID uuid, String username) {

        USERNAMES.put(uuid, username);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void playerJoin(PlayerEvent.PlayerLoggedInEvent event) {

        if (event.getEntity() instanceof ServerPlayer player) {
            MinecraftServer server = player.server;
            ProfileSavedData data = server.overworld().getDataStorage().computeIfAbsent(tag -> new ProfileSavedData(server, tag), ProfileSavedData::new, NAME);
            ProfileSyncPacket.sendToClient(player, data.usernames);
            String name = data.add(server, player.getUUID());
            if (name != null) {
                ProfileAddPacket.sendToClients(player.getUUID(), name);
            }
        }
    }
    
    private static class ProfileSavedData extends SavedData {
        
        private final Object2ObjectArrayMap<UUID, String> usernames;

        private ProfileSavedData() {

            usernames = new Object2ObjectArrayMap<>();
        }

        private ProfileSavedData(MinecraftServer server, CompoundTag tag) {

            long[] data = tag.getLongArray(NBTTags.TAG_UUID);
            UUID[] uuids = new UUID[data.length / 2];
            String[] names = new String[uuids.length];
            int count = 0;
            for (int i = 0; i < uuids.length; ++i) {
                UUID uuid = new UUID(data[i * 2], data[i * 2 + 1]);
                names[count] = lookup(server, uuid);
                if (names[count] != null) {
                    uuids[count++] = uuid;
                }
            }
            usernames = new Object2ObjectArrayMap<>(uuids, names, count);
            USERNAMES.putAll(usernames);
        }

        private String add(MinecraftServer server, UUID uuid) {

            if (usernames.containsKey(uuid)) {
                return null;
            }
            String name = lookup(server, uuid);
            usernames.put(uuid, name);
            USERNAMES.put(uuid, name);
            setDirty();
            return name;
        }

        @Nullable
        private String lookup(MinecraftServer server, UUID uuid) {

            GameProfileCache cache = server.getProfileCache();
            if (cache != null) {
                Optional<GameProfile> profile = cache.get(uuid);
                if (profile.isPresent()) {
                    return profile.get().getName();
                }
            }
            ServerPlayer online = server.getPlayerList().getPlayer(uuid);
            return online != null ? online.getGameProfile().getName() : null;
        }

        @Override
        public CompoundTag save(CompoundTag tag) {
            
            long[] uuids = new long[usernames.size() * 2];
            int i = 0;
            for (UUID uuid : usernames.keySet()) {
                uuids[i++] = uuid.getMostSignificantBits();
                uuids[i++] = uuid.getLeastSignificantBits();
            }
            tag.putLongArray(NBTTags.TAG_UUID, uuids);
            return tag;
        }
        
    }

}
