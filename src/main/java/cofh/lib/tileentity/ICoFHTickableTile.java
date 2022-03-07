package cofh.lib.tileentity;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Represents a TileEntity which may Tick.
 * <p>
 * May not exist alongside {@link IClientTickable} or {@link IServerTickable}.
 */
public interface ICoFHTickableTile {

    /**
     * Called both client and server side.
     */
    void tick();

    /**
     * Create a {@link BlockEntityTicker} instance for the given level, and BlockEntityType's.
     * <p>
     * This method should be called by implementations of {@link EntityBlock#getTicker(Level, BlockState, BlockEntityType)}.
     * <p>
     * Vanilla invokes the above method to determine if the Block should tick for the given {@link Level} and {@link BlockState}. Also
     * providing the {@link BlockEntityType} of the in-world block for context. (as you can have multiple per {@link Block}.)
     * <p>
     * This method should be provided the {@link Level} and {@link BlockEntityType} from this vanilla function as the first 2 parameters.
     *
     * @param level    The actual {@link Level} from {@link EntityBlock#getTicker}
     * @param actual   The actual {@link BlockEntityType} from {@link EntityBlock#getTicker}
     * @param expected The expected {@link BlockEntityType}, usually from your constants. Make sure this has generic types specified.
     * @param clazz    The {@link Class} of the {@link BlockEntity}, used for interface checks.
     * @return The {@link BlockEntityTicker} appropriate for the {@code clazz} and {@code level} provided.
     */
    static <A extends BlockEntity> BlockEntityTicker<A> createTicker(Level level, BlockEntityType<A> actual, BlockEntityType<?> expected, Class<?> clazz) {

        if (actual == expected) {
            if (ICoFHTickableTile.class.isAssignableFrom(clazz)) {
                return (level1, pos, state, instance) -> ((ICoFHTickableTile) instance).tick();
            }
            if (level.isClientSide && IClientTickable.class.isAssignableFrom(clazz)) {
                return (level1, pos, state, instance) -> ((IClientTickable) instance).tickClient();
            }
            if (!level.isClientSide && IServerTickable.class.isAssignableFrom(clazz)) {
                return (level1, pos, state, instance) -> ((IServerTickable) instance).tickServer();
            }
        }

        return null;
    }

    /**
     * Represents a Tile which may only tick on the Server.
     * <p>
     * May be combined with {@link IClientTickable}.
     * Will be ignored if {@link ICoFHTickableTile} exists.
     */
    interface IServerTickable {

        /**
         * Called server side.
         */
        void tickServer();
    }

    /**
     * Represents a Tile which may only tick on the Client.
     * <p>
     * May be combined with {@link IServerTickable}.
     * Will be ignored if {@link ICoFHTickableTile} exists.
     */
    interface IClientTickable {

        /**
         * Called client side.
         */
        void tickClient();
    }
}
