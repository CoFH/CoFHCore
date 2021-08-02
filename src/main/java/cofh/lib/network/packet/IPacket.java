package cofh.lib.network.packet;

import cofh.lib.network.PacketHandler;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import org.apache.commons.lang3.tuple.Pair;

/**
 * All packets implement this.
 *
 * @author covers1624
 */
public interface IPacket {

    /**
     * The id for this packet.
     * The packet id bounds are 0 < id < 255
     *
     * @return The id.
     */
    byte getId();

    /**
     * The PacketHandler that can handle this packet.
     *
     * @return The handler that handles this packet.
     */
    PacketHandler getHandler();

    /**
     * Write the packet's data to the buffer.
     *
     * @param buf The buffer.
     */
    void write(PacketBuffer buf);

    /**
     * Read the data from the packet's buffer.
     *
     * @param buf The buffer.
     */
    void read(PacketBuffer buf);

    /**
     * Creates a {@link net.minecraft.network.IPacket} from this packet.
     * This method is an overload for {@link #toVanillaPacket(NetworkDirection, int)}
     *
     * @param direction The Direction the packet will be sent.
     * @return The new {@link net.minecraft.network.IPacket}.
     */
    default net.minecraft.network.IPacket<?> toVanillaPacket(NetworkDirection direction) {

        return toVanillaPacket(direction, 0);
    }

    /**
     * Creates a {@link net.minecraft.network.IPacket} from this packet.
     *
     * @param direction The Direction the packet will be sent.
     * @param index     The packet index, FML uses this for some things, 0 is fine for most things.
     * @return The new {@link net.minecraft.network.IPacket}.
     */
    default net.minecraft.network.IPacket<?> toVanillaPacket(NetworkDirection direction, int index) {

        PacketBuffer buf = new PacketBuffer(Unpooled.buffer());
        buf.writeByte(getId());
        write(buf);
        return direction.buildPacket(Pair.of(buf, index), getHandler().getChannelName()).getThis();
    }

}
