//package cofh.lib.util.flags;
//
//import com.mojang.serialization.Codec;
//import com.mojang.serialization.codecs.RecordCodecBuilder;
//import net.minecraft.world.level.storage.loot.LootContext;
//import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
//import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
//
//public record FlagSetLootCondition(String flag) implements LootItemCondition {
//
//    public static final Codec<FlagSetLootCondition> CODEC = RecordCodecBuilder.create(
//            builder -> builder.group(
//                            Codec.STRING.fieldOf("flag").forGetter(FlagSetLootCondition::flag))
//                    .apply(builder, FlagSetLootCondition::new));
//
//    @Override
//    public LootItemConditionType getType() {
//
//        return FlagManager.FLAG_SET;
//    }
//
//    @Override
//    public boolean test(LootContext context) {
//
//        return FlagManager.getFlag(flag).get();
//    }
//
//    public static LootItemCondition.Builder flagSet(String flag) {
//
//        return () -> new FlagSetLootCondition(flag);
//    }
//
//}
