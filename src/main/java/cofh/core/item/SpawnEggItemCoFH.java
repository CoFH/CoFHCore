package cofh.core.item;

import cofh.lib.api.item.IColorableItem;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeSpawnEggItem;

import java.util.function.Supplier;

public class SpawnEggItemCoFH extends ForgeSpawnEggItem implements IColorableItem {

    public SpawnEggItemCoFH(Supplier<EntityType<? extends Mob>> typeSupIn, int primaryColorIn, int secondaryColorIn, Properties builder) {

        super(typeSupIn, primaryColorIn, secondaryColorIn, builder);
    }

    public int getColor(ItemStack item, int colorIndex) {

        return getColor(colorIndex);
    }

}
