package cofh.core.item;

import cofh.core.capability.templates.AreaEffectMiningItemWrapper;
import cofh.lib.api.item.ICoFHItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nullable;

import static cofh.lib.tags.BlockTagsCoFH.MINEABLE_WITH_SICKLE;

public class SickleItem extends DiggerItem implements ICoFHItem {

    private static final float DEFAULT_ATTACK_DAMAGE = 2.5F;
    private static final float DEFAULT_ATTACK_SPEED = -2.6F;
    private static final int DEFAULT_BASE_RADIUS = 2;
    private static final int DEFAULT_BASE_HEIGHT = 0;

    protected final int radius;
    protected final int height;

    public SickleItem(Tier tier, float attackDamageIn, float attackSpeedIn, int radius, int height, Properties builder) {

        super(attackDamageIn, attackSpeedIn, tier, MINEABLE_WITH_SICKLE, builder.durability(tier.getUses() * 4));
        this.radius = radius;
        this.height = height;
    }

    public SickleItem(Tier tier, float attackDamageIn, float attackSpeedIn, Properties builder) {

        this(tier, attackDamageIn, attackSpeedIn, DEFAULT_BASE_RADIUS, DEFAULT_BASE_HEIGHT, builder);
    }

    public SickleItem(Tier tier, float attackDamageIn, Properties builder) {

        this(tier, attackDamageIn, DEFAULT_ATTACK_SPEED, DEFAULT_BASE_RADIUS, DEFAULT_BASE_HEIGHT, builder);
    }

    public SickleItem(Tier tier, Properties builder) {

        this(tier, DEFAULT_ATTACK_DAMAGE, DEFAULT_ATTACK_SPEED, DEFAULT_BASE_RADIUS, DEFAULT_BASE_HEIGHT, builder);
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {

        Block block = state.getBlock();
        if (block == Blocks.COBWEB) {
            return 15.0F;
        } else {
            return super.getDestroySpeed(stack, state);
        }
    }

    @Nullable
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {

        return new AreaEffectMiningItemWrapper(stack, radius, height, AreaEffectMiningItemWrapper.Type.SICKLE);
    }

    // region DISPLAY
    protected String modId = "";

    @Override
    public SickleItem setModId(String modId) {

        this.modId = modId;
        return this;
    }

    @Override
    public String getCreatorModId(ItemStack itemStack) {

        return modId == null || modId.isEmpty() ? super.getCreatorModId(itemStack) : modId;
    }
    // endregion
}
