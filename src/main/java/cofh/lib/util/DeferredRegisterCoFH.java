package cofh.lib.util;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static cofh.lib.util.helpers.StringHelper.decompose;

public class DeferredRegisterCoFH<T> {

    private final String modid;
    private final DeferredRegister<T> wrappedRegister;
    private final Map<ResourceLocation, RegistryObject<T>> registryObjects = new HashMap<>();

    private DeferredRegisterCoFH(DeferredRegister<T> wrappedRegister, String modid) {

        this.modid = modid;
        this.wrappedRegister = wrappedRegister;
    }

    public static <B> DeferredRegisterCoFH<B> create(IForgeRegistry<B> reg, String modid) {

        return new DeferredRegisterCoFH<>(DeferredRegister.create(reg, modid), modid);
    }

    public static <B> DeferredRegisterCoFH<B> create(ResourceKey<? extends Registry<B>> key, String modid) {

        return new DeferredRegisterCoFH<>(DeferredRegister.create(key, modid), modid);
    }

    public static <B> DeferredRegisterCoFH<B> createOptional(ResourceKey<? extends Registry<B>> key, String modid) {

        return new DeferredRegisterCoFH<>(DeferredRegister.createOptional(key, modid), modid);
    }

    public static <B> DeferredRegisterCoFH<B> createOptional(ResourceLocation registryName, String modid) {

        return new DeferredRegisterCoFH<>(DeferredRegister.create(registryName, modid), modid);
    }

    @SuppressWarnings ({"rawtypes", "unchecked"})
    public <I extends T> RegistryObject<I> register(final String name, final Supplier<? extends I> sup) {

        RegistryObject<I> ret = wrappedRegister.register(name, sup);
        registryObjects.put(ret.getId(), (RegistryObject<T>) ret);

        return ret;
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

        RegistryObject<T> reg = registryObjects.get(resourceLoc);
        return reg == null ? null : reg.get();
    }
    // endregion

    // region SUPPLIER RETRIEVAL
    public RegistryObject<T> getSup(final String resourceLoc) {

        return getSup(decompose(modid, resourceLoc, ':'));
    }

    private RegistryObject<T> getSup(final String[] resourceLoc) {

        return getSup(resourceLoc[0], resourceLoc[1]);
    }

    public RegistryObject<T> getSup(final String modid, final String name) {

        return getSup(new ResourceLocation(modid, name));
    }

    @Nullable
    public RegistryObject<T> getSup(final ResourceLocation resourceLoc) {

        return registryObjects.get(resourceLoc);
    }
    // endregion

}
