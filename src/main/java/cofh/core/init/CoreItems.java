package cofh.core.init;

import cofh.lib.common.item.DyeableHorseArmorItemCoFH;
import cofh.lib.common.item.HorseArmorItemCoFH;
import cofh.lib.common.item.ShearsItemCoFH;
import cofh.lib.common.item.ShieldItemCoFH;

import static cofh.core.CoFHCore.ITEMS;
import static cofh.lib.util.Utils.itemProperties;

public class CoreItems {

    private CoreItems() {

    }

    public static void register() {

        // ITEMS.register("dev_stick", () -> new DevStickItem(properties().stacksTo(1).fireResistant()).setShowInGroups(FALSE));
    }

    public static void registerHorseArmorOverrides() {

        ITEMS.register("minecraft:iron_horse_armor", () -> new HorseArmorItemCoFH(5, "iron", itemProperties().stacksTo(1)).setEnchantability(9));
        ITEMS.register("minecraft:golden_horse_armor", () -> new HorseArmorItemCoFH(7, "gold", itemProperties().stacksTo(1)).setEnchantability(25));
        ITEMS.register("minecraft:diamond_horse_armor", () -> new HorseArmorItemCoFH(11, "diamond", itemProperties().stacksTo(1)).setEnchantability(10));
        ITEMS.register("minecraft:leather_horse_armor", () -> new DyeableHorseArmorItemCoFH(3, "leather", itemProperties().stacksTo(1)).setEnchantability(15));
    }

    public static void registerShearsOverride() {

        ITEMS.register("minecraft:shears", () -> new ShearsItemCoFH(itemProperties().durability(238)));
    }

    public static void registerShieldOverride() {

        ITEMS.register("minecraft:shield", () -> new ShieldItemCoFH(itemProperties().durability(336)).setEnchantability(15));
    }

}
