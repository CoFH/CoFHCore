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
    private final DataGenerator generator;

    public LootTableProviderCoFH(DataGenerator gen) {

        super(gen);
        this.generator = gen;
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

        LootPool.Builder builder = LootPool.builder()
                .name(name)
                .rolls(ConstantRange.of(1))
                .addEntry(AlternativesLootEntry.builder(ItemLootEntry.builder(block)
                        .acceptCondition(MatchTool.builder(ItemPredicate.Builder.create()
                                .enchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, MinMaxBounds.IntBound.atLeast(1))))), ItemLootEntry.builder(lootItem)
                        .acceptFunction(SetCount.builder(new RandomValueRange(min, max)))
                        .acceptFunction(ApplyBonus.uniformBonusCount(Enchantments.FORTUNE, bonus))
                        .acceptFunction(ExplosionDecay.builder())));
        return LootTable.builder().addLootPool(builder);
    }

    protected LootTable.Builder getSilkTouchOreTable(Block block, Item lootItem) {

        LootPool.Builder builder = LootPool.builder()
                .rolls(ConstantRange.of(1))
                .addEntry(AlternativesLootEntry.builder(ItemLootEntry.builder(block)
                        .acceptCondition(MatchTool.builder(ItemPredicate.Builder.create()
                                .enchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, MinMaxBounds.IntBound.atLeast(1))))), ItemLootEntry.builder(lootItem)
                        .acceptFunction(ApplyBonus.oreDrops(Enchantments.FORTUNE))
                        .acceptFunction(ExplosionDecay.builder())));
        return LootTable.builder().addLootPool(builder);
    }

    protected LootTable.Builder getCropTable(Block block, Item crop, Item seed, IntegerProperty ageProp, int age) {

        ILootCondition.IBuilder harvestAge = BlockStateProperty.builder(block).fromProperties(StatePropertiesPredicate.Builder.newBuilder().withIntProp(ageProp, age));
        return LootTable.builder()
                .addLootPool(LootPool.builder()
                        .addEntry(ItemLootEntry.builder(crop)
                                .acceptCondition(harvestAge)
                                .alternatively(ItemLootEntry.builder(seed))))
                .addLootPool(LootPool.builder()
                        .acceptCondition(harvestAge)
                        .addEntry(ItemLootEntry.builder(seed)
                                // These are Mojang's numbers. No idea.
                                .acceptFunction(ApplyBonus.binomialWithBonusCount(Enchantments.FORTUNE, 0.5714286F, 3))))
                .acceptFunction(ExplosionDecay.builder());
    }

    protected LootTable.Builder getTuberTable(Block block, Item crop, IntegerProperty ageProp, int age) {

        ILootCondition.IBuilder harvestAge = BlockStateProperty.builder(block).fromProperties(StatePropertiesPredicate.Builder.newBuilder().withIntProp(ageProp, age));
        return LootTable.builder()
                .addLootPool(LootPool.builder()
                        .addEntry(ItemLootEntry.builder(crop)))
                .addLootPool(LootPool.builder()
                        .acceptCondition(harvestAge)
                        .addEntry(ItemLootEntry.builder(crop)
                                .acceptFunction(ApplyBonus.binomialWithBonusCount(Enchantments.FORTUNE, 0.5714286F, 3)))
                        .acceptFunction(ExplosionDecay.builder()));
    }

    protected LootTable.Builder getSimpleDropTable(Block block) {

        LootPool.Builder builder = LootPool.builder()
                .rolls(ConstantRange.of(1))
                .addEntry(ItemLootEntry.builder(block))
                .acceptCondition(SurvivesExplosion.builder());
        return LootTable.builder().addLootPool(builder);
    }

    protected LootTable.Builder getEmptyTable() {

        return LootTable.builder();
    }

    protected LootTable.Builder getStandardTileTable(String name, Block block) {

        LootPool.Builder builder = LootPool.builder()
                .name(name)
                .rolls(ConstantRange.of(1))
                .addEntry(ItemLootEntry.builder(block)
                        .acceptFunction(CopyName.builder(CopyName.Source.BLOCK_ENTITY))
                        .acceptFunction(CopyNbt.builder(CopyNbt.Source.BLOCK_ENTITY)
                                .addOperation("Info", "BlockEntityTag.Info", CopyNbt.Action.REPLACE)
                                .addOperation("Items", "BlockEntityTag.Items", CopyNbt.Action.REPLACE)
                                .addOperation("Energy", "BlockEntityTag.Energy", CopyNbt.Action.REPLACE))
                        .acceptFunction(SetContents.builderIn()
                                .addLootEntry(DynamicLootEntry.func_216162_a(new ResourceLocation("minecraft", "contents")))));
        return LootTable.builder().addLootPool(builder);
    }

    protected LootTable.Builder getSyncDropTable(Block block) {

        LootPool.Builder builder = LootPool.builder()
                .rolls(ConstantRange.of(1))
                .addEntry(ItemLootEntry.builder(block)
                        .acceptFunction(TileNBTSync.builder()))
                .acceptCondition(SurvivesExplosion.builder());
        return LootTable.builder().addLootPool(builder);
    }
    // endregion

    @Override
    public void act(DirectoryCache cache) {

        addTables();

        Map<ResourceLocation, LootTable> tables = new HashMap<>();
        for (Map.Entry<Block, LootTable.Builder> entry : blockLootTables.entrySet()) {
            tables.put(entry.getKey().getLootTable(), entry.getValue().setParameterSet(LootParameterSets.BLOCK).build());
        }
        writeTables(cache, tables);
    }

    private void writeTables(DirectoryCache cache, Map<ResourceLocation, LootTable> tables) {

        Path outputFolder = this.generator.getOutputFolder();
        tables.forEach((key, lootTable) -> {
            Path path = outputFolder.resolve("data/" + key.getNamespace() + "/loot_tables/" + key.getPath() + ".json");
            try {
                IDataProvider.save(GSON, cache, LootTableManager.toJson(lootTable), path);
            } catch (IOException e) {
                LOGGER.error("Couldn't write loot table {}", path, e);
            }
        });
    }

}

