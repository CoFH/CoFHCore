package cofh.lib.common.item;

import cofh.lib.api.item.ICoFHItem;
import net.minecraft.world.item.HangingSignItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class HangingSignItemCoFH extends HangingSignItem implements ICoFHItem {

    public HangingSignItemCoFH(Block pBlock, Block pWallBlock, Item.Properties pProperties) {

        super(pBlock, pWallBlock, pProperties);
    }

    // region DISPLAY
    protected String modId = "";

    @Override
    public HangingSignItemCoFH setModId(String modId) {

        this.modId = modId;
        return this;
    }

    @Override
    public String getCreatorModId(ItemStack itemStack) {

        return modId == null || modId.isEmpty() ? super.getCreatorModId(itemStack) : modId;
    }
    // endregion
}
