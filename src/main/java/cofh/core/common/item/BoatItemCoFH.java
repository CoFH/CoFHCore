package cofh.core.common.item;

import cofh.core.common.entity.IOnPlaced;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class BoatItemCoFH extends ItemCoFH {

    protected static final Predicate<Entity> ENTITY_PREDICATE = EntitySelector.NO_SPECTATORS.and(Entity::isPickable);

    protected final Supplier<EntityType<? extends Boat>> type;
    protected final IBoatFactory<? extends Boat> factory;

    public BoatItemCoFH(Supplier<EntityType<? extends Boat>> type, IBoatFactory<? extends Boat> factory, Properties builder) {

        super(builder);
        this.type = type;
        this.factory = factory;
        DispenserBlock.registerBehavior(this, DISPENSER_BEHAVIOR);
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {

        ItemStack stack = player.getItemInHand(hand);
        HitResult hitresult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.ANY);
        if (hitresult.getType() == HitResult.Type.MISS) {
            return InteractionResultHolder.pass(stack);
        } else {
            Vec3 vec3 = player.getViewVector(1.0F);
            List<Entity> list = level.getEntities(player, player.getBoundingBox().expandTowards(vec3.scale(5.0D)).inflate(1.0D), ENTITY_PREDICATE);
            if (!list.isEmpty()) {
                Vec3 vec31 = player.getEyePosition();
                for (Entity entity : list) {
                    AABB aabb = entity.getBoundingBox().inflate(entity.getPickRadius());
                    if (aabb.contains(vec31)) {
                        return InteractionResultHolder.pass(stack);
                    }
                }
            }
            if (hitresult.getType() == HitResult.Type.BLOCK) {
                var boat = createBoat(stack, level, player.getYRot(), hitresult.getLocation().x, hitresult.getLocation().y, hitresult.getLocation().z);
                if (!level.noCollision(boat, boat.getBoundingBox())) {
                    return InteractionResultHolder.fail(stack);
                } else {
                    if (!level.isClientSide) {
                        level.addFreshEntity(boat);
                        level.gameEvent(player, GameEvent.ENTITY_PLACE, hitresult.getLocation());
                        if (!player.getAbilities().instabuild) {
                            stack.shrink(1);
                        }
                    }
                    player.awardStat(Stats.ITEM_USED.get(this));
                    return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
                }
            } else {
                return InteractionResultHolder.pass(stack);
            }
        }
    }

    protected Boat createBoat(ItemStack stack, Level level, float rotation, double posX, double posY, double posZ) {

        var boat = factory.createBoat(type, level, posX, posY, posZ);
        if (stack.hasCustomHoverName()) {
            boat.setCustomName(stack.getHoverName());
        }
        if (boat instanceof IOnPlaced placedBoat) {
            placedBoat.onPlaced(stack);
        }
        boat.setYRot(rotation);
        return boat;
    }

    // region FACTORY
    public interface IBoatFactory<T extends Boat> {

        T createBoat(Supplier<EntityType<? extends Boat>> type, Level world, double posX, double posY, double posZ);

    }
    // endregion

    // region DISPENSER BEHAVIOR
    private static final DispenseItemBehavior DISPENSER_BEHAVIOR = new DefaultDispenseItemBehavior() {

        private final DefaultDispenseItemBehavior behaviourDefaultDispenseItem = new DefaultDispenseItemBehavior();

        @Override
        public ItemStack execute(BlockSource source, ItemStack stack) {

            Direction direction = source.getBlockState().getValue(DispenserBlock.FACING);
            Level level = source.getLevel();
            double d0 = source.x() + ((double) direction.getStepX() * 1.125D);
            double d1 = source.y() + ((double) direction.getStepY() * 1.125D);
            double d2 = source.z() + ((double) direction.getStepZ() * 1.125D);
            BlockPos pos = source.getPos().relative(direction);

            if (stack.getItem() instanceof BoatItemCoFH boatItem) {
                var boat = boatItem.createBoat(stack, level, direction.toYRot(), d0, d1, d2);
                double d3;
                if (boat.canBoatInFluid(level.getFluidState(pos))) {
                    d3 = 1.0D;
                } else {
                    if (!level.getBlockState(pos).isAir() || !boat.canBoatInFluid(level.getFluidState(pos.below()))) {
                        return this.behaviourDefaultDispenseItem.dispense(source, stack);
                    }
                    d3 = 0.0D;
                }
                boat.setPos(d0, d1 + d3, d2);
                level.addFreshEntity(boat);
                stack.shrink(1);
                return stack;
            }
            return ItemStack.EMPTY;
        }
    };
    // endregion
}
