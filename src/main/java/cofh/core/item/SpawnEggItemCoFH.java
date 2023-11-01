package cofh.core.item;

import cofh.lib.api.item.IColorableItem;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeSpawnEggItem;

import java.util.function.Supplier;

import static cofh.lib.util.Constants.TRUE;

public class SpawnEggItemCoFH extends ForgeSpawnEggItem implements IColorableItem {

    protected Supplier<Boolean> showInGroups = TRUE;

    public SpawnEggItemCoFH(Supplier<EntityType<? extends Mob>> typeSupIn, int primaryColorIn, int secondaryColorIn, Properties builder) {

        super(typeSupIn, primaryColorIn, secondaryColorIn, builder);
    }

    public SpawnEggItemCoFH setShowInGroups(Supplier<Boolean> showInGroups) {

        this.showInGroups = showInGroups;
        return this;
    }

    //    @Override
    //    public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
    //
    //        if (!showInGroups.get()) {
    //            return;
    //        }
    //        super.fillItemCategory(group, items);
    //    }

    public int getColor(ItemStack item, int colorIndex) {

        return getColor(colorIndex);
    }

}
