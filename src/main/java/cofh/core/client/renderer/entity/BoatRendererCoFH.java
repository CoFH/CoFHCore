package cofh.core.client.renderer.entity;

import com.mojang.datafixers.util.Pair;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.vehicle.Boat;

public class BoatRendererCoFH extends BoatRenderer {

    Pair<ResourceLocation, BoatModel> modelPair;

    public BoatRendererCoFH(EntityRendererProvider.Context context, boolean hasChest, String modId, String name, ModelLayerLocation modelLayerLoc) {

        super(context, hasChest);
        modelPair = Pair.of(hasChest ? new ResourceLocation(modId, "textures/entity/chest_boat/" + name + ".png") : new ResourceLocation(modId, "textures/entity/boat/" + name + ".png"), new BoatModel(context.bakeLayer(modelLayerLoc), hasChest));

    }

    @Override
    public Pair<ResourceLocation, BoatModel> getModelWithLocation(Boat boat) {

        return modelPair;
    }

}
