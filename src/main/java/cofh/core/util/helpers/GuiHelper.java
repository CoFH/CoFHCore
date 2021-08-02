package cofh.core.util.helpers;

import cofh.core.client.gui.element.*;
import cofh.core.network.packet.server.ClaimXPPacket;
import cofh.core.network.packet.server.StorageClearPacket;
import cofh.core.tileentity.TileCoFH;
import cofh.lib.client.gui.IGuiAccess;
import cofh.lib.energy.EnergyStorageCoFH;
import cofh.lib.fluid.FluidStorageCoFH;
import cofh.lib.util.control.IReconfigurable;
import cofh.lib.xp.XpStorage;
import net.minecraftforge.fluids.FluidStack;

import java.util.function.BooleanSupplier;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

import static cofh.core.network.packet.server.StorageClearPacket.StorageType.ENERGY;
import static cofh.core.network.packet.server.StorageClearPacket.StorageType.FLUID;
import static cofh.lib.util.constants.Constants.PATH_ELEMENTS;
import static cofh.lib.util.constants.Constants.TRUE;
import static cofh.lib.util.helpers.StringHelper.canLocalize;
import static cofh.lib.util.helpers.StringHelper.localize;

public class GuiHelper {

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

    public static ElementResourceStorage setClearable(ElementEnergyStorage storage, TileCoFH tile, int coil) {

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

    public static ElementFluidStorage createDefaultFluidStorage(IGuiAccess gui, int posX, int posY, FluidStorageCoFH storage, int width, int height, String texture, String underlayTexture, BooleanSupplier drawUnderlay, String overlayTexture, int texW, int texH) {

        return (ElementFluidStorage) new ElementFluidStorage(gui, posX, posY, storage).setUnderlayTexture(underlayTexture, drawUnderlay).setOverlayTexture(overlayTexture).setSize(width, height).setTexture(texture, texW, texH);
    }

    public static ElementResourceStorage setClearable(ElementFluidStorage storage, TileCoFH tile, int tank) {

        return storage.setClearStorage(() -> StorageClearPacket.sendToServer(tile, FLUID, tank));
    }
    // endregion

    // region SLOTS
    public static ElementSlot createSlot(IGuiAccess gui, int posX, int posY) {

        return createDefaultSlot(gui, posX - 1, posY - 1, 18, 18, PATH_ELEMENTS + "slot.png", 32, 32);
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

    public static ElementSlot createDefaultSlot(IGuiAccess gui, int posX, int posY, int width, int height, String texture, String underlayTexture, BooleanSupplier drawUnderlay, int texW, int texH) {

        return (ElementSlot) new ElementSlot(gui, posX, posY)
                .setUnderlayTexture(underlayTexture, drawUnderlay)
                .setSize(width, height)
                .setTexture(texture, texW, texH);
    }

    public static ElementSlot createDefaultSlot(IGuiAccess gui, int posX, int posY, int width, int height, String texture, String underlayTexture, BooleanSupplier drawUnderlay, String overlayTexture, int texW, int texH) {

        return (ElementSlot) new ElementSlot(gui, posX, posY)
                .setUnderlayTexture(underlayTexture, drawUnderlay)
                .setOverlayTexture(overlayTexture)
                .setSize(width, height)
                .setTexture(texture, texW, texH);
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

    public static ElementResourceStorage setClaimable(ElementXpStorage storage, TileCoFH tile) {

        return storage.setClaimStorage(() -> ClaimXPPacket.sendToServer(tile));
    }
    // endregion

    // region COMMON UX
    public static ElementScaled createDefaultProgress(IGuiAccess gui, int posX, int posY, String texture, IntSupplier quantitySup) {

        return createDefaultProgress(gui, posX, posY, texture, quantitySup, TRUE);
    }

    public static ElementScaled createDefaultProgress(IGuiAccess gui, int posX, int posY, String texture, IntSupplier quantitySup, BooleanSupplier visible) {

        return (ElementScaled) new ElementScaled(gui, posX, posY)
                .setQuantity(quantitySup)
                .setDirection(ElementScaled.StartDirection.LEFT)
                .setSize(PROGRESS, 16)
                .setTexture(texture, 64, 16)
                .setVisible(visible);
    }

    public static ElementScaledFluid createDefaultFluidProgress(IGuiAccess gui, int posX, int posY, String texture, IntSupplier quantitySup, Supplier<FluidStack> fluidSup) {

        return createDefaultFluidProgress(gui, posX, posY, texture, quantitySup, fluidSup, TRUE);
    }

    public static ElementScaledFluid createDefaultFluidProgress(IGuiAccess gui, int posX, int posY, String texture, IntSupplier quantitySup, Supplier<FluidStack> fluidSup, BooleanSupplier visible) {

        return (ElementScaledFluid) new ElementScaledFluid(gui, posX, posY)
                .setFluid(fluidSup)
                .setQuantity(quantitySup)
                .setDirection(ElementScaled.StartDirection.LEFT)
                .setSize(PROGRESS, 16)
                .setTexture(texture, 64, 16)
                .setVisible(visible);
    }

    public static ElementScaled createDefaultSpeed(IGuiAccess gui, int posX, int posY, String texture, IntSupplier quantitySup) {

        return (ElementScaled) new ElementScaled(gui, posX, posY)
                .setQuantity(quantitySup)
                .setSize(16, SPEED)
                .setTexture(texture, 32, 16);
    }

    public static ElementScaled createDefaultDuration(IGuiAccess gui, int posX, int posY, String texture, IntSupplier quantitySup) {

        return (ElementScaled) new ElementScaled(gui, posX, posY)
                .setQuantity(quantitySup)
                .setSize(16, DURATION)
                .setTexture(texture, 32, 16);
    }
    // endregion

    // region CONSTANTS
    public static final int SLOT_SIZE = 18;
    public static final int LARGE_SLOT_SIZE = 26;

    public static final int DURATION = 16;
    public static final int PROGRESS = 24;
    public static final int SPEED = 16;
    public static final int HEIGHT = 16;
    // endregion

    // region ELEMENTS
    public static final String BUTTON_18 = PATH_ELEMENTS + "button_18.png";
    public static final String BUTTON_18_HIGHLIGHT = PATH_ELEMENTS + "button_18_highlight.png";
    public static final String BUTTON_18_INACTIVE = PATH_ELEMENTS + "button_18_inactive.png";

    public static final String INFO_INPUT = PATH_ELEMENTS + "info_input.png";
    public static final String INFO_OUTPUT = PATH_ELEMENTS + "info_output.png";

    public static final String NAV_BACK = PATH_ELEMENTS + "nav_back.png";
    public static final String NAV_FILTER = PATH_ELEMENTS + "nav_filter.png";

    public static final String TAB_BOTTOM = PATH_ELEMENTS + "tab_bottom.png";
    public static final String TAB_TOP = PATH_ELEMENTS + "tab_top.png";

    public static final String PROG_ARROW_LEFT = PATH_ELEMENTS + "progress_arrow_left.png";
    public static final String PROG_ARROW_RIGHT = PATH_ELEMENTS + "progress_arrow_right.png";
    public static final String PROG_ARROW_FLUID_LEFT = PATH_ELEMENTS + "progress_arrow_fluid_left.png";
    public static final String PROG_ARROW_FLUID_RIGHT = PATH_ELEMENTS + "progress_arrow_fluid_right.png";
    public static final String PROG_DROP_LEFT = PATH_ELEMENTS + "progress_fluid_left.png";
    public static final String PROG_DROP_RIGHT = PATH_ELEMENTS + "progress_fluid_right.png";

    public static final String SCALE_ALCHEMY = PATH_ELEMENTS + "scale_alchemy.png";
    public static final String SCALE_BOOK = PATH_ELEMENTS + "scale_book.png";
    public static final String SCALE_BUBBLE = PATH_ELEMENTS + "scale_bubble.png";
    public static final String SCALE_COMPACT = PATH_ELEMENTS + "scale_compact.png";
    public static final String SCALE_CRUSH = PATH_ELEMENTS + "scale_crush.png";
    public static final String SCALE_FLAME = PATH_ELEMENTS + "scale_flame.png";
    public static final String SCALE_FLAME_GREEN = PATH_ELEMENTS + "scale_flame_green.png";
    public static final String SCALE_FLUX = PATH_ELEMENTS + "scale_flux.png";
    public static final String SCALE_SAW = PATH_ELEMENTS + "scale_saw.png";
    public static final String SCALE_SPIN = PATH_ELEMENTS + "scale_spin.png";
    public static final String SCALE_SUN = PATH_ELEMENTS + "scale_sun.png";
    public static final String SCALE_SNOWFLAKE = PATH_ELEMENTS + "scale_snowflake.png";
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
