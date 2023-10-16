package cofh.lib.util.crafting;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.function.Function;

public class SimpleRecipeSerializer<T extends Recipe<?>> implements RecipeSerializer<T> {

    private final Function<ResourceLocation, T> constructor;

    public SimpleRecipeSerializer(Function<ResourceLocation, T> ctor) {

        this.constructor = ctor;
    }

    public T fromJson(ResourceLocation location, JsonObject json) {

        return this.constructor.apply(location);
    }

    public T fromNetwork(ResourceLocation location, FriendlyByteBuf buf) {

        return this.constructor.apply(location);
    }

    public void toNetwork(FriendlyByteBuf bif, T recipe) {

    }

}