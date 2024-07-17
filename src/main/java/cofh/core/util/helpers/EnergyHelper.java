package cofh.core.util.helpers;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

public class EnergyHelper {

    private EnergyHelper() {

    }

    public static boolean hasEnergyHandlerCap(BlockEntity tile, Direction face) {

        return tile != null && tile.getLevel() != null && tile.getLevel().getCapability(Capabilities.EnergyStorage.BLOCK, tile.getBlockPos(), tile.getBlockState(), tile, face) != null;
    }

    public static IEnergyStorage getEnergyHandlerCap(BlockEntity tile, Direction face) {

        return tile == null || tile.getLevel() == null ? null : tile.getLevel().getCapability(Capabilities.EnergyStorage.BLOCK, tile.getBlockPos(), tile.getBlockState(), tile, face);
    }

    public static boolean hasEnergyHandlerCap(ItemStack item) {

        return !item.isEmpty() && item.getCapability(Capabilities.EnergyStorage.ITEM) != null;
    }

}
