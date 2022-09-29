package cofh.core.item;

import cofh.core.config.CoreClientConfig;
import cofh.lib.api.item.ICoFHItem;
import cofh.lib.util.helpers.SecurityHelper;
import com.google.common.collect.Sets;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ToolAction;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static cofh.lib.util.Constants.TRUE;
import static cofh.lib.util.helpers.StringHelper.getTextComponent;
import static net.minecraft.ChatFormatting.*;

public class ItemCoFH extends Item implements ICoFHItem {

    protected int burnTime = -1;
    protected int enchantability;

    public ItemCoFH(Properties builder) {

        super(builder);
    }

    public ItemCoFH setEnchantability(int enchantability) {

        this.enchantability = enchantability;
        return this;
    }

    public ItemCoFH setBurnTime(int burnTime) {

        this.burnTime = burnTime;
        return this;
    }

    protected void tooltipDelegate(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {

    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {

        List<Component> additionalTooltips = new ArrayList<>();
        tooltipDelegate(stack, worldIn, additionalTooltips, flagIn);

        if (SecurityHelper.isItemClaimable(stack)) {
            tooltip.add(getTextComponent("info.cofh.claimable").withStyle(GREEN).withStyle(ITALIC));
        }
        if (!additionalTooltips.isEmpty()) {
            if (Screen.hasShiftDown() || CoreClientConfig.alwaysShowDetails.get()) {
                tooltip.addAll(additionalTooltips);
            } else if (CoreClientConfig.holdShiftForDetails.get()) {
                tooltip.add(getTextComponent("info.cofh.hold_shift_for_details").withStyle(GRAY));
            }
        }
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {

        return getItemEnchantability(stack) > 0;
    }

    @Override
    public int getItemEnchantability(ItemStack stack) {

        return enchantability;
    }

    @Override
    public int getBurnTime(ItemStack itemStack, RecipeType<?> recipeType) {

        return burnTime;
    }

    protected static Set<ToolAction> toolActions(ToolAction... actions) {

        return Stream.of(actions).collect(Collectors.toCollection(Sets::newIdentityHashSet));
    }

    // region DISPLAY
    protected Supplier<CreativeModeTab> displayGroup;
    protected Supplier<Boolean> showInGroups = TRUE;
    protected String modId = "";

    @Override
    public ItemCoFH setDisplayGroup(Supplier<CreativeModeTab> displayGroup) {

        this.displayGroup = displayGroup;
        return this;
    }

    @Override
    public ItemCoFH setModId(String modId) {

        this.modId = modId;
        return this;
    }

    @Override
    public ItemCoFH setShowInGroups(Supplier<Boolean> showInGroups) {

        this.showInGroups = showInGroups;
        return this;
    }

    @Override
    public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {

        if (!showInGroups.get() || displayGroup != null && displayGroup.get() != null && displayGroup.get() != group) {
            return;
        }
        super.fillItemCategory(group, items);
    }

    @Override
    public Collection<CreativeModeTab> getCreativeTabs() {

        return displayGroup != null && displayGroup.get() != null ? Collections.singletonList(displayGroup.get()) : super.getCreativeTabs();
    }

    @Override
    public String getCreatorModId(ItemStack itemStack) {

        return modId == null || modId.isEmpty() ? super.getCreatorModId(itemStack) : modId;
    }
    // endregion
}
