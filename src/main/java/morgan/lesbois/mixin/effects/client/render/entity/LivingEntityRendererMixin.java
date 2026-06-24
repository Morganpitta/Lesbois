package morgan.lesbois.mixin.effects.client.render.entity;

import morgan.lesbois.client.render.entity.feature.UnstableFeatureRenderer;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> implements FeatureRendererContext<T, M> {
    @Shadow
    protected abstract boolean addFeature(FeatureRenderer<T, M> feature);

    protected LivingEntityRendererMixin(EntityRendererFactory.Context context) {
        super(context);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void addUnstableRender(EntityRendererFactory.Context context, M model, float shadowRadius, CallbackInfo ci) {
        this.addFeature(new UnstableFeatureRenderer<>((LivingEntityRenderer<T, M>) (Object) this));
    }
}