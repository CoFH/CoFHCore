package cofh.core.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SignItem;

public class SignItemCoFH extends SignItem {

    protected String modId = "";

    public SignItemCoFH(Properties propertiesIn, Block floorBlockIn, Block wallBlockIn) {

        super(propertiesIn, floorBlockIn, wallBlockIn);
    }

    public SignItemCoFH setModId(String modId) {

        this.modId = modId;
        return this;
    }

    @Override
    public String getCreatorModId(ItemStack itemStack) {

        return modId == null || modId.isEmpty() ? super.getCreatorModId(itemStack) : modId;
    }

}
