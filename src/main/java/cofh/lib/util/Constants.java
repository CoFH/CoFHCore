package cofh.lib.util;

import cofh.lib.util.constants.ModIds;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.PlantType;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;

import java.util.UUID;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

import static net.minecraft.world.entity.EquipmentSlot.*;

public class Constants {

    private Constants() {

    }

    public static final VoxelShape FULL_CUBE_COLLISION = Block.box(1.0D, 0.0D, 1.0D, 15.9375D, 15.9375D, 15.9375D);

    // region GLOBALS
    public static final int AOE_BREAK_FACTOR = 8;
    public static final int BOTTLE_VOLUME = FluidAttributes.BUCKET_VOLUME / 4;
    public static final int BUCKET_VOLUME = FluidAttributes.BUCKET_VOLUME;
    public static final int ENTITY_TRACKING_DISTANCE = 64;
    public static final int ITEM_TIMER_DURATION = 40;
    public static final int MAGMATIC_TEMPERATURE = 1000;
    public static final int MAX_AUGMENTS = 9;
    public static final int MAX_CAPACITY = Integer.MAX_VALUE;
    public static final int MAX_ENCHANT_LEVEL = 10;
    public static final int MAX_FOOD_LEVEL = 20;
    public static final int MAX_POTION_AMPLIFIER = 3;
    public static final int MAX_POTION_DURATION = 72000;
    public static final int MB_PER_XP = 20;
    public static final int NETWORK_UPDATE_DISTANCE = 192;
    public static final int RF_PER_FURNACE_UNIT = 10;

    public static final float BASE_CHANCE = 1.0F;
    public static final float BASE_CHANCE_LOCKED = -1.0F;

    public static final int TANK_SMALL = BUCKET_VOLUME * 4;
    public static final int TANK_MEDIUM = BUCKET_VOLUME * 8;
    public static final int TANK_LARGE = BUCKET_VOLUME * 16;

    public static final EquipmentSlot[] ARMOR_SLOTS = new EquipmentSlot[]{HEAD, CHEST, LEGS, FEET};
    public static final Direction[] DIRECTIONS = Direction.values();
    // endregion

    // region COMMANDS
    public static final String CMD_DURATION = "duration";
    public static final String CMD_FLAG = "flag";
    public static final String CMD_PLAYER = "player";
    public static final String CMD_PLAYERS = "players";
    public static final String CMD_TARGETS = "targets";
    // endregion

    // region CONSTANTS
    public static final BooleanSupplier TRUE = () -> true;
    public static final BooleanSupplier FALSE = () -> false;

    public static final Supplier<Block> EMPTY_BLOCK = () -> Blocks.AIR;
    public static final Supplier<ItemStack> EMPTY_ITEM = () -> ItemStack.EMPTY;
    public static final Supplier<FluidStack> EMPTY_FLUID = () -> FluidStack.EMPTY;

    public static final String DAMAGE_ARROW = "arrow";
    public static final String DAMAGE_PLAYER = "player";

    public static final UUID EMPTY_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000");

    public static final UUID UUID_ARMOR_TOUGHNESS = UUID.fromString("D1ADBCE5-95CE-470C-BF99-6C59843084A2");
    public static final UUID UUID_WEAPON_KNOCKBACK = UUID.fromString("D5A16EFA-3B80-11EC-8D3D-0242AC130003");
    public static final UUID UUID_WEAPON_RANGE = UUID.fromString("DCED55C6-2D54-405A-B77B-30024694ABAB");
    public static final UUID UUID_TOOL_REACH = UUID.fromString("DAE66BD6-BAAE-4702-9F9F-9327C3BB6581");
    public static final UUID UUID_DUAL_WIELD_ATTACK_SPEED = UUID.fromString("173E4578-9E79-4197-AF3A-A00AB3C5D545");

    public static final UUID UUID_EFFECT_CHILLED_MOVEMENT_SPEED = UUID.fromString("D99513AE-6F0E-4987-82DE-80DCBAF058BC");
    public static final UUID UUID_EFFECT_CHILLED_ATTACK_SPEED = UUID.fromString("DF93E7E2-5056-49FA-B425-0D8C46902105");

    public static final UUID UUID_EFFECT_SHOCKED_ATTACK_DAMAGE = UUID.fromString("DD252988-17CC-4352-9EFF-88E11E925748");
    public static final UUID UUID_EFFECT_SUNDERED_ARMOR = UUID.fromString("D01654D3-EB8B-40AB-BB2B-1E8EE3E2E694");
    public static final UUID UUID_EFFECT_SUNDERED_ARMOR_TOUGHNESS = UUID.fromString("D415470F-AE7E-4986-8034-679CFC3128CC");

    public static final UUID UUID_ENCH_BULWARK_KNOCKBACK_RESISTANCE = UUID.fromString("DA9976EC-C764-4450-B5AA-1B11B10143E2");
    public static final UUID UUID_ENCH_PHALANX_MOVEMENT_SPEED = UUID.fromString("DBFFDB74-4607-4C2E-9573-A14D6A73397A");
    public static final UUID UUID_ENCH_REACH_DISTANCE = UUID.fromString("D7476206-4A89-4522-9D1D-9BB34076FFDA");
    public static final UUID UUID_ENCH_VITALITY_HEALTH = UUID.fromString("D9239D8A-AC3A-4A0D-8834-D50A80DFEE2B");

    public static final int RGB_DURABILITY_FLUX = 0xD01010;
    public static final int RGB_DURABILITY_WATER = 0x4060FF;
    public static final int RGB_DURABILITY_ENDER = 0x14594D;
    public static final int RGB_DURABILITY_XP = 0x7AAC52;

    public static float AUG_SCALE_MIN = 0.0F;
    public static float AUG_SCALE_MAX = 100.0F;
    // endregion

    public static final PlantType FUNGUS = PlantType.get("fungus");

    // region FONTS
    public static final ResourceLocation ENDER_FONT = new ResourceLocation(ModIds.ID_COFH_CORE, "ender");
    public static final ResourceLocation INVIS_FONT = new ResourceLocation(ModIds.ID_COFH_CORE, "invis");

    public static final Style ENDER_STYLE = Style.EMPTY.withFont(ENDER_FONT);
    public static final Style INVIS_STYLE = Style.EMPTY.withFont(INVIS_FONT);

    // region TEXTURES
    public static final String PATH_GFX = ModIds.ID_COFH_CORE + ":textures/";
    public static final String PATH_GUI = PATH_GFX + "gui/";
    public static final String PATH_ELEMENTS = PATH_GUI + "elements/";
    // endregion
}
