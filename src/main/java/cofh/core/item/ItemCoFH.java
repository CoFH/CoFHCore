package cofh.core.item;

import cofh.core.init.CoreConfig;
import cofh.lib.item.ICoFHItem;
import cofh.lib.util.helpers.SecurityHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;

import static cofh.lib.util.constants.Constants.TRUE;
import static cofh.lib.util.helpers.StringHelper.getTextComponent;
import static net.minecraft.util.text.TextFormatting.*;

public class ItemCoFH extends Item implements ICoFHItem {

    protected BooleanSupplier showInGroups = TRUE;
    protected BooleanSupplier showEnchantEffect = TRUE;

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

    protected void tooltipDelegate(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {

    }

    @Override
    public String getCreatorModId(ItemStack itemStack) {

        return modId == null || modId.isEmpty() ? super.getCreatorModId(itemStack) : modId;
    }

    @Override
    public void fillItemCategory(ItemGroup group, NonNullList<ItemStack> items) {

        if (!showInGroups.getAsBoolean()) {
            return;
        }
        super.fillItemCategory(group, items);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {

        List<ITextComponent> additionalTooltips = new ArrayList<>();
        tooltipDelegate(stack, worldIn, additionalTooltips, flagIn);

        if (SecurityHelper.isItemClaimable(stack)) {
            tooltip.add(getTextComponent("info.cofh.claimable").withStyle(GREEN).withStyle(ITALIC));
        }
        if (!additionalTooltips.isEmpty()) {
            if (Screen.hasShiftDown() || CoreConfig.alwaysShowDetails) {
                tooltip.addAll(additionalTooltips);
            } else if (CoreConfig.holdShiftForDetails) {
                tooltip.add(getTextComponent("info.cofh.hold_shift_for_details").withStyle(GRAY));
            }
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean isFoil(ItemStack stack) {

        return showEnchantEffect.getAsBoolean() && stack.isEnchanted();
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
    public int getBurnTime(ItemStack itemStack) {

        return burnTime;
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
