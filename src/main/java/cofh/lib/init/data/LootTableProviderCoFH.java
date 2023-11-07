package cofh.lib.init.data;

import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class LootTableProviderCoFH extends LootTableProvider {

    protected LootTableProviderCoFH(PackOutput output, List<LootTableProvider.SubProviderEntry> subProviders) {

        this(output, Collections.emptySet(), subProviders);
    }

    protected LootTableProviderCoFH(PackOutput output, Set<ResourceLocation> requiredTables, List<SubProviderEntry> subProviders) {

        super(output, requiredTables, subProviders);
    }

}


