package cofh.core.common.network.packet;

public class PacketIDs {

    private PacketIDs() {

    }

    public static final int PACKET_CONTROL = 1;
    public static final int PACKET_GUI = 2;
    public static final int PACKET_REDSTONE = 3;
    public static final int PACKET_STATE = 4;
    public static final int PACKET_RENDER = 5;

    public static final int PACKET_MODEL_UPDATE = 8;

    public static final int PACKET_OVERLAY = 16;
    public static final int PACKET_MOTION = 17;

    public static final int PACKET_FILTERABLE_GUI_OPEN = 20;
    public static final int PACKET_GHOST_ITEM = 21;

    public static final int PACKET_CONTAINER_CONFIG = 24;
    public static final int PACKET_CONTAINER_GUI = 25;

    public static final int PACKET_SECURITY = 28;

    public static final int PACKET_CONFIG = 32;
    public static final int PACKET_SECURITY_CONTROL = 33;
    public static final int PACKET_REDSTONE_CONTROL = 34;
    public static final int PACKET_TRANSFER_CONTROL = 35;
    public static final int PACKET_SIDE_CONFIG = 36;
    public static final int PACKET_STORAGE_CLEAR = 37;
    public static final int PACKET_CLAIM_XP = 38;

    public static final int PACKET_ITEM_MODE_CHANGE = 64;
    public static final int PACKET_ITEM_LEFT_CLICK = 65;
    public static final int PACKET_ITEM_RAYTRACE_BLOCK = 66;
    public static final int PACKET_ITEM_RAYTRACE_ENTITY = 67;

    public static final int PACKET_EFFECT_ADD = 96;
    public static final int PACKET_EFFECT_REMOVE = 97;

}
