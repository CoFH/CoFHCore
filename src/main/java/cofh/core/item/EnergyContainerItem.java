package cofh.core.item;

import cofh.lib.api.item.IEnergyContainerItem;
import cofh.lib.energy.EnergyContainerItemWrapper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nullable;
import java.util.List;

import static cofh.lib.api.ContainerType.ENERGY;
import static cofh.lib.util.Constants.RGB_DURABILITY_FLUX;
import static cofh.lib.util.helpers.StringHelper.*;

public abstract class EnergyContainerItem extends ItemCoFH implements IEnergyContainerItem {

    protected int maxEnergy;
    protected int extract;
    protected int receive;

    protected EnergyContainerItem(Properties builder, int maxEnergy, int extract, int receive) {

        super(builder);
        this.maxEnergy = maxEnergy;
        this.extract = extract;
        this.receive = receive;

        setEnchantability(5);
    }

    public EnergyContainerItem(Properties builder, int maxEnergy, int maxTransfer) {

        this(builder, maxEnergy, maxTransfer, maxTransfer);
    }

    public EnergyContainerItem setMaxEnergy(int maxEnergy) {

        this.maxEnergy = maxEnergy;
        return this;
    }

    @Override
    protected void tooltipDelegate(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {

        boolean creative = isCreative(stack, ENERGY);
        tooltip.add(getTextComponent(localize("info.cofh.energy") + ": "
                + (creative ?
                localize("info.cofh.infinite") :
                getScaledNumber(getEnergyStored(stack)) + " / " + getScaledNumber(getMaxEnergyStored(stack)) + " RF")));

        addEnergyTooltip(stack, worldIn, tooltip, flagIn, getExtract(stack), getReceive(stack), creative);
    }

    @Override
    public boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack) {

        return !(newStack.getItem() == oldStack.getItem()) || (getEnergyStored(oldStack) > 0 != getEnergyStored(newStack) > 0);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {

        return super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged) && (slotChanged || getEnergyStored(oldStack) > 0 != getEnergyStored(newStack) > 0);
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {

        return !isCreative(stack, ENERGY) && getEnergyStored(stack) > 0;
    }

    @Override
    public int getBarColor(ItemStack stack) {

        return RGB_DURABILITY_FLUX;
    }

    @Override
    public int getBarWidth(ItemStack stack) {

        if (stack.getTag() == null) {
            return 0;
        }
        return (int) Math.round(13.0D * getEnergyStored(stack) / (double) getMaxEnergyStored(stack));
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {

        return new EnergyContainerItemWrapper(stack, this, getEnergyCapability());
    }

    // region IEnergyContainerItem
    @Override
    public int getExtract(ItemStack container) {

        return extract;
    }

    @Override
    public int getReceive(ItemStack container) {

        return receive;
    }

    @Override
    public int getMaxEnergyStored(ItemStack container) {

        return getMaxStored(container, maxEnergy);
    }
    // endregion
}
