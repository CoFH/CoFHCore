package cofh.core.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.PacketUtils;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin (ClientPacketListener.class)
public abstract class ClientPacketListenerMixin {

    @Shadow @Final private Minecraft minecraft;

    @Shadow private ClientLevel level;

    @Inject(
            method = "handleSetEntityMotion",
            at = @At("HEAD"),
            cancellable = true
    )
    public void handle(ClientboundSetEntityMotionPacket packet, CallbackInfo ci) {

        PacketUtils.ensureRunningOnSameThread(packet, (ClientPacketListener) (Object) this, minecraft);
        Entity entity = level.getEntity(packet.getId());
        if (entity != null) {
            entity.lerpMotion(toFloat(packet.getXa()), toFloat(packet.getYa()), toFloat(packet.getZa()));
        }
        ci.cancel();
    }

    private static float toFloat(int hbits) {

        int mant = hbits & 0x03ff;
        int exp =  hbits & 0x7c00;
        if (exp == 0x7c00) {
            exp = 0x3fc00;
        } else if (exp != 0) {
            exp += 0x1c000;
            if (mant == 0 && exp > 0x1c400) {
                return Float.intBitsToFloat((hbits & 0x8000) << 16 | exp << 13 | 0x3ff);
            }
        } else if (mant != 0) {
            exp = 0x1c400;
            do {
                mant <<= 1;
                exp -= 0x400;
            } while ((mant & 0x400) == 0);
            mant &= 0x3ff;
        }
        return Float.intBitsToFloat((hbits & 0x8000) << 16 | (exp | mant) << 13);
    }

}