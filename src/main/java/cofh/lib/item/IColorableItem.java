package cofh.lib.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public interface IColorableItem {

    default int getColor(ItemStack item, int colorIndex) {

        if (colorIndex == 0) {
            CompoundTag nbt = item.getTagElement("display");
            return nbt != null && nbt.contains("color", 99) ? nbt.getInt("color") : 0xFFFFFF;
        }
        return 0xFFFFFF;
    }

}
