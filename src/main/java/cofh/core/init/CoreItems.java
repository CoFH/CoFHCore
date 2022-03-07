package cofh.core.init;

import cofh.core.item.ItemCoFH;
import cofh.lib.item.impl.DyeableHorseArmorItemCoFH;
import cofh.lib.item.impl.HorseArmorItemCoFH;
import cofh.lib.item.impl.ShearsItemCoFH;
import cofh.lib.item.impl.ShieldItemCoFH;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;

import static cofh.core.CoFHCore.ITEMS;
import static cofh.core.init.CoreFlags.FLAG_ECTOPLASM;
import static cofh.core.init.CoreFlags.getFlag;
import static cofh.lib.util.references.CoreIDs.ID_ECTOPLASM;

public class CoreItems {

    private CoreItems() {

    }

    public static void register() {

        ITEMS.register(ID_ECTOPLASM, () -> new ItemCoFH(new Item.Properties().tab(CreativeModeTab.TAB_BREWING)).setShowInGroups(getFlag(FLAG_ECTOPLASM)));
    }

    public static void registerHorseArmorOverrides() {

        ITEMS.register("minecraft:iron_horse_armor", () -> new HorseArmorItemCoFH(5, "iron", new Item.Properties().stacksTo(1).tab(CreativeModeTab.TAB_MISC)).setEnchantability(9));
        ITEMS.register("minecraft:golden_horse_armor", () -> new HorseArmorItemCoFH(7, "gold", new Item.Properties().stacksTo(1).tab(CreativeModeTab.TAB_MISC)).setEnchantability(25));
        ITEMS.register("minecraft:diamond_horse_armor", () -> new HorseArmorItemCoFH(11, "diamond", new Item.Properties().stacksTo(1).tab(CreativeModeTab.TAB_MISC)).setEnchantability(10));
        ITEMS.register("minecraft:leather_horse_armor", () -> new DyeableHorseArmorItemCoFH(3, "leather", new Item.Properties().stacksTo(1).tab(CreativeModeTab.TAB_MISC)).setEnchantability(15));
    }

    public static void registerShearsOverride() {

        ITEMS.register("minecraft:shears", () -> new ShearsItemCoFH(new Item.Properties().durability(238).tab(CreativeModeTab.TAB_TOOLS)));
    }

    public static void registerShieldOverride() {

        ITEMS.register("minecraft:shield", () -> new ShieldItemCoFH(new Item.Properties().durability(336).tab(CreativeModeTab.TAB_COMBAT)).setEnchantability(15));
    }

}
