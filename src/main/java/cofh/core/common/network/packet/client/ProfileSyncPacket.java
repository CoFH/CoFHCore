package cofh.core.common.network.packet.client;

import cofh.core.CoFHCore;
import cofh.core.util.ProxyUtils;
import cofh.lib.common.network.packet.IPacketClient;
import cofh.lib.common.network.packet.PacketBase;
import cofh.lib.util.helpers.ProfileHelper;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static cofh.core.common.network.packet.PacketIDs.PACKET_EFFECT_ADD;
import static cofh.core.common.network.packet.PacketIDs.PACKET_PROFILE_SYNC;
import static cofh.lib.util.Constants.NETWORK_UPDATE_DISTANCE;
import static cofh.lib.util.Utils.getRegistryName;

public class ProfileSyncPacket extends PacketBase implements IPacketClient {

    protected Map<UUID, String> profiles;

    public ProfileSyncPacket() {

        super(PACKET_PROFILE_SYNC, CoFHCore.PACKET_HANDLER);
    }

    @Override
    public void handleClient() {

        ProfileHelper.add(profiles);
    }

    @Override
    public void write(FriendlyByteBuf buf) {

        buf.writeVarInt(profiles.size());
        for (Map.Entry<UUID, String> entry : profiles.entrySet()) {
            buf.writeUUID(entry.getKey());
            buf.writeUtf(entry.getValue());
        }
    }

    @Override
    public void read(FriendlyByteBuf buf) {

        UUID[] uuids = new UUID[buf.readVarInt()];
        String[] usernames = new String[uuids.length];
        for (int i = 0; i < uuids.length; ++i) {
            uuids[i] = buf.readUUID();
            usernames[i] = buf.readUtf();
        }
        profiles = new Object2ObjectArrayMap<>(uuids, usernames);
    }

    public static void sendToClient(ServerPlayer player, Map<UUID, String> profiles) {

        ProfileSyncPacket packet = new ProfileSyncPacket();
        packet.profiles = profiles;
        packet.sendToPlayer(player);
    }

}
