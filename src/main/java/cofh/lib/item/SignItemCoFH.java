package cofh.lib.item;

import cofh.lib.api.item.ICoFHItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SignItem;
import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

import static cofh.lib.util.Constants.TRUE;

public class SignItemCoFH extends SignItem implements ICoFHItem {

    public SignItemCoFH(Properties propertiesIn, Block floorBlockIn, Block wallBlockIn) {

        super(propertiesIn, floorBlockIn, wallBlockIn);
    }

    // region DISPLAY
    protected Supplier<CreativeModeTab> displayGroup;
    protected Supplier<Boolean> showInGroups = TRUE;
    protected String modId = "";

    @Override
    public SignItemCoFH setDisplayGroup(Supplier<CreativeModeTab> displayGroup) {

        this.displayGroup = displayGroup;
        return this;
    }

    @Override
    public SignItemCoFH setModId(String modId) {

        this.modId = modId;
        return this;
    }

    @Override
    public SignItemCoFH setShowInGroups(Supplier<Boolean> showInGroups) {

        this.showInGroups = showInGroups;
        return this;
    }

    @Override
    public String getCreatorModId(ItemStack itemStack) {

        return modId == null || modId.isEmpty() ? super.getCreatorModId(itemStack) : modId;
    }
    // endregion
}
