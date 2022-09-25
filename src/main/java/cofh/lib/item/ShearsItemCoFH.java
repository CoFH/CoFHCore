package cofh.lib.item;

import cofh.lib.api.item.ICoFHItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.dispenser.ShearsDispenseItemBehavior;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.DispenserBlock;

public class ShearsItemCoFH extends ShearsItem implements ICoFHItem {

    protected int enchantability = 0;

    public ShearsItemCoFH(Properties builder) {

        super(builder);
        DispenserBlock.registerBehavior(this, new ShearsDispenseItemBehavior());
    }

    public ShearsItemCoFH(Tier tier, Properties builder) {

        this(builder);
        setParams(tier);
    }

    public ShearsItemCoFH setParams(Tier tier) {

        this.enchantability = tier.getEnchantmentValue();
        return this;
    }

    @Override
    public int getEnchantmentValue() {

        return enchantability;
    }

    @Override
    public boolean doesSneakBypassUse(ItemStack stack, LevelReader world, BlockPos pos, Player player) {

        return true;
    }

}
