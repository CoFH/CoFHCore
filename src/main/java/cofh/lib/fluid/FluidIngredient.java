package cofh.lib.fluid;

import com.google.common.collect.Lists;
import com.google.gson.*;
import net.minecraft.fluid.Fluid;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tags.ITag;
import net.minecraft.tags.TagCollectionManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
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

public class FluidIngredient implements Predicate<FluidStack> {

    public static final FluidIngredient EMPTY = new FluidIngredient(Stream.empty());
    private final IFluidList[] values;
    private FluidStack[] fluidStacks;

    protected FluidIngredient(Stream<? extends IFluidList> fluidLists) {

        this.values = fluidLists.toArray(IFluidList[]::new);
    }

    public FluidStack[] getFluids() {

        this.dissolve();
        return this.fluidStacks;
    }

    private void dissolve() {

        if (this.fluidStacks == null) {
            this.fluidStacks = Arrays.stream(this.values).flatMap((ingredientList) -> ingredientList.getFluids().stream()).distinct().toArray(FluidStack[]::new);
        }

    }

    @Override
    public boolean test(@Nullable FluidStack p_test_1_) {

        if (p_test_1_ == null) {
            return false;
        } else {
            this.dissolve();
            if (this.fluidStacks.length == 0) {
                return p_test_1_.isEmpty();
            } else {
                for (FluidStack fluidstack : this.fluidStacks) {
                    if (fluidstack.getFluid() == p_test_1_.getFluid()) {
                        return true;
                    }
                }
                return false;
            }
        }
    }

    public final void toNetwork(PacketBuffer buffer) {

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

    public static FluidIngredient of(ITag<Fluid> tagIn) {

        return fromValues(Stream.of(new FluidIngredient.TagList(tagIn)));
    }

    public static FluidIngredient fromNetwork(PacketBuffer buffer) {

        int i = buffer.readVarInt();
        return fromValues(Stream.generate(() -> new SingleFluidList(buffer.readFluidStack())).limit(i));
    }

    public static FluidIngredient fromJson(@Nullable JsonElement p_199802_0_) {

        if (p_199802_0_ != null && !p_199802_0_.isJsonNull()) {
            if (p_199802_0_.isJsonObject()) {
                return fromValues(Stream.of(valueFromJson(p_199802_0_.getAsJsonObject())));
            } else if (p_199802_0_.isJsonArray()) {
                JsonArray jsonarray = p_199802_0_.getAsJsonArray();
                if (jsonarray.size() == 0) {
                    throw new JsonSyntaxException("Fluid array cannot be empty, at least one fluid must be defined");
                } else {
                    return fromValues(StreamSupport.stream(jsonarray.spliterator(), false).map((p_209355_0_) -> valueFromJson(JSONUtils.convertToJsonObject(p_209355_0_, "fluid"))));
                }
            } else {
                throw new JsonSyntaxException("Expected fluid to be object or array of objects");
            }
        } else {
            throw new JsonSyntaxException("Fluid cannot be null");
        }
    }

    public static FluidIngredient.IFluidList valueFromJson(JsonObject p_199803_0_) {

        if (p_199803_0_.has("fluid") && p_199803_0_.has("fluid_tag")) {
            throw new JsonParseException("A fluid ingredient entry is either a fluid tag or a fluid, not both");
        } else if (p_199803_0_.has("fluid")) {
            ResourceLocation resourcelocation1 = new ResourceLocation(JSONUtils.getAsString(p_199803_0_, "fluid"));
            Fluid fluid = ForgeRegistries.FLUIDS.getValue(resourcelocation1);
            if (fluid == null) {
                throw new JsonSyntaxException("Unknown fluid '" + resourcelocation1 + "'");
            }
            return new FluidIngredient.SingleFluidList(new FluidStack(fluid, FluidAttributes.BUCKET_VOLUME));
        } else if (p_199803_0_.has("fluid_tag")) {
            ResourceLocation resourcelocation = new ResourceLocation(JSONUtils.getAsString(p_199803_0_, "fluid_tag"));
            ITag<Fluid> itag = TagCollectionManager.getInstance().getFluids().getTag(resourcelocation);
            if (itag == null) {
                throw new JsonSyntaxException("Unknown fluid tag '" + resourcelocation + "'");
            } else {
                return new FluidIngredient.TagList(itag);
            }
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
            return jsonobject;
        }

    }

    public static class TagList implements FluidIngredient.IFluidList {

        private final ITag<Fluid> tag;

        public TagList(ITag<Fluid> p_i48193_1_) {

            this.tag = p_i48193_1_;
        }

        public Collection<FluidStack> getFluids() {

            List<FluidStack> list = Lists.newArrayList();

            for (Fluid fluid : this.tag.getValues()) {
                list.add(new FluidStack(fluid, FluidAttributes.BUCKET_VOLUME));
            }
            return list;
        }

        public JsonObject serialize() {

            JsonObject jsonobject = new JsonObject();
            jsonobject.addProperty("tag", TagCollectionManager.getInstance().getFluids().getIdOrThrow(this.tag).toString());
            return jsonobject;
        }

    }

}
