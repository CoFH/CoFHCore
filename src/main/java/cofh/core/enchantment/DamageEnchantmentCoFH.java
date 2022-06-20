package cofh.core.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.DamageEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public abstract class DamageEnchantmentCoFH extends EnchantmentCoFH {

    protected DamageEnchantmentCoFH(Rarity rarityIn, EnchantmentCategory typeIn, EquipmentSlot[] slots) {

        super(rarityIn, typeIn, slots);
    }

    @Override
    public int getMinCost(int level) {

        return 10 + (level - 1) * 8;
    }

    @Override
    protected int maxDelegate(int level) {

        return getMinCost(level) + 20;
    }

    @Override
    public boolean checkCompatibility(Enchantment ench) {

        return super.checkCompatibility(ench) && !(ench instanceof DamageEnchantment) && !(ench instanceof DamageEnchantmentCoFH);
    }

    public static float getExtraDamage(int level) {

        return level * 2.5F;
    }

}
