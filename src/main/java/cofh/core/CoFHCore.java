package cofh.core;

import cofh.core.client.event.CoreClientEvents;
import cofh.core.client.gui.FluidFilterScreen;
import cofh.core.client.gui.ItemFilterScreen;
import cofh.core.client.renderer.entity.ElectricFieldRenderer;
import cofh.core.client.renderer.entity.KnifeRenderer;
import cofh.core.client.renderer.entity.model.ArmorFullSuitModel;
import cofh.core.common.capability.CapabilityArchery;
import cofh.core.common.capability.CapabilityAreaEffect;
import cofh.core.common.capability.CapabilityShieldItem;
import cofh.core.common.command.CoFHCommand;
import cofh.core.common.config.*;
import cofh.core.common.enchantment.HoldingEnchantment;
import cofh.core.common.event.ArmorEvents;
import cofh.core.common.network.packet.PacketIDs;
import cofh.core.common.network.packet.client.*;
import cofh.core.common.network.packet.server.*;
import cofh.core.compat.curios.CuriosProxy;
import cofh.core.compat.quark.QuarkFlags;
import cofh.core.init.*;
import cofh.core.util.CoreFlags;
import cofh.core.util.Proxy;
import cofh.core.util.ProxyClient;
import cofh.core.util.crafting.CustomIngredients;
import cofh.core.util.helpers.ArcheryHelper;
import cofh.core.util.references.IMCMethods;
import cofh.lib.client.renderer.entity.NothingRenderer;
import cofh.lib.common.loot.TileNBTSync;
import cofh.lib.common.network.PacketHandler;
import cofh.lib.util.Utils;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.DistExecutor;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.InterModProcessEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.common.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static cofh.core.client.renderer.entity.model.ArmorFullSuitModel.ARMOR_FULL_SUIT_LAYER;
import static cofh.core.init.CoreContainers.FLUID_FILTER_CONTAINER;
import static cofh.core.init.CoreContainers.ITEM_FILTER_CONTAINER;
import static cofh.core.init.CoreEntities.*;
import static cofh.lib.util.constants.ModIds.ID_COFH_CORE;
import static cofh.lib.util.constants.ModIds.ID_CURIOS;

@Mod (ID_COFH_CORE)
public class CoFHCore {

    public static final Logger LOG = LogManager.getLogger(ID_COFH_CORE);

