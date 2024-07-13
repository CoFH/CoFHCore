package cofh.lib.common.conditions;

import cofh.lib.util.FlagManager;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.neoforged.neoforge.common.conditions.ICondition;

public record FlagSetCondition(String flag) implements ICondition {

    public static final Codec<FlagSetCondition> CODEC = RecordCodecBuilder.create(
            builder -> builder.group(
                            Codec.STRING.fieldOf("flag").forGetter(FlagSetCondition::flag))
                    .apply(builder, FlagSetCondition::new));

    @Override
    public boolean test(IContext context) {

        return FlagManager.getFlag(flag).get();
    }

    @Override
    public Codec<? extends ICondition> codec() {

        return CODEC;
    }

    @Override
    public String toString() {

        return "flag_set(\"" + flag + "\")";
    }

}
