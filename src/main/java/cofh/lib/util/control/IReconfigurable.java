package cofh.lib.util.control;

import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;

public interface IReconfigurable {

    Direction getFacing();

    SideConfig getSideConfig(Direction side);

    boolean prevSideConfig(Direction side);

    boolean nextSideConfig(Direction side);

    boolean setSideConfig(Direction side, SideConfig config);

    boolean clearAllSides();

    boolean hasInputSide();

    boolean hasOutputSide();

    /**
     * This returns whether reconfiguration functionality is enabled at all.
     */
    boolean isReconfigurable();

    // region CONFIGS
    enum SideConfig implements StringRepresentable {

        SIDE_NONE("none", false, false),
        SIDE_INPUT("input", true, false),
        SIDE_OUTPUT("output", false, true),
        SIDE_BOTH("both", true, true),
        SIDE_ACCESSIBLE("accessible", false, false);

        public static final SideConfig[] VALUES = values();

        private final String name;
        private final boolean input;
        private final boolean output;

        SideConfig(String name, boolean input, boolean output) {

            this.name = name;
            this.input = input;
            this.output = output;
        }

        public boolean isInput() {

            return input;
        }

        public boolean isOutput() {

            return output;
        }

        public SideConfig prev() {

            switch (this) {
                case SIDE_INPUT:
                    return SIDE_NONE;
                case SIDE_OUTPUT:
                    return SIDE_INPUT;
                case SIDE_BOTH:
                    return SIDE_OUTPUT;
                case SIDE_ACCESSIBLE:
                    return SIDE_BOTH;
                default:
                    return SIDE_ACCESSIBLE;
            }
        }

        public SideConfig next() {

            switch (this) {
                case SIDE_NONE:
                    return SIDE_INPUT;
                case SIDE_INPUT:
                    return SIDE_OUTPUT;
                case SIDE_OUTPUT:
                    return SIDE_BOTH;
                case SIDE_BOTH:
                    return SIDE_ACCESSIBLE;
                default:
                    return SIDE_NONE;
            }
        }

        @Override
        public String toString() {

            return this.getSerializedName();
        }

        @Override
        public String getSerializedName() {

            return this.name;
        }
    }
    // endregion
}
