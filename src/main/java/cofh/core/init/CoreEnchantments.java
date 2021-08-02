package cofh.core.init;

import cofh.core.enchantment.HoldingEnchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.item.*;

import static cofh.core.CoFHCore.ENCHANTMENTS;
import static cofh.lib.util.references.CoreIDs.ID_HOLDING;

public class CoreEnchantments {

    private CoreEnchantments() {

    }

    public static void register() {

        Types.register();
    }

    public static void registerHoldingEnchantment() {

        ENCHANTMENTS.register(ID_HOLDING, HoldingEnchantment::new);
    }

    public static class Types {

        public static void register() {

            ENCHANTABLE = EnchantmentType.create("ENCHANTABLE", (item -> item.getItemEnchantability() > 0));
            HOE = EnchantmentType.create("HOE", (item -> item instanceof HoeItem));
            PICKAXE_OR_SHOVEL = EnchantmentType.create("PICKAXE_OR_SHOVEL", (item -> item instanceof PickaxeItem || item instanceof ShovelItem));
            SWORD_OR_AXE = EnchantmentType.create("SWORD_OR_AXE", (item -> item instanceof SwordItem || item instanceof AxeItem));
            SWORD_OR_AXE_OR_CROSSBOW = EnchantmentType.create("SWORD_OR_AXE_OR_CROSSBOW", (item -> item instanceof SwordItem || item instanceof AxeItem || item instanceof CrossbowItem));
        }

        public static EnchantmentType ENCHANTABLE;
        public static EnchantmentType HOE;
        public static EnchantmentType PICKAXE_OR_SHOVEL;
        public static EnchantmentType SWORD_OR_AXE;
        public static EnchantmentType SWORD_OR_AXE_OR_CROSSBOW;

    }

}
