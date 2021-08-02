package cofh.lib.network.packet;

import cofh.lib.network.PacketHandler;

/**
 * Created by covers1624 on 21/09/18.
 */
public abstract class PacketBase implements IPacket {

    protected final int id;
    protected final PacketHandler handler;

    protected PacketBase(int id, PacketHandler handler) {

        this.id = id;
        this.handler = handler;
    }

    @Override
    public byte getId() {

        return (byte) id;
    }

    @Override
    public PacketHandler getHandler() {

        return handler;
    }

}
