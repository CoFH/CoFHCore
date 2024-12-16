package cofh.lib.util.helpers;

import it.unimi.dsi.fastutil.longs.Long2IntFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.event.ForgeEventFactory;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.function.LongToIntFunction;
import java.util.function.ToIntFunction;

import static cofh.lib.util.Constants.DIRECTIONS;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.*;

/**
 * Contains various helper functions to assist with {@link Block} and Block-related manipulation and interaction.
 *
 * @author King Lemming
 */
public final class BlockHelper {

    private BlockHelper() {

    }

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

    private static final LongToIntFunction[] COMPONENT_GETTERS = {BlockPos::getX, BlockPos::getY, BlockPos::getZ};
    //private static final LongToIntFunction[] COMPONENT_SETTERS = {BlockPos::, BlockPos::getY, BlockPos::getZ};
    private static final Direction[][] DIRECTION_LOOKUP = Arrays.stream(Direction.Axis.values()).map(axis -> Arrays.stream(Direction.AxisDirection.values()).map(pole -> Direction.get(pole, axis)).toArray(Direction[]::new)).toArray(Direction[][]::new);

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

        return (state) -> state.getValue(property) ? lightValue : 0;
    }

    public static ToIntFunction<BlockState> lightValue(int lightValue) {

        return (state) -> lightValue;
    }

    // region TILE ENTITIES
    public static BlockEntity getAdjacentTileEntity(Level world, BlockPos pos, Direction dir) {

        pos = pos.relative(dir);
        return world == null || !world.hasChunkAt(pos) ? null : world.getBlockEntity(pos);
    }

    public static BlockEntity getAdjacentTileEntity(Level world, BlockPos pos, int side) {

        return world == null ? null : getAdjacentTileEntity(world, pos, DIRECTIONS[side]);
    }

    public static BlockEntity getAdjacentTileEntity(BlockEntity refTile, Direction dir) {

        return refTile == null ? null : getAdjacentTileEntity(refTile.getLevel(), refTile.getBlockPos(), dir);
    }
    // endregion

    // region ROTATION
    public static boolean attemptRotateBlock(BlockState state, Level world, BlockPos pos) {

        Collection<Property<?>> properties = state.getProperties();

        // DOORS, BEDS, END PORTAL FRAMES
        if (properties.contains(DOOR_HINGE) || properties.contains(BED_PART) || properties.contains(EYE)) {
            return false;
        }
        // DOUBLE CHESTS
        if (properties.contains(CHEST_TYPE) && state.getValue(CHEST_TYPE) != ChestType.SINGLE) {
            return false;
        }
        // EXTENDED PISTONS
        if (properties.contains(EXTENDED) && state.getValue(EXTENDED)) {
            return false;
        }
        BlockState rotState;

        if (properties.contains(FACING)) {
            int index = state.getValue(FACING).get3DDataValue();
            for (int i = 1; i < 6; ++i) {
                rotState = state.setValue(FACING, Direction.from3DDataValue(index + i));
                if (rotState != state && rotState.canSurvive(world, pos)) {
                    world.setBlockAndUpdate(pos, rotState);
                    if (rotState.isSignalSource()) {
                        Block block = rotState.getBlock();
                        world.updateNeighborsAt(pos, block);
                        if (rotState.getDirectSignal(world, pos, rotState.getValue(FACING)) > 0) {
                            world.updateNeighborsAt(pos.relative(state.getValue(FACING).getOpposite()), block);
                            world.updateNeighborsAt(pos.relative(rotState.getValue(FACING).getOpposite()), block);
                        }
                    }
                    return true;
                }
            }
            return true;
        }
        if (properties.contains(HORIZONTAL_FACING)) {
            int index = state.getValue(HORIZONTAL_FACING).get2DDataValue();
            for (int i = 1; i < 4; ++i) {
                rotState = state.setValue(HORIZONTAL_FACING, Direction.from2DDataValue(index + i));
                if (rotState != state && rotState.canSurvive(world, pos)) {
                    world.setBlockAndUpdate(pos, rotState);
                    if (rotState.isSignalSource()) {
                        Block block = rotState.getBlock();
                        world.updateNeighborsAt(pos, block);
                        if (rotState.getDirectSignal(world, pos, rotState.getValue(HORIZONTAL_FACING)) > 0) {
                            world.updateNeighborsAt(pos.relative(state.getValue(HORIZONTAL_FACING).getOpposite()), block);
                            world.updateNeighborsAt(pos.relative(rotState.getValue(HORIZONTAL_FACING).getOpposite()), block);
                        }
                    }
                    return true;
                }
            }
            return true;
        }
        if (properties.contains(AXIS)) {
            rotState = switch (state.getValue(AXIS)) {
                case Y -> state.setValue(AXIS, Direction.Axis.X);
                case X -> state.setValue(AXIS, Direction.Axis.Z);
                default -> state.setValue(AXIS, Direction.Axis.Y);
            };
            if (rotState != state && rotState.canSurvive(world, pos)) {
                world.setBlockAndUpdate(pos, rotState);
            }
            return true;
        }
        if (properties.contains(FACING_HOPPER)) {
            rotState = state.setValue(FACING_HOPPER, Rotation.CLOCKWISE_90.rotate(state.getValue(FACING_HOPPER)));
            if (rotState != state && rotState.canSurvive(world, pos)) {
                world.setBlockAndUpdate(pos, rotState);
            }
            return true;
        }
        if (properties.contains(ROTATION_16)) {
            rotState = state.setValue(ROTATION_16, (state.getValue(ROTATION_16) + 1) % 16);
            if (rotState != state && rotState.canSurvive(world, pos)) {
                world.setBlockAndUpdate(pos, rotState);
                return true;
            }
        }
        // RAILS
        if (state.getBlock() instanceof BaseRailBlock) {
            rotState = state.rotate(world, pos, Rotation.CLOCKWISE_90);
            if (rotState != state && rotState.canSurvive(world, pos)) {
                world.setBlockAndUpdate(pos, rotState);
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

    // region HELPERS
    public static boolean isAxial(BlockPos pos) {

        return pos.getX() == 0 ? (pos.getY() == 0 || pos.getZ() == 0) : (pos.getY() == 0 && pos.getZ() == 0);
    }

    public static boolean isAxial(long pos) {

        return BlockPos.getX(pos) == 0 ? (BlockPos.getY(pos) == 0 || BlockPos.getZ(pos) == 0) : (BlockPos.getY(pos) == 0 && BlockPos.getZ(pos) == 0);
    }

    @Nullable
    public static Direction getSide(BlockPos pos) {

        if (!isAxial(pos)) {
            return null;
        }
        if (pos.getY() < 0) {
            return Direction.DOWN;
        }
        if (pos.getY() > 0) {
            return Direction.UP;
        }
        if (pos.getZ() < 0) {
            return Direction.NORTH;
        }
        if (pos.getZ() > 0) {
            return Direction.SOUTH;
        }
        if (pos.getX() < 0) {
            return Direction.WEST;
        }
        if (pos.getX() > 0) {
            return Direction.EAST;
        }

        return null;
    }

    @Nullable
    public static Direction getSide(long pos) {

        if (!isAxial(pos)) {
            return null;
        }
        if (BlockPos.getY(pos) < 0) {
            return Direction.DOWN;
        }
        if (BlockPos.getY(pos) > 0) {
            return Direction.UP;
        }
        if (BlockPos.getZ(pos) < 0) {
            return Direction.NORTH;
        }
        if (BlockPos.getZ(pos) > 0) {
            return Direction.SOUTH;
        }
        if (BlockPos.getX(pos) < 0) {
            return Direction.WEST;
        }
        if (BlockPos.getX(pos) > 0) {
            return Direction.EAST;
        }
        return null;
    }

    public static int getComponent(long pos, Direction.Axis axis) {

        return COMPONENT_GETTERS[axis.ordinal()].applyAsInt(pos);
    }

    public static BlockPos.MutableBlockPos setComponent(BlockPos.MutableBlockPos pos, Direction.Axis axis, int value) {

        return switch (axis) {
            case X -> pos.setX(value);
            case Y -> pos.setY(value);
            case Z -> pos.setZ(value);
        };
    }

    public static long blockToChunk(long blockPos) {

        return ChunkPos.asLong(SectionPos.blockToSectionCoord(BlockPos.getX(blockPos)), SectionPos.blockToSectionCoord(BlockPos.getZ(blockPos)));
    }

    public static Direction getDirection(Direction.Axis axis, Direction.AxisDirection polarity) {

        return DIRECTION_LOOKUP[axis.ordinal()][polarity.ordinal()];
    }

    public static void updateNeighbors(Level level, BlockPos pos, BlockState state, EnumSet<Direction> sides) {

        if (ForgeEventFactory.onNeighborNotify(level, pos, state, sides, false).isCanceled()) {
            return;
        }
        BlockPos.MutableBlockPos cursor = pos.mutable();
        for (Direction direction : sides) {
            level.neighborChanged(cursor.set(pos).move(direction), state.getBlock(), pos);
        }
    }
    // endregion

    // region INTERNAL

    // Convert a byte[] side lookup to an EnumMap.
    private static EnumMap<Direction, Direction> computeMap(byte[] arr) {

        EnumMap<Direction, Direction> map = new EnumMap<>(Direction.class);
        for (int i = 0; i < 6; ++i) {
            map.put(DIRECTIONS[i], DIRECTIONS[arr[i]]);
        }
        return map;
    }
    // endregion
}
