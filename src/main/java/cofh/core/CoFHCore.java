package cofh.core;

import cofh.core.client.gui.HeldItemFilterScreen;
import cofh.core.client.gui.TileItemFilterScreen;
import cofh.core.command.CoFHCommand;
import cofh.core.compat.quark.QuarkFlags;
import cofh.core.event.ArmorEvents;
import cofh.core.init.*;
import cofh.core.network.packet.client.*;
import cofh.core.network.packet.server.*;
import cofh.core.util.Proxy;
import cofh.core.util.ProxyClient;
import cofh.lib.capability.CapabilityArchery;
import cofh.lib.capability.CapabilityAreaEffect;
import cofh.lib.capability.CapabilityEnchantableItem;
import cofh.lib.capability.CapabilityShieldItem;
import cofh.lib.loot.TileNBTSync;
import cofh.lib.network.PacketHandler;
import cofh.lib.util.DeferredRegisterCoFH;
import net.minecraft.block.Block;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.particles.ParticleType;
import net.minecraft.potion.Effect;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static cofh.lib.util.constants.Constants.*;
import static cofh.lib.util.references.CoreReferences.HELD_ITEM_FILTER_CONTAINER;
import static cofh.lib.util.references.CoreReferences.TILE_ITEM_FILTER_CONTAINER;

@Mod(ID_COFH_CORE)
public class CoFHCore {

    public static final PacketHandler PACKET_HANDLER = new PacketHandler(new ResourceLocation(ID_COFH_CORE, "general"));
    public static final Logger LOG = LogManager.getLogger(ID_COFH_CORE);
    public static final Proxy PROXY = DistExecutor.unsafeRunForDist(() -> ProxyClient::new, () -> Proxy::new);

    public static final DeferredRegisterCoFH<Block> BLOCKS = DeferredRegisterCoFH.create(ForgeRegistries.BLOCKS, ID_COFH_CORE);
    public static final DeferredRegisterCoFH<Fluid> FLUIDS = DeferredRegisterCoFH.create(ForgeRegistries.FLUIDS, ID_COFH_CORE);
    public static final DeferredRegisterCoFH<Item> ITEMS = DeferredRegisterCoFH.create(ForgeRegistries.ITEMS, ID_COFH_CORE);

    public static final DeferredRegisterCoFH<ContainerType<?>> CONTAINERS = DeferredRegisterCoFH.create(ForgeRegistries.CONTAINERS, ID_COFH_CORE);
    public static final DeferredRegisterCoFH<Effect> EFFECTS = DeferredRegisterCoFH.create(ForgeRegistries.POTIONS, ID_COFH_CORE);
    public static final DeferredRegisterCoFH<Enchantment> ENCHANTMENTS = DeferredRegisterCoFH.create(ForgeRegistries.ENCHANTMENTS, ID_COFH_CORE);
    public static final DeferredRegisterCoFH<ParticleType<?>> PARTICLES = DeferredRegisterCoFH.create(ForgeRegistries.PARTICLE_TYPES, ID_COFH_CORE);
    public static final DeferredRegisterCoFH<TileEntityType<?>> TILE_ENTITIES = DeferredRegisterCoFH.create(ForgeRegistries.TILE_ENTITIES, ID_COFH_CORE);

    public CoFHCore() {

        registerPackets();

        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::clientSetup);

        MinecraftForge.EVENT_BUS.addListener(this::registerCommands);

        BLOCKS.register(modEventBus);
        FLUIDS.register(modEventBus);
        ITEMS.register(modEventBus);

        CONTAINERS.register(modEventBus);
        EFFECTS.register(modEventBus);
        ENCHANTMENTS.register(modEventBus);
        // PARTICLES.register(modEventBus);
        TILE_ENTITIES.register(modEventBus);

        CoreConfig.register();

        CoreBlocks.register();
        CoreFluids.register();
        CoreItems.register();

        CoreContainers.register();
        CoreEffects.register();
        CoreEnchantments.register();
        // CoreParticles.register();
    }

    private void registerPackets() {

        PACKET_HANDLER.registerPacket(PACKET_CONTROL, TileControlPacket::new);
        PACKET_HANDLER.registerPacket(PACKET_GUI, TileGuiPacket::new);
        PACKET_HANDLER.registerPacket(PACKET_REDSTONE, TileRedstonePacket::new);
        PACKET_HANDLER.registerPacket(PACKET_STATE, TileStatePacket::new);

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
    }

    // region INITIALIZATION
    private void commonSetup(final FMLCommonSetupEvent event) {

        CapabilityAreaEffect.register();
        CapabilityArchery.register();
        CapabilityEnchantableItem.register();
        CapabilityShieldItem.register();

        event.enqueueWork(TileNBTSync::setup);

        ArmorEvents.setup();
        QuarkFlags.setup();

        event.enqueueWork(TileNBTSync::setup);
    }

    private void clientSetup(final FMLClientSetupEvent event) {

        ScreenManager.registerFactory(HELD_ITEM_FILTER_CONTAINER, HeldItemFilterScreen::new);
        ScreenManager.registerFactory(TILE_ITEM_FILTER_CONTAINER, TileItemFilterScreen::new);

        CoreKeys.register();

        ProxyClient.registerItemModelProperties();
    }

    private void registerCommands(final RegisterCommandsEvent event) {

        CoFHCommand.initialize(event.getDispatcher());
    }
    // endregion
}
