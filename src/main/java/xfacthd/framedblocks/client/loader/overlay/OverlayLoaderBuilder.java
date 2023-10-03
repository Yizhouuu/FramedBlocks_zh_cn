package xfacthd.framedblocks.client.loader.overlay;

import com.google.common.base.Preconditions;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.math.Vector3f;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.CustomLoaderBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;

public final class OverlayLoaderBuilder extends CustomLoaderBuilder<BlockModelBuilder>
{
    private BlockModelBuilder model;
    private Vector3f center;

    public OverlayLoaderBuilder(BlockModelBuilder parent, ExistingFileHelper fileHelper)
    {
        super(OverlayLoader.ID, parent, fileHelper);
    }

    public OverlayLoaderBuilder model(BlockModelBuilder model)
    {
        Preconditions.checkState(this.model == null, "Model already set");
        this.model = model;
        return this;
    }

    public OverlayLoaderBuilder center(Vector3f center)
    {
        Preconditions.checkState(this.center == null, "Center vector already set");
        this.center = center;
        return this;
    }

    @Override
    public JsonObject toJson(JsonObject json)
    {
        Preconditions.checkState(this.model != null, "Model not set");
        json.add("model", model.toJson());

        if (center != null)
        {
            JsonArray centerArr = new JsonArray(3);
            centerArr.add(center.x());
            centerArr.add(center.y());
            centerArr.add(center.z());
            json.add("center", centerArr);
        }

        return super.toJson(json);
    }
}
