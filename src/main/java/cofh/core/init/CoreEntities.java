package cofh.core.init;

import cofh.lib.entity.KnifeEntity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;

import static cofh.core.CoFHCore.ENTITIES;
import static cofh.lib.util.references.CoreIDs.ID_KNIFE;

public class CoreEntities {

    private CoreEntities() {

    }

    public static void register() {

        ENTITIES.register(ID_KNIFE, () -> EntityType.Builder.<KnifeEntity>of(KnifeEntity::new, EntityClassification.MISC).sized(0.5F, 0.5F).fireImmune().build(ID_KNIFE));
    }

}
