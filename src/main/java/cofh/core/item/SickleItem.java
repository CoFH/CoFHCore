package cofh.core.item;

import cofh.core.capability.templates.AreaEffectMiningItemWrapper;
import cofh.lib.api.item.ICoFHItem;
import cofh.lib.util.constants.ModIds;
import com.google.common.collect.ImmutableSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

import static cofh.lib.util.Constants.TRUE;

public class SickleItem extends DiggerItem implements ICoFHItem {

    @Deprecated // TOOD move to Tags class somewhere and generate.
    public static final TagKey<Block> EFFECTIVE_BLOCKS = BlockTags.create(new ResourceLocation(ModIds.ID_THERMAL, "mineable/sickle"));
    public static final Set<Material> EFFECTIVE_MATERIALS = ImmutableSet.of(Material.LEAVES, Material.PLANT, Material.REPLACEABLE_PLANT, Material.WEB);

    private static final float DEFAULT_ATTACK_DAMAGE = 2.5F;
    private static final float DEFAULT_ATTACK_SPEED = -2.6F;
    private static final int DEFAULT_BASE_RADIUS = 2;
    private static final int DEFAULT_BASE_HEIGHT = 0;

    private final int radius;
    private final int height;

    public SickleItem(Tier tier, float attackDamageIn, float attackSpeedIn, int radius, int height, Properties builder) {

        super(attackDamageIn, attackSpeedIn, tier, EFFECTIVE_BLOCKS, builder.durability(tier.getUses() * 4));
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
    public boolean isCorrectToolForDrops(BlockState state) {

        // TODO Tags are used for this now
        //        if (state.getHarvestTool() == SICKLE) {
        //            return getTier().getLevel() >= state.getHarvestLevel();
        //        }
        return EFFECTIVE_MATERIALS.contains(state.getMaterial());
    }

    public boolean mineBlock(ItemStack stack, Level world, BlockState state, BlockPos pos, LivingEntity entity) {

        if (!world.isClientSide && !state.is(BlockTags.FIRE)) {
            stack.hurtAndBreak(1, entity, (p_220036_0_) -> {
                p_220036_0_.broadcastBreakEvent(EquipmentSlot.MAINHAND);
            });
        }
        return EFFECTIVE_MATERIALS.contains(state.getMaterial());
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {

        Block block = state.getBlock();
        if (block == Blocks.COBWEB) {
            return 15.0F;
        } else {
            return EFFECTIVE_MATERIALS.contains(state.getMaterial()) ? this.speed : 1.0F;
        }
    }

    @Nullable
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {

        return new AreaEffectMiningItemWrapper(stack, radius, height, AreaEffectMiningItemWrapper.Type.SICKLE);
    }

    // region DISPLAY
    protected Supplier<CreativeModeTab> displayGroup;
    protected BooleanSupplier showInGroups = TRUE;
    protected String modId = "";

    @Override
    public SickleItem setDisplayGroup(Supplier<CreativeModeTab> displayGroup) {

        this.displayGroup = displayGroup;
        return this;
    }

    @Override
    public SickleItem setModId(String modId) {

        this.modId = modId;
        return this;
    }

    @Override
    public SickleItem setShowInGroups(BooleanSupplier showInGroups) {

        this.showInGroups = showInGroups;
        return this;
    }

    @Override
    public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {

        if (!showInGroups.getAsBoolean() || displayGroup != null && displayGroup.get() != null && displayGroup.get() != group) {
            return;
        }
        super.fillItemCategory(group, items);
    }

    @Override
    public Collection<CreativeModeTab> getCreativeTabs() {

        return displayGroup != null && displayGroup.get() != null ? Collections.singletonList(displayGroup.get()) : super.getCreativeTabs();
    }

    @Override
    public String getCreatorModId(ItemStack itemStack) {

        return modId == null || modId.isEmpty() ? super.getCreatorModId(itemStack) : modId;
    }
    // endregion
}
