package morgan.lesbos.mixin.grapple.entity.player;

import morgan.lesbos.entity.GrappleHookEntity;
import morgan.lesbos.interfaces.DoubleJumpInterface;
import morgan.lesbos.interfaces.GrappleInterface;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements GrappleInterface {
    @Unique
    @Nullable
    GrappleHookEntity grappleHook;

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Unique
    public GrappleHookEntity lesbos$getGrappleHook() {
        return grappleHook;
    }

    @Unique
    public void lesbos$setGrappleHook(@Nullable GrappleHookEntity hook) {
        this.grappleHook = hook;
    }


    @Unique
    @Nullable
    public GrappleHookEntity lesbos$grapple(double maxDistance, double minDistance, double pullSpeed, double lookAssist, double damping) {
        if (this.grappleHook != null) {
            this.grappleHook.discard();
            this.grappleHook = null;
        }

        RaycastContext raycastContext = new RaycastContext(
                this.getEyePos(),
                this.getEyePos().add(this.getRotationVec(1.0F).multiply(maxDistance)),
                RaycastContext.ShapeType.COLLIDER,
                RaycastContext.FluidHandling.NONE,
                this
        );

        BlockHitResult hit = this.getWorld().raycast(raycastContext);

        if (hit.getType() == HitResult.Type.MISS) return null;

        GrappleHookEntity hook = new GrappleHookEntity(this.getWorld(), (PlayerEntity) (Object) this, hit.getPos(), minDistance, pullSpeed, lookAssist, damping);
        this.getWorld().spawnEntity(hook);
        this.grappleHook = hook;

        DoubleJumpInterface doubleJumps = (DoubleJumpInterface) (Object) this;
        doubleJumps.lesbos$setDoubleJumps(doubleJumps.lesbos$getMaxDoubleJumps());

        return hook;
    }

    @Unique
    public boolean lesbos$unGrapple() {
        if (this.grappleHook == null) return false;

        this.grappleHook.discard();
        this.grappleHook = null;
        return true;
    }

    @Redirect(
            method = "getBlockBreakingSpeed",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/PlayerEntity;isOnGround()Z"
            )
    )
    public boolean getBlockBreakingSpeedIncreaseMiningSpeedWhenGrappled(PlayerEntity player) {
        return player.isOnGround() || ((GrappleInterface) player).lesbos$getGrappleHook() != null;
    }
}