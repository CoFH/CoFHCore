package cofh.core.mixin;

import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin (ClientboundSetEntityMotionPacket.class)
public abstract class ClientboundSetEntityMotionPacketMixin {

    @Mutable @Shadow @Final private int xa;

    @Mutable @Shadow @Final private int ya;

    @Mutable @Shadow @Final private int za;

    @Inject(
            method = "<init>(ILnet/minecraft/world/phys/Vec3;)V",
            at = @At("TAIL")
    )
    public void init(int id, Vec3 velocity, CallbackInfo ci) {

        xa = fromFloat((float) velocity.x);
        ya = fromFloat((float) velocity.y);
        za = fromFloat((float) velocity.z);
    }

    private static int fromFloat(float fval) {

        int fbits = Float.floatToIntBits(fval);
        int sign = fbits >>> 16 & 0x8000;
        int val = (fbits & 0x7fffffff) + 0x1000;
        if (val >= 0x47800000) {
            if ((fbits & 0x7fffffff) >= 0x47800000) {
                if (val < 0x7f800000) {
                    return sign | 0x7c00;
                }
                return sign | 0x7c00 | (fbits & 0x007fffff) >>> 13;
            }
            return sign | 0x7bff;
        }
        if (val >= 0x38800000) {
            return sign | val - 0x38000000 >>> 13;
        }
        if (val < 0x33000000) {
            return sign;
        }
        val = (fbits & 0x7fffffff) >>> 23;
        return sign | ((fbits & 0x7fffff | 0x800000) + ( 0x800000 >>> val - 102 ) >>> 126 - val);
    }

}