package cofh.core.item;

import cofh.lib.item.ICoFHItem;
import net.minecraft.block.DispenserBlock;
import net.minecraft.dispenser.IPosition;
import net.minecraft.dispenser.ProjectileDispenseBehavior;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Collection;
import java.util.Collections;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

import static cofh.lib.util.Utils.getItemEnchantmentLevel;
import static cofh.lib.util.constants.Constants.TRUE;

public class ArrowItemCoFH extends ArrowItem implements ICoFHItem {

    protected BooleanSupplier showEnchantEffect = TRUE;
    protected BooleanSupplier showInItemGroup = TRUE;

    protected Supplier<ItemGroup> displayGroup;

    protected final IArrowFactory<? extends AbstractArrowEntity> factory;
    protected boolean infinitySupport = false;

    public ArrowItemCoFH(IArrowFactory<? extends AbstractArrowEntity> factory, Properties builder) {

        super(builder);
        this.factory = factory;

        DispenserBlock.registerDispenseBehavior(this, DISPENSER_BEHAVIOR);
    }

    public ArrowItemCoFH setDisplayGroup(Supplier<ItemGroup> displayGroup) {

        this.displayGroup = displayGroup;
        return this;
    }

    public ArrowItemCoFH setInfinitySupport(boolean infinitySupport) {

        this.infinitySupport = infinitySupport;
        return this;
    }

    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {

        if (!showInItemGroup.getAsBoolean()) {
            return;
        }
        super.fillItemGroup(group, items);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean hasEffect(ItemStack stack) {

        return showEnchantEffect.getAsBoolean() && stack.isEnchanted();
    }

    @Override
    protected boolean isInGroup(ItemGroup group) {

        return group == ItemGroup.SEARCH || getCreativeTabs().stream().anyMatch(tab -> tab == group);
    }

    @Override
    public Collection<ItemGroup> getCreativeTabs() {

        return displayGroup != null && displayGroup.get() != null ? Collections.singletonList(displayGroup.get()) : super.getCreativeTabs();
    }

    @Override
    public AbstractArrowEntity createArrow(World worldIn, ItemStack stack, LivingEntity shooter) {

        return factory.createArrow(worldIn, shooter);
    }

    @Override
    public boolean isInfinite(ItemStack stack, ItemStack bow, PlayerEntity player) {

        return infinitySupport && getItemEnchantmentLevel(Enchantments.INFINITY, bow) > 0 || super.isInfinite(stack, bow, player);
    }

    // region FACTORY
    public interface IArrowFactory<T extends AbstractArrowEntity> {

        T createArrow(World world, LivingEntity living);

        T createArrow(World world, double posX, double posY, double posZ);

    }
    // endregion

    // region DISPENSER BEHAVIOR
    private static final ProjectileDispenseBehavior DISPENSER_BEHAVIOR = new ProjectileDispenseBehavior() {

        @Override
        protected ProjectileEntity getProjectileEntity(World worldIn, IPosition position, ItemStack stackIn) {

            ArrowItemCoFH arrowItem = ((ArrowItemCoFH) stackIn.getItem());
            AbstractArrowEntity arrow = arrowItem.factory.createArrow(worldIn, position.getX(), position.getY(), position.getZ());
            arrow.pickupStatus = AbstractArrowEntity.PickupStatus.ALLOWED;
            return arrow;
        }
    };
    // endregion
}
