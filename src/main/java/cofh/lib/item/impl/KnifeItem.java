package cofh.lib.item.impl;

import cofh.lib.entity.KnifeEntity;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

import static cofh.lib.util.constants.ToolTypes.KNIFE;

public class KnifeItem extends SwordItemCoFH {

    private static final int DEFAULT_ATTACK_DAMAGE = 1;
    private static final float DEFAULT_ATTACK_SPEED = -2.0F;

    public KnifeItem(IItemTier tier, int attackDamageIn, float attackSpeedIn, Properties builder) {

        super(tier, attackDamageIn, attackSpeedIn, builder);
    }

    public KnifeItem(IItemTier tier, Properties builder) {

        this(tier, DEFAULT_ATTACK_DAMAGE, DEFAULT_ATTACK_SPEED, builder.addToolType(KNIFE, tier.getLevel()));
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {

        return super.canApplyAtEnchantingTable(stack, enchantment) || enchantment.equals(Enchantments.LOYALTY);
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {

        player.startUsingItem(hand);
        return ActionResult.consume(player.getItemInHand(hand));
    }

    @Override
    public int getUseDuration(ItemStack stack) {

        return 72000;
    }

    @Override
    public UseAction getUseAnimation(ItemStack stack) {

        return UseAction.BOW;
    }

    @Override
    public void releaseUsing(ItemStack stack, World world, LivingEntity living, int durationRemaining) {

        if (living instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) living;
            float power = BowItem.getPowerForTime(this.getUseDuration(stack) - durationRemaining);
            if (power < 0.1D) {
                return;
            }

            if (!world.isClientSide) {
                KnifeEntity knifeEntity = new KnifeEntity(world, player, stack);
                knifeEntity.shootFromRotation(player, player.xRot, player.yRot, 0.0F, power * 3.0F, 0.1F);
                if (player.abilities.instabuild) {
                    knifeEntity.pickup = AbstractArrowEntity.PickupStatus.CREATIVE_ONLY;
                }
                world.addFreshEntity(knifeEntity);
            }
            world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.TRIDENT_THROW, SoundCategory.PLAYERS, 1.0F, 1.0F / (random.nextFloat() * 0.4F + 1.2F) + power * 0.5F);
            if (!player.abilities.instabuild) {
                player.inventory.removeItem(stack);
            }
            player.awardStat(Stats.ITEM_USED.get(this));
        }
    }

}
