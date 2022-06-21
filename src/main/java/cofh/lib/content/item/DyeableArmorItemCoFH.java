package cofh.lib.content.item;

import cofh.core.content.item.ArmorItemCoFH;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.DyeableLeatherItem;

public class DyeableArmorItemCoFH extends ArmorItemCoFH implements DyeableLeatherItem {

    public DyeableArmorItemCoFH(ArmorMaterial materialIn, EquipmentSlot slot, Properties builder) {

        super(materialIn, slot, builder);
    }

}
