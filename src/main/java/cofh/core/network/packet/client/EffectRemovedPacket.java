package cofh.core.network.packet.client;

import cofh.core.CoFHCore;
import cofh.lib.network.packet.IPacketClient;
import cofh.lib.network.packet.PacketBase;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.registries.ForgeRegistries;

import static cofh.core.network.packet.PacketIDs.PACKET_EFFECT_REMOVE;
import static cofh.lib.util.Utils.getRegistryName;

public class EffectRemovedPacket extends PacketBase implements IPacketClient {

    protected LivingEntity entity;
    protected MobEffect effect;

    public EffectRemovedPacket() {

        super(PACKET_EFFECT_REMOVE, CoFHCore.PACKET_HANDLER);
    }

    @Override
    public void handleClient() {

        if (!entity.equals(Minecraft.getInstance().player)) {
            MobEffectInstance existing = entity.removeEffectNoUpdate(effect);
            if (existing != null) {
                entity.onEffectRemoved(existing);
            }
        }
    }

    @Override
    public void write(FriendlyByteBuf buf) {

        buf.writeInt(entity.getId());
        buf.writeResourceLocation(getRegistryName(effect));
    }

    @Override
    public void read(FriendlyByteBuf buf) {

        Entity e = Minecraft.getInstance().player.level.getEntity(buf.readInt());
        if (e instanceof LivingEntity) {
            this.entity = (LivingEntity) e;
            effect = ForgeRegistries.MOB_EFFECTS.getValue(buf.readResourceLocation());
        }
    }

    public static void sendToClient(LivingEntity entity, MobEffect effect) {

        if (!entity.level.isClientSide) {
            EffectRemovedPacket packet = new EffectRemovedPacket();
            packet.entity = entity;
            packet.effect = effect;
            packet.sendToClients();
        }
    }

    public static void sendToClient(LivingEntity entity, MobEffectInstance effect) {

        sendToClient(entity, effect.getEffect());
    }

}
