package cofh.core.network.packet.client;

import cofh.core.CoFHCore;
import cofh.lib.network.packet.IPacketClient;
import cofh.lib.network.packet.PacketBase;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.registries.ForgeRegistries;

import static cofh.core.network.packet.PacketIDs.PACKET_EFFECT_ADD;
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

        if (entity != null && !entity.equals(Minecraft.getInstance().player)) {
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

        Player p = Minecraft.getInstance().player;
        if (p == null) {
            return;
        }
        Entity e = p.level.getEntity(buf.readInt());
        if (e instanceof LivingEntity) {
            this.entity = (LivingEntity) e;
            effect = new MobEffectInstance(ForgeRegistries.MOB_EFFECTS.getValue(buf.readResourceLocation()), buf.readInt());
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
