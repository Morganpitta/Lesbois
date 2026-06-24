package morgan.lesbois.client.render.entity.feature;

import morgan.lesbois.entity.effect.LesboisStatusEffects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class UnstableFeatureRenderer<T extends LivingEntity, M extends EntityModel<T>> extends FeatureRenderer<T, M> {
    private static final Identifier SKIN = Identifier.ofVanilla("textures/entity/creeper/creeper_armor.png");
    private final M model;

    public UnstableFeatureRenderer(FeatureRendererContext<T, M> context) {
        super(context);
        this.model = context.getModel();
    }

    protected Identifier getTexture() {
        return SKIN;
    }

    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        if (entity.hasStatusEffect(LesboisStatusEffects.UNSTABLE)) {
            int duration = entity.getStatusEffect(LesboisStatusEffects.UNSTABLE).getDuration();
            float f = entity.age + tickDelta;

            this.model.animateModel(entity, limbAngle, limbDistance, tickDelta);
            this.getContextModel().copyStateTo(this.model);
            this.model.setAngles(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch);

            VertexConsumer vertexConsumer = vertexConsumers.getBuffer(
                    RenderLayer.getEnergySwirl(this.getTexture(), f * 0.03F % 1.0F, f * 0.01F % 1.0F)
            );

            this.model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, getColour(tickDelta, duration));
        }
    }

    private static int getColour(float tickDelta, int duration) {
        float startR = 0.5f, startG = 0.5f, startB = 0.5f;
        float endR = 1.0f, endG = 0.0f, endB = 0.0f;

        float lerpFactor = 0.0f;
        if (duration <= 20) {
            float smoothedDuration = duration - tickDelta;
            lerpFactor = 1.0f - Math.max(0.0f, smoothedDuration / 20.0f);
        }

        int r = (int) ((startR + (endR - startR) * lerpFactor) * 255.0f);
        int g = (int) ((startG + (endG - startG) * lerpFactor) * 255.0f);
        int b = (int) ((startB + (endB - startB) * lerpFactor) * 255.0f);
        int a = 255;

        return (a << 24) | (r << 16) | (g << 8) | b;
    }
}