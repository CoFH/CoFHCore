package cofh.lib.common.item;

import cofh.lib.api.item.ICoFHItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeableHorseArmorItem;
import net.minecraft.world.item.ItemStack;

public class DyeableHorseArmorItemCoFH extends DyeableHorseArmorItem implements ICoFHItem {

    protected int enchantability = 1;

    public DyeableHorseArmorItemCoFH(int protection, String texture, Properties builder) {

        super(protection, texture, builder);
    }

    public DyeableHorseArmorItemCoFH(int protection, ResourceLocation texture, Properties builder) {

        super(protection, texture, builder);
    }

    public DyeableHorseArmorItemCoFH setEnchantability(int enchantability) {

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
    public DyeableHorseArmorItemCoFH setModId(String modId) {

        this.modId = modId;
        return this;
    }

    @Override
    public String getCreatorModId(ItemStack itemStack) {

        return modId == null || modId.isEmpty() ? super.getCreatorModId(itemStack) : modId;
    }
    // endregion
}
