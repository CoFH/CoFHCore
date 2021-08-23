package cofh.lib.data;

import cofh.lib.loot.TileNBTSync;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.advancements.criterion.EnchantmentPredicate;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.advancements.criterion.MinMaxBounds;
import net.minecraft.advancements.criterion.StatePropertiesPredicate;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.data.LootTableProvider;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.loot.*;
import net.minecraft.loot.conditions.BlockStateProperty;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.loot.conditions.MatchTool;
import net.minecraft.loot.conditions.SurvivesExplosion;
import net.minecraft.loot.functions.*;
import net.minecraft.state.IntegerProperty;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public abstract class LootTableProviderCoFH extends LootTableProvider {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    protected final Map<Block, LootTable.Builder> blockLootTables = new HashMap<>();
    protected final DataGenerator generator;

    public LootTableProviderCoFH(DataGenerator dataGeneratorIn) {

        super(dataGeneratorIn);
        this.generator = dataGeneratorIn;
    }

    protected abstract void addTables();

    protected void createSimpleDropTable(Block block) {

        blockLootTables.put(block, getSimpleDropTable(block));
    }

    protected void createSyncDropTable(Block block) {

        blockLootTables.put(block, getSyncDropTable(block));
    }

    // region TABLE HELPERS
    protected LootTable.Builder getSilkTouchTable(String name, Block block, Item lootItem, float min, float max, int bonus) {

        LootPool.Builder builder = LootPool.lootPool()
                .name(name)
                .setRolls(ConstantRange.exactly(1))
                .add(AlternativesLootEntry.alternatives(ItemLootEntry.lootTableItem(block)
                        .when(MatchTool.toolMatches(ItemPredicate.Builder.item()
                                .hasEnchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, MinMaxBounds.IntBound.atLeast(1))))), ItemLootEntry.lootTableItem(lootItem)
                        .apply(SetCount.setCount(new RandomValueRange(min, max)))
                        .apply(ApplyBonus.addUniformBonusCount(Enchantments.BLOCK_FORTUNE, bonus))
                        .apply(ExplosionDecay.explosionDecay())));
        return LootTable.lootTable().withPool(builder);
    }

    protected LootTable.Builder getSilkTouchOreTable(Block block, Item lootItem) {

        LootPool.Builder builder = LootPool.lootPool()
                .setRolls(ConstantRange.exactly(1))
                .add(AlternativesLootEntry.alternatives(ItemLootEntry.lootTableItem(block)
                        .when(MatchTool.toolMatches(ItemPredicate.Builder.item()
                                .hasEnchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, MinMaxBounds.IntBound.atLeast(1))))), ItemLootEntry.lootTableItem(lootItem)
                        .apply(ApplyBonus.addOreBonusCount(Enchantments.BLOCK_FORTUNE))
                        .apply(ExplosionDecay.explosionDecay())));
        return LootTable.lootTable().withPool(builder);
    }

    protected LootTable.Builder getCropTable(Block block, Item crop, Item seed, IntegerProperty ageProp, int age) {

        ILootCondition.IBuilder harvestAge = BlockStateProperty.hasBlockStateProperties(block).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(ageProp, age));
        return LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(ItemLootEntry.lootTableItem(crop)
                                .when(harvestAge)
                                .otherwise(ItemLootEntry.lootTableItem(seed))))
                .withPool(LootPool.lootPool()
                        .when(harvestAge)
                        .add(ItemLootEntry.lootTableItem(seed)
                                // These are Mojang's numbers. No idea.
                                .apply(ApplyBonus.addBonusBinomialDistributionCount(Enchantments.BLOCK_FORTUNE, 0.5714286F, 3))))
                .apply(ExplosionDecay.explosionDecay());
    }

    protected LootTable.Builder getTuberTable(Block block, Item crop, IntegerProperty ageProp, int age) {

        ILootCondition.IBuilder harvestAge = BlockStateProperty.hasBlockStateProperties(block).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(ageProp, age));
        return LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(ItemLootEntry.lootTableItem(crop)))
                .withPool(LootPool.lootPool()
                        .when(harvestAge)
                        .add(ItemLootEntry.lootTableItem(crop)
                                .apply(ApplyBonus.addBonusBinomialDistributionCount(Enchantments.BLOCK_FORTUNE, 0.5714286F, 3)))
                        .apply(ExplosionDecay.explosionDecay()));
    }

    protected LootTable.Builder getSimpleDropTable(Block block) {

        LootPool.Builder builder = LootPool.lootPool()
                .setRolls(ConstantRange.exactly(1))
                .add(ItemLootEntry.lootTableItem(block))
                .when(SurvivesExplosion.survivesExplosion());
        return LootTable.lootTable().withPool(builder);
    }

    protected LootTable.Builder getEmptyTable() {

        return LootTable.lootTable();
    }

    protected LootTable.Builder getStandardTileTable(String name, Block block) {

        LootPool.Builder builder = LootPool.lootPool()
                .name(name)
                .setRolls(ConstantRange.exactly(1))
                .add(ItemLootEntry.lootTableItem(block)
                        .apply(CopyName.copyName(CopyName.Source.BLOCK_ENTITY))
                        .apply(CopyNbt.copyData(CopyNbt.Source.BLOCK_ENTITY)
                                .copy("Info", "BlockEntityTag.Info", CopyNbt.Action.REPLACE)
                                .copy("Items", "BlockEntityTag.Items", CopyNbt.Action.REPLACE)
                                .copy("Energy", "BlockEntityTag.Energy", CopyNbt.Action.REPLACE))
                        .apply(SetContents.setContents()
                                .withEntry(DynamicLootEntry.dynamicEntry(new ResourceLocation("minecraft", "contents")))));
        return LootTable.lootTable().withPool(builder);
    }

    protected LootTable.Builder getSyncDropTable(Block block) {

        LootPool.Builder builder = LootPool.lootPool()
                .setRolls(ConstantRange.exactly(1))
                .add(ItemLootEntry.lootTableItem(block)
                        .apply(TileNBTSync.builder()))
                .when(SurvivesExplosion.survivesExplosion());
        return LootTable.lootTable().withPool(builder);
    }
    // endregion

    @Override
    public void run(DirectoryCache cache) {

        addTables();

        Map<ResourceLocation, LootTable> tables = new HashMap<>();
        for (Map.Entry<Block, LootTable.Builder> entry : blockLootTables.entrySet()) {
            tables.put(entry.getKey().getLootTable(), entry.getValue().setParamSet(LootParameterSets.BLOCK).build());
        }
        writeTables(cache, tables);
    }

    private void writeTables(DirectoryCache cache, Map<ResourceLocation, LootTable> tables) {

        Path outputFolder = this.generator.getOutputFolder();
        tables.forEach((key, lootTable) -> {
            Path path = outputFolder.resolve("data/" + key.getNamespace() + "/loot_tables/" + key.getPath() + ".json");
            try {
                IDataProvider.save(GSON, cache, LootTableManager.serialize(lootTable), path);
            } catch (IOException e) {
                LOGGER.error("Couldn't write loot table {}", path, e);
            }
        });
    }

}

