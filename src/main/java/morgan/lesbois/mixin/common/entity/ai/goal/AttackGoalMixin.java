package morgan.lesbois.mixin.common.entity.ai.goal;

import morgan.lesbois.entity.effect.LesboisStatusEffects;
import net.minecraft.entity.ai.goal.AttackGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AttackGoal.class)
public abstract class AttackGoalMixin extends Goal {
    @Shadow
    @Final
    private MobEntity mob;

    @Shadow
    private int cooldown;

    @Inject(method = "tick", at=@At("HEAD"))
    public void tick(CallbackInfo ci) {
        if (this.mob.hasStatusEffect(LesboisStatusEffects.FALTERED)) {
            this.cooldown = 20;
        }
    }
}
