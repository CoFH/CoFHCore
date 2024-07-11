package cofh.lib.util;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegistryBuilder;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static cofh.lib.util.helpers.StringHelper.decompose;

public class DeferredRegisterCoFH<T> {

    private final String modid;
    private final DeferredRegister<T> wrappedRegister;
    private final Map<ResourceLocation, DeferredHolder<T, ? extends T>> registryObjects = new HashMap<>();

    private DeferredRegisterCoFH(DeferredRegister<T> wrappedRegister, String modid) {

        this.modid = modid;
        this.wrappedRegister = wrappedRegister;
    }

    public static <B> DeferredRegisterCoFH<B> create(ResourceLocation registryName, String modid) {

        return new DeferredRegisterCoFH<>(DeferredRegister.create(registryName, modid), modid);
    }

    public static <T> DeferredRegisterCoFH<T> create(Registry<T> reg, String modid) {

        return new DeferredRegisterCoFH<>(DeferredRegister.create(reg, modid), modid);
    }

    public static <T> DeferredRegisterCoFH<T> create(ResourceKey<? extends Registry<T>> key, String modid) {

        return new DeferredRegisterCoFH<>(DeferredRegister.create(key, modid), modid);
    }

    @SuppressWarnings ({"rawtypes", "unchecked"})
    public synchronized <I extends T> DeferredHolder<T, I> register(final String name, final Supplier<? extends I> sup) {

        DeferredHolder<T, I> ret = wrappedRegister.register(name, sup);
        registryObjects.put(ret.getId(), ret);

        return ret;
    }

    public Registry<T> makeRegistry(final Consumer<RegistryBuilder<T>> consumer) {

        return wrappedRegister.makeRegistry(consumer);
    }

    public void register(IEventBus bus) {

        wrappedRegister.register(bus);
    }

    // region OBJECT RETRIEVAL
    public T get(final String resourceLoc) {

        return get(decompose(modid, resourceLoc, ':'));
    }

    private T get(final String[] resourceLoc) {

        return get(resourceLoc[0], resourceLoc[1]);
    }

    public T get(final String modid, final String name) {

        return get(new ResourceLocation(modid, name));
    }

    public T get(final ResourceLocation resourceLoc) {

        DeferredHolder<T, ? extends T> reg = registryObjects.get(resourceLoc);
        return reg == null ? null : reg.get();
    }
    // endregion

    // region SUPPLIER RETRIEVAL
    public Supplier<T> getSup(final String resourceLoc) {

        return getSup(decompose(modid, resourceLoc, ':'));
    }

    private Supplier<T> getSup(final String[] resourceLoc) {

        return getSup(resourceLoc[0], resourceLoc[1]);
    }

    public Supplier<T> getSup(final String modid, final String name) {

        return getSup(new ResourceLocation(modid, name));
    }

    @Nullable
    public Supplier<T> getSup(final ResourceLocation resourceLoc) {

        return (Supplier<T>) registryObjects.get(resourceLoc);
    }
    // endregion
}
