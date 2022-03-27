package cofh.core.network.packet.client;

import cofh.core.CoFHCore;
import cofh.lib.network.packet.IPacketClient;
import cofh.lib.network.packet.PacketBase;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.potion.EffectInstance;
import net.minecraftforge.registries.ForgeRegistries;

import static cofh.lib.util.constants.Constants.PACKET_EFFECT_ADD;

public class EffectAddedPacket extends PacketBase implements IPacketClient {

    protected LivingEntity entity;
    protected EffectInstance effect;

    public EffectAddedPacket() {

        super(PACKET_EFFECT_ADD, CoFHCore.PACKET_HANDLER);
    }

    @Override
    public void handleClient() {

        if (!entity.equals(Minecraft.getInstance().player)) {
            entity.forceAddEffect(effect);
        }
    }

    @Override
    public void write(PacketBuffer buf) {
        
        buf.writeInt(entity.getId());
        buf.writeResourceLocation(effect.getEffect().getRegistryName());
        buf.writeInt(effect.getDuration());
    }

    @Override
    public void read(PacketBuffer buf) {

        Entity e = Minecraft.getInstance().player.level.getEntity(buf.readInt());
        if (e instanceof LivingEntity) {
            this.entity = (LivingEntity) e;
            effect = new EffectInstance(ForgeRegistries.POTIONS.getValue(buf.readResourceLocation()), buf.readInt());
        }
    }

    public static void sendToClient(LivingEntity entity, EffectInstance effect) {

        if (!entity.level.isClientSide) {
            EffectAddedPacket packet = new EffectAddedPacket();
            packet.entity = entity;
            packet.effect = effect;
            packet.sendToClients();
        }
    }
}
