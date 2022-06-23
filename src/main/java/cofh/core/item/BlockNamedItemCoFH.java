package cofh.core.item;

import net.minecraft.world.level.block.Block;

public class BlockNamedItemCoFH extends BlockItemCoFH {

    public BlockNamedItemCoFH(Block blockIn, Properties builder) {

        super(blockIn, builder);
    }

    public String getDescriptionId() {

        return getOrCreateDescriptionId();
    }

}
