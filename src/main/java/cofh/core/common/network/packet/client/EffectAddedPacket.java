package cofh.core.common.network.packet.client;

import cofh.core.common.network.data.client.EffectAddedPayload;
import cofh.core.util.ProxyUtils;
import cofh.lib.util.Utils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import static cofh.lib.util.Utils.getRegistryName;

public class EffectAddedPacket {

    public static final EffectAddedPacket INSTANCE = new EffectAddedPacket();

    public static EffectAddedPacket get() {

        return INSTANCE;
    }

    public void handle(final EffectAddedPayload payload, final PlayPayloadContext context) {

        context.workHandler().submitAsync(() -> {
            int id = payload.entityId();
            MobEffect effectType = BuiltInRegistries.MOB_EFFECT.get(payload.effect());
            int effectDur = payload.duration();

            MobEffectInstance effect = effectType != null ? new MobEffectInstance(effectType, effectDur) : null;

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
        });
    }

    public static void sendToClient(LivingEntity entity, MobEffectInstance effect) {

        if (entity == null || effect == null) {
            return;
        }
        PacketDistributor.NEAR.with(Utils.createTargetPoint(entity)).send(new EffectAddedPayload(entity.getId(), getRegistryName(effect.getEffect()), effect.getDuration()));

    }

    public static void sendToClient(LivingEntity entity, MobEffectInstance effect, Player player) {

        if (entity == null || effect == null) {
            return;
        }
        if (player instanceof ServerPlayer serverPlayer) {
            PacketDistributor.PLAYER.with(serverPlayer).send(new EffectAddedPayload(entity.getId(), getRegistryName(effect.getEffect()), effect.getDuration()));
        }
    }

}
