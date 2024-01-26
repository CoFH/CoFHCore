package cofh.core.mixin;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ShieldItem;
import org.spongepowered.asm.mixin.Mixin;

@Mixin (ShieldItem.class)
public abstract class ShieldItemMixin extends Item {

    public ShieldItemMixin(Properties pProperties) {

        super(pProperties);
    }

    @Override
    public int getEnchantmentValue() {

        return 1;
    }

}