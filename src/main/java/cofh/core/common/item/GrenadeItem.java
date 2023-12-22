package cofh.core.common.item;

import cofh.core.common.entity.AbstractGrenade;
import cofh.core.util.ProxyUtils;
import cofh.lib.util.helpers.MathHelper;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.AbstractProjectileDispenseBehavior;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;

import static cofh.core.util.helpers.ItemHelper.cloneStack;

public class GrenadeItem extends ItemCoFH {

    protected final IGrenadeFactory<? extends AbstractGrenade> factory;

    protected int radius = 4;
    protected int cooldown = 20;

    public GrenadeItem(IGrenadeFactory<? extends AbstractGrenade> factory, Properties builder) {

        super(builder);
        this.factory = factory;

        ProxyUtils.registerItemModelProperty(this, new ResourceLocation("thrown"), (stack, world, living, seed) -> (stack.getDamageValue() > 0 ? 1.0F : 0.0F));
        DispenserBlock.registerBehavior(this, DISPENSER_BEHAVIOR);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {

        ItemStack stack = playerIn.getItemInHand(handIn);
        worldIn.playSound(null, playerIn.getX(), playerIn.getY(), playerIn.getZ(), SoundEvents.SNOWBALL_THROW, SoundSource.NEUTRAL, 0.5F, 0.4F / (MathHelper.RANDOM.nextFloat() * 0.4F + 0.8F));
        playerIn.getCooldowns().addCooldown(this, cooldown);
        if (!worldIn.isClientSide) {
            createGrenade(stack, worldIn, playerIn);
        }
        playerIn.awardStat(Stats.ITEM_USED.get(this));
        if (!playerIn.getAbilities().instabuild) {
            stack.shrink(1);
        }
        return InteractionResultHolder.sidedSuccess(stack, worldIn.isClientSide());
    }

    protected void createGrenade(ItemStack stack, Level world, Player player) {

        AbstractGrenade grenade = factory.createGrenade(world, player);
        ItemStack throwStack = cloneStack(stack, 1);
        throwStack.setDamageValue(1);
        grenade.setItem(throwStack);
        grenade.setRadius(1 + radius);
        grenade.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.5F, 0.5F);
        world.addFreshEntity(grenade);
    }

    // region FACTORY
    public interface IGrenadeFactory<T extends AbstractGrenade> {

        T createGrenade(Level world, LivingEntity living);

        T createGrenade(Level world, double posX, double posY, double posZ);

    }
    // endregion

    // region DISPENSER BEHAVIOR
    private static final AbstractProjectileDispenseBehavior DISPENSER_BEHAVIOR = new AbstractProjectileDispenseBehavior() {

        @Override
        public Projectile getProjectile(Level worldIn, Position position, ItemStack stackIn) {

            GrenadeItem grenadeItem = ((GrenadeItem) stackIn.getItem());
            AbstractGrenade grenade = grenadeItem.factory.createGrenade(worldIn, position.x(), position.y(), position.z());
            ItemStack throwStack = cloneStack(stackIn, 1);
            throwStack.setDamageValue(1);
            grenade.setItem(throwStack);
            grenade.setRadius(1 + grenadeItem.radius);
            return grenade;
        }

        @Override
        protected float getUncertainty() {

            return 3.0F;
        }
    };
    // endregion
}
