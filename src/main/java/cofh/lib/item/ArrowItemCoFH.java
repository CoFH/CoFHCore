package cofh.lib.item;

import cofh.lib.api.item.ICoFHItem;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.AbstractProjectileDispenseBehavior;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;

import java.util.function.Supplier;

import static cofh.lib.util.Constants.TRUE;
import static cofh.lib.util.Utils.getItemEnchantmentLevel;

public class ArrowItemCoFH extends ArrowItem implements ICoFHItem {

    protected final IArrowFactory<? extends AbstractArrow> factory;
    protected boolean infinitySupport = false;

    public ArrowItemCoFH(IArrowFactory<? extends AbstractArrow> factory, Properties builder) {

        super(builder);
        this.factory = factory;

        DispenserBlock.registerBehavior(this, DISPENSER_BEHAVIOR);
    }

    public ArrowItemCoFH setInfinitySupport(boolean infinitySupport) {

        this.infinitySupport = infinitySupport;
        return this;
    }

    @Override
    public AbstractArrow createArrow(Level worldIn, ItemStack stack, LivingEntity shooter) {

        return factory.createArrow(worldIn, shooter);
    }

    @Override
    public boolean isInfinite(ItemStack stack, ItemStack bow, Player player) {

        return infinitySupport && getItemEnchantmentLevel(Enchantments.INFINITY_ARROWS, bow) > 0 || super.isInfinite(stack, bow, player);
    }

    // region DISPLAY
    protected Supplier<CreativeModeTab> displayGroup;
    protected Supplier<Boolean> showInGroups = TRUE;
    protected String modId = "";

    @Override
    public ArrowItemCoFH setDisplayGroup(Supplier<CreativeModeTab> displayGroup) {

        this.displayGroup = displayGroup;
        return this;
    }

    @Override
    public ArrowItemCoFH setModId(String modId) {

        this.modId = modId;
        return this;
    }

    @Override
    public ArrowItemCoFH setShowInGroups(Supplier<Boolean> showInGroups) {

        this.showInGroups = showInGroups;
        return this;
    }

    @Override
    public String getCreatorModId(ItemStack itemStack) {

        return modId == null || modId.isEmpty() ? super.getCreatorModId(itemStack) : modId;
    }
    // endregion

    // region FACTORY
    public interface IArrowFactory<T extends AbstractArrow> {

        T createArrow(Level world, LivingEntity living);

        T createArrow(Level world, double posX, double posY, double posZ);

    }
    // endregion

    // region DISPENSER BEHAVIOR
    private static final AbstractProjectileDispenseBehavior DISPENSER_BEHAVIOR = new AbstractProjectileDispenseBehavior() {

        @Override
        public Projectile getProjectile(Level worldIn, Position position, ItemStack stackIn) {

            ArrowItemCoFH arrowItem = ((ArrowItemCoFH) stackIn.getItem());
            AbstractArrow arrow = arrowItem.factory.createArrow(worldIn, position.x(), position.y(), position.z());
            arrow.pickup = AbstractArrow.Pickup.ALLOWED;
            return arrow;
        }
    };
    // endregion
}
