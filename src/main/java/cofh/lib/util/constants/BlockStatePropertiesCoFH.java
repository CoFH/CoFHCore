package cofh.lib.util.constants;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.*;

import java.util.function.Predicate;

public class BlockStatePropertiesCoFH {

    private BlockStatePropertiesCoFH() {

    }

    private static final Predicate<RailShape> PRED_STRAIGHT = dir -> dir != RailShape.NORTH_EAST && dir != RailShape.NORTH_WEST && dir != RailShape.SOUTH_EAST && dir != RailShape.SOUTH_WEST;
    private static final Predicate<RailShape> PRED_NO_SLOPE = dir -> dir != RailShape.ASCENDING_EAST && dir != RailShape.ASCENDING_WEST && dir != RailShape.ASCENDING_NORTH && dir != RailShape.ASCENDING_SOUTH;

    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");
    public static final BooleanProperty TOP = BooleanProperty.create("top");

    public static final IntegerProperty AGE_0_4 = IntegerProperty.create("age", 0, 4);
    public static final IntegerProperty AGE_0_6 = IntegerProperty.create("age", 0, 6);
    public static final IntegerProperty AGE_0_7 = IntegerProperty.create("age", 0, 7);
    public static final IntegerProperty AGE_0_9 = IntegerProperty.create("age", 0, 9);
    public static final IntegerProperty AGE_0_10 = IntegerProperty.create("age", 0, 10);

    public static final IntegerProperty BITES_0_3 = IntegerProperty.create("bites", 0, 3);
    public static final IntegerProperty BITES_0_7 = IntegerProperty.create("bites", 0, 7);

    public static final DirectionProperty FACING_ALL = DirectionProperty.create("facing", Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST, Direction.UP, Direction.DOWN);
    public static final DirectionProperty FACING_HORIZONTAL = DirectionProperty.create("facing", Direction.Plane.HORIZONTAL);

    public static final EnumProperty<RailShape> RAIL_DEFAULT = EnumProperty.create("shape", RailShape.class);
    public static final EnumProperty<RailShape> RAIL_STRAIGHT = EnumProperty.create("shape", RailShape.class, PRED_STRAIGHT);
    public static final EnumProperty<RailShape> RAIL_STRAIGHT_FLAT = EnumProperty.create("shape", RailShape.class, dir -> PRED_STRAIGHT.test(dir) && PRED_NO_SLOPE.test(dir));
    public static final EnumProperty<RailShape> RAIL_FLAT = EnumProperty.create("shape", RailShape.class, PRED_NO_SLOPE);

}
