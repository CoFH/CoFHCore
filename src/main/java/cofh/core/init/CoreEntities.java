package cofh.core.init;

import cofh.lib.entity.ElectricArcEntity;
import cofh.lib.entity.KnifeEntity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;

import static cofh.core.CoFHCore.ENTITIES;
import static cofh.lib.util.references.CoreIDs.ID_ELECTRIC_ARC;
import static cofh.lib.util.references.CoreIDs.ID_KNIFE;

public class CoreEntities {

    private CoreEntities() {

    }

    public static void register() {

        ENTITIES.register(ID_KNIFE, () -> EntityType.Builder.<KnifeEntity>of(KnifeEntity::new, EntityClassification.MISC).sized(0.5F, 0.5F).fireImmune().build(ID_KNIFE));
        ENTITIES.register(ID_ELECTRIC_ARC, () -> EntityType.Builder.<ElectricArcEntity>of(ElectricArcEntity::new, EntityClassification.MISC).sized(1.0F, 1.0F).fireImmune().build(ID_ELECTRIC_ARC));
    }

}
