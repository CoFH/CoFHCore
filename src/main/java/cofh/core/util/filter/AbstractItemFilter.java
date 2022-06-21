package cofh.core.util.filter;

import cofh.core.util.helpers.ItemHelper;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import static cofh.lib.util.constants.NBTTags.*;
import static net.minecraft.nbt.Tag.TAG_COMPOUND;

public abstract class AbstractItemFilter implements IFilter, IFilterOptions {

    public static final int SIZE = 15;

    protected List<ItemStack> items;
    protected Predicate<ItemStack> rules;

    protected boolean allowList = false;
    protected boolean checkNBT = false;

    public AbstractItemFilter(int size) {

        items = new ArrayList<>(size);
        for (int i = 0; i < size; ++i) {
            items.add(ItemStack.EMPTY);
        }
    }

    public int size() {

        return items.size();
    }

    public List<ItemStack> getItems() {

        return items;
    }

    public void setItems(List<ItemStack> items) {

        this.items = items;
        reset();
    }

    public void reset() {

        this.rules = null;
    }

    @Override
    public Predicate<ItemStack> getItemRules() {

        if (rules == null) {
            Set<Item> itemSet = new ObjectOpenHashSet<>();
            for (ItemStack item : items) {
                itemSet.add(item.getItem());
            }
            rules = stack -> {
                if (stack.isEmpty()) {
                    return false;
                }
                if (checkNBT) {
                    for (ItemStack item : items) {
                        if (ItemHelper.itemsEqualWithTags(stack, item)) {
                            return allowList;
                        }
                    }
                    return !allowList;
                }
                return allowList == itemSet.contains(stack.getItem());
            };
        }
        return rules;
    }

    @Override
    public IFilter read(CompoundTag nbt) {

        CompoundTag subTag = nbt.getCompound(TAG_FILTER);
        //        int size = subTag.getInt(TAG_SLOTS);
        //        if (size > 0) {
        //            items = new ArrayList<>(size);
        //            for (int i = 0; i < size; ++i) {
        //                items.add(ItemStack.EMPTY);
        //            }
        //        }
        ListTag list = subTag.getList(TAG_ITEM_INV, TAG_COMPOUND);
        for (int i = 0; i < list.size(); ++i) {
            CompoundTag slotTag = list.getCompound(i);
            int slot = slotTag.getByte(TAG_SLOT);
            if (slot >= 0 && slot < items.size()) {
                items.set(slot, ItemStack.of(slotTag));
            }
        }
        allowList = subTag.getBoolean(TAG_FILTER_OPT_LIST);
        checkNBT = subTag.getBoolean(TAG_FILTER_OPT_NBT);
        return this;
    }

    @Override
    public CompoundTag write(CompoundTag nbt) {

        CompoundTag subTag = new CompoundTag();
        ListTag list = new ListTag();
        //        if (items.size() != SIZE) {
        //            subTag.putInt(TAG_SLOTS, items.size());
        //        }
        for (int i = 0; i < items.size(); ++i) {
            if (!items.get(i).isEmpty()) {
                CompoundTag slotTag = new CompoundTag();
                slotTag.putByte(TAG_SLOT, (byte) i);
                items.get(i).save(slotTag);
                list.add(slotTag);
            }
        }
        subTag.put(TAG_ITEM_INV, list);

        subTag.putBoolean(TAG_FILTER_OPT_LIST, allowList);
        subTag.putBoolean(TAG_FILTER_OPT_NBT, checkNBT);

        nbt.put(TAG_FILTER, subTag);
        return nbt;
    }

    // region IFilterOptions
    @Override
    public boolean getAllowList() {

        return allowList;
    }

    @Override
    public boolean setAllowList(boolean allowList) {

        this.allowList = allowList;
        return true;
    }

    @Override
    public boolean getCheckNBT() {

        return checkNBT;
    }

    @Override
    public boolean setCheckNBT(boolean checkNBT) {

        this.checkNBT = checkNBT;
        return true;
    }
    // endregion

    // region MenuProvider
    @Override
    public Component getDisplayName() {

        return Component.translatable("info.cofh.item_filter");
    }
    // endregion
}
