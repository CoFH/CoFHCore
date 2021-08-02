package cofh.lib.item.impl;

import net.minecraft.item.IDyeableArmorItem;
import net.minecraft.util.ResourceLocation;

public class DyeableHorseArmorItemCoFH extends HorseArmorItemCoFH implements IDyeableArmorItem {

    public DyeableHorseArmorItemCoFH(int protection, String texture, Properties builder) {

        super(protection, texture, builder);
    }

    public DyeableHorseArmorItemCoFH(int protection, ResourceLocation texture, Properties builder) {

        super(protection, texture, builder);
    }

}
