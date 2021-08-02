package cofh.lib.item;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

import static cofh.lib.util.constants.NBTTags.TAG_COLORS;
import static cofh.lib.util.constants.NBTTags.TAG_INDEX;

public interface IColorableItem extends INBTCopyIngredient {

    default void applyColor(ItemStack item, int color, int colorIndex) {

        if (item.getTag() == null) {
            item.setTag(new CompoundNBT());
        }
        CompoundNBT colorTag = item.getTag().getCompound(TAG_COLORS);
        colorTag.putInt(TAG_INDEX + colorIndex, color);
    }

    default void removeColor(ItemStack item, int colorIndex) {

        if (item.getTag() == null || item.getChildTag(TAG_COLORS) == null) {
            return;
        }
        CompoundNBT colorTag = item.getTag().getCompound(TAG_COLORS);
        colorTag.remove(TAG_INDEX + colorIndex);
        if (colorTag.isEmpty()) {
            item.removeChildTag(TAG_COLORS);
        }
    }

    default void removeAllColors(ItemStack item) {

        if (item.getChildTag(TAG_COLORS) != null) {
            item.removeChildTag(TAG_COLORS);
        }
    }

    int getColor(ItemStack stack, int tintIndex);

}
