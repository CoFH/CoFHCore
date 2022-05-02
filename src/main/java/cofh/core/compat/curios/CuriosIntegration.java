package cofh.core.compat.curios;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.items.IItemHandlerModifiable;

public class CuriosIntegration extends CuriosProxy {

    public static void sendImc(final InterModEnqueueEvent event) {

        //        InterModComms.sendTo(ID_CURIOS, SlotTypeMessage.REGISTER_TYPE, () -> SlotTypePreset.BACK.getMessageBuilder().build());
        //        InterModComms.sendTo(ID_CURIOS, SlotTypeMessage.REGISTER_TYPE, () -> SlotTypePreset.BELT.getMessageBuilder().build());
        //        InterModComms.sendTo(ID_CURIOS, SlotTypeMessage.REGISTER_TYPE, () -> SlotTypePreset.CHARM.getMessageBuilder().build());
    }

    @Override
    public LazyOptional<IItemHandlerModifiable> getAllWornItems(LivingEntity living) {

        // TODO
        //        return CuriosApi.getCuriosHelper().getEquippedCurios(living);
        return LazyOptional.empty();
    }

}
