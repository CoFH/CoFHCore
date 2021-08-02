package cofh.lib.util.helpers;

import cofh.lib.item.IAugmentItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

import javax.annotation.Nullable;

import static cofh.lib.util.constants.NBTTags.TAG_AUGMENT_DATA;
import static cofh.lib.util.constants.NBTTags.TAG_TYPE;

public class AugmentDataHelper {

    private AugmentDataHelper() {

    }

    public static boolean isAugmentItem(ItemStack stack) {

        return !stack.isEmpty() && stack.getItem() instanceof IAugmentItem;
    }

    public static boolean hasAugmentData(ItemStack stack) {

        return getAugmentData(stack) != null;
    }

    @Nullable
    public static CompoundNBT getAugmentData(ItemStack augment) {

        CompoundNBT augmentData = augment.getChildTag(TAG_AUGMENT_DATA);
        if (augmentData == null && isAugmentItem(augment)) {
            return ((IAugmentItem) augment.getItem()).getAugmentData(augment);
        }
        return augmentData;
    }

    public static String getAugmentType(ItemStack augment) {

        CompoundNBT augmentTag = getAugmentData(augment);
        return augmentTag != null ? augmentTag.getString(TAG_TYPE) : "";
    }

    public static Builder builder() {

        return new Builder();
    }

    // region DATA BUILDER
    public static class Builder {

        CompoundNBT augmentData = new CompoundNBT();

        public CompoundNBT build() {

            return augmentData.isEmpty() ? null : augmentData;
        }

        public Builder type(String type) {

            augmentData.putString(TAG_TYPE, type);
            return this;
        }

        public Builder mod(String mod, float value) {

            augmentData.putFloat(mod, value);
            return this;
        }

        public Builder feature(String mod, String value) {

            augmentData.putString(mod, value);
            return this;
        }

    }
    // endregion
}
