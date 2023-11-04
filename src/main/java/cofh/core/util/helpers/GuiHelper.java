package cofh.core.util.helpers;

import cofh.core.block.entity.ITileXpHandler;
import cofh.core.client.gui.IGuiAccess;
import cofh.core.client.gui.element.*;
import cofh.core.network.packet.server.ClaimXPPacket;
import cofh.core.network.packet.server.StorageClearPacket;
import cofh.lib.api.block.entity.ITileCallback;
import cofh.lib.api.control.IReconfigurable;
import cofh.lib.energy.EnergyStorageCoFH;
import cofh.lib.fluid.FluidStorageCoFH;
import cofh.lib.inventory.ItemStorageCoFH;
import cofh.lib.xp.XpStorage;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import java.util.Collections;
import java.util.List;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

import static cofh.core.network.packet.server.StorageClearPacket.StorageType.*;
import static cofh.lib.util.Constants.*;
import static cofh.lib.util.helpers.StringHelper.*;
import static net.minecraft.client.gui.screens.Screen.hasControlDown;
import static net.minecraft.client.gui.screens.Screen.hasShiftDown;

public final class GuiHelper {

    private GuiHelper() {

    }

    // region ENERGY
    public static ElementEnergyStorage createDefaultEnergyStorage(IGuiAccess gui, int posX, int posY, EnergyStorageCoFH storage) {

        return createDefaultEnergyStorage(gui, posX, posY, storage, 16, 42, 32, 64);
    }

    public static ElementEnergyStorage createDefaultEnergyStorage(IGuiAccess gui, int posX, int posY, EnergyStorageCoFH storage, int width, int height, int texW, int texH) {

        return (ElementEnergyStorage) new ElementEnergyStorage(gui, posX, posY, storage)
                .setCreativeTexture(PATH_ELEMENTS + "storage_energy_c.png")
                .setTexture(PATH_ELEMENTS + "storage_energy.png", texW, texH)
                .setSize(width, height);
    }

    public static ElementResourceStorage setClearable(ElementEnergyStorage storage, ITileCallback tile, int coil) {

        return storage.setClearStorage(() -> StorageClearPacket.sendToServer(tile, ENERGY, coil));
    }
    // endregion

    // region TANKS
    public static ElementFluidStorage createLargeFluidStorage(IGuiAccess gui, int posX, int posY, FluidStorageCoFH storage) {

        return createDefaultFluidStorage(gui, posX, posY, storage, 18, 62, PATH_ELEMENTS + "storage_fluid_large.png", PATH_ELEMENTS + "overlay_fluid_large.png", 32, 64);
    }

    public static ElementFluidStorage createLargeInputFluidStorage(IGuiAccess gui, int posX, int posY, FluidStorageCoFH storage, IReconfigurable reconfig) {

        return createDefaultFluidStorage(gui, posX, posY, storage, 18, 62, PATH_ELEMENTS + "storage_fluid_large.png", PATH_ELEMENTS + "input_underlay_fluid_large.png", reconfig::hasInputSide, PATH_ELEMENTS + "overlay_fluid_large.png", 32, 64);
    }

    public static ElementFluidStorage createLargeOutputFluidStorage(IGuiAccess gui, int posX, int posY, FluidStorageCoFH storage, IReconfigurable reconfig) {

        return createDefaultFluidStorage(gui, posX, posY, storage, 18, 62, PATH_ELEMENTS + "storage_fluid_large.png", PATH_ELEMENTS + "output_underlay_fluid_large.png", reconfig::hasOutputSide, PATH_ELEMENTS + "overlay_fluid_large.png", 32, 64);
    }

    public static ElementFluidStorage createMediumFluidStorage(IGuiAccess gui, int posX, int posY, FluidStorageCoFH storage) {

        return createDefaultFluidStorage(gui, posX, posY, storage, 18, 42, PATH_ELEMENTS + "storage_fluid_medium.png", PATH_ELEMENTS + "overlay_fluid_medium.png", 32, 64);
    }

    public static ElementFluidStorage createMediumInputFluidStorage(IGuiAccess gui, int posX, int posY, FluidStorageCoFH storage, IReconfigurable reconfig) {

        return createDefaultFluidStorage(gui, posX, posY, storage, 18, 42, PATH_ELEMENTS + "storage_fluid_medium.png", PATH_ELEMENTS + "input_underlay_fluid_medium.png", reconfig::hasInputSide, PATH_ELEMENTS + "overlay_fluid_medium.png", 32, 64);
    }

