package morgan.lesbos.mixin.possession.entity;

import morgan.lesbos.interfaces.PossessionInterface;
import morgan.lesbos.interfaces.PossessorInterface;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow
    public abstract World getWorld();

    @Inject(method = "isConnectedThroughVehicle", at = @At("HEAD"), cancellable = true)
    public void isConnectedThroughVehiclePossession(Entity other, CallbackInfoReturnable<Boolean> cir) {
        if ( (Object) this instanceof PlayerEntity playerEntity ) {
            if (((PossessionInterface) playerEntity).lesbos$getPossessedEntity() == other)
                cir.setReturnValue(true);
        }
        else if ( (Object) this instanceof MobEntity mobEntity ) {
            if (((PossessorInterface) mobEntity).lesbos$getPossessor() == other)
                cir.setReturnValue(true);
        }
    }

    @Inject(method = "getEyeHeight", at = @At("HEAD"), cancellable = true)
    private void redirectEyeHeight(EntityPose pose, CallbackInfoReturnable<Float> cir) {
        if ((Entity) (Object) this instanceof PlayerEntity player) {
            PossessionInterface possession = (PossessionInterface) player;

            if (possession.lesbos$isPossessing()) {
                MobEntity puppet = possession.lesbos$getPossessedEntity();

                if (puppet != null) {
                    cir.setReturnValue(puppet.getEyeHeight(puppet.getPose()));
                }
            }
        }
    }

    @Inject(
            method = "shouldRender(D)Z",
            at = @At("HEAD"),
            cancellable = true
    )
    private void shouldRenderDisablePossessionRender(double distance, CallbackInfoReturnable<Boolean> cir) {
        if ((Entity) (Object) this instanceof PlayerEntity player) {
            if (((PossessionInterface) player).lesbos$isPossessing()) {
                cir.setReturnValue(false);
            }
        }
    }
}
