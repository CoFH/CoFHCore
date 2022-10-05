package cofh.core.fluid;

import cofh.core.util.helpers.FluidHelper;
import cofh.lib.fluid.FluidCoFH;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static cofh.core.CoFHCore.FLUIDS;
import static cofh.core.CoFHCore.FLUID_TYPES;
import static cofh.core.util.references.CoreIDs.ID_FLUID_POTION;
import static cofh.lib.util.constants.NBTTags.TAG_POTION;

public class PotionFluid extends FluidCoFH {

    private static PotionFluid INSTANCE;

    public static PotionFluid create() {

        if (INSTANCE == null) {
            INSTANCE = new PotionFluid();
        }
        return INSTANCE;
    }

    protected PotionFluid() {

        super(FLUIDS, ID_FLUID_POTION);

        // This is only used for testing.
        // bucket = ITEMS.register(bucket(key), () -> new BucketItem(stillFluid, new Item.Properties().containerItem(Items.BUCKET).maxStackSize(1).group(ItemGroup.BREWING)));
    }

    @Override
    protected ForgeFlowingFluid.Properties fluidProperties() {

        return new ForgeFlowingFluid.Properties(type(), stillFluid, flowingFluid);
    }

    @Override
    protected Supplier<FluidType> type() {

        return TYPE;
    }

