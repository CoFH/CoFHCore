package cofh.core;

import cofh.core.client.gui.HeldItemFilterScreen;
import cofh.core.client.gui.TileItemFilterScreen;
import cofh.core.command.CoFHCommand;
import cofh.core.compat.curios.CuriosProxy;
import cofh.core.compat.quark.QuarkFlags;
import cofh.core.event.ArmorEvents;
import cofh.core.init.*;
import cofh.core.network.packet.client.*;
import cofh.core.network.packet.server.*;
import cofh.core.util.Proxy;
import cofh.core.util.ProxyClient;
import cofh.core.util.helpers.FluidHelper;
import cofh.lib.capability.CapabilityArchery;
import cofh.lib.capability.CapabilityAreaEffect;
import cofh.lib.capability.CapabilityEnchantableItem;
import cofh.lib.capability.CapabilityShieldItem;
import cofh.lib.client.renderer.entity.ElectricArcRenderer;
import cofh.lib.client.renderer.entity.KnifeRenderer;
import cofh.lib.client.renderer.entity.NothingRenderer;
import cofh.lib.item.impl.SpawnEggItemCoFH;
import cofh.lib.loot.TileNBTSync;
import cofh.lib.network.PacketHandler;
import cofh.lib.util.DeferredRegisterCoFH;
<<<<<<< HEAD
import net.minecraft.block.Block;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.particles.ParticleType;
import net.minecraft.potion.Effect;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
=======
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Fluid;
>>>>>>> caa1a35 (Initial 1.18.2 compile pass.)
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static cofh.lib.util.constants.Constants.*;
import static cofh.lib.util.references.CoreReferences.*;

@Mod (ID_COFH_CORE)
public class CoFHCore {

    public static final PacketHandler PACKET_HANDLER = new PacketHandler(new ResourceLocation(ID_COFH_CORE, "general"));
    public static final Logger LOG = LogManager.getLogger(ID_COFH_CORE);
    public static final Proxy PROXY = DistExecutor.unsafeRunForDist(() -> ProxyClient::new, () -> Proxy::new);

    public static final DeferredRegisterCoFH<Block> BLOCKS = DeferredRegisterCoFH.create(ForgeRegistries.BLOCKS, ID_COFH_CORE);
    public static final DeferredRegisterCoFH<Fluid> FLUIDS = DeferredRegisterCoFH.create(ForgeRegistries.FLUIDS, ID_COFH_CORE);
    public static final DeferredRegisterCoFH<Item> ITEMS = DeferredRegisterCoFH.create(ForgeRegistries.ITEMS, ID_COFH_CORE);
    public static final DeferredRegisterCoFH<EntityType<?>> ENTITIES = DeferredRegisterCoFH.create(ForgeRegistries.ENTITIES, ID_COFH_CORE);

    public static final DeferredRegisterCoFH<MenuType<?>> CONTAINERS = DeferredRegisterCoFH.create(ForgeRegistries.CONTAINERS, ID_COFH_CORE);
    public static final DeferredRegisterCoFH<MobEffect> EFFECTS = DeferredRegisterCoFH.create(ForgeRegistries.MOB_EFFECTS, ID_COFH_CORE);
    public static final DeferredRegisterCoFH<Enchantment> ENCHANTMENTS = DeferredRegisterCoFH.create(ForgeRegistries.ENCHANTMENTS, ID_COFH_CORE);
    public static final DeferredRegisterCoFH<ParticleType<?>> PARTICLES = DeferredRegisterCoFH.create(ForgeRegistries.PARTICLE_TYPES, ID_COFH_CORE);
<<<<<<< HEAD
    public static final DeferredRegisterCoFH<IRecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegisterCoFH.create(ForgeRegistries.RECIPE_SERIALIZERS, ID_COFH_CORE);
    public static final DeferredRegisterCoFH<SoundEvent> SOUND_EVENTS = DeferredRegisterCoFH.create(ForgeRegistries.SOUND_EVENTS, ID_COFH_CORE);
    public static final DeferredRegisterCoFH<TileEntityType<?>> TILE_ENTITIES = DeferredRegisterCoFH.create(ForgeRegistries.TILE_ENTITIES, ID_COFH_CORE);
=======
    public static final DeferredRegisterCoFH<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegisterCoFH.create(ForgeRegistries.RECIPE_SERIALIZERS, ID_COFH_CORE);
    public static final DeferredRegisterCoFH<BlockEntityType<?>> TILE_ENTITIES = DeferredRegisterCoFH.create(ForgeRegistries.BLOCK_ENTITIES, ID_COFH_CORE);
>>>>>>> caa1a35 (Initial 1.18.2 compile pass.)

    public static boolean curiosLoaded = false;

    public CoFHCore() {

        curiosLoaded = ModList.get().isLoaded(ID_CURIOS);

        registerPackets();

        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(this::registerCapabilities);
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::clientSetup);

        MinecraftForge.EVENT_BUS.addListener(this::registerCommands);

