package cofh.core.init;

import cofh.core.enchantment.HoldingEnchantment;
import cofh.lib.enchantment.EnchantmentCoFH;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraftforge.registries.RegistryObject;

import static cofh.core.CoFHCore.ENCHANTMENTS;
import static cofh.core.util.references.CoreIDs.ID_HOLDING;

public class CoreEnchantments {

    private CoreEnchantments() {

    }

    public static void register() {

        Types.register();
    }

    public static class Types {

        public static void register() {

            ENCHANTABLE = EnchantmentCategory.create("ENCHANTABLE", (item -> item.getEnchantmentValue() > 0));
            HOE = EnchantmentCategory.create("HOE", (item -> item instanceof HoeItem));
            PICKAXE_OR_SHOVEL = EnchantmentCategory.create("PICKAXE_OR_SHOVEL", (item -> item instanceof PickaxeItem || item instanceof ShovelItem));
            SWORD_OR_AXE = EnchantmentCategory.create("SWORD_OR_AXE", (item -> item instanceof SwordItem || item instanceof AxeItem));
            SWORD_OR_AXE_OR_CROSSBOW = EnchantmentCategory.create("SWORD_OR_AXE_OR_CROSSBOW", (item -> item instanceof SwordItem || item instanceof AxeItem || item instanceof CrossbowItem));
        }

        public static EnchantmentCategory ENCHANTABLE;
        public static EnchantmentCategory HOE;
        public static EnchantmentCategory PICKAXE_OR_SHOVEL;
        public static EnchantmentCategory SWORD_OR_AXE;
        public static EnchantmentCategory SWORD_OR_AXE_OR_CROSSBOW;

    }

    public static final RegistryObject<EnchantmentCoFH> HOLDING = ENCHANTMENTS.register(ID_HOLDING, HoldingEnchantment::new);

}
