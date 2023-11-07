package cofh.lib.common.item;

import cofh.lib.api.item.ICoFHItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.Tier;

public class ShovelItemCoFH extends ShovelItem implements ICoFHItem {

    public ShovelItemCoFH(Tier tier, float attackDamageIn, float attackSpeedIn, Properties builder) {

        super(tier, attackDamageIn, attackSpeedIn, builder);
    }

    // region DISPLAY
    protected String modId = "";

    @Override
    public ShovelItemCoFH setModId(String modId) {

        this.modId = modId;
        return this;
    }

    @Override
    public String getCreatorModId(ItemStack itemStack) {

        return modId == null || modId.isEmpty() ? super.getCreatorModId(itemStack) : modId;
    }
    // endregion
}