    public static ElementFluidStorage createMediumOutputFluidStorage(IGuiAccess gui, int posX, int posY, FluidStorageCoFH storage, IReconfigurable reconfig) {

        return createDefaultFluidStorage(gui, posX, posY, storage, 18, 42, PATH_ELEMENTS + "storage_fluid_medium.png", PATH_ELEMENTS + "output_underlay_fluid_medium.png", reconfig::hasOutputSide, PATH_ELEMENTS + "overlay_fluid_medium.png", 32, 64);
    }

    public static ElementFluidStorage createSmallFluidStorage(IGuiAccess gui, int posX, int posY, FluidStorageCoFH storage) {

        return createDefaultFluidStorage(gui, posX, posY, storage, 18, 34, PATH_ELEMENTS + "storage_fluid_small.png", PATH_ELEMENTS + "overlay_fluid_small.png", 32, 64);
    }

    public static ElementFluidStorage createSmallInputFluidStorage(IGuiAccess gui, int posX, int posY, FluidStorageCoFH storage, IReconfigurable reconfig) {

        return createDefaultFluidStorage(gui, posX, posY, storage, 18, 34, PATH_ELEMENTS + "storage_fluid_small.png", PATH_ELEMENTS + "input_underlay_fluid_small.png", reconfig::hasInputSide, PATH_ELEMENTS + "overlay_fluid_small.png", 32, 64);
    }

    public static ElementFluidStorage createSmallOutputFluidStorage(IGuiAccess gui, int posX, int posY, FluidStorageCoFH storage, IReconfigurable reconfig) {

        return createDefaultFluidStorage(gui, posX, posY, storage, 18, 34, PATH_ELEMENTS + "storage_fluid_small.png", PATH_ELEMENTS + "output_underlay_fluid_small.png", reconfig::hasOutputSide, PATH_ELEMENTS + "overlay_fluid_small.png", 32, 64);
    }

    public static ElementFluidStorage createDefaultFluidStorage(IGuiAccess gui, int posX, int posY, FluidStorageCoFH storage, int width, int height, String texture, String overlayTexture, int texW, int texH) {

        return (ElementFluidStorage) new ElementFluidStorage(gui, posX, posY, storage).setOverlayTexture(overlayTexture).setSize(width, height).setTexture(texture, texW, texH);
    }

    public static ElementFluidStorage createDefaultFluidStorage(IGuiAccess gui, int posX, int posY, FluidStorageCoFH storage, int width, int height, String texture, String underlayTexture, Supplier<Boolean> drawUnderlay, String overlayTexture, int texW, int texH) {

        return (ElementFluidStorage) new ElementFluidStorage(gui, posX, posY, storage).setUnderlayTexture(underlayTexture, drawUnderlay).setOverlayTexture(overlayTexture).setSize(width, height).setTexture(texture, texW, texH);
    }

    public static ElementResourceStorage setClearable(ElementFluidStorage storage, ITileCallback tile, int tank) {

        return storage.setClearStorage(() -> StorageClearPacket.sendToServer(tile, FLUID, tank));
    }
    // endregion

    // region SLOTS
    public static ElementSlot createSlot(IGuiAccess gui, int posX, int posY) {

        return createDefaultSlot(gui, posX - 1, posY - 1, 18, 18, PATH_ELEMENTS + "slot.png", 32, 32);
    }

    public static ElementSlot createLockedSlot(IGuiAccess gui, int posX, int posY) {

        return createDefaultSlot(gui, posX - 1, posY - 1, 18, 18, PATH_ELEMENTS + "slot.png", 32, 32).setOverlayTexture(PATH_ELEMENTS + "locked_overlay_slot.png");
    }

    public static ElementSlot createInputSlot(IGuiAccess gui, int posX, int posY, IReconfigurable reconfig) {

        return createDefaultSlot(gui, posX - 1, posY - 1, 18, 18, PATH_ELEMENTS + "slot.png", PATH_ELEMENTS + "input_underlay_slot.png", reconfig::hasInputSide, 32, 32);
    }

    public static ElementSlot createOutputSlot(IGuiAccess gui, int posX, int posY, IReconfigurable reconfig) {

        return createDefaultSlot(gui, posX - 1, posY - 1, 18, 18, PATH_ELEMENTS + "slot.png", PATH_ELEMENTS + "output_underlay_slot.png", reconfig::hasOutputSide, 32, 32);
    }

