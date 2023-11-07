package cofh.core.init.data.tables;

import cofh.lib.init.data.loot.BlockLootSubProviderCoFH;

import static cofh.core.init.CoreBlocks.GLOSSED_MAGMA;

public class CoreBlockLootTables extends BlockLootSubProviderCoFH {

    @Override
    protected void generate() {

        add(GLOSSED_MAGMA.get(), getEmptyTable());
    }

}
