package cofh.core.data;

import cofh.lib.data.LootTableProviderCoFH;
import net.minecraft.data.DataGenerator;

import static cofh.lib.util.references.CoreReferences.GLOSSED_MAGMA;

public class CoreLootTableProvider extends LootTableProviderCoFH {

    public CoreLootTableProvider(DataGenerator gen) {

        super(gen);
    }

    @Override
    public String getName() {

        return "CoFH Core: Loot Tables";
    }

    @Override
    protected void addTables() {

        blockLootTables.put(GLOSSED_MAGMA, getEmptyTable());
    }

}
