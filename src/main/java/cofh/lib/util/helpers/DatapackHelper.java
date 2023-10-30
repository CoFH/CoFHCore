package cofh.lib.util.helpers;

import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;
import net.minecraftforge.registries.holdersets.AndHolderSet;
import net.minecraftforge.registries.holdersets.OrHolderSet;

import java.util.Arrays;
import java.util.List;

public final class DatapackHelper {

    private DatapackHelper() {

    }

    // TODO: Fix

    //    public static <T> JsonCodecProvider<T> datapackProvider(String modId, PackOutput output, ExistingFileHelper existingFileHelper, RegistryOps<JsonElement> registryOps, ResourceKey<Registry<T>> registryKey, Map<ResourceLocation, T> entries) {
    //
    //        return forDatapackRegistry(output, existingFileHelper, modId, registryOps, registryKey, entries);
    //    }
    //
    //    public static <T> JsonCodecProvider<T> datapackProviderBiome(String modId, PackOutput output, ExistingFileHelper existingFileHelper, RegistryOps<JsonElement> registryOps, ResourceKey<Registry<T>> registryKey, Map<ResourceLocation, T> entries) {
    //
    //        return forDatapackRegistry(output, existingFileHelper, modId, registryOps, Biome.DIRECT_CODEC, registryKey, entries);
    //    }

    //    public static <T> HolderSet<T> tagSingle(Registry<T> tagGetter, TagKey<T> biome) {
    //
    //        return new HolderSet.Named<>(tagGetter, biome);
    //    }

    @SafeVarargs
    public static <T> HolderSet<T> tagsAnd(Registry<T> tagGetter, TagKey<T>... biomes) {

        // noinspection unchecked
        return holderSetIntersection(Arrays.stream(biomes).<HolderSet<T>>map(tagGetter::getOrCreateTag).toArray(i -> new HolderSet[i]));
    }

    @SafeVarargs
    public static <T> HolderSet<T> tagsOr(Registry<T> tagGetter, TagKey<T>... biomes) {

        // noinspection unchecked
        return holderSetUnion(Arrays.stream(biomes).<HolderSet<T>>map(tagGetter::getOrCreateTag).toArray(i -> new HolderSet[i]));
    }

    @SafeVarargs
    public static <T> HolderSet<T> holderSetIntersection(HolderSet<T>... holderSets) {

        return new AndHolderSet<>(List.of(holderSets));
    }

    @SafeVarargs
    public static <T> HolderSet<T> holderSetUnion(HolderSet<T>... holderSets) {

        return new OrHolderSet<>(List.of(holderSets));
    }

    //    public static <T> JsonCodecProvider<T> forDatapackRegistry(PackOutput output, ExistingFileHelper existingFileHelper, String modid, RegistryOps<JsonElement> registryOps, Codec<T> codec, ResourceKey<Registry<T>> registryKey, Map<ResourceLocation, T> entries) {
    //
    //        final ResourceLocation registryId = registryKey.location();
    //        // Minecraft datapack registry folders are in data/json-namespace/registry-name/
    //        // Non-vanilla registry folders are data/json-namespace/registry-namespace/registry-name/
    //        final String registryFolder = registryId.getNamespace().equals("minecraft")
    //                ? registryId.getPath()
    //                : registryId.getNamespace() + "/" + registryId.getPath();
    //        final Codec<T> codec = (Codec<T>) RegistryAccess.REGISTRIES.get(registryKey).codec();
    //        return new JsonCodecProvider<>(output, existingFileHelper, modid, registryOps, PackType.SERVER_DATA, registryFolder, codec, entries);
    //    }

}
