package cofh.core.item;

import cofh.core.util.ProxyUtils;
import cofh.lib.item.ICoFHItem;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

import static cofh.lib.util.constants.Constants.TRUE;

public class ArmorItemCoFH extends ArmorItem implements ICoFHItem {

    protected static final double[] RESISTANCE_RATIO = new double[]{0.10D, 0.25D, 0.40D, 0.25D};

    protected static final UUID UUID_FALL_DISTANCE = UUID.fromString("3262A4A1-72DB-4A4E-8F67-8691f6E49C8B");

    protected static final UUID[] UUID_HAZARD_RESISTANCE = new UUID[]{
            UUID.fromString("301B15CD-450C-4D08-AD16-9B1F1F3322CC"),
            UUID.fromString("6060D3DF-0B01-40DB-9056-BA87E1779344"),
            UUID.fromString("DE8D2ABD-A382-43BF-8BAA-06965C3B7C19"),
            UUID.fromString("45ED4DDF-2AFD-40D3-8DD4-E82AD97DEBD6")
    };

    protected static final UUID[] UUID_STING_RESISTANCE = new UUID[]{
            UUID.fromString("8CEB464F-F750-4A90-850E-E0D77A6C812E"),
            UUID.fromString("7356C745-0EB7-4266-B0E5-899967D45025"),
            UUID.fromString("EED777DD-5E25-4648-BE4b-12AF4430AA7D"),
            UUID.fromString("66785022-03BE-4987-BF53-BD392DB96DB0")
    };

    protected static final UUID[] UUID_SWIM_SPEED = new UUID[]{
            UUID.fromString("367C0155-9577-4914-9E51-6D9A151ED489"),
            UUID.fromString("5B7192D4-103B-4480-B75E-1EAB8AD104E5"),
            UUID.fromString("3BA931C6-7F68-41F5-99DA-BC636B5E0C8C"),
            UUID.fromString("A8BD3E20-FA60-47AF-8A09-B1A57D26F3CC")
    };

    protected BooleanSupplier showInGroups = TRUE;
    protected BooleanSupplier showEnchantEffect = TRUE;

    protected Supplier<ItemGroup> displayGroup;

    public ArmorItemCoFH(IArmorMaterial materialIn, EquipmentSlotType slot, Properties builder) {

        super(materialIn, slot, builder);
    }

    public ArmorItemCoFH setDisplayGroup(Supplier<ItemGroup> displayGroup) {

        this.displayGroup = displayGroup;
        return this;
    }

    public ArmorItemCoFH setShowInGroups(BooleanSupplier showInGroups) {

        this.showInGroups = showInGroups;
        return this;
    }

    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {

        if (!showInGroups.getAsBoolean()) {
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

    @OnlyIn(Dist.CLIENT)
    @Nullable
    public <A extends BipedModel<?>> A getArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlotType armorSlot, A _default) {

        return (A) ProxyUtils.getModel(this.getRegistryName());
    }

}
