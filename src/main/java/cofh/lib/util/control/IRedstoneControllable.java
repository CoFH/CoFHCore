package cofh.lib.util.control;

public interface IRedstoneControllable {

    int getPower();

    int getThreshold();

    ControlMode getMode();

    void setPower(int power);

    void setControl(int threshold, ControlMode mode);

    default boolean getState() {

        return getMode().matches(getPower(), getThreshold());
    }

    /**
     * This returns whether or not redstone control functionality is enabled at all.
     */
    boolean isControllable();

    // region CONTROL MODE
    enum ControlMode {
        DISABLED, LOW, HIGH, EQUAL, UNDER, UNDER_INC, OVER, OVER_INC;

        public static final ControlMode[] VALUES = values();

        boolean matches(int power, int threshold) {

            switch (this) {
                case LOW:
                    return power <= 0;
                case HIGH:
                    return power > 0;
                case EQUAL:
                    return power == threshold;
                case UNDER:
                    return power < threshold;
                case OVER:
                    return power > threshold;
                case UNDER_INC:
                    return power <= threshold;
                case OVER_INC:
                    return power >= threshold;
                default:
                    return true;
            }
        }
    }
    // endregion
}
