package cofh.core.compat.curios;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.items.IItemHandlerModifiable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotTypeMessage;
import top.theillusivec4.curios.api.SlotTypePreset;

import static cofh.lib.util.constants.Constants.ID_CURIOS;

public class CuriosIntegration extends CuriosProxy {

    public static void sendImc(final InterModEnqueueEvent event) {

        InterModComms.sendTo(ID_CURIOS, SlotTypeMessage.REGISTER_TYPE, () -> SlotTypePreset.BACK.getMessageBuilder().build());
        InterModComms.sendTo(ID_CURIOS, SlotTypeMessage.REGISTER_TYPE, () -> SlotTypePreset.BELT.getMessageBuilder().build());
        InterModComms.sendTo(ID_CURIOS, SlotTypeMessage.REGISTER_TYPE, () -> SlotTypePreset.CHARM.getMessageBuilder().build());
    }

    @Override
    public LazyOptional<IItemHandlerModifiable> getAllWornItems(LivingEntity living) {

        return CuriosApi.getCuriosHelper().getEquippedCurios(living);
    }

}
