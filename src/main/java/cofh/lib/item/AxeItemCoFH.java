package cofh.lib.item;

import cofh.lib.api.item.ICoFHItem;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Supplier;

import static cofh.lib.util.Constants.TRUE;

public class AxeItemCoFH extends AxeItem implements ICoFHItem {

    public AxeItemCoFH(Tier tier, float attackDamageIn, float attackSpeedIn, Properties builder) {

        super(tier, attackDamageIn, attackSpeedIn, builder);
    }

    // region DISPLAY
    protected Supplier<CreativeModeTab> displayGroup;
    protected Supplier<Boolean> showInGroups = TRUE;
    protected String modId = "";

    @Override
    public AxeItemCoFH setDisplayGroup(Supplier<CreativeModeTab> displayGroup) {

        this.displayGroup = displayGroup;
        return this;
    }

    @Override
    public AxeItemCoFH setModId(String modId) {

        this.modId = modId;
        return this;
    }

    @Override
    public AxeItemCoFH setShowInGroups(Supplier<Boolean> showInGroups) {

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
