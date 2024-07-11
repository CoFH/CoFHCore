//package cofh.core.common.network.packet.client;
//
//import cofh.core.CoFHCore;
//import cofh.core.util.ProxyUtils;
//import cofh.lib.common.network.packet.IPacketClient;
//import cofh.lib.common.network.packet.PacketBase;
//import net.minecraft.core.registries.BuiltInRegistries;
//import net.minecraft.network.FriendlyByteBuf;
//import net.minecraft.world.effect.MobEffect;
//import net.minecraft.world.effect.MobEffectInstance;
//import net.minecraft.world.entity.LivingEntity;
//
//import static cofh.core.common.network.packet.PacketIDs.PACKET_EFFECT_REMOVE;
//import static cofh.lib.util.Constants.NETWORK_UPDATE_DISTANCE;
//import static cofh.lib.util.Utils.getRegistryName;
//
//public class EffectRemovedPacket extends PacketBase implements IPacketClient {
//
//    protected int id;
//    protected MobEffect effect;
//
//    public EffectRemovedPacket() {
//
//        super(PACKET_EFFECT_REMOVE, CoFHCore.PACKET_HANDLER);
//    }
//
//    @Override
//    public void handleClient() {
//
//        if (ProxyUtils.getClientWorld().getEntity(id) instanceof LivingEntity entity && !entity.equals(ProxyUtils.getClientPlayer())) {
//            MobEffectInstance existing = entity.removeEffectNoUpdate(effect);
//            if (existing != null) {
//                entity.onEffectRemoved(existing);
//            }
//        }
//    }
//
//    @Override
//    public void write(FriendlyByteBuf buf) {
//
//        buf.writeVarInt(id);
//        buf.writeResourceLocation(getRegistryName(effect));
//    }
//
//    @Override
//    public void read(FriendlyByteBuf buf) {
//
//        this.id = buf.readVarInt();
//        effect = BuiltInRegistries.MOB_EFFECT.get(buf.readResourceLocation());
//    }
//
//    public static void sendToClient(LivingEntity entity, MobEffect effect) {
//
//        if (!entity.level.isClientSide) {
//            EffectRemovedPacket packet = new EffectRemovedPacket();
//            packet.id = entity.getId();
//            packet.effect = effect;
//            packet.sendToAllAround(entity.position(), NETWORK_UPDATE_DISTANCE, entity.level.dimension());
//        }
//    }
//
//    public static void sendToClient(LivingEntity entity, MobEffectInstance effect) {
//
//        sendToClient(entity, effect.getEffect());
//    }
//
//}
