package cofh.lib.util.helpers;

import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.Property;
import net.minecraft.state.properties.ChestType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Collection;
import java.util.EnumMap;
import java.util.function.ToIntFunction;

import static net.minecraft.state.properties.BlockStateProperties.*;

/**
 * Contains various helper functions to assist with {@link Block} and Block-related manipulation and interaction.
 *
 * @author King Lemming
 */
public class BlockHelper {

    private BlockHelper() {

    }

    public static final Direction[] DIR_VALUES = Direction.values();

    public static final byte[] SIDE_LEFT = {4, 5, 5, 4, 2, 3};
    public static final byte[] SIDE_RIGHT = {5, 4, 4, 5, 3, 2};
    public static final byte[] SIDE_OPPOSITE = {1, 0, 3, 2, 5, 4};
    public static final byte[] SIDE_ABOVE = {3, 2, 1, 1, 1, 1};
    public static final byte[] SIDE_BELOW = {2, 3, 0, 0, 0, 0};

    private static final EnumMap<Direction, Direction> SIDE_LEFT_LOOKUP = computeMap(SIDE_LEFT);
    private static final EnumMap<Direction, Direction> SIDE_RIGHT_LOOKUP = computeMap(SIDE_RIGHT);
    private static final EnumMap<Direction, Direction> SIDE_OPPOSITE_LOOKUP = computeMap(SIDE_OPPOSITE);
    private static final EnumMap<Direction, Direction> SIDE_ABOVE_LOOKUP = computeMap(SIDE_ABOVE);
    private static final EnumMap<Direction, Direction> SIDE_BELOW_LOOKUP = computeMap(SIDE_BELOW);

    // These assume facing is towards negative - looking AT side 1, 3, or 5.
    public static final byte[] ROTATE_CLOCK_Y = {0, 1, 4, 5, 3, 2};
    public static final byte[] ROTATE_CLOCK_Z = {5, 4, 2, 3, 0, 1};
    public static final byte[] ROTATE_CLOCK_X = {2, 3, 1, 0, 4, 5};

    public static final byte[] ROTATE_COUNTER_Y = {0, 1, 5, 4, 2, 3};
    public static final byte[] ROTATE_COUNTER_Z = {4, 5, 2, 3, 1, 0};
    public static final byte[] ROTATE_COUNTER_X = {3, 2, 0, 1, 4, 5};

    public static final byte[] INVERT_AROUND_Y = {0, 1, 3, 2, 5, 4};
    public static final byte[] INVERT_AROUND_Z = {1, 0, 2, 3, 5, 4};
    public static final byte[] INVERT_AROUND_X = {1, 0, 3, 2, 4, 5};

    public static ToIntFunction<BlockState> lightValue(BooleanProperty property, int lightValue) {

        return (state) -> state.get(property) ? lightValue : 0;
    }

    public static ToIntFunction<BlockState> lightValue(int lightValue) {

        return (state) -> lightValue;
    }

    // region TILE ENTITIES
    public static TileEntity getAdjacentTileEntity(World world, BlockPos pos, Direction dir) {

        pos = pos.offset(dir);
        return world == null || !world.isBlockLoaded(pos) ? null : world.getTileEntity(pos);
    }

    public static TileEntity getAdjacentTileEntity(World world, BlockPos pos, int side) {

        return world == null ? null : getAdjacentTileEntity(world, pos, DIR_VALUES[side]);
    }

    public static TileEntity getAdjacentTileEntity(TileEntity refTile, Direction dir) {

        return refTile == null ? null : getAdjacentTileEntity(refTile.getWorld(), refTile.getPos(), dir);
    }
    // endregion

