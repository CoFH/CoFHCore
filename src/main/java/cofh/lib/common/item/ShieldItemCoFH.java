package cofh.lib.common.item;

import cofh.lib.api.item.ICoFHItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;

public class ShieldItemCoFH extends ShieldItem implements ICoFHItem {

    protected int enchantability = 1;

    public ShieldItemCoFH(Properties builder) {

        super(builder);
    }

    public ShieldItemCoFH setEnchantability(int enchantability) {

        this.enchantability = enchantability;
        return this;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {

        return enchantability > 0;
    }

    @Override
    public int getEnchantmentValue(ItemStack stack) {

        return enchantability;
    }

    // region DISPLAY
    protected String modId = "";

    @Override
    public ICoFHItem setModId(String modId) {

        this.modId = modId;
        return this;
    }

    @Override
    public String getCreatorModId(ItemStack itemStack) {

        return modId == null || modId.isEmpty() ? super.getCreatorModId(itemStack) : modId;
    }
    // endregion
}
