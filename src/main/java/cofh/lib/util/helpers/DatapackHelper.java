package cofh.lib.util.helpers;

import com.google.gson.JsonElement;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.JsonCodecProvider;
import net.minecraftforge.registries.holdersets.AndHolderSet;
import net.minecraftforge.registries.holdersets.OrHolderSet;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public final class DatapackHelper {

    private DatapackHelper() {

    }

    public static <T> JsonCodecProvider<T> datapackProvider(String modId, DataGenerator dataGenerator, ExistingFileHelper existingFileHelper, RegistryOps<JsonElement> registryOps, ResourceKey<Registry<T>> registryKey, Map<ResourceLocation, T> entries) {

        return JsonCodecProvider.forDatapackRegistry(dataGenerator, existingFileHelper, modId, registryOps, registryKey, entries);
    }

    public static <T> HolderSet<T> tagSingle(Registry<T> tagGetter, TagKey<T> biome) {

        return new HolderSet.Named<>(tagGetter, biome);
    }

    @SafeVarargs
    public static <T> HolderSet<T> tagsAnd(Registry<T> tagGetter, TagKey<T>... biomes) {

        return holderSetIntersection(Arrays.stream(biomes).<HolderSet<T>>map(tagGetter::getOrCreateTag).toArray(i -> new HolderSet[i]));
    }

    @SafeVarargs
    public static <T> HolderSet<T> tagsOr(Registry<T> tagGetter, TagKey<T>... biomes) {

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

}
