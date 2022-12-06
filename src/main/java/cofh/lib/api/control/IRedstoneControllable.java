package cofh.lib.api.control;

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
     * This returns whether redstone control functionality is enabled at all.
     */
    boolean isControllable();

    // region CONTROL MODE
    enum ControlMode {
        DISABLED, LOW, HIGH, EQUAL, UNDER, UNDER_INC, OVER, OVER_INC;

        public static final ControlMode[] VALUES = values();

        boolean matches(int power, int threshold) {

            return switch (this) {
                case LOW -> power <= 0;
                case HIGH -> power > 0;
                case EQUAL -> power == threshold;
                case UNDER -> power < threshold;
                case OVER -> power > threshold;
                case UNDER_INC -> power <= threshold;
                case OVER_INC -> power >= threshold;
                default -> true;
            };
        }
    }
    // endregion
}