    public static final RegistryObject<FluidType> TYPE = FLUID_TYPES.register(ID_FLUID_POTION, () -> new FluidType(FluidType.Properties.create()
            .density(1100)
            .viscosity(1100)
            .sound(SoundActions.BUCKET_FILL, SoundEvents.BOTTLE_FILL)
            .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BOTTLE_EMPTY)) {

        @Override
        public Component getDescription(FluidStack stack) {

            Potion potion = PotionUtils.getPotion(stack.getTag());
            if (potion == Potions.EMPTY || potion == Potions.WATER) {
                return super.getDescription(stack);
            }
            return Component.translatable(potion.getName(Items.POTION.getDescriptionId() + ".effect."));
        }

        @Override
        public Rarity getRarity(FluidStack stack) {

            return FluidHelper.getPotionFromFluidTag(stack.getTag()).getEffects().isEmpty() ? Rarity.COMMON : Rarity.UNCOMMON;
        }

        @Override
        public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {

            consumer.accept(new IClientFluidTypeExtensions() {

                private static final ResourceLocation
                        STILL = new ResourceLocation("cofh_core:block/fluids/potion_still"),
                        FLOW = new ResourceLocation("cofh_core:block/fluids/potion_flow");

                @Override
                public int getTintColor(FluidStack stack) {

                    return 0xFF000000 | getPotionColor(stack);
                }

                @Override
                public ResourceLocation getStillTexture() {

                    return STILL;
                }

                @Override
                public ResourceLocation getFlowingTexture() {

                    return FLOW;
                }
            });
        }
    });

    // region HELPERS
    public static int DEFAULT_COLOR = 0xF800F8;

    public static int getPotionColor(FluidStack stack) {

        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(PotionUtils.TAG_CUSTOM_POTION_COLOR, 99)) {
            return tag.getInt(PotionUtils.TAG_CUSTOM_POTION_COLOR);
        } else {
            return FluidHelper.getPotionFromFluidTag(stack.getTag()) == Potions.EMPTY ? DEFAULT_COLOR : PotionUtils.getColor(PotionUtils.getAllEffects(stack.getTag()));
        }
    }

    public static FluidStack getPotionAsFluid(int amount, Potion type, boolean hasCustom) {

        if (type == null) {
            return FluidStack.EMPTY;
        }
        if (type == Potions.WATER && !hasCustom) {
            return new FluidStack(Fluids.WATER, amount);
        }
        return addPotionToFluidStack(new FluidStack(INSTANCE.stillFluid.get(), amount), type);
    }

    public static FluidStack getPotionAsFluid(int amount, Potion type) {

        return getPotionAsFluid(amount, type, false);
    }

    public static FluidStack addPotionToFluidStack(FluidStack stack, Potion type) {

        ResourceLocation resourceLoc = ForgeRegistries.POTIONS.getKey(type);
        // NOTE: This can actually happen.
        if (resourceLoc == null) {
            return FluidStack.EMPTY;
        }
        stack.getOrCreateTag().putString(TAG_POTION, resourceLoc.toString());
        return stack;
    }

    public static FluidStack setCustomEffects(FluidStack stack, Collection<MobEffectInstance> effects) {

        if (stack.isEmpty() || effects.isEmpty()) {
            return stack;
        }
        CompoundTag compoundtag = stack.getOrCreateTag();
        ListTag listtag = compoundtag.getList(PotionUtils.TAG_CUSTOM_POTION_EFFECTS, 9);
        for (MobEffectInstance mobeffectinstance : effects) {
            listtag.add(mobeffectinstance.save(new CompoundTag()));
        }
        compoundtag.put(PotionUtils.TAG_CUSTOM_POTION_EFFECTS, listtag);
        return stack;
    }

    public static Collection<MobEffectInstance> getCustomEffects(FluidStack stack) {

        if (stack.isEmpty() || !stack.hasTag()) {
            return Collections.emptyList();
        }
        return PotionUtils.getCustomEffects(stack.getOrCreateTag());
    }

    public static FluidStack setCustomColor(FluidStack stack, int color) {

        stack.getOrCreateTag().putInt(PotionUtils.TAG_CUSTOM_POTION_COLOR, color);
        return stack;
    }

    public static ItemStack setCustomColor(ItemStack stack, int color) {

        stack.getOrCreateTag().putInt(PotionUtils.TAG_CUSTOM_POTION_COLOR, color);
        return stack;
    }

    public static FluidStack getPotionFluidFromItem(int amount, ItemStack stack) {

        Item item = stack.getItem();

        if (item.equals(Items.POTION)) {
            Collection<MobEffectInstance> custom = PotionUtils.getCustomEffects(stack);
            FluidStack fluid = setCustomEffects(getPotionAsFluid(amount, PotionUtils.getPotion(stack), !custom.isEmpty()), custom);
            int color = PotionUtils.getColor(stack);
            if (color != PotionUtils.getColor(PotionUtils.getMobEffects(stack))) {
                setCustomColor(fluid, color);
            }
            return fluid;
        }
        return FluidStack.EMPTY;
    }

    public static ItemStack getItemFromPotionFluid(FluidStack fluid) {

        ItemStack stack = PotionUtils.setCustomEffects(PotionUtils.setPotion(new ItemStack(Items.POTION), FluidHelper.getPotionFromFluid(fluid)), getCustomEffects(fluid));
        int color = getPotionColor(fluid);
        if (color != PotionUtils.getColor(stack)) {
            setCustomColor(stack, color);
        }
        return stack;
    }
    // endregion

    //    protected static class PotionFluidAttributes extends FluidAttributes {
    //
    //        protected PotionFluidAttributes(Builder builder, Fluid fluid) {
    //
    //            super(builder, fluid);
    //        }
    //
    //        @Override
    //        public Component getDisplayName(FluidStack stack) {
    //
    //            Potion potion = PotionUtils.getPotion(stack.getTag());
    //            if (potion == Potions.EMPTY || potion == Potions.WATER) {
    //                return super.getDisplayName(stack);
    //            }
    //            return new Component.translatable(potion.getName(Items.POTION.getDescriptionId() + ".effect."));
    //        }
    //
    //        public Rarity getRarity(FluidStack stack) {
    //
    //            return FluidHelper.getPotionFromFluidTag(stack.getTag()).getEffects().isEmpty() ? Rarity.COMMON : Rarity.UNCOMMON;
    //        }
    //
    //        @Override
    //        public int getColor(FluidStack stack) {
    //
    //            return 0xFF000000 | getPotionColor(stack);
    //        }
    //
    //        public static Builder builder(ResourceLocation stillTexture, ResourceLocation flowingTexture) {
    //
    //            return new Builder(stillTexture, flowingTexture, PotionFluidAttributes::new) {};
    //        }
    //
    //    }

}
