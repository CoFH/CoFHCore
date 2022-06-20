package cofh.core.content.item;

import cofh.core.content.entity.Knife;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.AbstractProjectileDispenseBehavior;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;

public class KnifeItem extends SwordItemCoFH {

    private static final int DEFAULT_ATTACK_DAMAGE = 1;
    private static final float DEFAULT_ATTACK_SPEED = -2.0F;

    public KnifeItem(Tier tier, int attackDamageIn, float attackSpeedIn, Properties builder) {

        super(tier, attackDamageIn, attackSpeedIn, builder);

        DispenserBlock.registerBehavior(this, DISPENSER_BEHAVIOR);
    }

    public KnifeItem(Tier tier, Properties builder) {

        this(tier, DEFAULT_ATTACK_DAMAGE, DEFAULT_ATTACK_SPEED, builder);
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {

        return super.canApplyAtEnchantingTable(stack, enchantment) || enchantment.equals(Enchantments.LOYALTY);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {

        player.startUsingItem(hand);
        return InteractionResultHolder.consume(player.getItemInHand(hand));
    }

    @Override
    public int getUseDuration(ItemStack stack) {

        return 72000;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {

        return UseAnim.SPEAR;
    }

    @Override
    public void releaseUsing(ItemStack stack, Level world, LivingEntity living, int durationRemaining) {

        if (living instanceof Player player) {
            float power = BowItem.getPowerForTime(this.getUseDuration(stack) - durationRemaining);
            if (power < 0.1D) {
                return;
            }
            if (!world.isClientSide) {
                Knife knife = new Knife(world, player, stack);
                knife.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, power * 3.0F, 0.1F);
                if (player.getAbilities().instabuild) {
                    knife.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
                }
                world.addFreshEntity(knife);
            }
            world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.TRIDENT_THROW, SoundSource.PLAYERS, 1.0F, 1.0F / (random.nextFloat() * 0.4F + 1.2F) + power * 0.5F);
            if (!player.getAbilities().instabuild) {
                player.getInventory().removeItem(stack);
            }
            player.awardStat(Stats.ITEM_USED.get(this));
        }
    }

    // region DISPENSER BEHAVIOR
    private static final AbstractProjectileDispenseBehavior DISPENSER_BEHAVIOR = new AbstractProjectileDispenseBehavior() {

        @Override
        protected Projectile getProjectile(Level worldIn, Position position, ItemStack stackIn) {

            Knife knife = new Knife(worldIn, position.x(), position.y(), position.z(), stackIn);
            knife.pickup = AbstractArrow.Pickup.ALLOWED;
            return knife;
        }

        @Override
        protected float getUncertainty() {

            return 3.0F;
        }
    };
    // endregion

}
