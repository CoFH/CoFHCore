package cofh.core;

import cofh.core.capability.CapabilityArchery;
import cofh.core.capability.CapabilityAreaEffect;
import cofh.core.capability.CapabilityShieldItem;
import cofh.core.client.gui.FluidFilterScreen;
import cofh.core.client.gui.ItemFilterScreen;
import cofh.core.client.renderer.entity.model.ArmorFullSuitModel;
import cofh.core.client.renderer.entity.model.ElectricArcRenderer;
import cofh.core.client.renderer.entity.model.KnifeRenderer;
import cofh.core.command.CoFHCommand;
import cofh.core.compat.curios.CuriosProxy;
import cofh.core.compat.quark.QuarkFlags;
import cofh.core.config.*;
import cofh.core.enchantment.HoldingEnchantment;
import cofh.core.event.ArmorEvents;
import cofh.core.event.CoreClientEvents;
import cofh.core.init.*;
import cofh.core.network.packet.PacketIDs;
import cofh.core.network.packet.client.*;
import cofh.core.network.packet.server.*;
import cofh.core.util.Proxy;
import cofh.core.util.ProxyClient;
import cofh.core.util.helpers.ArcheryHelper;
import cofh.core.util.references.IMCMethods;
import cofh.lib.client.renderer.entity.NothingRenderer;
import cofh.lib.loot.TileNBTSync;
import cofh.lib.network.PacketHandler;
import cofh.lib.util.DeferredRegisterCoFH;
import cofh.lib.util.Utils;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.resources.ResourceLocation;
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
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
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

    public static final DeferredRegisterCoFH<Block> BLOCKS = DeferredRegisterCoFH.create(ForgeRegistries.BLOCKS, ID_COFH_CORE);
    public static final DeferredRegisterCoFH<Item> ITEMS = DeferredRegisterCoFH.create(ForgeRegistries.ITEMS, ID_COFH_CORE);
    public static final DeferredRegisterCoFH<Fluid> FLUIDS = DeferredRegisterCoFH.create(ForgeRegistries.FLUIDS, ID_COFH_CORE);

    public static final DeferredRegisterCoFH<MenuType<?>> CONTAINERS = DeferredRegisterCoFH.create(ForgeRegistries.MENU_TYPES, ID_COFH_CORE);
    public static final DeferredRegisterCoFH<Enchantment> ENCHANTMENTS = DeferredRegisterCoFH.create(ForgeRegistries.ENCHANTMENTS, ID_COFH_CORE);
    public static final DeferredRegisterCoFH<EntityType<?>> ENTITIES = DeferredRegisterCoFH.create(ForgeRegistries.ENTITY_TYPES, ID_COFH_CORE);
    public static final DeferredRegisterCoFH<MobEffect> MOB_EFFECTS = DeferredRegisterCoFH.create(ForgeRegistries.MOB_EFFECTS, ID_COFH_CORE);
    public static final DeferredRegisterCoFH<ParticleType<?>> PARTICLES = DeferredRegisterCoFH.create(ForgeRegistries.PARTICLE_TYPES, ID_COFH_CORE);
    public static final DeferredRegisterCoFH<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegisterCoFH.create(ForgeRegistries.RECIPE_SERIALIZERS, ID_COFH_CORE);
    public static final DeferredRegisterCoFH<SoundEvent> SOUND_EVENTS = DeferredRegisterCoFH.create(ForgeRegistries.SOUND_EVENTS, ID_COFH_CORE);
    public static final DeferredRegisterCoFH<BlockEntityType<?>> TILE_ENTITIES = DeferredRegisterCoFH.create(ForgeRegistries.BLOCK_ENTITY_TYPES, ID_COFH_CORE);

    public static final DeferredRegisterCoFH<FluidType> FLUID_TYPES = DeferredRegisterCoFH.create(ForgeRegistries.Keys.FLUID_TYPES, ID_COFH_CORE);

    public static boolean curiosLoaded = false;

    public CoFHCore() {

        ForgeMod.enableMilkFluid();

        curiosLoaded = Utils.isModLoaded(ID_CURIOS);

        registerPackets();

        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(this::entityLayerSetup);
        modEventBus.addListener(this::entityRendererSetup);
        modEventBus.addListener(this::capSetup);
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::clientSetup);
        modEventBus.addListener(this::handleIMC);
        modEventBus.addListener(this::registerLootData);

        MinecraftForge.EVENT_BUS.addListener(this::registerCommands);

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

        FLUID_TYPES.register(modEventBus);

        CONFIG_MANAGER.register(modEventBus)
                .addClientConfig(new CoreClientConfig())
                .addServerConfig(new CoreServerConfig())
                .addServerConfig(new CoreCommandConfig())
                .addServerConfig(new CoreEnchantConfig());
        CONFIG_MANAGER.setupClient();
        CONFIG_MANAGER.setupServer();

        CoreItems.register();
        CoreBlocks.register();
        CoreFluids.register();

        CoreContainers.register();
        CoreEnchantments.register();
        CoreEntities.register();
        CoreMobEffects.register();
        CoreParticles.register();
        CoreRecipeSerializers.register();
        CoreSounds.register();
        CoreTileEntities.register();

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

        PACKET_HANDLER.registerPacket(PacketIDs.PACKET_MOTION, PlayerMotionPacket::new);

        PACKET_HANDLER.registerPacket(PacketIDs.PACKET_FILTERABLE_GUI_OPEN, TileFilterGuiOpenPacket::new);
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

        event.registerEntityRenderer(KNIFE.get(), KnifeRenderer::new);
        event.registerEntityRenderer(ELECTRIC_ARC.get(), ElectricArcRenderer::new);
        event.registerEntityRenderer(ELECTRIC_FIELD.get(), NothingRenderer::new);
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
