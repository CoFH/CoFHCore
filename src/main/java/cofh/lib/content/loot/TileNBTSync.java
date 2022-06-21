package cofh.lib.content.loot;

import cofh.lib.api.block.entity.ITileCallback;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import static cofh.lib.util.constants.ModIds.ID_COFH_CORE;
import static net.minecraft.world.level.storage.loot.parameters.LootContextParams.BLOCK_ENTITY;

public class TileNBTSync extends LootItemConditionalFunction {

    private static LootItemFunctionType INSTANCE;

    public static void setup() {

        if (INSTANCE != null) {
            return;
        }
        INSTANCE = Registry.register(Registry.LOOT_FUNCTION_TYPE, new ResourceLocation(ID_COFH_CORE + ":nbt_sync"), new LootItemFunctionType(new Serializer()));
    }

    protected TileNBTSync(LootItemCondition[] conditionsIn) {

        super(conditionsIn);
    }

    @Override
    public LootItemFunctionType getType() {

        return INSTANCE;
    }

    @Override
    public ItemStack run(ItemStack stack, LootContext context) {

        return applyToStack(stack, context.getParamOrNull(BLOCK_ENTITY));
    }

    public static ItemStack applyToStack(ItemStack stack, BlockEntity tile) {

        if (tile instanceof ITileCallback) {
            return ((ITileCallback) tile).createItemStackTag(stack);
        }
        return stack;
    }

    public static LootItemConditionalFunction.Builder<?> builder() {

        return simpleBuilder(TileNBTSync::new);
    }

    public static class Serializer extends LootItemConditionalFunction.Serializer<TileNBTSync> {

        public TileNBTSync deserialize(JsonObject object, JsonDeserializationContext deserializationContext, LootItemCondition[] conditionsIn) {

            return new TileNBTSync(conditionsIn);
        }

    }

}