    public static ElementSlot createLargeSlot(IGuiAccess gui, int posX, int posY) {

        return createDefaultSlot(gui, posX - 1, posY - 1, 26, 26, PATH_ELEMENTS + "slot_large.png", 32, 32);
    }

    public static ElementSlot createLargeInputSlot(IGuiAccess gui, int posX, int posY, IReconfigurable reconfig) {

        return createDefaultSlot(gui, posX - 5, posY - 5, 26, 26, PATH_ELEMENTS + "slot_large.png", PATH_ELEMENTS + "input_underlay_slot_large.png", reconfig::hasInputSide, 32, 32);
    }

    public static ElementSlot createLargeOutputSlot(IGuiAccess gui, int posX, int posY, IReconfigurable reconfig) {

        return createDefaultSlot(gui, posX - 5, posY - 5, 26, 26, PATH_ELEMENTS + "slot_large.png", PATH_ELEMENTS + "output_underlay_slot_large.png", reconfig::hasOutputSide, 32, 32);
    }

    public static ElementSlot createDefaultSlot(IGuiAccess gui, int posX, int posY, int width, int height, String texture, int texW, int texH) {

        return (ElementSlot) new ElementSlot(gui, posX, posY)
                .setSize(width, height)
                .setTexture(texture, texW, texH);
    }

    public static ElementSlot createDefaultSlot(IGuiAccess gui, int posX, int posY, int width, int height, String texture, String overlayTexture, int texW, int texH) {

        return (ElementSlot) new ElementSlot(gui, posX, posY)
                .setOverlayTexture(overlayTexture)
                .setSize(width, height)
                .setTexture(texture, texW, texH);
    }

    public static ElementSlot createDefaultSlot(IGuiAccess gui, int posX, int posY, int width, int height, String texture, String underlayTexture, Supplier<Boolean> drawUnderlay, int texW, int texH) {

        return (ElementSlot) new ElementSlot(gui, posX, posY)
                .setUnderlayTexture(underlayTexture, drawUnderlay)
                .setSize(width, height)
                .setTexture(texture, texW, texH);
    }

    public static ElementSlot createDefaultSlot(IGuiAccess gui, int posX, int posY, int width, int height, String texture, String underlayTexture, Supplier<Boolean> drawUnderlay, String overlayTexture, int texW, int texH) {

        return (ElementSlot) new ElementSlot(gui, posX, posY)
                .setUnderlayTexture(underlayTexture, drawUnderlay)
                .setOverlayTexture(overlayTexture)
                .setSize(width, height)
                .setTexture(texture, texW, texH);
    }

    public static ElementItemStorage createDefaultItemStorage(IGuiAccess gui, int posX, int posY, ItemStorageCoFH storage) {

        return createDefaultItemStorage(gui, posX, posY, storage, 16, 34, 32, 64);
    }

    public static ElementItemStorage createDefaultItemStorage(IGuiAccess gui, int posX, int posY, ItemStorageCoFH storage, int width, int height, int texW, int texH) {

        return (ElementItemStorage) new ElementItemStorage(gui, posX, posY, storage)
                .setCreativeTexture(PATH_ELEMENTS + "storage_item_c.png")
                .setTexture(PATH_ELEMENTS + "storage_item.png", texW, texH)
                .setSize(width, height);
    }

    public static ElementResourceStorage setClearable(ElementItemStorage storage, ITileCallback tile, int slot) {

        return storage.setClearStorage(() -> StorageClearPacket.sendToServer(tile, ITEM, slot));
    }
    // endregion

    // region EXPERIENCE
    public static ElementXpStorage createDefaultXpStorage(IGuiAccess gui, int posX, int posY, XpStorage storage) {

        return createDefaultXpStorage(gui, posX, posY, storage, 16, 16, PATH_ELEMENTS + "storage_xp.png", 16, 80);
    }

    public static ElementXpStorage createDefaultXpStorage(IGuiAccess gui, int posX, int posY, XpStorage storage, int width, int height, String texture, int texW, int texH) {

        return (ElementXpStorage) new ElementXpStorage(gui, posX, posY, storage)
                .setSize(width, height)
                .setTexture(texture, texW, texH);
    }

