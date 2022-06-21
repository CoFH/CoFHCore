package cofh.core.content.item;

import cofh.core.content.fluid.FluidContainerItemWrapper;
import cofh.lib.api.item.IFluidContainerItem;
import cofh.core.content.xp.IXpContainerItem;
import cofh.core.util.helpers.FluidHelper;
import cofh.lib.api.ContainerType;
import cofh.lib.util.Utils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nullable;
import java.util.List;

import static cofh.core.util.helpers.ItemHelper.areItemStacksEqualIgnoreTags;
import static cofh.core.util.helpers.XpHelper.*;
import static cofh.lib.api.ContainerType.EXPERIENCE;
import static cofh.lib.util.Constants.MB_PER_XP;
import static cofh.lib.util.Constants.RGB_DURABILITY_XP;
import static cofh.lib.util.constants.NBTTags.TAG_FLUID;
import static cofh.lib.util.helpers.StringHelper.*;

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
    protected void tooltipDelegate(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {

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
    public boolean isBarVisible(ItemStack stack) {

        return !isCreative(stack, EXPERIENCE) && getStoredXp(stack) > 0;
    }

    @Override
    public int getBarWidth(ItemStack stack) {

        return (int) Math.round(13.0D * getStoredXp(stack) / (double) getCapacityXP(stack));
    }

    @Override
    public int getBarColor(ItemStack stack) {

        return RGB_DURABILITY_XP;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {

        ItemStack stack = player.getItemInHand(hand);
        if (Utils.isFakePlayer(player)) {
            return InteractionResultHolder.fail(stack);
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
        return InteractionResultHolder.success(stack);
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {

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


        // TODO: FIXME
        int xp = getStoredXp(container);
        return /*xp > 0 ? new FluidStack(EXPERIENCE.get(), xp * MB_PER_XP) :*/ FluidStack.EMPTY;
    }

    @Override
    public boolean isFluidValid(ItemStack container, FluidStack resource) {

        return FluidHelper.IS_XP.test(resource);
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
        return FluidStack.EMPTY;
        // TODO: FIXME
        // return new FluidStack(EXPERIENCE.get(), drained * MB_PER_XP);
    }
    // endregion
}
