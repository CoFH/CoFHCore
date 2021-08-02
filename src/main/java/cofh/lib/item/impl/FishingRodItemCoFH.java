package cofh.lib.item.impl;

import cofh.lib.item.ICoFHItem;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.FishingRodItem;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

public class FishingRodItemCoFH extends FishingRodItem implements ICoFHItem {

    protected int enchantability = 1;
    protected int luckModifier;
    protected int speedModifier;

    public FishingRodItemCoFH(Properties builder) {

        super(builder);
    }

    public FishingRodItemCoFH(IItemTier tier, Properties builder) {

        super(builder);
        setParams(tier);
    }

    public FishingRodItemCoFH setParams(IItemTier tier) {

        enchantability = tier.getEnchantability();
        luckModifier = tier.getHarvestLevel() / 2;
        speedModifier = (int) tier.getEfficiency() / 3;
        return this;
    }

    public FishingRodItemCoFH setParams(int enchantability, int luckModifier, int speedModifier) {

        this.enchantability = enchantability;
        this.luckModifier = luckModifier;
        this.speedModifier = speedModifier;
        return this;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {

        ItemStack stack = playerIn.getHeldItem(handIn);
        if (playerIn.fishingBobber != null) {
            if (!worldIn.isRemote) {
                int i = playerIn.fishingBobber.handleHookRetraction(stack);
                stack.damageItem(i, playerIn, (entity) -> {
                    entity.sendBreakAnimation(handIn);
                });
            }
            playerIn.swingArm(handIn);
            worldIn.playSound(null, playerIn.getPosX(), playerIn.getPosY(), playerIn.getPosZ(), SoundEvents.ENTITY_FISHING_BOBBER_RETRIEVE, SoundCategory.NEUTRAL, 1.0F, 0.4F / (random.nextFloat() * 0.4F + 0.8F));
        } else {
            worldIn.playSound(null, playerIn.getPosX(), playerIn.getPosY(), playerIn.getPosZ(), SoundEvents.ENTITY_FISHING_BOBBER_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (random.nextFloat() * 0.4F + 0.8F));
            if (!worldIn.isRemote) {
                int luck = EnchantmentHelper.getFishingLuckBonus(stack) + luckModifier;
                int speed = EnchantmentHelper.getFishingSpeedBonus(stack) + speedModifier;
                worldIn.addEntity(new FishingBobberEntity(playerIn, worldIn, luck, speed));
            }

            playerIn.swingArm(handIn);
            playerIn.addStat(Stats.ITEM_USED.get(this));
        }

        return ActionResult.resultSuccess(stack);
    }

    @Override
    public int getItemEnchantability() {

        return enchantability;
    }

}
