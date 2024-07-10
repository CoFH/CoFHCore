package cofh.core.common.network.packet.client;

import cofh.core.CoFHCore;
import cofh.core.util.ProxyUtils;
import cofh.lib.common.network.packet.IPacketClient;
import cofh.lib.common.network.packet.PacketBase;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import static cofh.core.common.network.packet.PacketIDs.PACKET_EFFECT_ADD;
import static cofh.lib.util.Constants.NETWORK_UPDATE_DISTANCE;
import static cofh.lib.util.Utils.getRegistryName;

public class EffectAddedPacket extends PacketBase implements IPacketClient {

    protected int id;
    protected MobEffectInstance effect;

    public EffectAddedPacket() {

        super(PACKET_EFFECT_ADD, CoFHCore.PACKET_HANDLER);
    }

    @Override
    public void handleClient() {

        if (effect == null) {
            return;
        }
        Level level = ProxyUtils.getClientWorld();
        if (level == null) {
            return;
        }
        Entity entity = level.getEntity(id);
        if (entity instanceof LivingEntity living && !entity.equals(ProxyUtils.getClientPlayer())) {
            living.forceAddEffect(effect, null);
        }
    }

    @Override
    public void write(FriendlyByteBuf buf) {

        buf.writeVarInt(id);
        buf.writeResourceLocation(getRegistryName(effect.getEffect()));
        buf.writeInt(effect.getDuration());
    }

    @Override
    public void read(FriendlyByteBuf buf) {

        this.id = buf.readVarInt();
        MobEffect effectType = BuiltInRegistries.MOB_EFFECT.get(buf.readResourceLocation());
        int duration = buf.readInt();
        if (effectType != null) {
            effect = new MobEffectInstance(effectType, duration);
        }
    }

    public static void sendToClient(LivingEntity entity, MobEffectInstance effect) {

        if (!entity.level.isClientSide) {
            EffectAddedPacket packet = new EffectAddedPacket();
            packet.id = entity.getId();
            packet.effect = effect;
            packet.sendToAllAround(entity.position(), NETWORK_UPDATE_DISTANCE, entity.level.dimension());
        }
    }

    public static void sendToClient(LivingEntity entity, MobEffectInstance effect, Player client) {

        if (!entity.level.isClientSide && client instanceof ServerPlayer player) {
            EffectAddedPacket packet = new EffectAddedPacket();
            packet.id = entity.getId();
            packet.effect = effect;
            packet.sendToPlayer(player);
        }
    }

}
