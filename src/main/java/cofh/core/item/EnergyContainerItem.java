package cofh.core.item;

import cofh.lib.energy.EnergyContainerItemWrapper;
import cofh.lib.energy.IEnergyContainerItem;
import cofh.lib.util.helpers.MathHelper;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nullable;
import java.util.List;

import static cofh.lib.item.ContainerType.ENERGY;
import static cofh.lib.util.constants.Constants.RGB_DURABILITY_FLUX;
import static cofh.lib.util.helpers.StringHelper.*;

public class EnergyContainerItem extends ItemCoFH implements IEnergyContainerItem {

    protected int maxEnergy;
    protected int extract;
    protected int receive;

    private EnergyContainerItem(Properties builder, int maxEnergy, int extract, int receive) {

        super(builder);
        this.maxEnergy = maxEnergy;
        this.extract = extract;
        this.receive = receive;

        setEnchantability(5);
    }

    public EnergyContainerItem(Properties builder, int maxEnergy, int maxTransfer) {

        this(builder, maxEnergy, maxTransfer, maxTransfer);
    }

    @Override
    protected void tooltipDelegate(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {

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
    public boolean showDurabilityBar(ItemStack stack) {

        return !isCreative(stack, ENERGY) && getEnergyStored(stack) > 0;
    }

    @Override
    public int getRGBDurabilityForDisplay(ItemStack stack) {

        return RGB_DURABILITY_FLUX;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {

        if (stack.getTag() == null) {
            return 0;
        }
        return MathHelper.clamp(1.0D - getEnergyStored(stack) / (double) getMaxEnergyStored(stack), 0.0D, 1.0D);
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {

        return new EnergyContainerItemWrapper(stack, this);
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
