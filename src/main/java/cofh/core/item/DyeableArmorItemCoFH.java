package cofh.core.item;

import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.Item;

public class DyeableArmorItemCoFH extends ArmorItemCoFH implements DyeableLeatherItem {

    public DyeableArmorItemCoFH(ArmorMaterial pMaterial, ArmorItem.Type pType, Item.Properties pProperties) {

        super(pMaterial, pType, pProperties);
    }

}
