package cofh.lib.common.block;

import cofh.lib.util.Utils;
import cofh.lib.util.recipes.RecipeJsonUtils;
import com.google.gson.*;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class BlockIngredient implements Predicate<BlockState> {

    public static final BlockIngredient EMPTY = new BlockIngredient(new IBlockStateList[0]) {

        @Override
        public boolean test(@Nullable BlockState state) {

            return false;
        }
    };
    protected static IBlockStateList EMPTY_LIST = new IBlockStateList() {

        @Override
        public Stream<BlockState> getBlockStates() {

            return Stream.empty();
        }

        @Override
        public JsonObject serialize() {

            return new JsonObject();
        }
    };
    private final IBlockStateList[] values;
    private Set<BlockState> blockStates;

    protected BlockIngredient(IBlockStateList[] values) {

        this.values = values;
    }

    protected BlockIngredient(Set<BlockState> blockStates) {

        this(new IBlockStateList[0]);
        this.blockStates = blockStates;
    }

    protected BlockIngredient(Stream<? extends IBlockStateList> blockLists) {

        this(blockLists.toArray(IBlockStateList[]::new));
    }

    public Collection<BlockState> getBlockStates() {

        this.dissolve();
        return this.blockStates;
    }

    private void dissolve() {

        if (this.blockStates == null) {
            this.blockStates = Arrays.stream(this.values).flatMap(IBlockStateList::getBlockStates).collect(Collectors.toCollection(ReferenceOpenHashSet::new));
        }
    }

    @Override
    public boolean test(@Nullable BlockState state) {

        if (state == null) {
            return false;
        }
        this.dissolve();
        return this.blockStates.contains(state);
    }

    public final void toNetwork(FriendlyByteBuf buffer) {

        this.dissolve();
        buffer.writeVarInt(this.blockStates.size());
        for (BlockState state : this.blockStates) {
            buffer.writeVarInt(Block.getId(state));
        }
    }

    public JsonElement toJson() {

        if (this.values.length == 1) {
            return this.values[0].serialize();
        } else {
            JsonArray jsonarray = new JsonArray();
            for (IBlockStateList list : this.values) {
                jsonarray.add(list.serialize());
            }
            return jsonarray;
        }
    }

    public static BlockIngredient fromValues(IBlockStateList... values) {

        return values.length == 0 ? EMPTY : new BlockIngredient(values);
    }

    public static BlockIngredient fromNetwork(FriendlyByteBuf buffer) {

        int i = buffer.readVarInt();
        Set<BlockState> states = new ReferenceOpenHashSet<>(i);
        for (; i > 0; --i) {
            states.add(Block.stateById(buffer.readVarInt()));
        }
        return new BlockIngredient(states);
    }

    public static BlockIngredient fromJsonObject(JsonObject jsonObject) {

        return fromValues(valueFromJson(jsonObject));
    }

    public static BlockIngredient fromJsonArray(JsonArray jsonArray) {

        if (jsonArray.size() == 0) {
            throw new JsonSyntaxException("Block array cannot be empty, at least one block must be defined");
        }
        return fromValues(StreamSupport.stream(jsonArray.spliterator(), false).map(elem -> valueFromJson(GsonHelper.convertToJsonObject(elem, RecipeJsonUtils.BLOCK))).toArray(IBlockStateList[]::new));
    }

    protected static IBlockStateList valueFromJson(JsonObject jsonObject) {

        if (jsonObject.has(RecipeJsonUtils.NAME)) {
            if (jsonObject.has(RecipeJsonUtils.TAG)) {
                throw new JsonParseException("A block ingredient entry is either a block tag or a block, not both");
            }
            ResourceLocation resLoc = new ResourceLocation(GsonHelper.getAsString(jsonObject, RecipeJsonUtils.NAME));
            if (!BuiltInRegistries.BLOCK.containsKey(resLoc)) {
                throw new JsonSyntaxException("Unknown block '" + resLoc + "'");
            }
            BlockState state = BuiltInRegistries.BLOCK.get(resLoc).defaultBlockState();
            JsonElement element = jsonObject.get(RecipeJsonUtils.PROPERTIES);
            if (element != null && element.isJsonObject()) {
                Collection<Property<?>> variable = new ArrayList<>();
                JsonObject properties = element.getAsJsonObject();
                for (Property<?> prop : state.getProperties()) {
                    String name = prop.getName();
                    if (properties.has(name)) {
                        state = setValue(state, prop, properties.get(name).getAsString());
                    } else {
                        variable.add(prop);
                    }
                }
                return new BlockList(state, variable);
            }
            return new BlockList(state, state.getProperties());
        } else if (jsonObject.has(RecipeJsonUtils.TAG)) {
            return new TagList(BlockTags.create(new ResourceLocation(GsonHelper.getAsString(jsonObject, RecipeJsonUtils.TAG))));
        } else {
            throw new JsonParseException("A block ingredient entry needs either a tag or a block");
        }
    }

    private static <T extends Comparable<T>> BlockState setValue(BlockState state, Property<T> property, String value) {

        return property.getValue(value).map(t -> state.setValue(property, t)).orElse(state);
    }

    public interface IBlockStateList {

        Stream<BlockState> getBlockStates();

        JsonObject serialize();

    }

    public static class BlockList implements IBlockStateList {

        private final BlockState state;
        private final Collection<Property<?>> properties;

        public BlockList(BlockState state, Collection<Property<?>> properties) {

            this.state = state;
            this.properties = properties;
        }

        public Stream<BlockState> getBlockStates() {

            Stream<BlockState> states = Stream.of(state);
            for (Property<?> property : properties) {
                states = states.flatMap(state -> getNeighbors(state, property));
            }
            return states;
        }

        private <T extends Comparable<T>> Stream<BlockState> getNeighbors(BlockState state, Property<T> property) {

            return property.getAllValues().map(v -> state.setValue(property, v.value()));
        }

        public JsonObject serialize() {

            JsonObject jsonobject = new JsonObject();
            jsonobject.addProperty(RecipeJsonUtils.BLOCK, Utils.getName(this.state.getBlock()));
            JsonObject props = new JsonObject();
            this.state.getProperties().stream()
                    .filter(property -> !properties.contains(property))
                    .forEach(p -> props.addProperty(p.getName(), this.state.getValue(p).toString()));
            jsonobject.add(RecipeJsonUtils.STATE, props);
            return jsonobject;
        }

    }

    public static class TagList implements IBlockStateList {

        private final TagKey<Block> tag;

        public TagList(TagKey<Block> tag) {

            this.tag = tag;
        }

        public Stream<BlockState> getBlockStates() {

            return getStreamFromIterator(BuiltInRegistries.BLOCK.getTagOrEmpty(tag).iterator()).flatMap(holder -> holder.value().getStateDefinition().getPossibleStates().stream());
            // return BuiltInRegistries.BLOCK.tags().getTag(this.tag).stream().flatMap(block -> block.getStateDefinition().getPossibleStates().stream());
        }

        public JsonObject serialize() {

            JsonObject jsonobject = new JsonObject();
            jsonobject.addProperty(RecipeJsonUtils.TAG, tag.location().toString());
            return jsonobject;
        }

    }

    public static <T> Stream<T>
    getStreamFromIterator(Iterator<T> iterator) {

        // Convert the iterator to Spliterator
        Spliterator<T>
                spliterator = Spliterators
                .spliteratorUnknownSize(iterator, 0);

        // Get a Sequential Stream from spliterator
        return StreamSupport.stream(spliterator, false);
    }

}
