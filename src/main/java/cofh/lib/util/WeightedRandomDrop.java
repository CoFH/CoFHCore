package cofh.lib.util;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandom;

public class WeightedRandomDrop extends WeightedRandom.Item {

    public final Item item;

    public WeightedRandomDrop(Item item, int itemWeightIn) {

        super(itemWeightIn);
        this.item = item;
    }

    public ItemStack toItemStack(int count) {

        return new ItemStack(item, count);
    }

}
