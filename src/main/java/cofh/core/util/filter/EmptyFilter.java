package cofh.core.util.filter;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

import javax.annotation.Nullable;

public class EmptyFilter implements IFilter {

    public static final EmptyFilter INSTANCE = new EmptyFilter();

    @Override
    public IFilter read(CompoundTag nbt) {

        return INSTANCE;
    }

    @Override
    public CompoundTag write(CompoundTag nbt) {

        return nbt;
    }

    // region MenuProvider
    @Override
    public Component getDisplayName() {

        return null;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {

        return null;
    }
    // endregion
}