    public static ElementResourceStorage setClaimable(ElementXpStorage storage, ITileXpHandler tile) {

        return storage.setClaimStorage(() -> ClaimXPPacket.sendToServer(tile));
    }
    // endregion

    // region COMMON UI
    public static ElementScaled createDefaultProgress(IGuiAccess gui, int posX, int posY, ResourceLocation texture, IntSupplier quantitySup) {

        return createDefaultProgress(gui, posX, posY, texture, quantitySup, TRUE);
    }

    public static ElementScaled createDefaultProgress(IGuiAccess gui, int posX, int posY, ResourceLocation texture, IntSupplier quantitySup, Supplier<Boolean> visible) {

        return (ElementScaled) new ElementScaled(gui, posX, posY)
                .setQuantity(quantitySup)
                .setDirection(ElementScaled.StartDirection.LEFT)
                .setSize(PROGRESS, 16)
                .setTexture(texture, 64, 16)
                .setVisible(visible);
    }

    public static ElementScaledFluid createDefaultFluidProgress(IGuiAccess gui, int posX, int posY, ResourceLocation texture, IntSupplier quantitySup, Supplier<FluidStack> fluidSup) {

        return createDefaultFluidProgress(gui, posX, posY, texture, quantitySup, fluidSup, TRUE);
    }

    public static ElementScaledFluid createDefaultFluidProgress(IGuiAccess gui, int posX, int posY, ResourceLocation texture, IntSupplier quantitySup, Supplier<FluidStack> fluidSup, Supplier<Boolean> visible) {

        return (ElementScaledFluid) new ElementScaledFluid(gui, posX, posY)
                .setFluid(fluidSup)
                .setQuantity(quantitySup)
                .setDirection(ElementScaled.StartDirection.LEFT)
                .setSize(PROGRESS, 16)
                .setTexture(texture, 64, 16)
                .setVisible(visible);
    }

    public static ElementScaled createDefaultSpeed(IGuiAccess gui, int posX, int posY, ResourceLocation texture, IntSupplier quantitySup) {

        return (ElementScaled) new ElementScaled(gui, posX, posY)
                .setQuantity(quantitySup)
                .setSize(16, SPEED)
                .setTexture(texture, 32, 16);
    }

    public static ElementScaled createDefaultDuration(IGuiAccess gui, int posX, int posY, ResourceLocation texture, IntSupplier quantitySup) {

        return (ElementScaled) new ElementScaled(gui, posX, posY)
                .setQuantity(quantitySup)
                .setSize(16, DURATION)
                .setTexture(texture, 32, 16);
    }
    // endregion

    // region BUTTONS AND TOOLTIPS
    public static List<Component> createDecControlTooltip(ElementBase element, int mouseX, int mouseY) {

        if (element.enabled()) {
            int change = 1000;

            if (hasShiftDown()) {
                change *= 10;
            }
            if (hasControlDown()) {
                change /= 100;
            }
            return Collections.singletonList(Component.literal(
                    localize("info.cofh.decrease_by")
                            + " " + format(change)
                            + "/" + format(change / 10)));
        }
        return Collections.emptyList();
    }

    public static List<Component> createIncControlTooltip(ElementBase element, int mouseX, int mouseY) {

        if (element.enabled()) {
            int change = 1000;

            if (hasShiftDown()) {
                change *= 10;
            }
            if (hasControlDown()) {
                change /= 100;
            }
            return Collections.singletonList(Component.literal(
                    localize("info.cofh.increase_by")
                            + " " + format(change)
                            + "/" + format(change / 10)));
        }
        return Collections.emptyList();
    }

    public static int getChangeAmount(int mouseButton) {

        int change = 1000;

        if (hasShiftDown()) {
            change *= 10;
        }
        if (hasControlDown()) {
            change /= 100;
        }
        if (mouseButton == 1) {
            change /= 10;
        }
        return change;
    }

    public static float getPitch(int mouseButton) {

        float pitch = 0.7F;

        if (hasShiftDown()) {
            pitch += 0.1F;
        }
        if (hasControlDown()) {
            pitch -= 0.2F;
        }
        if (mouseButton == 1) {
            pitch -= 0.1F;
        }
        return pitch;
    }
    // endregion

    // region CONSTANTS
    public static final int SLOT_SIZE_INNER = 16;
    public static final int SLOT_SIZE = 18;
    public static final int LARGE_SLOT_SIZE_INNER = 24;
    public static final int LARGE_SLOT_SIZE = 26;

