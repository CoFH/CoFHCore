package cofh.lib.fluid;

import com.google.common.collect.Lists;
import com.google.gson.*;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static cofh.lib.util.Constants.BUCKET_VOLUME;

public class FluidIngredient implements Predicate<FluidStack> {

    public static final FluidIngredient EMPTY = new FluidIngredient(Stream.empty());
    private final IFluidList[] values;
    private FluidStack[] fluidStacks;
    private int amount = BUCKET_VOLUME;
    private CompoundTag tag;

    protected FluidIngredient(Stream<? extends IFluidList> fluidLists) {

        this.values = fluidLists.toArray(IFluidList[]::new);
    }

    public FluidIngredient setAmount(int amount) {

        this.amount = amount;
        return this;
    }

    public FluidIngredient setTag(CompoundTag tag) {

        this.tag = tag;
        return this;
    }

    public FluidStack[] getFluids() {

        this.dissolve();
        return this.fluidStacks;
    }

    private void dissolve() {

        if (this.fluidStacks == null) {
            this.fluidStacks = Arrays.stream(this.values).flatMap((ingredientList) -> ingredientList.getFluids().stream()).distinct().toArray(FluidStack[]::new);
        }
        for (FluidStack stack : fluidStacks) {
            if (stack.getRawFluid() != Fluids.EMPTY) {
                stack.setAmount(amount);
                stack.setTag(tag);
            }
        }
    }

    @Override
    public boolean test(@Nullable FluidStack test) {

        if (test == null) {
            return false;
        } else {
            this.dissolve();
            if (this.fluidStacks.length == 0) {
                return test.isEmpty();
            } else {
                for (FluidStack fluidstack : this.fluidStacks) {
                    if (fluidstack.getFluid() == test.getFluid()) {
                        return true;
                    }
                }
                return false;
            }
        }
    }

    public final void toNetwork(FriendlyByteBuf buffer) {

        this.dissolve();
        buffer.writeVarInt(this.fluidStacks.length);
        for (FluidStack matchingStack : this.fluidStacks) {
            buffer.writeFluidStack(matchingStack);
        }
    }

    public JsonElement toJson() {

        if (this.values.length == 1) {
            return this.values[0].serialize();
        } else {
            JsonArray jsonarray = new JsonArray();
            for (IFluidList list : this.values) {
                jsonarray.add(list.serialize());
            }
            return jsonarray;
        }
    }

    public boolean isEmpty() {

        return this.values.length == 0 && (this.fluidStacks == null || this.fluidStacks.length == 0);
    }

    public static FluidIngredient fromValues(Stream<? extends IFluidList> stream) {

        FluidIngredient fluidIngredient = new FluidIngredient(stream);
        return fluidIngredient.values.length == 0 ? EMPTY : fluidIngredient;
    }

    public static FluidIngredient of(FluidStack... stacks) {

        return of(Arrays.stream(stacks));
    }

    public static FluidIngredient of(Stream<FluidStack> stacks) {

        return fromValues(stacks.filter((stack) -> !stack.isEmpty()).map(SingleFluidList::new));
    }

    public static FluidIngredient of(TagKey<Fluid> tagIn, int amount) {

        return fromValues(Stream.of(new FluidIngredient.TagList(tagIn, amount)));
    }

    public static FluidIngredient fromNetwork(FriendlyByteBuf buffer) {

        int i = buffer.readVarInt();
        return fromValues(Stream.generate(() -> new SingleFluidList(buffer.readFluidStack())).limit(i));
    }

    public static FluidIngredient fromJson(@Nullable JsonElement jsonElement) {

        if (jsonElement != null && !jsonElement.isJsonNull()) {
            if (jsonElement.isJsonObject()) {
                return fromValues(Stream.of(valueFromJson(jsonElement.getAsJsonObject())));
            } else if (jsonElement.isJsonArray()) {
                JsonArray jsonarray = jsonElement.getAsJsonArray();
                if (jsonarray.size() == 0) {
                    throw new JsonSyntaxException("Fluid array cannot be empty, at least one fluid must be defined");
                } else {
                    return fromValues(StreamSupport.stream(jsonarray.spliterator(), false).map((p_209355_0_) -> valueFromJson(GsonHelper.convertToJsonObject(p_209355_0_, "fluid"))));
                }
            } else {
                throw new JsonSyntaxException("Expected fluid to be object or array of objects");
            }
        } else {
            throw new JsonSyntaxException("Fluid cannot be null");
        }
    }

    public static FluidIngredient.IFluidList valueFromJson(JsonObject jsonObject) {

        if (jsonObject.has("fluid") && jsonObject.has("fluid_tag")) {
            throw new JsonParseException("A fluid ingredient entry is either a fluid tag or a fluid, not both");
        } else if (jsonObject.has("fluid")) {
            ResourceLocation resourcelocation1 = new ResourceLocation(GsonHelper.getAsString(jsonObject, "fluid"));
            Fluid fluid = ForgeRegistries.FLUIDS.getValue(resourcelocation1);
            if (fluid == null) {
                throw new JsonSyntaxException("Unknown fluid '" + resourcelocation1 + "'");
            }
            int amount = FluidAttributes.BUCKET_VOLUME;
            if (jsonObject.has("amount")) {
                amount = jsonObject.get("amount").getAsInt();
            } else if (jsonObject.has("count")) {
                amount = jsonObject.get("count").getAsInt();
            }
            return new FluidIngredient.SingleFluidList(new FluidStack(fluid, amount));
        } else if (jsonObject.has("fluid_tag")) {
            ResourceLocation resourcelocation = new ResourceLocation(GsonHelper.getAsString(jsonObject, "fluid_tag"));
            TagKey<Fluid> key = FluidTags.create(resourcelocation);
            int amount = FluidAttributes.BUCKET_VOLUME;
            if (jsonObject.has("amount")) {
                amount = jsonObject.get("amount").getAsInt();
            } else if (jsonObject.has("count")) {
                amount = jsonObject.get("count").getAsInt();
            }
            return new FluidIngredient.TagList(key, amount);
        } else {
            throw new JsonParseException("A fluid ingredient entry needs either a fluid_tag or a fluid");
        }
    }

    public interface IFluidList {

        Collection<FluidStack> getFluids();

        JsonObject serialize();

    }

    public static class SingleFluidList implements FluidIngredient.IFluidList {

        private final FluidStack fluid;

        public SingleFluidList(FluidStack fluid) {

            this.fluid = fluid;
        }

        public Collection<FluidStack> getFluids() {

            return Collections.singleton(this.fluid);
        }

        public JsonObject serialize() {

            JsonObject jsonobject = new JsonObject();
            jsonobject.addProperty("fluid", ForgeRegistries.FLUIDS.getKey(this.fluid.getFluid()).toString());
            jsonobject.addProperty("amount", this.fluid.getAmount());
            return jsonobject;
        }

    }

    public static class TagList implements FluidIngredient.IFluidList {

        private final TagKey<Fluid> tag;
        private final int amount;

        public TagList(TagKey<Fluid> tag, int amount) {

            this.tag = tag;
            this.amount = amount;
        }

        public Collection<FluidStack> getFluids() {

            List<FluidStack> list = Lists.newArrayList();

            for (Holder<Fluid> fluid : Registry.FLUID.getTagOrEmpty(this.tag)) {
                list.add(new FluidStack(fluid.value(), amount));
            }
            return list;
        }

        public JsonObject serialize() {

            JsonObject jsonobject = new JsonObject();
            jsonobject.addProperty("fluid_tag", tag.location().toString());
            jsonobject.addProperty("amount", this.amount);
            return jsonobject;
        }

    }

}