    // region ROTATION
    public static boolean attemptRotateBlock(BlockState state, World world, BlockPos pos) {

        Collection<Property<?>> properties = state.getProperties();

        // DOORS, BEDS, END PORTAL FRAMES
        if (properties.contains(DOOR_HINGE) || properties.contains(BED_PART) || properties.contains(EYE)) {
            return false;
        }
        // DOUBLE CHESTS
        if (properties.contains(CHEST_TYPE) && state.get(CHEST_TYPE) != ChestType.SINGLE) {
            return false;
        }
        // EXTENDED PISTONS
        if (properties.contains(EXTENDED) && state.get(EXTENDED)) {
            return false;
        }
        BlockState rotState;

        if (properties.contains(FACING)) {
            int index = state.get(FACING).getIndex();
            for (int i = 1; i < 6; ++i) {
                rotState = state.with(FACING, Direction.byIndex(index + i));
                if (rotState != state && rotState.isValidPosition(world, pos)) {
                    world.setBlockState(pos, rotState);
                    if (rotState.canProvidePower()) {
                        Block block = rotState.getBlock();
                        world.notifyNeighborsOfStateChange(pos, block);
                        if (rotState.getStrongPower(world, pos, rotState.get(FACING)) > 0) {
                            world.notifyNeighborsOfStateChange(pos.offset(state.get(FACING).getOpposite()), block);
                            world.notifyNeighborsOfStateChange(pos.offset(rotState.get(FACING).getOpposite()), block);
                        }
                    }
                    return true;
                }
            }
            return true;
        }
        if (properties.contains(HORIZONTAL_FACING)) {
            int index = state.get(HORIZONTAL_FACING).getHorizontalIndex();
            for (int i = 1; i < 4; ++i) {
                rotState = state.with(HORIZONTAL_FACING, Direction.byHorizontalIndex(index + i));
                if (rotState != state && rotState.isValidPosition(world, pos)) {
                    world.setBlockState(pos, rotState);
                    if (rotState.canProvidePower()) {
                        Block block = rotState.getBlock();
                        world.notifyNeighborsOfStateChange(pos, block);
                        if (rotState.getStrongPower(world, pos, rotState.get(HORIZONTAL_FACING)) > 0) {
                            world.notifyNeighborsOfStateChange(pos.offset(state.get(HORIZONTAL_FACING).getOpposite()), block);
                            world.notifyNeighborsOfStateChange(pos.offset(rotState.get(HORIZONTAL_FACING).getOpposite()), block);
                        }
                    }
                    return true;
                }
            }
            return true;
        }
        if (properties.contains(AXIS)) {
            switch (state.get(AXIS)) {
                case Y:
                    rotState = state.with(AXIS, Direction.Axis.X);
                    break;
                case X:
                    rotState = state.with(AXIS, Direction.Axis.Z);
                    break;
                default:
                    rotState = state.with(AXIS, Direction.Axis.Y);
                    break;
            }
            if (rotState != state && rotState.isValidPosition(world, pos)) {
                world.setBlockState(pos, rotState);
            }
            return true;
        }
        if (properties.contains(FACING_EXCEPT_UP)) {
            rotState = state.with(FACING_EXCEPT_UP, Rotation.CLOCKWISE_90.rotate(state.get(FACING_EXCEPT_UP)));
            if (rotState != state && rotState.isValidPosition(world, pos)) {
                world.setBlockState(pos, rotState);
            }
            return true;
        }
        if (properties.contains(ROTATION_0_15)) {
            rotState = state.with(ROTATION_0_15, (state.get(ROTATION_0_15) + 1) % 16);
            if (rotState != state && rotState.isValidPosition(world, pos)) {
                world.setBlockState(pos, rotState);
                return true;
            }
        }
        // RAILS
        if (state.getBlock() instanceof AbstractRailBlock) {
            rotState = state.rotate(world, pos, Rotation.CLOCKWISE_90);
            if (rotState != state && rotState.isValidPosition(world, pos)) {
                world.setBlockState(pos, rotState);
                return true;
            }
        }
        return false;
    }

    public static Direction left(Direction face) {

        return SIDE_LEFT_LOOKUP.get(face);
    }

    public static Direction right(Direction face) {

        return SIDE_RIGHT_LOOKUP.get(face);
    }

    public static Direction opposite(Direction face) {

        return SIDE_OPPOSITE_LOOKUP.get(face);
    }

    public static Direction above(Direction face) {

        return SIDE_ABOVE_LOOKUP.get(face);
    }

    public static Direction below(Direction face) {

        return SIDE_BELOW_LOOKUP.get(face);
    }
    // endregion

    // region INTERNAL

    // Convert a byte[] side lookup to an EnumMap.
    private static EnumMap<Direction, Direction> computeMap(byte[] arr) {

        EnumMap<Direction, Direction> map = new EnumMap<>(Direction.class);
        for (int i = 0; i < 6; ++i) {
            map.put(DIR_VALUES[i], DIR_VALUES[arr[i]]);
        }
        return map;
    }
    // endregion
}
