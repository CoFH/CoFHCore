package cofh.core.item;

import cofh.core.util.ProxyUtils;
import cofh.lib.api.item.ICoFHItem;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.UUID;
import java.util.function.Supplier;

import static cofh.lib.util.Constants.TRUE;

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

    public ArmorItemCoFH(ArmorMaterial materialIn, EquipmentSlot slot, Properties builder) {

        super(materialIn, slot, builder);
    }

    // region DISPLAY
    protected Supplier<CreativeModeTab> displayGroup;
    protected Supplier<Boolean> showInGroups = TRUE;
    protected String modId = "";

    @Override
    public ArmorItemCoFH setDisplayGroup(Supplier<CreativeModeTab> displayGroup) {

        this.displayGroup = displayGroup;
        return this;
    }

    @Override
    public ArmorItemCoFH setModId(String modId) {

        this.modId = modId;
        return this;
    }

    @Override
    public ArmorItemCoFH setShowInGroups(Supplier<Boolean> showInGroups) {

        this.showInGroups = showInGroups;
        return this;
    }

    @Override
    public String getCreatorModId(ItemStack itemStack) {

        return modId == null || modId.isEmpty() ? super.getCreatorModId(itemStack) : modId;
    }
    // endregion

    @Nullable
    public <A extends HumanoidModel<?>> A getArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlot armorSlot, A _default) {

        return (A) ProxyUtils.getModel(ForgeRegistries.ITEMS.getKey(this));
    }

}
