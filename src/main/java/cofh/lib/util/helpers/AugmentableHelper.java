package cofh.lib.util.helpers;

import cofh.lib.item.IAugmentableItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static cofh.lib.util.constants.Constants.MAX_AUGMENTS;
import static cofh.lib.util.constants.NBTTags.*;
import static net.minecraftforge.common.util.Constants.NBT.TAG_COMPOUND;

public class AugmentableHelper {

    private AugmentableHelper() {

    }

    public static boolean isAugmentableItem(ItemStack stack) {

        return !stack.isEmpty() && stack.getItem() instanceof IAugmentableItem;
    }

    public static List<ItemStack> readAugmentsFromItem(ItemStack stack) {

        ListNBT augmentTag = getAugmentNBT(stack);
        if (augmentTag.isEmpty()) {
            return Collections.emptyList();
        }
        return getAugments(augmentTag);
    }

    public static void writeAugmentsToItem(ItemStack stack, List<ItemStack> augments) {

        writeAugmentsToItem(stack, convertAugments(augments));
    }

    // region AUGMENTABLE REDIRECTS
    public static List<ItemStack> getAugments(ItemStack augmentable) {

        return !isAugmentableItem(augmentable) ? Collections.emptyList() : ((IAugmentableItem) augmentable.getItem()).getAugments(augmentable);
    }

    public static int getAugmentSlots(ItemStack augmentable) {

        return !isAugmentableItem(augmentable) ? 0 : MathHelper.clamp(((IAugmentableItem) augmentable.getItem()).getAugmentSlots(augmentable), 0, MAX_AUGMENTS);
    }

    public static boolean validAugment(ItemStack augmentable, ItemStack augment, List<ItemStack> augments) {

        return isAugmentableItem(augmentable) && ((IAugmentableItem) augmentable.getItem()).validAugment(augmentable, augment, augments);
    }

    public static void setAugments(ItemStack stack, List<ItemStack> augments) {

        if (!isAugmentableItem(stack)) {
            return;
        }
        ((IAugmentableItem) stack.getItem()).setAugments(stack, augments);
    }
    // endregion

    // region ATTRIBUTES
    public static void setAttribute(CompoundNBT subTag, String attribute, float value) {

        subTag.putFloat(attribute, value);
    }

    public static void setAttributeFromAugmentMax(CompoundNBT subTag, CompoundNBT augmentData, String attribute) {

        float mod = Math.max(getAttributeMod(augmentData, attribute), getAttributeMod(subTag, attribute));
        if (mod > 0.0F) {
            subTag.putFloat(attribute, mod);
        }
    }

    public static void setAttributeFromAugmentAdd(CompoundNBT subTag, CompoundNBT augmentData, String attribute) {

        float mod = getAttributeMod(augmentData, attribute) + getAttributeMod(subTag, attribute);
        subTag.putFloat(attribute, mod);
    }

    public static void setAttributeFromAugmentString(CompoundNBT subTag, CompoundNBT augmentData, String attribute) {

        String mod = getAttributeModString(augmentData, attribute);
        if (!mod.isEmpty()) {
            subTag.putString(attribute, mod);
        }
    }

    public static float getAttributeMod(CompoundNBT augmentData, String key) {

        return augmentData.getFloat(key);
    }

    public static String getAttributeModString(CompoundNBT augmentData, String key) {

        return augmentData.getString(key);
    }

    public static float getAttributeModWithDefault(CompoundNBT augmentData, String key, float defaultValue) {

        return augmentData.contains(key) ? augmentData.getFloat(key) : defaultValue;
    }

    public static String getAttributeModWithDefault(CompoundNBT augmentData, String key, String defaultValue) {

        return augmentData.contains(key) ? augmentData.getString(key) : defaultValue;
    }

    public static float getPropertyWithDefault(ItemStack container, String key, float defaultValue) {

        CompoundNBT subTag = container.getChildTag(TAG_PROPERTIES);
        return subTag == null ? defaultValue : getAttributeModWithDefault(subTag, key, defaultValue);
    }

    public static String getPropertyWithDefault(ItemStack container, String key, String defaultValue) {

        CompoundNBT subTag = container.getChildTag(TAG_PROPERTIES);
        return subTag == null ? defaultValue : getAttributeModWithDefault(subTag, key, defaultValue);
    }

    // endregion

    // region INTERNAL HELPERS
    private static void writeAugmentsToItem(ItemStack stack, ListNBT list) {

        CompoundNBT nbt = stack.getChildTag(TAG_BLOCK_ENTITY);
        if (nbt != null) {
            nbt.put(TAG_AUGMENTS, list);
            return;
        }
        if (stack.getItem() instanceof BlockItem) {
            nbt = new CompoundNBT();
            nbt.put(TAG_AUGMENTS, list);
            stack.setTagInfo(TAG_BLOCK_ENTITY, nbt);
            return;
        }
        stack.setTagInfo(TAG_AUGMENTS, list);
    }

    private static List<ItemStack> getAugments(ListNBT list) {

        ArrayList<ItemStack> ret = new ArrayList<>();
        for (int i = 0; i < list.size(); ++i) {
            ret.add(ItemStack.read(list.getCompound(i)));
        }
        return ret.isEmpty() ? Collections.emptyList() : ret;
    }

    private static ListNBT getAugmentNBT(ItemStack stack) {

        if (stack.getTag() == null) {
            return new ListNBT();
        }
        CompoundNBT nbt = stack.getChildTag(TAG_BLOCK_ENTITY);
        if (nbt != null) {
            return nbt.contains(TAG_AUGMENTS) ? nbt.getList(TAG_AUGMENTS, TAG_COMPOUND) : new ListNBT();
        }
        return stack.getTag().getList(TAG_AUGMENTS, TAG_COMPOUND);
    }

    private static ListNBT convertAugments(List<ItemStack> augments) {

        ListNBT list = new ListNBT();
        for (ItemStack augment : augments) {
            // Empty slots are intentionally written.
            //if (!augment.isEmpty()) {
            list.add(augment.write(new CompoundNBT()));
            //}
        }
        return list;
    }
    // endregion
}