    public static final int DURATION = 16;
    public static final int PROGRESS = 24;
    public static final int SPEED = 16;
    public static final int HEIGHT = 16;

    public static final int PLAYER_INV_SIZE = 36;
    public static final int PRIMARY_HIGHLIGHT_COLOR = 0x700A76D0;   // INPUT BLUE
    public static final int SECONDARY_HIGHLIGHT_COLOR = 0x7076D00A; // HUE - 120
    public static final int TERTIARY_HIGHLIGHT_COLOR = 0x700AD064;  // HUE - 60
    // endregion

    // region ELEMENTS
    public static final String BUTTON_18 = PATH_ELEMENTS + "button_18.png";
    public static final String BUTTON_18_HIGHLIGHT = PATH_ELEMENTS + "button_18_highlight.png";
    public static final String BUTTON_18_INACTIVE = PATH_ELEMENTS + "button_18_inactive.png";

    public static final ResourceLocation ICON_ACCESS_PUBLIC = new ResourceLocation(PATH_ICONS + "icon_access_public.png");
    public static final ResourceLocation ICON_ACCESS_PRIVATE = new ResourceLocation(PATH_ICONS + "icon_access_private.png");
    public static final ResourceLocation ICON_ACCESS_FRIENDS = new ResourceLocation(PATH_ICONS + "icon_access_friends.png");
    public static final ResourceLocation ICON_ACCESS_TEAM = new ResourceLocation(PATH_ICONS + "icon_access_team.png");

    public static final ResourceLocation ICON_ACCEPT = new ResourceLocation(PATH_ICONS + "icon_accept.png");
    public static final ResourceLocation ICON_ACCEPT_INACTIVE = new ResourceLocation(PATH_ICONS + "icon_accept_inactive.png");
    public static final ResourceLocation ICON_AUGMENT = new ResourceLocation(PATH_ICONS + "icon_augment.png");
    public static final ResourceLocation ICON_BUTTON = new ResourceLocation(PATH_ICONS + "icon_button.png");
    public static final ResourceLocation ICON_BUTTON_HIGHLIGHT = new ResourceLocation(PATH_ICONS + "icon_button_highlight.png");
    public static final ResourceLocation ICON_BUTTON_INACTIVE = new ResourceLocation(PATH_ICONS + "icon_button_inactive.png");
    public static final ResourceLocation ICON_CANCEL = new ResourceLocation(PATH_ICONS + "icon_cancel.png");
    public static final ResourceLocation ICON_CANCEL_INACTIVE = new ResourceLocation(PATH_ICONS + "icon_cancel_inactive.png");
    public static final ResourceLocation ICON_CONFIG = new ResourceLocation(PATH_ICONS + "icon_config.png");
    public static final ResourceLocation ICON_ENCHANTMENT = new ResourceLocation(PATH_ICONS + "icon_enchantment.png");
    public static final ResourceLocation ICON_ENERGY = new ResourceLocation(PATH_ICONS + "icon_energy.png");
    public static final ResourceLocation ICON_NOPE = new ResourceLocation(PATH_ICONS + "icon_nope.png");
    public static final ResourceLocation ICON_INFORMATION = new ResourceLocation(PATH_ICONS + "icon_information.png");
    public static final ResourceLocation ICON_STEAM = new ResourceLocation(PATH_ICONS + "icon_steam.png");
    public static final ResourceLocation ICON_TUTORIAL = new ResourceLocation(PATH_ICONS + "icon_tutorial.png");

    public static final ResourceLocation ICON_INPUT = new ResourceLocation(PATH_ICONS + "icon_input.png");
    public static final ResourceLocation ICON_OUTPUT = new ResourceLocation(PATH_ICONS + "icon_output.png");

    public static final ResourceLocation ICON_REDSTONE_OFF = new ResourceLocation(PATH_ICONS + "icon_redstone_off.png");
    public static final ResourceLocation ICON_REDSTONE_ON = new ResourceLocation(PATH_ICONS + "icon_redstone_on.png");

    public static final ResourceLocation ICON_RS_TORCH_OFF = new ResourceLocation(PATH_ICONS + "icon_rs_torch_off.png");
    public static final ResourceLocation ICON_RS_TORCH_ON = new ResourceLocation(PATH_ICONS + "icon_rs_torch_on.png");

