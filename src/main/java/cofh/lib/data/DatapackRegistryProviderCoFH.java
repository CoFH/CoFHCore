package cofh.lib.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class DatapackRegistryProviderCoFH extends DatapackBuiltinEntriesProvider {

    private final String modid;

    protected DatapackRegistryProviderCoFH(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, RegistrySetBuilder registrySetBuilder, String modid) {

        super(output, registries, registrySetBuilder, Set.of("minecraft", modid));
        this.modid = modid;
    }

    @NotNull
    @Override
    public String getName() {

        return "Datapack Registries: " + modid;
    }

}
