package cofh.lib.item;

import cofh.lib.api.item.ICoFHItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TridentItem;

public class TridentItemCoFH extends TridentItem implements ICoFHItem {

    protected int enchantability = 1;

    public TridentItemCoFH(Properties builder) {

        super(builder);
    }

    public TridentItemCoFH(Tier tier, Properties builder) {

        super(builder);
        enchantability = tier.getEnchantmentValue();
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {

        return enchantability > 0;
    }

    @Override
    public int getEnchantmentValue() {

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
