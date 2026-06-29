package morgan.lesbois.mixin.bewitched.entity;

import morgan.lesbois.interfaces.Bewitchable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "canTarget(Lnet/minecraft/entity/LivingEntity;)Z", at=@At("HEAD"), cancellable = true)
    public void preventOwnerTargeting(LivingEntity target, CallbackInfoReturnable<Boolean> cir) {
        if ((LivingEntity) (Object) this instanceof Bewitchable bewitchable) {
            if (bewitchable.lesbois$isBewitched() && bewitchable.lesbois$getOwner() == target) {
                cir.setReturnValue(false);
            }
        }
    }
}