    public static final ConfigManager CONFIG_MANAGER = new ConfigManager();
    public static final PacketHandler PACKET_HANDLER = new PacketHandler(new ResourceLocation(ID_COFH_CORE, "general"), LOG);
    public static final Proxy PROXY = DistExecutor.unsafeRunForDist(() -> ProxyClient::new, () -> Proxy::new);

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(ID_COFH_CORE);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ID_COFH_CORE);
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(BuiltInRegistries.FLUID, ID_COFH_CORE);

    public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(BuiltInRegistries.MENU, ID_COFH_CORE);
    public static final DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister.create(BuiltInRegistries.ENCHANTMENT, ID_COFH_CORE);
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, ID_COFH_CORE);
    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(BuiltInRegistries.MOB_EFFECT, ID_COFH_CORE);
    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, ID_COFH_CORE);
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, ID_COFH_CORE);
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, ID_COFH_CORE);
    public static final DeferredRegister<BlockEntityType<?>> TILE_ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, ID_COFH_CORE);

    public static final DeferredRegister<EntityDataSerializer<?>> ENTITY_DATA_SERIALIZERS = DeferredRegister.create(NeoForgeRegistries.Keys.ENTITY_DATA_SERIALIZERS, ID_COFH_CORE);
    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.FLUID_TYPES, ID_COFH_CORE);

    public static boolean curiosLoaded = false;

    public CoFHCore(ModContainer modContainer, IEventBus modEventBus) {

        NeoForgeMod.enableMilkFluid();

        curiosLoaded = Utils.isModLoaded(ID_CURIOS);

        registerPackets();

        modEventBus.addListener(this::registrySetup);
        modEventBus.addListener(this::entityLayerSetup);
        modEventBus.addListener(this::entityRendererSetup);
        modEventBus.addListener(this::capSetup);
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::clientSetup);
        modEventBus.addListener(this::handleIMC);
        modEventBus.addListener(this::registerLootData);

        NeoForge.EVENT_BUS.addListener(this::registerCommands);

        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        FLUIDS.register(modEventBus);

        CONTAINERS.register(modEventBus);
        ENTITY_DATA_SERIALIZERS.register(modEventBus);
        ENCHANTMENTS.register(modEventBus);
        ENTITIES.register(modEventBus);
        MOB_EFFECTS.register(modEventBus);
        PARTICLES.register(modEventBus);
        RECIPE_SERIALIZERS.register(modEventBus);
        SOUND_EVENTS.register(modEventBus);
        TILE_ENTITIES.register(modEventBus);

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

        CoreContainers.register();
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

    private void registerPackets() {

        PACKET_HANDLER.registerPacket(PacketIDs.PACKET_CONTROL, TileControlPacket::new);
        PACKET_HANDLER.registerPacket(PacketIDs.PACKET_GUI, TileGuiPacket::new);
        PACKET_HANDLER.registerPacket(PacketIDs.PACKET_REDSTONE, TileRedstonePacket::new);
        PACKET_HANDLER.registerPacket(PacketIDs.PACKET_STATE, TileStatePacket::new);
        PACKET_HANDLER.registerPacket(PacketIDs.PACKET_RENDER, TileRenderPacket::new);

        PACKET_HANDLER.registerPacket(PacketIDs.PACKET_MODEL_UPDATE, ModelUpdatePacket::new);

        PACKET_HANDLER.registerPacket(PacketIDs.PACKET_OVERLAY, OverlayMessagePacket::new);
        PACKET_HANDLER.registerPacket(PacketIDs.PACKET_MOTION, PlayerMotionPacket::new);

        PACKET_HANDLER.registerPacket(PacketIDs.PACKET_FILTERABLE_GUI_OPEN, FilterableGuiTogglePacket::new);
        PACKET_HANDLER.registerPacket(PacketIDs.PACKET_GHOST_ITEM, GhostItemPacket::new);

        PACKET_HANDLER.registerPacket(PacketIDs.PACKET_CONTAINER_CONFIG, ContainerConfigPacket::new);
        PACKET_HANDLER.registerPacket(PacketIDs.PACKET_CONTAINER_GUI, ContainerGuiPacket::new);

        PACKET_HANDLER.registerPacket(PacketIDs.PACKET_SECURITY, SecurityPacket::new);

        PACKET_HANDLER.registerPacket(PacketIDs.PACKET_CONFIG, TileConfigPacket::new);
        PACKET_HANDLER.registerPacket(PacketIDs.PACKET_SECURITY_CONTROL, SecurityControlPacket::new);
        PACKET_HANDLER.registerPacket(PacketIDs.PACKET_REDSTONE_CONTROL, RedstoneControlPacket::new);
        PACKET_HANDLER.registerPacket(PacketIDs.PACKET_TRANSFER_CONTROL, TransferControlPacket::new);
        PACKET_HANDLER.registerPacket(PacketIDs.PACKET_SIDE_CONFIG, SideConfigPacket::new);
        PACKET_HANDLER.registerPacket(PacketIDs.PACKET_STORAGE_CLEAR, StorageClearPacket::new);
        PACKET_HANDLER.registerPacket(PacketIDs.PACKET_CLAIM_XP, ClaimXPPacket::new);

        PACKET_HANDLER.registerPacket(PacketIDs.PACKET_ITEM_MODE_CHANGE, ItemModeChangePacket::new);
        PACKET_HANDLER.registerPacket(PacketIDs.PACKET_ITEM_LEFT_CLICK, ItemLeftClickPacket::new);
        PACKET_HANDLER.registerPacket(PacketIDs.PACKET_ITEM_RAYTRACE_BLOCK, ItemRayTraceBlockPacket::new);
        PACKET_HANDLER.registerPacket(PacketIDs.PACKET_ITEM_RAYTRACE_ENTITY, ItemRayTraceEntityPacket::new);

        PACKET_HANDLER.registerPacket(PacketIDs.PACKET_EFFECT_ADD, EffectAddedPacket::new);
        PACKET_HANDLER.registerPacket(PacketIDs.PACKET_EFFECT_REMOVE, EffectRemovedPacket::new);
    }

    // region INITIALIZATION
    private void registrySetup(final NewRegistryEvent event) {

        CONFIG_MANAGER.setupCommon();
    }

    private void registerLootData(final RegisterEvent event) {

        if (event.getRegistryKey() == ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS) {
            CoreFlags.manager().setup();
            QuarkFlags.setup();
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

    private void capSetup(RegisterCapabilitiesEvent event) {

        CapabilityArchery.register(event);
        CapabilityAreaEffect.register(event);
        CapabilityShieldItem.register(event);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {

        event.enqueueWork(TileNBTSync::setup);
        event.enqueueWork(ArmorEvents::setup);
        event.enqueueWork(CoreFluids::setup);
        event.enqueueWork(CustomIngredients::setup);
    }

    private void clientSetup(final FMLClientSetupEvent event) {

        event.enqueueWork(() -> {
            MenuScreens.register(FLUID_FILTER_CONTAINER.get(), FluidFilterScreen::new);
            MenuScreens.register(ITEM_FILTER_CONTAINER.get(), ItemFilterScreen::new);
        });
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
