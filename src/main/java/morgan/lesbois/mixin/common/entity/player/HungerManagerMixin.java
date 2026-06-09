package morgan.lesbois.mixin.common.entity.player;

import morgan.lesbois.powers.DisableHungerPowerType;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HungerManager.class)
public class HungerManagerMixin {
    @Shadow
    private int foodLevel;

    @Shadow
    private int prevFoodLevel;

    @Shadow
    private float exhaustion;

    @Shadow
    private float saturationLevel;

    @Inject(method = "update", at = @At("HEAD"), cancellable = true)
    public void disableHunger(PlayerEntity player, CallbackInfo ci) {
        if (DisableHungerPowerType.shouldDisableHunger(player)) {
            this.foodLevel = 20;
            this.prevFoodLevel = 20;
            this.exhaustion = 0;
            this.saturationLevel = 0;
        }
    }
}