    public static final ResourceLocation ICON_ARROW_DOWN = new ResourceLocation(PATH_ICONS + "icon_arrow_down.png");
    public static final ResourceLocation ICON_ARROW_DOWN_INACTIVE = new ResourceLocation(PATH_ICONS + "icon_arrow_down_inactive.png");

    public static final ResourceLocation ICON_ARROW_UP = new ResourceLocation(PATH_ICONS + "icon_arrow_up.png");
    public static final ResourceLocation ICON_ARROW_UP_INACTIVE = new ResourceLocation(PATH_ICONS + "icon_arrow_up_inactive.png");

    public static final ResourceLocation INFO_INPUT = new ResourceLocation(PATH_ELEMENTS + "info_input.png");
    public static final ResourceLocation INFO_OUTPUT = new ResourceLocation(PATH_ELEMENTS + "info_output.png");

    public static final ResourceLocation NAV_BACK = new ResourceLocation(PATH_ELEMENTS + "nav_back.png");
    public static final ResourceLocation NAV_FILTER = new ResourceLocation(PATH_ELEMENTS + "nav_filter.png");

    public static final ResourceLocation TAB_BOTTOM = new ResourceLocation(PATH_ELEMENTS + "tab_bottom.png");
    public static final ResourceLocation TAB_TOP = new ResourceLocation(PATH_ELEMENTS + "tab_top.png");

    public static final ResourceLocation PROG_ARROW_LEFT = new ResourceLocation(PATH_ELEMENTS + "progress_arrow_left.png");
    public static final ResourceLocation PROG_ARROW_RIGHT = new ResourceLocation(PATH_ELEMENTS + "progress_arrow_right.png");
    public static final ResourceLocation PROG_ARROW_FLUID_LEFT = new ResourceLocation(PATH_ELEMENTS + "progress_arrow_fluid_left.png");
    public static final ResourceLocation PROG_ARROW_FLUID_RIGHT = new ResourceLocation(PATH_ELEMENTS + "progress_arrow_fluid_right.png");
    public static final ResourceLocation PROG_DROP_LEFT = new ResourceLocation(PATH_ELEMENTS + "progress_fluid_left.png");
    public static final ResourceLocation PROG_DROP_RIGHT = new ResourceLocation(PATH_ELEMENTS + "progress_fluid_right.png");

    public static final ResourceLocation SCALE_ALCHEMY = new ResourceLocation(PATH_ELEMENTS + "scale_alchemy.png");
    public static final ResourceLocation SCALE_BOOK = new ResourceLocation(PATH_ELEMENTS + "scale_book.png");
    public static final ResourceLocation SCALE_BUBBLE = new ResourceLocation(PATH_ELEMENTS + "scale_bubble.png");
    public static final ResourceLocation SCALE_COMPACT = new ResourceLocation(PATH_ELEMENTS + "scale_compact.png");
    public static final ResourceLocation SCALE_CRUSH = new ResourceLocation(PATH_ELEMENTS + "scale_crush.png");
    public static final ResourceLocation SCALE_FLAME = new ResourceLocation(PATH_ELEMENTS + "scale_flame.png");
    public static final ResourceLocation SCALE_FLAME_GREEN = new ResourceLocation(PATH_ELEMENTS + "scale_flame_green.png");
    public static final ResourceLocation SCALE_FLUX = new ResourceLocation(PATH_ELEMENTS + "scale_flux.png");
    public static final ResourceLocation SCALE_SAW = new ResourceLocation(PATH_ELEMENTS + "scale_saw.png");
    public static final ResourceLocation SCALE_SPIN = new ResourceLocation(PATH_ELEMENTS + "scale_spin.png");
    public static final ResourceLocation SCALE_SUN = new ResourceLocation(PATH_ELEMENTS + "scale_sun.png");
    public static final ResourceLocation SCALE_SNOWFLAKE = new ResourceLocation(PATH_ELEMENTS + "scale_snowflake.png");
    // endregion

    // region PANELS
    public static String generatePanelInfo(String key) {

        int i = 0;
        String line = key + "." + i;
        StringBuilder builder = new StringBuilder();
        while (canLocalize(line)) {
            if (i > 0) {
                builder.append("\n\n");
            }
            builder.append(localize(line));
            ++i;
            line = key + "." + i;
        }
        return builder.toString();
    }

    public static String appendLine(String existing, String line) {

        return existing + "\n\n" + localize(line);
    }
    // endregion
}
