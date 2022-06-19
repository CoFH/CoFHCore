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
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.ForgeFlowingFluid;

import java.util.Collection;

import static cofh.core.CoFHCore.FLUIDS;
import static cofh.lib.util.constants.NBTTags.TAG_POTION;
import static cofh.lib.util.references.CoreIDs.ID_FLUID_POTION;
import static cofh.lib.util.references.CoreReferences.FLUID_POTION;

public class PotionFluid extends FluidCoFH {

    public static int DEFAULT_COLOR = 0xF800F8;

    public static PotionFluid create() {

        return new PotionFluid(ID_FLUID_POTION, "cofh_core:block/fluids/potion_still", "cofh_core:block/fluids/potion_flow");
    }

    protected PotionFluid(String key, String stillTexture, String flowTexture) {

        stillFluid = FLUIDS.register(key, () -> new ForgeFlowingFluid.Source(properties));
        flowingFluid = FLUIDS.register(flowing(key), () -> new ForgeFlowingFluid.Flowing(properties));

        // This is only used for testing.
        // bucket = ITEMS.register(bucket(key), () -> new BucketItem(stillFluid, new Item.Properties().containerItem(Items.BUCKET).maxStackSize(1).group(ItemGroup.BREWING)));

        properties = new ForgeFlowingFluid.Properties(stillFluid, flowingFluid, PotionFluidAttributes.builder(new ResourceLocation(stillTexture), new ResourceLocation(flowTexture))
                .sound(SoundEvents.BOTTLE_FILL, SoundEvents.BOTTLE_EMPTY));
    }

    public static int getPotionColor(FluidStack stack) {

        if (stack.getTag() != null && stack.getTag().contains("CustomPotionColor", 99)) {
            return stack.getTag().getInt("CustomPotionColor");
        } else {
            return FluidHelper.getPotionFromFluidTag(stack.getTag()) == Potions.EMPTY ? DEFAULT_COLOR : PotionUtils.getColor(PotionUtils.getAllEffects(stack.getTag()));
        }
    }

    public static FluidStack getPotionAsFluid(int amount, Potion type) {

        if (type == null || type == Potions.EMPTY) {
            return FluidStack.EMPTY;
        }
        if (type == Potions.WATER) {
            return new FluidStack(Fluids.WATER, amount);
        }
        return addPotionToFluidStack(new FluidStack(FLUID_POTION, amount), type);
    }

    public static FluidStack addPotionToFluidStack(FluidStack stack, Potion type) {

        ResourceLocation resourceLoc = type.getRegistryName();
        // NOTE: This can actually happen.
        if (resourceLoc == null) {
            return FluidStack.EMPTY;
        }
        if (type == Potions.EMPTY) {
            if (stack.getTag() != null) {
                stack.getTag().remove(TAG_POTION);
                if (stack.getTag().isEmpty()) {
                    stack.setTag(null);
                }
            }
        } else {
            if (stack.getTag() == null) {
                stack.setTag(new CompoundTag());
            }
            stack.getTag().putString(TAG_POTION, resourceLoc.toString());
        }
        return stack;
    }

    public static FluidStack setCustomEffects(FluidStack stack, Collection<MobEffectInstance> effects) {

        if (stack.isEmpty() || effects.isEmpty()) {
            return stack;
        }
        CompoundTag compoundtag = stack.getOrCreateTag();
        ListTag listtag = compoundtag.getList("CustomPotionEffects", 9);
        for (MobEffectInstance mobeffectinstance : effects) {
            listtag.add(mobeffectinstance.save(new CompoundTag()));
        }
        compoundtag.put("CustomPotionEffects", listtag);
        return stack;
    }

    public static FluidStack getPotionFluidFromItem(int amount, ItemStack stack) {

        Item item = stack.getItem();

        if (item.equals(Items.POTION)) {
            return setCustomEffects(getPotionAsFluid(amount, PotionUtils.getPotion(stack)), PotionUtils.getCustomEffects(stack));
        }
        return FluidStack.EMPTY;
    }

    protected static class PotionFluidAttributes extends FluidAttributes {

        protected PotionFluidAttributes(Builder builder, Fluid fluid) {

            super(builder, fluid);
        }

        @Override
        public Component getDisplayName(FluidStack stack) {

            Potion potion = PotionUtils.getPotion(stack.getTag());
            if (potion == Potions.EMPTY || potion == Potions.WATER) {
                return super.getDisplayName(stack);
            }
            return Component.translatable(potion.getName(Items.POTION.getDescriptionId() + ".effect."));
        }

        public Rarity getRarity(FluidStack stack) {

            return FluidHelper.getPotionFromFluidTag(stack.getTag()).getEffects().isEmpty() ? Rarity.COMMON : Rarity.UNCOMMON;
        }

        @Override
        public int getColor(FluidStack stack) {

            return 0xFF000000 | getPotionColor(stack);
        }

        public static Builder builder(ResourceLocation stillTexture, ResourceLocation flowingTexture) {

            return new Builder(stillTexture, flowingTexture, PotionFluidAttributes::new) {};
        }

    }

}
