package cofh.core.item;

import cofh.lib.fluid.FluidContainerItemWrapper;
import cofh.lib.fluid.IFluidContainerItem;
import cofh.lib.item.IColorableItem;
import cofh.lib.util.helpers.MathHelper;
import cofh.lib.util.helpers.StringHelper;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;

import static cofh.core.util.helpers.FluidHelper.addPotionTooltip;
import static cofh.core.util.helpers.FluidHelper.hasPotionTag;
import static cofh.lib.item.ContainerType.FLUID;
import static cofh.lib.util.constants.NBTTags.TAG_FLUID;
import static cofh.lib.util.helpers.ItemHelper.areItemStacksEqualIgnoreTags;
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

    @Override
    protected void tooltipDelegate(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {

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

    protected void potionTooltip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn, List<EffectInstance> effects) {

        potionTooltip(stack, worldIn, tooltip, flagIn, effects, 1.0F);
    }

    protected void potionTooltip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn, List<EffectInstance> effects, float durationFactor) {

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
    public boolean showDurabilityBar(ItemStack stack) {

        return !isCreative(stack, FLUID) && getFluidAmount(stack) > 0;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {

        return MathHelper.clamp(1.0D - getFluidAmount(stack) / (double) getCapacity(stack), 0.0D, 1.0D);
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {

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
    public int getColor(ItemStack stack, int tintIndex) {

        // TODO: Add base recoloration.
        //        if (tintIndex == 0) {
        //            return 0xD78D5B;
        //        }
        if (tintIndex == 1) {
            return getFluidAmount(stack) > 0 ? getFluid(stack).getFluid().getAttributes().getColor(getFluid(stack)) : 0xFFFFFF;
        }
        return 0xFFFFFF;
    }
    // endregion
}
