package cofh.core.item;

import cofh.core.entity.AbstractMinecartCoFH;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.level.gameevent.GameEvent;

public class MinecartItemCoFH extends ItemCoFH {

    protected final IMinecartFactory<? extends AbstractMinecartCoFH> factory;

    public MinecartItemCoFH(IMinecartFactory<? extends AbstractMinecartCoFH> factory, Properties builder) {

        super(builder);
        this.factory = factory;
        DispenserBlock.registerBehavior(this, DISPENSER_BEHAVIOR);
    }

    public InteractionResult useOn(UseOnContext context) {

        Level level = context.getLevel();
        BlockPos blockpos = context.getClickedPos();
        BlockState blockstate = level.getBlockState(blockpos);

        if (!blockstate.is(BlockTags.RAILS)) {
            return InteractionResult.FAIL;
        }
        ItemStack stack = context.getItemInHand();
        if (!level.isClientSide) {
            RailShape railshape = blockstate.getBlock() instanceof BaseRailBlock rail ? rail.getRailDirection(blockstate, level, blockpos, null) : RailShape.NORTH_SOUTH;
            double d0 = 0.0D;
            if (railshape.isAscending()) {
                d0 = 0.5D;
            }
            createMinecart(stack, level, (double) blockpos.getX() + 0.5D, (double) blockpos.getY() + 0.0625D + d0, (double) blockpos.getZ() + 0.5D);
            level.gameEvent(GameEvent.ENTITY_PLACE, blockpos, GameEvent.Context.of(context.getPlayer(), level.getBlockState(blockpos.below())));
        }
        stack.shrink(1);
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    protected void createMinecart(ItemStack stack, Level level, double posX, double posY, double posZ) {

        AbstractMinecartCoFH minecart = factory.createMinecart(level, posX, posY, posZ);
        if (stack.hasCustomHoverName()) {
            minecart.setCustomName(stack.getHoverName());
        }
        minecart.onPlaced(stack);
        level.addFreshEntity(minecart);
    }

    // region FACTORY
    public interface IMinecartFactory<T extends AbstractMinecart> {

        T createMinecart(Level world, double posX, double posY, double posZ);

    }
    // endregion

    // region DISPENSER BEHAVIOR
    private static final DispenseItemBehavior DISPENSER_BEHAVIOR = new DefaultDispenseItemBehavior() {

        private final DefaultDispenseItemBehavior behaviourDefaultDispenseItem = new DefaultDispenseItemBehavior();

        /**
         * Dispense the specified stack, play the dispense sound and spawn particles.
         */
        @Override
        public ItemStack execute(BlockSource source, ItemStack stack) {

            Direction direction = source.getBlockState().getValue(DispenserBlock.FACING);
            Level world = source.getLevel();

            double d0 = source.x() + (double) direction.getStepX() * 1.125D;
            double d1 = Math.floor(source.y()) + (double) direction.getStepY();
            double d2 = source.z() + (double) direction.getStepZ() * 1.125D;

            BlockPos blockpos = source.getPos().relative(direction);
            BlockState blockstate = world.getBlockState(blockpos);
            RailShape railshape = blockstate.getBlock() instanceof BaseRailBlock rail ? rail.getRailDirection(blockstate, world, blockpos, null) : RailShape.NORTH_SOUTH;
            double d3;
            if (blockstate.is(BlockTags.RAILS)) {
                if (railshape.isAscending()) {
                    d3 = 0.6D;
                } else {
                    d3 = 0.1D;
                }
            } else {
                if (!blockstate.isAir() || !world.getBlockState(blockpos.below()).is(BlockTags.RAILS)) {
                    return this.behaviourDefaultDispenseItem.dispense(source, stack);
                }
                BlockState state = world.getBlockState(blockpos.below());
                RailShape shape = state.getBlock() instanceof BaseRailBlock rail ? rail.getRailDirection(state, world, blockpos.below(), null) : RailShape.NORTH_SOUTH;
                if (direction != Direction.DOWN && shape.isAscending()) {
                    d3 = -0.4D;
                } else {
                    d3 = -0.9D;
                }
            }
            if (stack.getItem() instanceof MinecartItemCoFH minecartItem) {
                minecartItem.createMinecart(stack, world, d0, d1 + d3, d2);
                stack.shrink(1);
                return stack;
            }
            return ItemStack.EMPTY;
        }
    };
    // endregion
}
