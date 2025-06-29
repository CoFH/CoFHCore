package cofh.core.mixin;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.BlockLightEngine;
import net.minecraft.world.level.lighting.BlockLightSectionStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

/**
 * @author Hekera
 */
@Mixin (BlockLightEngine.class)
public abstract class BlockLightEngineMixin extends LightEngineMixin<BlockLightSectionStorage.BlockDataLayerStorageMap, BlockLightSectionStorage> {

    @Inject(
            method = "checkNode",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/lighting/BlockLightSectionStorage;setStoredLevel(JI)V"
            )
    )
    private void captureDirection(long pos, CallbackInfo ci) {

        changed.add(pos);
    }

    @Inject(
        method = "propagateIncrease",
        locals = LocalCapture.CAPTURE_FAILHARD,
        at = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/world/level/lighting/BlockLightSectionStorage;setStoredLevel(JI)V"
        )
    )
    private void captureDirection(long neighbor, long entry, int brightness, CallbackInfo ci, BlockState state, Direction[] dirs, int i, int j, Direction direction, long pos) {

        changed.add(pos);
    }

    @Inject(
            method = "propagateDecrease",
            locals = LocalCapture.CAPTURE_FAILHARD,
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/lighting/BlockLightSectionStorage;setStoredLevel(JI)V"
            )
    )
    private void captureDirection(long neighbor, long entry, CallbackInfo ci, int brightness, Direction[] dirs, int i, int j, Direction direction, long pos) {

        changed.add(pos);
    }

}
