package cofh.core.common.network.packet.client;

import cofh.core.CoFHCore;
import cofh.core.util.ProxyUtils;
import cofh.lib.common.network.packet.IPacketClient;
import cofh.lib.common.network.packet.PacketBase;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.registries.ForgeRegistries;

import static cofh.core.common.network.packet.PacketIDs.PACKET_EFFECT_ADD;
import static cofh.lib.util.Constants.NETWORK_UPDATE_DISTANCE;
import static cofh.lib.util.Utils.getRegistryName;

public class EffectAddedPacket extends PacketBase implements IPacketClient {

    protected LivingEntity entity;
    protected MobEffectInstance effect;

    public EffectAddedPacket() {

        super(PACKET_EFFECT_ADD, CoFHCore.PACKET_HANDLER);
    }

    @Override
    public void handleClient() {

        if (entity != null && effect != null && !entity.equals(Minecraft.getInstance().player)) {
            entity.forceAddEffect(effect, null);
        }
    }

    @Override
    public void write(FriendlyByteBuf buf) {

        buf.writeInt(entity.getId());
        buf.writeResourceLocation(getRegistryName(effect.getEffect()));
        buf.writeInt(effect.getDuration());
    }

    @Override
    public void read(FriendlyByteBuf buf) {

        Player client = ProxyUtils.getClientPlayer();
        if (client == null) {
            return;
        }
        Entity entity = client.level.getEntity(buf.readInt());
        MobEffect effectType = ForgeRegistries.MOB_EFFECTS.getValue(buf.readResourceLocation());
        if (entity instanceof LivingEntity && effectType != null) {
            this.entity = (LivingEntity) entity;
            effect = new MobEffectInstance(effectType, buf.readInt());
        }
    }

    public static void sendToClient(LivingEntity entity, MobEffectInstance effect) {

        if (!entity.level.isClientSide) {
            EffectAddedPacket packet = new EffectAddedPacket();
            packet.entity = entity;
            packet.effect = effect;
            packet.sendToAllAround(entity.position(), NETWORK_UPDATE_DISTANCE, entity.level.dimension());
        }
    }

}
