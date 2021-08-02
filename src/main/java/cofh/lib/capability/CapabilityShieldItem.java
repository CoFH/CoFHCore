package cofh.lib.capability;

import cofh.lib.capability.templates.ShieldItemWrapper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

import javax.annotation.Nullable;

public class CapabilityShieldItem {

    private static boolean registered = false;

    @CapabilityInject(IShieldItem.class)
    public static Capability<IShieldItem> SHIELD_ITEM_CAPABILITY = null;

    public static void register() {

        if (registered) {
            return;
        }
        registered = true;

        CapabilityManager.INSTANCE.register(IShieldItem.class, new DefaultShieldHandlerStorage<>(), () -> new ShieldItemWrapper(new ItemStack(Items.SHIELD)));
    }

    private static class DefaultShieldHandlerStorage<T extends IShieldItem> implements IStorage<T> {

        @Nullable
        @Override
        public INBT writeNBT(Capability<T> capability, T instance, Direction side) {

            return null;
        }

        @Override
        public void readNBT(Capability<T> capability, T instance, Direction side, INBT nbt) {

        }

    }

}
