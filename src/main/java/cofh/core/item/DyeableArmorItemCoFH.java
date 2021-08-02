package cofh.core.item;

import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.IDyeableArmorItem;

public class DyeableArmorItemCoFH extends ArmorItemCoFH implements IDyeableArmorItem {

    public DyeableArmorItemCoFH(IArmorMaterial materialIn, EquipmentSlotType slot, Properties builder) {

        super(materialIn, slot, builder);
    }

}