        BLOCKS.register(modEventBus);
        FLUIDS.register(modEventBus);
        ITEMS.register(modEventBus);
        ENTITIES.register(modEventBus);

        PARTICLES.register(modEventBus);
        CONTAINERS.register(modEventBus);
        EFFECTS.register(modEventBus);
        ENCHANTMENTS.register(modEventBus);
        RECIPE_SERIALIZERS.register(modEventBus);
        SOUND_EVENTS.register(modEventBus);
        TILE_ENTITIES.register(modEventBus);

        CoreConfig.register();

        CoreBlocks.register();
        CoreFluids.register();
        CoreItems.register();
        CoreEntities.register();

        CoreParticles.register();
        CoreContainers.register();
        CoreEffects.register();
        CoreEnchantments.register();
        CoreRecipeSerializers.register();
        CoreSounds.register();

        CuriosProxy.register();
    }

    private void registerPackets() {

        PACKET_HANDLER.registerPacket(PACKET_CONTROL, TileControlPacket::new);
        PACKET_HANDLER.registerPacket(PACKET_GUI, TileGuiPacket::new);
        PACKET_HANDLER.registerPacket(PACKET_REDSTONE, TileRedstonePacket::new);
        PACKET_HANDLER.registerPacket(PACKET_STATE, TileStatePacket::new);
        PACKET_HANDLER.registerPacket(PACKET_RENDER, TileRenderPacket::new);

        PACKET_HANDLER.registerPacket(PACKET_MODEL_UPDATE, ModelUpdatePacket::new);

        PACKET_HANDLER.registerPacket(PACKET_CHAT, IndexedChatPacket::new);
        PACKET_HANDLER.registerPacket(PACKET_MOTION, PlayerMotionPacket::new);

        PACKET_HANDLER.registerPacket(PACKET_GUI_OPEN, FilterGuiOpenPacket::new);

        PACKET_HANDLER.registerPacket(PACKET_CONTAINER, ContainerPacket::new);
        PACKET_HANDLER.registerPacket(PACKET_SECURITY, SecurityPacket::new);

        PACKET_HANDLER.registerPacket(PACKET_CONFIG, TileConfigPacket::new);
        PACKET_HANDLER.registerPacket(PACKET_SECURITY_CONTROL, SecurityControlPacket::new);
        PACKET_HANDLER.registerPacket(PACKET_REDSTONE_CONTROL, RedstoneControlPacket::new);
        PACKET_HANDLER.registerPacket(PACKET_TRANSFER_CONTROL, TransferControlPacket::new);
        PACKET_HANDLER.registerPacket(PACKET_SIDE_CONFIG, SideConfigPacket::new);
        PACKET_HANDLER.registerPacket(PACKET_STORAGE_CLEAR, StorageClearPacket::new);
        PACKET_HANDLER.registerPacket(PACKET_CLAIM_XP, ClaimXPPacket::new);

        PACKET_HANDLER.registerPacket(PACKET_ITEM_MODE_CHANGE, ItemModeChangePacket::new);
        PACKET_HANDLER.registerPacket(PACKET_ITEM_LEFT_CLICK, ItemLeftClickPacket::new);

        PACKET_HANDLER.registerPacket(PACKET_EFFECT_ADD, EffectAddedPacket::new);
        PACKET_HANDLER.registerPacket(PACKET_EFFECT_REMOVE, EffectRemovedPacket::new);
    }

    // region INITIALIZATION
    private void registerCapabilities(RegisterCapabilitiesEvent event) {

        CapabilityArchery.register(event);
        CapabilityAreaEffect.register(event);
        CapabilityEnchantableItem.register(event);
        CapabilityShieldItem.register(event);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {

        event.enqueueWork(SpawnEggItemCoFH::setup);

        event.enqueueWork(TileNBTSync::setup);

        ArmorEvents.setup();
        QuarkFlags.setup();

        FluidHelper.init();
    }

    private void clientSetup(final FMLClientSetupEvent event) {

        MenuScreens.register(HELD_ITEM_FILTER_CONTAINER, HeldItemFilterScreen::new);
        MenuScreens.register(TILE_ITEM_FILTER_CONTAINER, TileItemFilterScreen::new);

        CoreKeys.register();

        ProxyClient.registerItemModelProperties();
        this.registerEntityRenderingHandlers();
    }

    private void registerCommands(final RegisterCommandsEvent event) {

        CoFHCommand.initialize(event.getDispatcher());
    }
    // endregion

    // region HELPERS
    private void registerEntityRenderingHandlers() {

<<<<<<< HEAD
        RenderingRegistry.registerEntityRenderingHandler(KNIFE_ENTITY, KnifeRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(ELECTRIC_ARC_ENTITY, ElectricArcRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(ELECTRIC_FIELD_ENTITY, NothingRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(BLACK_HOLE_ENTITY, NothingRenderer::new);
=======
        // TODO Covers, there is an event for this now.
//        EntityRenderers.register(KNIFE_ENTITY, KnifeRenderer::new);
>>>>>>> caa1a35 (Initial 1.18.2 compile pass.)
    }
    // endregion
}
