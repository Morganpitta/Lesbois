package morgan.lesbos.mixin.possession.client.network;

import morgan.lesbos.interfaces.PossessionInterface;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
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
        if ((Entity) (Object) this instanceof PlayerEntity player) {
            MobEntity entity = ((PossessionInterface) player).lesbos$getPossessedEntity();

            if (entity != null) {
                cir.setReturnValue(false);
            }
        }
    }
}
