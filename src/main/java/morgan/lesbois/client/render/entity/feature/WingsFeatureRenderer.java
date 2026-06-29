package morgan.lesbois.client.render.entity.feature;

import morgan.lesbois.client.render.entity.model.WingsEntityModel;
import morgan.lesbois.interfaces.Winged;
import morgan.lesbois.powers.WingsPowerType;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class WingsFeatureRenderer extends FeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {
    private final WingsEntityModel model;

    public WingsFeatureRenderer(FeatureRendererContext<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> context) {
        super(context);
        this.model = new WingsEntityModel(WingsEntityModel.getTexturedModelData().createModel());
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        if (!WingsPowerType.hasWings(entity)) return;

        Identifier texture = WingsPowerType.getTexture(entity);
        if (texture == null) return;

        matrices.push();

        float yOffset = (0.375F) * this.getContextModel().body.yScale;
        float zOffset = (0.125F - 0.015625F) * this.getContextModel().body.zScale;

        this.getContextModel().body.rotate(matrices);
        matrices.translate(0.0F, yOffset, zOffset);
        matrices.scale(0.5F, 0.5F, 0.5F);

        this.getContextModel().copyStateTo(this.model);

        float wingAngle = MathHelper.lerp(tickDelta, ((Winged) entity).lesbois$getPrevWingAngle(), ((Winged) entity).lesbois$getWingAngle());
        float wingDistance = MathHelper.lerp(tickDelta, ((Winged) entity).lesbois$getPrevWingDistance(), ((Winged) entity).lesbois$getWingDistance());

        this.model.setAngles(entity, wingAngle, wingDistance, animationProgress, headYaw, headPitch);

        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucentCull(texture));
        this.model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV);

        matrices.pop();
    }
}
