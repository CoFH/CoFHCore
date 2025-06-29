package cofh.core.mixin;

import cofh.core.common.capability.CapabilityPersistentLight;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LightChunkGetter;
import net.minecraft.world.level.lighting.DataLayerStorageMap;
import net.minecraft.world.level.lighting.LayerLightSectionStorage;
import net.minecraft.world.level.lighting.LightEngine;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author Hekera
 */
@Mixin (LightEngine.class)
public abstract class LightEngineMixin<M extends DataLayerStorageMap<M>, S extends LayerLightSectionStorage<M>> {

    @Shadow @Final protected LightChunkGetter chunkSource;
    @Shadow @Final public S storage;
    @Shadow @Final private BlockPos.MutableBlockPos mutablePos;
    @Unique
    protected final LongOpenHashSet changed = new LongOpenHashSet();

    @Inject(
            method = "runLightUpdates",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/lighting/LightEngine;clearChunkCache()V"
            )
    )
    private void modifyBrightness(CallbackInfoReturnable<Integer> cir) {

        if (!changed.isEmpty()) {
            LongIterator it = changed.longIterator();
            while (it.hasNext()) {
                long pos = it.nextLong();
                mutablePos.set(pos);
                if (chunkSource.getChunkForLighting(SectionPos.blockToSectionCoord(mutablePos.getX()), SectionPos.blockToSectionCoord(mutablePos.getZ())) instanceof LevelChunk chunk) {
                    storage.setStoredLevel(pos, Math.max(storage.getStoredLevel(pos), chunk.getCapability(CapabilityPersistentLight.LIGHT_CAPABILITY).map(lights -> lights.getBrightness(mutablePos)).orElse(0)));
                }
            }
            changed.clear();
            changed.trim(256);
        }
    }

}