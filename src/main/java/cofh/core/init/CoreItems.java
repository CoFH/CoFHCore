package cofh.core.init;

import cofh.lib.item.DyeableHorseArmorItemCoFH;
import cofh.lib.item.HorseArmorItemCoFH;
import cofh.lib.item.ShearsItemCoFH;
import cofh.lib.item.ShieldItemCoFH;
import net.minecraft.world.item.Item;

import static cofh.core.CoFHCore.ITEMS;

public class CoreItems {

    private CoreItems() {

    }

    public static void register() {

        // ITEMS.register("dev_stick", () -> new DevStickItem(new Item.Properties().stacksTo(1).fireResistant()).setShowInGroups(FALSE));
    }

    public static void registerHorseArmorOverrides() {

        ITEMS.register("minecraft:iron_horse_armor", () -> new HorseArmorItemCoFH(5, "iron", new Item.Properties().stacksTo(1)).setEnchantability(9));
        ITEMS.register("minecraft:golden_horse_armor", () -> new HorseArmorItemCoFH(7, "gold", new Item.Properties().stacksTo(1)).setEnchantability(25));
        ITEMS.register("minecraft:diamond_horse_armor", () -> new HorseArmorItemCoFH(11, "diamond", new Item.Properties().stacksTo(1)).setEnchantability(10));
        ITEMS.register("minecraft:leather_horse_armor", () -> new DyeableHorseArmorItemCoFH(3, "leather", new Item.Properties().stacksTo(1)).setEnchantability(15));
    }

    public static void registerShearsOverride() {

        ITEMS.register("minecraft:shears", () -> new ShearsItemCoFH(new Item.Properties().durability(238)));
    }

    public static void registerShieldOverride() {

        ITEMS.register("minecraft:shield", () -> new ShieldItemCoFH(new Item.Properties().durability(336)).setEnchantability(15));
    }

}
