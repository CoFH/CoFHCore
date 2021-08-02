package cofh.lib.item.impl;

import cofh.lib.item.ICoFHItem;
import net.minecraft.item.HorseArmorItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.BooleanSupplier;

import static cofh.lib.util.constants.Constants.TRUE;

public class HorseArmorItemCoFH extends HorseArmorItem implements ICoFHItem {

    protected BooleanSupplier showInGroups = TRUE;
    protected BooleanSupplier showEnchantEffect = TRUE;

    protected int enchantability;

    public HorseArmorItemCoFH(int protection, String texture, Properties builder) {

        super(protection, texture, builder);
    }

    public HorseArmorItemCoFH(int protection, ResourceLocation texture, Properties builder) {

        super(protection, texture, builder);
    }

    public HorseArmorItemCoFH setEnchantability(int enchantability) {

        this.enchantability = enchantability;
        return this;
    }

    public HorseArmorItemCoFH setShowInGroups(BooleanSupplier showInGroups) {

        this.showInGroups = showInGroups;
        return this;
    }

    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {

        if (!showInGroups.getAsBoolean()) {
            return;
        }
        super.fillItemGroup(group, items);
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

}
