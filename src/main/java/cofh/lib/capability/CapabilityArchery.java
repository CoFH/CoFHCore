package cofh.lib.capability;

import cofh.lib.capability.templates.ArcheryAmmoItemWrapper;
import cofh.lib.capability.templates.ArcheryBowItemWrapper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

import javax.annotation.Nullable;

public class CapabilityArchery {

    private static boolean registered = false;

    @CapabilityInject(IArcheryBowItem.class)
    public static Capability<IArcheryBowItem> BOW_ITEM_CAPABILITY = null;

    @CapabilityInject(IArcheryAmmoItem.class)
    public static Capability<IArcheryAmmoItem> AMMO_ITEM_CAPABILITY = null;

    public static void register() {

        if (registered) {
            return;
        }
        registered = true;

        CapabilityManager.INSTANCE.register(IArcheryBowItem.class, new DefaultArcheryHandlerStorage<>(), () -> new ArcheryBowItemWrapper(new ItemStack(Items.BOW)));
        CapabilityManager.INSTANCE.register(IArcheryAmmoItem.class, new DefaultArcheryHandlerStorage<>(), () -> new ArcheryAmmoItemWrapper(new ItemStack(Items.ARROW)));
    }

    private static class DefaultArcheryHandlerStorage<T extends IArcheryItem> implements IStorage<T> {

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
