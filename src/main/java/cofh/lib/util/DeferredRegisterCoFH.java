package cofh.lib.util;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedConstants;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.*;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Supplier;

import static cofh.lib.util.helpers.StringHelper.decompose;

/**
 * Basically a copy of Forge's Deferred Register system, with a little more cowbell. See {@link DeferredRegister}
 *
 * @param <T> The base registry type, must be a concrete base class, do not use subclasses or wild cards.
 * @author King Lemming
 */

@SuppressWarnings({"rawtypes", "unchecked"})
public class DeferredRegisterCoFH<T extends IForgeRegistryEntry<T>> {

    /**
     * Use for vanilla/forge registries. See example above.
     */
    public static <B extends IForgeRegistryEntry<B>> DeferredRegisterCoFH<B> create(IForgeRegistry<B> reg, String modid) {

        return new DeferredRegisterCoFH<>(reg, modid);
    }

    /**
     * Use for custom registries that are made during the NewRegistry event.
     */
    public static <B extends IForgeRegistryEntry<B>> DeferredRegisterCoFH<B> create(Class<B> base, String modid) {

        return new DeferredRegisterCoFH<>(base, modid);
    }

    private final Class<T> superType;
    private final String modid;
    private final Map<RegistryObject<T>, Supplier<? extends T>> entries = new LinkedHashMap<>();
    private final Set<RegistryObject<T>> entriesView = Collections.unmodifiableSet(entries.keySet());
    private final Map<ResourceLocation, RegistryObject> registryObjects = new HashMap<>();

    private IForgeRegistry<T> type;
    private Supplier<RegistryBuilder<T>> registryFactory;
    // private boolean seenRegisterEvent = false;
    private boolean preventDataFixers;

    private DeferredRegisterCoFH(Class<T> base, String modid) {

        this.superType = base;
        this.modid = modid;
    }

    private DeferredRegisterCoFH(IForgeRegistry<T> reg, String modid) {

        this(reg.getRegistrySuperType(), modid);
        this.type = reg;
    }

    public DeferredRegisterCoFH<T> preventDataFixers(boolean preventDataFixers) {

        this.preventDataFixers = preventDataFixers;
        return this;
    }

    // region REGISTRATION
    public synchronized <I extends T> RegistryObject<I> register(final String resourceLoc, final Supplier<I> sup) {

        return register(decompose(modid, resourceLoc, ':'), sup);
    }

    private synchronized <I extends T> RegistryObject<I> register(final String[] resourceLoc, final Supplier<I> sup) {

        return register(resourceLoc[0], resourceLoc[1], sup);
    }

    public synchronized <I extends T> RegistryObject<I> register(final String modid, final String name, final Supplier<I> sup) {

        return register(new ResourceLocation(modid, name), sup);
    }

    public synchronized <I extends T> RegistryObject<I> register(final ResourceLocation resourceLoc, final Supplier<I> sup) {

        //        if (seenRegisterEvent)
        //            throw new IllegalStateException("Cannot register new entries to DeferredRegister after RegistryEvent.Register has been fired.");
        //        Objects.requireNonNull(name);
        //        Objects.requireNonNull(sup);
        //        final ResourceLocation key = new ResourceLocation(modid, name);
        //
        //        RegistryObject<I> ret;
        //        if (this.type != null)
        //            ret = RegistryObject.of(key, this.type);
        //        else if (this.superType != null)
        //            ret = RegistryObject.of(key, this.superType, this.modid);
        //        else
        //            throw new IllegalStateException("Could not create RegistryObject in DeferredRegister");
        //
        //        if (entries.putIfAbsent((RegistryObject<T>) ret, () -> sup.get().setRegistryName(key)) != null) {
        //            throw new IllegalArgumentException("Duplicate registration " + name);
        //        }
        //
        //        return ret;

        if (registryObjects.containsKey(resourceLoc)) {
            return registryObjects.get(resourceLoc);
        }
        RegistryObject<I> reg;
        if (this.type != null) {
            reg = RegistryObject.of(resourceLoc, this.type);
        } else if (this.superType != null) {
            reg = RegistryObject.of(resourceLoc, this.superType, this.modid);
        } else {
            throw new IllegalStateException("Could not create RegistryObject in DeferredRegister");
        }
        entries.put((RegistryObject<T>) reg, () -> sup.get().setRegistryName(resourceLoc));
        registryObjects.put(resourceLoc, reg);
        return reg;
    }
    // endregion

