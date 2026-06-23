package morgan.lesbois.mixin.common.entity;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.type.*;
import morgan.lesbois.entity.effect.LesboisStatusEffects;
import morgan.lesbois.powers.DragModifierPowerType;
import net.minecraft.block.Block;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    @Shadow
    private @Nullable LivingEntity attacker;

    @Shadow
    public abstract boolean addStatusEffect(StatusEffectInstance effect);

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Redirect(method = "travel", at= @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;getSlipperiness()F"))
    public float travelIgnoreBlockFriction(Block instance) {
        boolean ignoreBlockFriction = DragModifierPowerType.shouldIgnoreBlockFriction((LivingEntity) (Object) this);

        if (ignoreBlockFriction)
            return 1;

        return instance.getSlipperiness();
    }

    @ModifyConstant(
            method = "travel",
            constant = @Constant(floatValue = 0.91F, ordinal = 0)
    )
    public float travelModifyFriction(float constant) {
        return DragModifierPowerType.getFriction((LivingEntity) (Object) this);
    }

    @ModifyConstant(
            method = "travel",
            constant = @Constant(floatValue = 0.91F, ordinal = 1)
    )
    public float travelModifyAirDrag(float constant) {
        return DragModifierPowerType.getAirDrag((LivingEntity) (Object) this);
    }

    @Inject(method = "damage", at = @At("RETURN"), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;isSleeping()Z")))
    private void applyUnstableOnHit(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue()) {
            return;
        }

        Entity attacker = source.getAttacker();

        if (attacker instanceof LivingEntity livingEntity) {
            if (livingEntity.hasStatusEffect(LesboisStatusEffects.OVERCHARGED)) {
                StatusEffectInstance effect = livingEntity.getStatusEffect(LesboisStatusEffects.OVERCHARGED);

                this.addStatusEffect(new StatusEffectInstance(LesboisStatusEffects.UNSTABLE, 200, effect.getAmplifier()));
                livingEntity.removeStatusEffect(LesboisStatusEffects.OVERCHARGED);
                // Set duration
            }
        }
    }
}
