package cofh.core.common.block;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Equipable;

public class EquipableCarvedPumpkinBlockCoFH extends CarvedPumpkinBlockCoFH implements Equipable {

    public EquipableCarvedPumpkinBlockCoFH(Properties properties) {

        super(properties);
    }

    @Override
    public EquipmentSlot getEquipmentSlot() {

        return EquipmentSlot.HEAD;
    }

}
