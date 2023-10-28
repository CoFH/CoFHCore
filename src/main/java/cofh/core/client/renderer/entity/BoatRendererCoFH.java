package cofh.core.client.renderer.entity;

import com.mojang.datafixers.util.Pair;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.ChestBoatModel;
import net.minecraft.client.model.ListModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.vehicle.Boat;

public class BoatRendererCoFH extends BoatRenderer {

    Pair<ResourceLocation, ListModel<Boat>> modelPair;

    public BoatRendererCoFH(EntityRendererProvider.Context context, boolean chestBoat, String modId, String name, ModelLayerLocation modelLayerLoc) {

        super(context, chestBoat);
        if (chestBoat) {
            modelPair = Pair.of(new ResourceLocation(modId, "textures/entity/chest_boat/" + name + ".png"), new ChestBoatModel(context.bakeLayer(modelLayerLoc)));
        } else {
            modelPair = Pair.of(new ResourceLocation(modId, "textures/entity/boat/" + name + ".png"), new BoatModel(context.bakeLayer(modelLayerLoc)));
        }
    }

    @Override
    public Pair<ResourceLocation, ListModel<Boat>> getModelWithLocation(Boat boat) {

        return modelPair;
    }

}
