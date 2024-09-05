package cofh.core;

import cofh.core.client.event.CoreClientEvents;
import cofh.core.client.gui.FluidFilterScreen;
import cofh.core.client.gui.ItemFilterScreen;
import cofh.core.client.renderer.entity.ElectricFieldRenderer;
import cofh.core.client.renderer.entity.KnifeRenderer;
import cofh.core.client.renderer.entity.model.ArmorFullSuitModel;
import cofh.core.common.command.CoFHCommand;
import cofh.core.common.config.*;
import cofh.core.common.enchantment.HoldingEnchantment;
import cofh.core.common.event.ArmorEvents;
import cofh.core.common.network.PacketHandler;
import cofh.core.compat.curios.CuriosProxy;
import cofh.core.init.*;
import cofh.core.util.Proxy;
import cofh.core.util.ProxyClient;
import cofh.core.util.helpers.ArcheryHelper;
import cofh.core.util.references.IMCMethods;
import cofh.lib.client.renderer.entity.NothingRenderer;
import cofh.lib.common.loot.TileNBTSync;
import cofh.lib.util.DeferredRegisterCoFH;
import cofh.lib.util.Utils;
import com.mojang.serialization.Codec;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.InterModProcessEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static cofh.core.client.renderer.entity.model.ArmorFullSuitModel.ARMOR_FULL_SUIT_LAYER;
import static cofh.core.init.CoreMenus.FLUID_FILTER_CONTAINER;
import static cofh.core.init.CoreMenus.ITEM_FILTER_CONTAINER;
import static cofh.core.init.CoreEntities.*;
import static cofh.lib.util.constants.ModIds.ID_COFH_CORE;
import static cofh.lib.util.constants.ModIds.ID_CURIOS;

@Mod(ID_COFH_CORE)
public class CoFHCore {

    public static final Logger LOG = LogManager.getLogger(ID_COFH_CORE);

    public static final ConfigManager CONFIG_MANAGER = new ConfigManager();
    public static final Proxy PROXY = FMLEnvironment.dist.isClient() ? new ProxyClient() : new Proxy();

    public static final DeferredRegisterCoFH<Block> BLOCKS = DeferredRegisterCoFH.create(BuiltInRegistries.BLOCK, ID_COFH_CORE);
    public static final DeferredRegisterCoFH<Item> ITEMS = DeferredRegisterCoFH.create(BuiltInRegistries.ITEM, ID_COFH_CORE);
    public static final DeferredRegisterCoFH<Fluid> FLUIDS = DeferredRegisterCoFH.create(BuiltInRegistries.FLUID, ID_COFH_CORE);

