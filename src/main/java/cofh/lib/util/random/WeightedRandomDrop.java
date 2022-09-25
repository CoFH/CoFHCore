package cofh.lib.util;

import net.minecraft.util.random.WeightedEntry;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class WeightedRandomDrop extends WeightedEntry.IntrusiveBase {

    public final Item item;

    public WeightedRandomDrop(Item item, int itemWeightIn) {

        super(itemWeightIn);
        this.item = item;
    }

    public ItemStack toItemStack(int count) {

        return new ItemStack(item, count);
    }

}
