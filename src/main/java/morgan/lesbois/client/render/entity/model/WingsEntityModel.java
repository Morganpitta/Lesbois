package morgan.lesbois.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.*;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.model.AnimalModel;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.util.math.MathHelper;

public class WingsEntityModel extends AnimalModel<AbstractClientPlayerEntity> {
    public static final float DEFAULT_ANGLE = (float) (45 * (Math.PI) / 180.0F);
    public static final float FLAP_SPEED = 1.5F;
    public static final float FLAP_SIZE = (float) (20 * (Math.PI) / 180.0F);

    private final ModelPart rightWing;
    private final ModelPart leftWing;

    public WingsEntityModel(ModelPart root) {
        this.leftWing = root.getChild(EntityModelPartNames.LEFT_WING);
        this.rightWing = root.getChild(EntityModelPartNames.RIGHT_WING);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        Dilation dilation = new Dilation(0.0F);
        modelPartData.addChild(
                EntityModelPartNames.LEFT_WING,
                ModelPartBuilder.create().uv(0, 36).cuboid(0.0F, -18.0F, 0.0F, 24.0F, 36.0F, 0.0F, dilation),
                ModelTransform.of(0.0F, 0.0F, -1.0F, 0.0F, -0.7854F, 0.0F)
        );
        modelPartData.addChild(
                EntityModelPartNames.RIGHT_WING,
                ModelPartBuilder.create().uv(0, 0).cuboid(-24.0F, -18.0F, 0.0F, 24.0F, 36.0F, 0.0F, dilation),
                ModelTransform.of(-1.0F, 0.0F, -1.0F, 0.0F, 0.7854F, 0.0F)
        );
        return TexturedModelData.of(modelData, 128, 128);
    }

    @Override
    protected Iterable<ModelPart> getHeadParts() {
        return ImmutableList.of();
    }

    @Override
    protected Iterable<ModelPart> getBodyParts() {
        return ImmutableList.of(this.leftWing, this.rightWing);
    }

    @Override
    public void setAngles(AbstractClientPlayerEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        float flapAngle = MathHelper.cos(limbAngle * FLAP_SPEED) * FLAP_SIZE * limbDistance;

        this.leftWing.yaw = -(DEFAULT_ANGLE + flapAngle);
        this.rightWing.yaw = DEFAULT_ANGLE + flapAngle;
    }
}
