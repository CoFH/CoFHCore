package cofh.core.common.capability.templates;

import cofh.core.util.helpers.ArcheryHelper;
import cofh.lib.api.capability.IArcheryAmmoItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;

import static cofh.lib.util.Utils.getItemEnchantmentLevel;

public class ArcheryAmmoItemWrapper implements IArcheryAmmoItem {

    final ItemStack ammoItem;

    public ArcheryAmmoItemWrapper(ItemStack ammoItem) {

        this.ammoItem = ammoItem;
    }

    @Override
    public void onArrowLoosed(Player shooter) {

        ammoItem.shrink(1);
    }

    @Override
    public AbstractArrow createArrowEntity(Level world, Player shooter) {

        return ArcheryHelper.createDefaultArrow(world, ammoItem, shooter);
    }

    @Override
    public boolean isEmpty(Player shooter) {

        return ammoItem.isEmpty();
    }

    @Override
    public boolean isInfinite(ItemStack bow, Player shooter) {

        return shooter != null && shooter.getAbilities().instabuild || getItemEnchantmentLevel(Enchantments.INFINITY_ARROWS, bow) > 0 && ammoItem.getItem().getClass() == ArrowItem.class;
    }

}
