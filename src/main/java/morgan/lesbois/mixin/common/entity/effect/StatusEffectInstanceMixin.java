package morgan.lesbois.mixin.common.entity.effect;

import morgan.lesbois.entity.effect.StackingStatusEffect;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.entry.RegistryEntry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(StatusEffectInstance.class)
public abstract class StatusEffectInstanceMixin implements Comparable<StatusEffectInstance> {
    @Shadow
    public abstract RegistryEntry<StatusEffect> getEffectType();

    @Shadow
    private int amplifier;

    @Shadow
    private int duration;

    @Shadow
    private boolean ambient;

    @Shadow
    private boolean showParticles;

    @Shadow
    private boolean showIcon;

    @Inject(method = "upgrade", at = @At("HEAD"), cancellable = true)
    public void upgrade(StatusEffectInstance that, CallbackInfoReturnable<Boolean> cir) {
        if (this.getEffectType().equals(that.getEffectType()) &&
            this.getEffectType().value() instanceof StackingStatusEffect stackingStatusEffect) {
            boolean changed = false;
            if (that.getAmplifier() >= 0) {
                this.amplifier = Math.min(this.amplifier+that.getAmplifier()+1, stackingStatusEffect.getMaxStack());
                changed = true;
            }

            if (that.getDuration() > this.duration) {
                this.duration = that.getDuration();
                changed = true;
            }

            if (!that.isAmbient() && this.ambient || changed) {
                this.ambient = that.isAmbient();
                changed = true;
            }

            if (that.shouldShowParticles() != this.showParticles) {
                this.showParticles = that.shouldShowParticles();
                changed = true;
            }

            if (that.shouldShowIcon() != this.showIcon) {
                this.showIcon = that.shouldShowIcon();
                changed = true;
            }

            cir.setReturnValue(changed);
        }
    }
}