    /**
     * For custom registries only, fills the {@link #registryFactory} to be called later see {@link #register(IEventBus)}
     * <p>
     * Calls {@link RegistryBuilder#setName} and {@link RegistryBuilder#setType} automatically.
     *
     * @param name Path of the registry's {@link ResourceLocation}
     * @param sup  Supplier of the RegistryBuilder that is called to fill {@link #type} during the NewRegistry event
     * @return A supplier of the {@link IForgeRegistry} created by the builder.
     */
    public Supplier<IForgeRegistry<T>> makeRegistry(final String name, final Supplier<RegistryBuilder<T>> sup) {

        if (this.superType == null)
            throw new IllegalStateException("Cannot create a registry without specifying a base type");
        if (this.type != null || this.registryFactory != null)
            throw new IllegalStateException("Cannot create a registry for a type that already exists");

        this.registryFactory = () -> sup.get().setName(new ResourceLocation(modid, name)).setType(this.superType);
        return () -> this.type;
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

    /**
     * Adds our event handler to the specified event bus, this MUST be called in order for this class to function.
     * See the example usage.
     *
     * @param bus The Mod Specific event bus.
     */
    public void register(IEventBus bus) {

        bus.register(new DeferredRegisterCoFH.EventDispatcher(this));
        if (this.type == null && this.registryFactory != null) {
            bus.addListener(this::createRegistry);
        }
    }

    public static class EventDispatcher {

        private final DeferredRegisterCoFH<?> register;

        public EventDispatcher(final DeferredRegisterCoFH<?> register) {

            this.register = register;
        }

        @SubscribeEvent
        public void handleEvent(RegistryEvent.Register<?> event) {

            if (register.preventDataFixers) {
                SharedConstants.useDatafixers = false;
            }
            register.addEntries(event);

            if (register.preventDataFixers) {
                SharedConstants.useDatafixers = true;
            }
        }

    }

    /**
     * @return The unmodifiable view of registered entries. Useful for bulk operations on all values.
     */
    public Collection<RegistryObject<T>> getEntries() {

        return entriesView;
    }

    private void addEntries(RegistryEvent.Register<?> event) {

        if (this.type == null && this.registryFactory == null) {
            //If there is no type yet and we don't have a registry factory, attempt to capture the registry
            //Note: This will only ever get run on the first registry event, as after that time,
            // the type will no longer be null. This is needed here rather than during the NewRegistry event
            // to ensure that mods can properly use deferred registers for custom registries added by other mods
            captureRegistry();
        }
        if (this.type != null && event.getGenericType() == this.type.getRegistrySuperType()) {
            // this.seenRegisterEvent = true;
            @SuppressWarnings("unchecked")
            IForgeRegistry<T> reg = (IForgeRegistry<T>) event.getRegistry();
            for (Map.Entry<RegistryObject<T>, Supplier<? extends T>> e : entries.entrySet()) {
                reg.register(e.getValue().get());
                e.getKey().updateReference(reg);
            }
        }
    }

    private void createRegistry(RegistryEvent.NewRegistry event) {

        this.type = this.registryFactory.get().create();
    }

    private void captureRegistry() {

        if (this.superType != null) {
            this.type = RegistryManager.ACTIVE.getRegistry(this.superType);
            if (this.type == null)
                throw new IllegalStateException("Unable to find registry for type " + this.superType.getName() + " for modid \"" + modid + "\" after NewRegistry event");
        } else
            throw new IllegalStateException("Unable to find registry for mod \"" + modid + "\" No lookup criteria specified.");
    }

}