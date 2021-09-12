package cofh.lib.item.impl;

import cofh.lib.item.ICoFHItem;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.util.NonNullList;

import java.util.function.BooleanSupplier;

import static cofh.lib.util.constants.Constants.TRUE;

public class SwordItemCoFH extends SwordItem implements ICoFHItem {

    protected BooleanSupplier showInGroups = TRUE;

    public SwordItemCoFH(IItemTier tier, int attackDamageIn, float attackSpeedIn, Properties builder) {

        super(tier, attackDamageIn, attackSpeedIn, builder);
    }

    public SwordItemCoFH setShowInGroups(BooleanSupplier showInGroups) {

        this.showInGroups = showInGroups;
        return this;
    }

    @Override
    public void fillItemCategory(ItemGroup group, NonNullList<ItemStack> items) {

        if (!showInGroups.getAsBoolean()) {
            return;
        }
        super.fillItemCategory(group, items);
    }

}
