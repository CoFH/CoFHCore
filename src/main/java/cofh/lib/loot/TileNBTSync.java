package cofh.lib.loot;

import cofh.lib.tileentity.ITileCallback;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootFunction;
import net.minecraft.loot.LootFunctionType;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

import static cofh.lib.util.constants.Constants.ID_COFH_CORE;
import static net.minecraft.loot.LootParameters.BLOCK_ENTITY;

public class TileNBTSync extends LootFunction {

    private static LootFunctionType INSTANCE;

    public static void setup() {

        if (INSTANCE != null) {
            return;
        }
        INSTANCE = Registry.register(Registry.LOOT_FUNCTION_TYPE, new ResourceLocation(ID_COFH_CORE + ":nbt_sync"), new LootFunctionType(new Serializer()));
    }

    protected TileNBTSync(ILootCondition[] conditionsIn) {

        super(conditionsIn);
    }

    @Override
    public LootFunctionType getFunctionType() {

        return INSTANCE;
    }

    @Override
    public ItemStack doApply(ItemStack stack, LootContext context) {

        return applyToStack(stack, context.get(BLOCK_ENTITY));
    }

    public static ItemStack applyToStack(ItemStack stack, TileEntity tile) {

        if (tile instanceof ITileCallback) {
            return ((ITileCallback) tile).createItemStackTag(stack);
        }
        return stack;
    }

    public static LootFunction.Builder<?> builder() {

        return builder(TileNBTSync::new);
    }

    public static class Serializer extends LootFunction.Serializer<TileNBTSync> {

        public TileNBTSync deserialize(JsonObject object, JsonDeserializationContext deserializationContext, ILootCondition[] conditionsIn) {

            return new TileNBTSync(conditionsIn);
        }

    }

}
