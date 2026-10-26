package cofh.core.mixin;

import cofh.core.common.light.LightSavedData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.chunk.LightChunkGetter;
import net.minecraft.world.level.lighting.BlockLightSectionStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockLightSectionStorage.class)
public abstract class BlockLightSectionStorageMixin {

    @Unique
    private LightSavedData data;

    @Inject(method = "<init>", at = @At("RETURN"))
    protected void init(LightChunkGetter chunkSource, CallbackInfo ci) {

        data = LightSavedData.get(chunkSource);
    }

    @Inject(method = "getLightValue", at = @At("RETURN"), cancellable = true)
    private void overlay(long pos, CallbackInfoReturnable<Integer> cir) {

        int base = cir.getReturnValueI();
        if (base >= 15) {
            return;
        }
        DataLayer layer = data.layer(SectionPos.blockToSection(pos));
        if (layer != null) {
            int overlay = layer.get(SectionPos.sectionRelative(BlockPos.getX(pos)), SectionPos.sectionRelative(BlockPos.getY(pos)), SectionPos.sectionRelative(BlockPos.getZ(pos)));
            if (overlay > base) {
                cir.setReturnValue(overlay);
            }
        }
    }

}
