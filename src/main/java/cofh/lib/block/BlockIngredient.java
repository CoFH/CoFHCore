package cofh.lib.block;

import com.google.gson.*;
import com.mojang.serialization.JsonOps;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class BlockIngredient implements Predicate<BlockState> {

    public static final BlockIngredient EMPTY = new BlockIngredient(Stream.empty());
    protected static IBlockStateList EMPTY_LIST = new IBlockStateList() {

        @Override
        public Collection<BlockState> getBlockStates() {

            return Collections.emptyList();
        }

        @Override
        public JsonObject serialize() {

            return new JsonObject();
        }
    };
    private final IBlockStateList[] values;
    private Set<BlockState> blockStates;

    protected BlockIngredient(Stream<? extends IBlockStateList> blockLists) {

        this.values = blockLists.toArray(IBlockStateList[]::new);
    }

    public Collection<BlockState> getBlockStates() {

        this.dissolve();
        return this.blockStates;
    }

    private void dissolve() {

        if (this.blockStates == null) {
            this.blockStates = Arrays.stream(this.values).flatMap((ingredientList) -> ingredientList.getBlockStates().stream()).collect(Collectors.toCollection(ObjectOpenHashSet::new));
        }
    }

    @Override
    public boolean test(@Nullable BlockState test) {

        if (test == null) {
            return false;
        }
        this.dissolve();
        return this.blockStates.contains(test);
    }

    public final void toNetwork(FriendlyByteBuf buffer) {

        this.dissolve();
        buffer.writeVarInt(this.values.length);
        for (IBlockStateList list : this.values) {
            CompoundTag.CODEC.parse(JsonOps.INSTANCE, list.serialize())
                    .resultOrPartial(err -> buffer.writeNbt(new CompoundTag()))
                    .ifPresent(buffer::writeNbt);
        }
        //buffer.writeVarInt(this.blockStates.size());
        //this.blockStates.forEach(match -> buffer.writeNbt(NbtUtils.writeBlockState(match)));
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

    public boolean isEmpty() {

        return this.values.length == 0 && (this.blockStates == null || this.blockStates.isEmpty());
    }

    public static BlockIngredient fromValues(Stream<? extends IBlockStateList> stream) {

        BlockIngredient ingredient = new BlockIngredient(stream);
        return ingredient.values.length == 0 ? EMPTY : ingredient;
    }

    public static BlockIngredient of() {

        return EMPTY;
    }

    public static BlockIngredient of(BlockState... states) {

        return of(Arrays.stream(states));
    }

    public static BlockIngredient of(Stream<BlockState> states) {

        return fromValues(states.map(SingleStateList::new));
    }

    public static BlockIngredient of(TagKey<Block> tag) {

        return fromValues(Stream.of(new TagList(tag)));
    }

    public static BlockIngredient fromNetwork(FriendlyByteBuf buffer) {

        int i = buffer.readVarInt();
        return fromValues(Stream.generate(() ->
                CompoundTag.CODEC.encodeStart(JsonOps.INSTANCE, buffer.readNbt()).result().map(BlockIngredient::valueFromJson).orElse(EMPTY_LIST)).limit(i));
        //int i = buffer.readVarInt();
        //return fromValues(Stream.generate(() -> new SingleStateList(NbtUtils.readBlockState(buffer.readNbt()))).limit(i));
    }

    public static BlockIngredient fromJson(@Nullable JsonElement jsonElement) {

        if (jsonElement != null && !jsonElement.isJsonNull()) {
            if (jsonElement.isJsonObject()) {
                return fromValues(Stream.of(valueFromJson(jsonElement.getAsJsonObject())));
            } else if (jsonElement.isJsonArray()) {
                JsonArray jsonarray = jsonElement.getAsJsonArray();
                if (jsonarray.size() == 0) {
                    throw new JsonSyntaxException("Block array cannot be empty, at least one block must be defined");
                } else {
                    return fromValues(StreamSupport.stream(jsonarray.spliterator(), false).map(elem -> valueFromJson(GsonHelper.convertToJsonObject(elem, "block"))));
                }
            } else {
                throw new JsonSyntaxException("Expected block to be object or array of objects");
            }
        } else {
            throw new JsonSyntaxException("Block cannot be null");
        }
    }

    public static IBlockStateList valueFromJson(JsonElement jsonElement) {

        if (jsonElement.isJsonObject()) {
            return valueFromJson(jsonElement.getAsJsonObject());
        }
        return EMPTY_LIST;
    }

    public static IBlockStateList valueFromJson(JsonObject jsonObject) {

        if (jsonObject.has("block")) {
            if (jsonObject.has("block_tag")) {
                throw new JsonParseException("A block ingredient entry is either a block tag or a block, not both");
            }
            ResourceLocation resLoc = new ResourceLocation(GsonHelper.getAsString(jsonObject, "block"));
            Block block = ForgeRegistries.BLOCKS.getValue(resLoc);
            if (block == null) {
                throw new JsonSyntaxException("Unknown block '" + resLoc + "'");
            }
            JsonElement element = jsonObject.get("properties");
            if (element != null && element.isJsonObject()) {
                BlockState state = block.defaultBlockState();
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
                return new PartialStateList(state, variable);
            }
            return new BlockList(block);
        } else if (jsonObject.has("block_tag")) {
            ResourceLocation resLoc = new ResourceLocation(GsonHelper.getAsString(jsonObject, "block_tag"));
            return new TagList(BlockTags.create(resLoc));
        } else {
            throw new JsonParseException("A block ingredient entry needs either a block_tag or a block");
        }
    }

    private static <T extends Comparable<T>> BlockState setValue(BlockState state, Property<T> property, String value) {

        return property.getValue(value).map(t -> state.setValue(property, t)).orElse(state);
    }

    public interface IBlockStateList {

        Collection<BlockState> getBlockStates();

        JsonObject serialize();

    }

    public static class SingleStateList implements IBlockStateList {

        private final BlockState state;

        public SingleStateList(BlockState state) {

            this.state = state;
        }

        public Collection<BlockState> getBlockStates() {

            return Collections.singleton(this.state);
        }

        public JsonObject serialize() {

            JsonObject jsonobject = new JsonObject();
            jsonobject.addProperty("block", this.state.getBlock().getRegistryName().toString());
            JsonObject properties = new JsonObject();
            this.state.getValues().forEach((p, v) -> properties.addProperty(p.getName(), v.toString()));
            jsonobject.add("properties", properties);
            return jsonobject;
        }

    }

    public static class PartialStateList implements IBlockStateList {

        private final BlockState state;
        private final Collection<Property<?>> properties;

        public PartialStateList(BlockState state, Collection<Property<?>> properties) {

            this.state = state;
            this.properties = properties;
        }

        public Collection<BlockState> getBlockStates() {

            if (properties.isEmpty()) {
                return Collections.singleton(state);
            }
            Collection<BlockState> states = new HashSet<>();
            states.add(state);
            properties.forEach(p -> {
                Collection<BlockState> add = new ArrayList<>();
                states.forEach(st -> p.getPossibleValues().forEach(val -> add.add(setValue(st, p, val.toString()))));
                states.addAll(add);
            });
            return states;
        }

        public JsonObject serialize() {

            JsonObject jsonobject = new JsonObject();
            jsonobject.addProperty("block", this.state.getBlock().getRegistryName().toString());
            JsonObject props = new JsonObject();
            Collection<Property<?>> used = new HashSet<>(this.state.getProperties());
            used.removeAll(this.properties);
            used.forEach(p -> props.addProperty(p.getName(), this.state.getValue(p).toString()));
            jsonobject.add("properties", props);
            return jsonobject;
        }

    }

    public static class BlockList implements IBlockStateList {

        private final Block block;

        public BlockList(Block block) {

            this.block = block;
        }

        public Collection<BlockState> getBlockStates() {

            return block.getStateDefinition().getPossibleStates();
        }

        public JsonObject serialize() {

            JsonObject jsonobject = new JsonObject();
            jsonobject.addProperty("block", this.block.getRegistryName().toString());
            return jsonobject;
        }

    }

    public static class TagList implements IBlockStateList {

        private final TagKey<Block> tag;

        public TagList(TagKey<Block> tag) {

            this.tag = tag;
        }

        public Collection<BlockState> getBlockStates() {

            return ForgeRegistries.BLOCKS.tags().getTag(this.tag).stream().flatMap(block -> block.getStateDefinition().getPossibleStates().stream()).collect(Collectors.toList());
        }

        public JsonObject serialize() {

            JsonObject jsonobject = new JsonObject();
            jsonobject.addProperty("block_tag", tag.location().toString());
            return jsonobject;
        }

    }

}