    public static final DeferredRegisterCoFH<MenuType<?>> CONTAINERS = DeferredRegisterCoFH.create(BuiltInRegistries.MENU, ID_COFH_CORE);
    public static final DeferredRegisterCoFH<Enchantment> ENCHANTMENTS = DeferredRegisterCoFH.create(BuiltInRegistries.ENCHANTMENT, ID_COFH_CORE);
    public static final DeferredRegisterCoFH<EntityType<?>> ENTITIES = DeferredRegisterCoFH.create(BuiltInRegistries.ENTITY_TYPE, ID_COFH_CORE);
    public static final DeferredRegisterCoFH<MobEffect> MOB_EFFECTS = DeferredRegisterCoFH.create(BuiltInRegistries.MOB_EFFECT, ID_COFH_CORE);
    public static final DeferredRegisterCoFH<ParticleType<?>> PARTICLES = DeferredRegisterCoFH.create(BuiltInRegistries.PARTICLE_TYPE, ID_COFH_CORE);
    public static final DeferredRegisterCoFH<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegisterCoFH.create(BuiltInRegistries.RECIPE_SERIALIZER, ID_COFH_CORE);
    public static final DeferredRegisterCoFH<SoundEvent> SOUND_EVENTS = DeferredRegisterCoFH.create(BuiltInRegistries.SOUND_EVENT, ID_COFH_CORE);
    public static final DeferredRegisterCoFH<BlockEntityType<?>> TILE_ENTITIES = DeferredRegisterCoFH.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, ID_COFH_CORE);

    public static final DeferredRegister<Codec<? extends ICondition>> CONDITION_CODECS = DeferredRegister.create(NeoForgeRegistries.Keys.CONDITION_CODECS, ID_COFH_CORE);
    public static final DeferredRegisterCoFH<EntityDataSerializer<?>> ENTITY_DATA_SERIALIZERS = DeferredRegisterCoFH.create(NeoForgeRegistries.Keys.ENTITY_DATA_SERIALIZERS, ID_COFH_CORE);
    public static final DeferredRegisterCoFH<FluidType> FLUID_TYPES = DeferredRegisterCoFH.create(NeoForgeRegistries.Keys.FLUID_TYPES, ID_COFH_CORE);

    public static boolean curiosLoaded = false;

    public CoFHCore(ModContainer modContainer, IEventBus modEventBus) {

        NeoForgeMod.enableMilkFluid();

        curiosLoaded = Utils.isModLoaded(ID_CURIOS);

        modEventBus.addListener(this::registrySetup);
        modEventBus.addListener(this::entityLayerSetup);
        modEventBus.addListener(this::entityRendererSetup);
        modEventBus.addListener(this::menuScreenSetup);
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::clientSetup);
        modEventBus.addListener(this::handleIMC);
        modEventBus.addListener(this::registerLootData);

        modEventBus.addListener(PacketHandler::registerNetworking);

        NeoForge.EVENT_BUS.addListener(this::registerCommands);

        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        FLUIDS.register(modEventBus);

        CONTAINERS.register(modEventBus);
        ENCHANTMENTS.register(modEventBus);
        ENTITIES.register(modEventBus);
        MOB_EFFECTS.register(modEventBus);
        PARTICLES.register(modEventBus);
        RECIPE_SERIALIZERS.register(modEventBus);
        SOUND_EVENTS.register(modEventBus);
        TILE_ENTITIES.register(modEventBus);

        CONDITION_CODECS.register(modEventBus);
        ENTITY_DATA_SERIALIZERS.register(modEventBus);
        FLUID_TYPES.register(modEventBus);

        CONFIG_MANAGER.register(modEventBus)
                .addClientConfig(new CoreClientConfig())
                .addCommonConfig(new CoreCommonConfig())
                .addServerConfig(new CoreCommandConfig())
                .addServerConfig(new CoreEnchantConfig());
        CONFIG_MANAGER.setupClient();
        CONFIG_MANAGER.setupServer();

        CoreItems.register();
        CoreBlocks.register();
        CoreFluids.register();

        CoreMenus.register();
        CoreEnchantments.register();
        CoreEntityDataSerializers.register();
        CoreEntities.register();
        CoreMobEffects.register();
        CoreParticles.register();
        CoreRecipeSerializers.register();
        CoreSounds.register();
        CoreBlockEntities.register();

        CuriosProxy.register();

        ArcheryHelper.addValidBow(Items.BOW);
    }

    // region INITIALIZATION
    private void registrySetup(final NewRegistryEvent event) {

        CONFIG_MANAGER.setupCommon();
    }

    private void registerLootData(final RegisterEvent event) {

        if (event.getRegistryKey() == BuiltInRegistries.LOOT_FUNCTION_TYPE.key()) {
            TileNBTSync.setup();
        }
    }

    private void entityLayerSetup(final EntityRenderersEvent.RegisterLayerDefinitions event) {

        event.registerLayerDefinition(ARMOR_FULL_SUIT_LAYER, ArmorFullSuitModel::createBodyLayer);
    }

    private void entityRendererSetup(final EntityRenderersEvent.RegisterRenderers event) {

        event.registerEntityRenderer(THROWN_KNIFE.get(), KnifeRenderer::new);
        event.registerEntityRenderer(ELECTRIC_FIELD.get(), ElectricFieldRenderer::new);
        event.registerEntityRenderer(FROST_FIELD.get(), NothingRenderer::new);
    }

    private void menuScreenSetup(final RegisterMenuScreensEvent event) {

        event.register(FLUID_FILTER_CONTAINER.get(), FluidFilterScreen::new);
        event.register(ITEM_FILTER_CONTAINER.get(), ItemFilterScreen::new);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {

        event.enqueueWork(ArmorEvents::setup);
        event.enqueueWork(CoreFluids::setup);
    }

    private void clientSetup(final FMLClientSetupEvent event) {

        event.enqueueWork(ProxyClient::registerItemModelProperties);
        event.enqueueWork(() -> CoreClientEvents.addNamespace(ID_COFH_CORE));
    }

    private void handleIMC(final InterModProcessEvent event) {

        event.getIMCStream().forEach(
                (msg) -> {
                    if (msg.method().equalsIgnoreCase(IMCMethods.ADD_BOW_COMPATIBILITY) && msg.messageSupplier().get() instanceof Item bow) {
                        ArcheryHelper.addValidBow(bow);
                    } else if (msg.method().equalsIgnoreCase(IMCMethods.ADD_HOLDING_COMPATIBILITY) && msg.messageSupplier().get() instanceof Item container) {
                        HoldingEnchantment.addValidItem(container);
                    }
                }
        );
    }

    private void registerCommands(final RegisterCommandsEvent event) {

        CoFHCommand.register(event.getDispatcher());
    }
    // endregion
}
