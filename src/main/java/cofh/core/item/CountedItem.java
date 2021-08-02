package cofh.core.item;

import cofh.core.util.ProxyUtils;
import net.minecraft.util.ResourceLocation;

public class CountedItem extends ItemCoFH {

    public CountedItem(Properties builder) {

        super(builder);

        ProxyUtils.registerItemModelProperty(this, new ResourceLocation("count"), (stack, world, living) -> ((float) stack.getCount()) / stack.getMaxStackSize());
    }

}
