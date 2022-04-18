package cofh.core.network.packet.client;

import cofh.core.CoFHCore;
import cofh.lib.network.packet.IPacketClient;
import cofh.lib.network.packet.PacketBase;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraftforge.registries.ForgeRegistries;

import static cofh.lib.util.constants.Constants.PACKET_EFFECT_REMOVE;

public class EffectRemovedPacket extends PacketBase implements IPacketClient {

    protected LivingEntity entity;
    protected Effect effect;

    public EffectRemovedPacket() {

        super(PACKET_EFFECT_REMOVE, CoFHCore.PACKET_HANDLER);
    }

    @Override
    public void handleClient() {

        if (!entity.equals(Minecraft.getInstance().player)) {
            EffectInstance existing = entity.removeEffectNoUpdate(effect);
            if (existing != null) {
                entity.onEffectRemoved(existing);
            }
        }
    }

    @Override
    public void write(PacketBuffer buf) {

        buf.writeInt(entity.getId());
        buf.writeResourceLocation(effect.getEffect().getRegistryName());
    }

    @Override
    public void read(PacketBuffer buf) {

        Entity e = Minecraft.getInstance().player.level.getEntity(buf.readInt());
        if (e instanceof LivingEntity) {
            this.entity = (LivingEntity) e;
            effect = ForgeRegistries.POTIONS.getValue(buf.readResourceLocation());
        }
    }

    public static void sendToClient(LivingEntity entity, Effect effect) {

        if (!entity.level.isClientSide) {
            EffectRemovedPacket packet = new EffectRemovedPacket();
            packet.entity = entity;
            packet.effect = effect;
            packet.sendToClients();
        }
    }

    public static void sendToClient(LivingEntity entity, EffectInstance effect) {

        sendToClient(entity, effect.getEffect());
    }

}
