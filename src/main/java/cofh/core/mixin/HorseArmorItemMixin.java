package cofh.core.mixin;

import net.minecraft.world.item.HorseArmorItem;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;

@Mixin (HorseArmorItem.class)
public abstract class HorseArmorItemMixin extends Item {

    public HorseArmorItemMixin(Properties pProperties) {

        super(pProperties);
    }

    @Override
    public int getEnchantmentValue() {

        return 15;
    }

}