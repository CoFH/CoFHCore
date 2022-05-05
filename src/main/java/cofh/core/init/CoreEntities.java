package cofh.core.init;

import cofh.lib.entity.BlackHole;
import cofh.lib.entity.ElectricArc;
import cofh.lib.entity.ElectricField;
import cofh.lib.entity.KnifeEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import static cofh.core.CoFHCore.ENTITIES;
import static cofh.lib.util.references.CoreIDs.*;

public class CoreEntities {

    private CoreEntities() {

    }

    public static void register() {

        ENTITIES.register(ID_KNIFE, () -> EntityType.Builder.<KnifeEntity>of(KnifeEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).fireImmune().build(ID_KNIFE));
        ENTITIES.register(ID_ELECTRIC_ARC, () -> EntityType.Builder.<ElectricArc>of(ElectricArc::new, MobCategory.MISC).sized(0.2F, 10.0F).fireImmune().noSave().build(ID_ELECTRIC_ARC));
        ENTITIES.register(ID_ELECTRIC_FIELD, () -> EntityType.Builder.<ElectricField>of(ElectricField::new, MobCategory.MISC).sized(0.1F, 0.1F).fireImmune().noSave().build(ID_ELECTRIC_FIELD));
        ENTITIES.register(ID_BLACK_HOLE, () -> EntityType.Builder.<BlackHole>of(BlackHole::new, MobCategory.MISC).sized(0.1F, 0.1F).build(ID_BLACK_HOLE));
    }

}
