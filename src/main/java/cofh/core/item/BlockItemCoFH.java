package cofh.core.item;

import cofh.core.init.CoreConfig;
import cofh.lib.item.ICoFHItem;
import net.minecraft.block.Block;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

import static cofh.lib.util.constants.Constants.TRUE;
import static cofh.lib.util.helpers.StringHelper.getTextComponent;
import static net.minecraft.util.text.TextFormatting.GRAY;

public class BlockItemCoFH extends BlockItem implements ICoFHItem {

    protected BooleanSupplier showInGroups = TRUE;
    protected BooleanSupplier showEnchantEffect = TRUE;

    protected int burnTime = -1;
    protected int enchantability;

    protected Supplier<ItemGroup> displayGroup;

    public BlockItemCoFH(Block blockIn, Properties builder) {

        super(blockIn, builder);
    }

    public BlockItemCoFH setEnchantability(int enchantability) {

        this.enchantability = enchantability;
        return this;
    }

    public BlockItemCoFH setBurnTime(int burnTime) {

        this.burnTime = burnTime;
        return this;
    }

    public BlockItemCoFH setDisplayGroup(Supplier<ItemGroup> displayGroup) {

        this.displayGroup = displayGroup;
        return this;
    }

    public BlockItemCoFH setShowInGroups(BooleanSupplier showInGroups) {

        this.showInGroups = showInGroups;
        return this;
    }

    protected void tooltipDelegate(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {

    }

    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {

        if (!showInGroups.getAsBoolean() || getBlock() == null) {
            return;
        }
        super.fillItemGroup(group, items);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {

        List<ITextComponent> additionalTooltips = new ArrayList<>();
        tooltipDelegate(stack, worldIn, additionalTooltips, flagIn);

        if (!additionalTooltips.isEmpty()) {
            if (Screen.hasShiftDown() || CoreConfig.alwaysShowDetails) {
                tooltip.addAll(additionalTooltips);
            } else if (CoreConfig.holdShiftForDetails) {
                tooltip.add(getTextComponent("info.cofh.hold_shift_for_details").mergeStyle(GRAY));
            }
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean hasEffect(ItemStack stack) {

        return showEnchantEffect.getAsBoolean() && stack.isEnchanted();
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {

        return enchantability > 0;
    }

    @Override
    public int getItemEnchantability() {

        return enchantability;
    }

    @Override
    public int getBurnTime(ItemStack itemStack) {

        return burnTime;
    }

    @Override
    protected boolean isInGroup(ItemGroup group) {

        return group == ItemGroup.SEARCH || getCreativeTabs().stream().anyMatch(tab -> tab == group);
    }

    @Override
    public Collection<ItemGroup> getCreativeTabs() {

        return displayGroup != null && displayGroup.get() != null ? Collections.singletonList(displayGroup.get()) : super.getCreativeTabs();
    }

}
