package cofh.core.common.item;

import cofh.core.util.ProxyUtils;
import net.minecraft.resources.ResourceLocation;

public class CountedItem extends ItemCoFH {

    public CountedItem(Properties builder) {

        super(builder);

        ProxyUtils.registerItemModelProperty(this, new ResourceLocation("count"), (stack, world, living, seed) -> ((float) stack.getCount()) / stack.getMaxStackSize());
    }

}
