package cofh.lib.item;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

public interface IColorableItem {

    default int getColor(ItemStack item, int colorIndex) {

        if (colorIndex == 0) {
            CompoundNBT nbt = item.getTagElement("display");
            return nbt != null && nbt.contains("color", 99) ? nbt.getInt("color") : 0xFFFFFF;
        }
        return 0xFFFFFF;
    }

}
