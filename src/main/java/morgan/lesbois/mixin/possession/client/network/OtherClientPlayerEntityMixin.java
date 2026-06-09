package morgan.lesbois.mixin.possession.client.network;

import morgan.lesbois.interfaces.PossessionInterface;
import net.minecraft.client.network.OtherClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(OtherClientPlayerEntity.class)
public class OtherClientPlayerEntityMixin {
    @Inject(
            method = "shouldRender(D)Z",
            at = @At("HEAD"),
            cancellable = true
    )
    private void shouldRenderDisablePossessionRender(double distance, CallbackInfoReturnable<Boolean> cir) {
        if (((PossessionInterface) this).lesbois$isPossessing()) {
            cir.setReturnValue(false);
        }
    }
}
