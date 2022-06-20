package cofh.core.content.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeableLeatherItem;

public class DyeableHorseArmorItemCoFH extends HorseArmorItemCoFH implements DyeableLeatherItem {

    public DyeableHorseArmorItemCoFH(int protection, String texture, Properties builder) {

        super(protection, texture, builder);
    }

    public DyeableHorseArmorItemCoFH(int protection, ResourceLocation texture, Properties builder) {

        super(protection, texture, builder);
    }

}
