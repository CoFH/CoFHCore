package cofh.lib.common.conditions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.conditions.ICondition;

public record TagExistsCondition(TagKey<Item> tag) implements ICondition {

    public static final Codec<TagExistsCondition> CODEC = RecordCodecBuilder.create(
            builder -> builder.group(
                            ResourceLocation.CODEC.xmap(loc -> TagKey.create(Registries.ITEM, loc), TagKey::location).fieldOf("tag").forGetter(TagExistsCondition::tag))
                    .apply(builder, TagExistsCondition::new));

    public TagExistsCondition(String location) {

        this(new ResourceLocation(location));
    }

    public TagExistsCondition(String namespace, String path) {

        this(new ResourceLocation(namespace, path));
    }

    public TagExistsCondition(ResourceLocation tag) {

        this(TagKey.create(Registries.ITEM, tag));
    }

    @Override
    public boolean test(IContext context) {

        return !context.getTag(tag).isEmpty();
    }

    @Override
    public Codec<? extends ICondition> codec() {

        return CODEC;
    }

    @Override
    public String toString() {

        return "tag_exists(\"" + tag.location() + "\")";
    }

}
