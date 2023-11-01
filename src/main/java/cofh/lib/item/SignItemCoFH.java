package cofh.lib.item;

import cofh.lib.api.item.ICoFHItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SignItem;
import net.minecraft.world.level.block.Block;

public class SignItemCoFH extends SignItem implements ICoFHItem {

    public SignItemCoFH(Properties propertiesIn, Block floorBlockIn, Block wallBlockIn) {

        super(propertiesIn, floorBlockIn, wallBlockIn);
    }

    // region DISPLAY
    protected String modId = "";

    @Override
    public SignItemCoFH setModId(String modId) {

        this.modId = modId;
        return this;
    }

    @Override
    public String getCreatorModId(ItemStack itemStack) {

        return modId == null || modId.isEmpty() ? super.getCreatorModId(itemStack) : modId;
    }
    // endregion
}
