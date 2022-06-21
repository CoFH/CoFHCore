package cofh.core.content.item;

import cofh.core.content.fluid.FluidContainerItemWrapper;
import cofh.core.content.fluid.IFluidContainerItem;
import cofh.core.util.helpers.FluidHelper;
import cofh.lib.api.item.IColorableItem;
import cofh.lib.util.helpers.StringHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;

import static cofh.core.util.helpers.FluidHelper.addPotionTooltip;
import static cofh.core.util.helpers.FluidHelper.hasPotionTag;
import static cofh.core.util.helpers.ItemHelper.areItemStacksEqualIgnoreTags;
import static cofh.lib.api.ContainerType.FLUID;
import static cofh.lib.util.NBTTags.TAG_FLUID;
import static cofh.lib.util.helpers.StringHelper.*;

public class FluidContainerItem extends ItemCoFH implements IFluidContainerItem, IColorableItem {

    protected Predicate<FluidStack> validator;
    protected int fluidCapacity;

    public FluidContainerItem(Properties builder, int fluidCapacity, Predicate<FluidStack> validator) {

        super(builder);
        this.fluidCapacity = fluidCapacity;
        this.validator = validator;

        setEnchantability(5);
    }

    public FluidContainerItem(Properties builder, int fluidCapacity) {

        this(builder, fluidCapacity, e -> true);
    }

    public FluidContainerItem setFluidCapacity(int fluidCapacity) {

        this.fluidCapacity = fluidCapacity;
        return this;
    }

    @Override
    protected void tooltipDelegate(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {

        FluidStack fluid = getFluid(stack);
        if (!fluid.isEmpty()) {
            tooltip.add(StringHelper.getFluidName(fluid));
        }
        boolean creative = isCreative(stack, FLUID);
        tooltip.add(getTextComponent(localize("info.cofh.amount") + ": "
                + (creative ?
                localize("info.cofh.infinite") :
                format(fluid.getAmount()) + " / " + format(getCapacity(stack)) + " mB")));

        if (hasPotionTag(fluid)) {
            tooltip.add(getEmptyLine());
            tooltip.add(getTextComponent(localize("info.cofh.effects") + ":"));
            addPotionTooltip(fluid, tooltip);
        }
    }

    protected void potionTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn, List<MobEffectInstance> effects) {

        potionTooltip(stack, worldIn, tooltip, flagIn, effects, 1.0F);
    }

    protected void potionTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn, List<MobEffectInstance> effects, float durationFactor) {

        FluidStack fluid = getFluid(stack);
        if (!fluid.isEmpty()) {
            tooltip.add(StringHelper.getFluidName(fluid));
        }
        tooltip.add(isCreative(stack, FLUID)
                ? getTextComponent("info.cofh.infinite_source")
                : getTextComponent(localize("info.cofh.amount") + ": " + format(fluid.getAmount()) + " / " + format(getCapacity(stack)) + " mB"));

        if (hasPotionTag(fluid)) {
            tooltip.add(getEmptyLine());
            tooltip.add(getTextComponent(localize("info.cofh.effects") + ":"));
            addPotionTooltip(effects, tooltip, durationFactor);
        }
    }

    @Override
    public boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack) {

        return !(newStack.getItem() == oldStack.getItem()) || !areItemStacksEqualIgnoreTags(oldStack, newStack, TAG_FLUID);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {

        return super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged) && (slotChanged || !areItemStacksEqualIgnoreTags(oldStack, newStack, TAG_FLUID));
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {

        return !isCreative(stack, FLUID) && getFluidAmount(stack) > 0;
    }

    @Override
    public int getBarWidth(ItemStack stack) {

        return (int) Math.round(13.0D * getFluidAmount(stack) / (double) getCapacity(stack));
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {

        return new FluidContainerItemWrapper(stack, this);
    }

    // region IFluidContainerItem
    @Override
    public boolean isFluidValid(ItemStack container, FluidStack resource) {

        return validator.test(resource);
    }

    @Override
    public int getCapacity(ItemStack container) {

        return getMaxStored(container, fluidCapacity);
    }
    // endregion

    // region IColorableItem
    @Override
    public int getColor(ItemStack item, int colorIndex) {

        // TODO: Add base recoloration.
        //        if (colorIndex == 0) {
        //            return 0xD78D5B;
        //        }
        if (colorIndex == 1) {
            return getFluidAmount(item) > 0 ? FluidHelper.color(getFluid(item)) : 0xFFFFFF;
        }
        return 0xFFFFFF;
    }
    // endregion
}
