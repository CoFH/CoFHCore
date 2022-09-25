//package cofh.lib.capability;
//
//import cofh.lib.xp.IXpStorage;
//import cofh.lib.xp.XpStorage;
//import net.minecraft.nbt.INBT;
//import net.minecraft.util.Direction;
//import net.minecraftforge.common.capabilities.Capability;
//import net.minecraftforge.common.capabilities.Capability.IStorage;
//import net.minecraftforge.common.capabilities.CapabilityInject;
//import net.minecraftforge.common.capabilities.CapabilityManager;
//
//import javax.annotation.Nullable;
//
//public class CapabilityXpHandler {
//
//    private static boolean registered = false;
//
//    @CapabilityInject(IXpStorage.class)
//    public static Capability<IXpStorage> XP_STORAGE = null;
//
//    // TODO: Currently Unused!
//    public static void register() {
//
//        if (registered) {
//            return;
//        }
//        registered = true;
//
//        CapabilityManager.INSTANCE.register(IXpStorage.class, new DefaultXpStorage<>(), () -> new XpStorage(0));
//    }
//
//    private static class DefaultXpStorage<T extends IXpStorage> implements IStorage<T> {
//
//        @Nullable
//        @Override
//        public INBT writeNBT(Capability<T> capability, T instance, Direction side) {
//
//            return null;
//        }
//
//        @Override
//        public void readNBT(Capability<T> capability, T instance, Direction side, INBT nbt) {
//
//        }
//
//    }
//
//}
