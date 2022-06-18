package cofh.core.item;

import cofh.core.config.CoreClientConfig;
import cofh.lib.item.ICoFHItem;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.BooleanSupplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static cofh.lib.util.constants.Constants.TRUE;
import static cofh.lib.util.helpers.StringHelper.getTextComponent;
import static net.minecraft.ChatFormatting.*;

public class ItemCoFH extends Item implements ICoFHItem {

    protected static final Random random = new Random();

    protected BooleanSupplier showInGroups = TRUE;

    protected int burnTime = -1;
    protected int enchantability;
    protected String modId = "";

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

    public ItemCoFH setShowInGroups(BooleanSupplier showInGroups) {

        this.showInGroups = showInGroups;
        return this;
    }

    public ItemCoFH setModId(String modId) {

        this.modId = modId;
        return this;
    }

    protected void tooltipDelegate(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {

    }

    @Override
    public String getCreatorModId(ItemStack itemStack) {

        return modId == null || modId.isEmpty() ? super.getCreatorModId(itemStack) : modId;
    }

    @Override
    public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {

        if (!showInGroups.getAsBoolean()) {
            return;
        }
        super.fillItemCategory(group, items);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {

        List<Component> additionalTooltips = new ArrayList<>();
        tooltipDelegate(stack, worldIn, additionalTooltips, flagIn);

        if (SecurityHelper.isItemClaimable(stack)) {
            tooltip.add(getTextComponent("info.cofh.claimable").withStyle(GREEN).withStyle(ITALIC));
        }
        if (!additionalTooltips.isEmpty()) {
            if (Screen.hasShiftDown() || CoreClientConfig.alwaysShowDetails) {
                tooltip.addAll(additionalTooltips);
            } else if (CoreClientConfig.holdShiftForDetails) {
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

    //    @Override
    //    public String getHighlightTip(ItemStack stack, String displayName) {
    //
    //        if (isActive(stack)) {
    //            return "";
    //        }
    //        return displayName;
    //    }
    //
    //    // region HELPERS
    //    public static boolean isActive(ItemStack stack) {
    //
    //        return stack.hasTag() && stack.getTag().getBoolean(TAG_ACTIVE);
    //    }
    //
    //    public static void clearActive(ItemStack stack) {
    //
    //        if (stack.hasTag()) {
    //            stack.getTag().remove(TAG_ACTIVE);
    //        }
    //    }
    //    // endregion
}
