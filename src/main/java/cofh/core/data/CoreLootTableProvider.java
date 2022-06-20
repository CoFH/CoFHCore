package cofh.core.data;

import net.minecraft.data.DataGenerator;

import static cofh.core.init.CoreBlocks.GLOSSED_MAGMA;

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

        blockLootTables.put(GLOSSED_MAGMA.get(), getEmptyTable());
    }

}
