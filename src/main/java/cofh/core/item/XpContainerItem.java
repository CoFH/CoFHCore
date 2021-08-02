package cofh.core.item;

import cofh.lib.fluid.FluidContainerItemWrapper;
import cofh.lib.fluid.IFluidContainerItem;
import cofh.lib.item.ContainerType;
import cofh.lib.util.Utils;
import cofh.lib.util.helpers.MathHelper;
import cofh.lib.xp.IXpContainerItem;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nullable;
import java.util.List;

import static cofh.lib.item.ContainerType.XP;
import static cofh.lib.util.constants.Constants.MB_PER_XP;
import static cofh.lib.util.constants.Constants.RGB_DURABILITY_XP;
import static cofh.lib.util.constants.NBTTags.TAG_FLUID;
import static cofh.lib.util.helpers.ItemHelper.areItemStacksEqualIgnoreTags;
import static cofh.lib.util.helpers.StringHelper.*;
import static cofh.lib.util.helpers.XpHelper.*;
import static cofh.lib.util.references.CoreReferences.FLUID_XP;

/**
 * This class does not set an XP Timer on the player entity.
 */
public class XpContainerItem extends ItemCoFH implements IXpContainerItem, IFluidContainerItem {

    protected int xpCapacity;

    public XpContainerItem(Properties builder, int xpCapacity) {

        super(builder);
        this.xpCapacity = xpCapacity;

        setEnchantability(5);
    }

    @Override
    protected void tooltipDelegate(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {

        tooltip.add(getTextComponent(localize("info.cofh.amount") + ": " + getScaledNumber(getStoredXp(stack)) + " / " + getScaledNumber(getCapacityXP(stack))));
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

        return !isCreative(stack, XP) && getStoredXp(stack) > 0;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {

        return MathHelper.clamp(1.0D - getStoredXp(stack) / (double) getCapacityXP(stack), 0.0D, 1.0D);
    }

    @Override
    public int getRGBDurabilityForDisplay(ItemStack stack) {

        return RGB_DURABILITY_XP;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {

        ItemStack stack = player.getHeldItem(hand);
        if (Utils.isFakePlayer(player)) {
            return ActionResult.resultFail(stack);
        }
        int xp;
        int curLevel = player.experienceLevel;

        if (player.isSecondaryUseActive()) {
            if (getExtraPlayerXp(player) > 0) {
                xp = Math.min(getTotalXpForLevel(player.experienceLevel + 1) - getTotalXpForLevel(player.experienceLevel) - getExtraPlayerXp(player), getStoredXp(stack));
            } else {
                xp = Math.min(getTotalXpForLevel(player.experienceLevel + 1) - getTotalXpForLevel(player.experienceLevel), getStoredXp(stack));
            }
            setPlayerXP(player, getPlayerXP(player) + xp);
            if (player.experienceLevel < curLevel + 1 && getPlayerXP(player) >= getTotalXpForLevel(curLevel + 1)) {
                setPlayerLevel(player, curLevel + 1);
            }
            modifyXp(stack, -xp);
        } else {
            if (getExtraPlayerXp(player) > 0) {
                xp = Math.min(getExtraPlayerXp(player), getSpaceXP(stack));
                setPlayerXP(player, getPlayerXP(player) - xp);
                if (player.experienceLevel < curLevel) {
                    setPlayerLevel(player, curLevel);
                }
                modifyXp(stack, xp);
            } else if (player.experienceLevel > 0) {
                xp = Math.min(getTotalXpForLevel(player.experienceLevel) - getTotalXpForLevel(player.experienceLevel - 1), getSpaceXP(stack));
                setPlayerXP(player, getPlayerXP(player) - xp);
                if (player.experienceLevel < curLevel - 1) {
                    setPlayerLevel(player, curLevel - 1);
                }
                modifyXp(stack, xp);
            }
        }
        return ActionResult.resultSuccess(stack);
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {

        return new FluidContainerItemWrapper(stack, this);
    }

    // region IXpContainerItem
    public int getCapacityXP(ItemStack stack) {

        return getMaxStored(stack, xpCapacity);
    }
    // endregion

    // region IFluidContainerItem
    @Override
    public FluidStack getFluid(ItemStack container) {

        int xp = getStoredXp(container);
        return xp > 0 ? new FluidStack(FLUID_XP, xp * MB_PER_XP) : FluidStack.EMPTY;
    }

    @Override
    public int getCapacity(ItemStack container) {

        return getCapacityXP(container) * MB_PER_XP;
    }

    @Override
    public int fill(ItemStack container, FluidStack resource, IFluidHandler.FluidAction action) {

        if (resource.isEmpty() || !isFluidValid(container, resource)) {
            return 0;
        }
        int xp = getStoredXp(container);
        int filled = Math.min(getCapacityXP(container) - xp, resource.getAmount() / MB_PER_XP);

        if (action.execute()) {
            modifyXp(container, filled);
        }
        return filled * MB_PER_XP;
    }

    @Override
    public FluidStack drain(ItemStack container, int maxDrain, IFluidHandler.FluidAction action) {

        int xp = getStoredXp(container);
        if (xp <= 0) {
            return FluidStack.EMPTY;
        }
        int drained = Math.min(xp, maxDrain / MB_PER_XP);

        if (action.execute() && !isCreative(container, ContainerType.FLUID)) {
            modifyXp(container, -drained);
        }
        return new FluidStack(FLUID_XP, drained * MB_PER_XP);
    }
    // endregion
}