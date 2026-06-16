package morgan.lesbois.mixin.grapple.entity.player;

import morgan.lesbois.entity.GrappleHookEntity;
import morgan.lesbois.interfaces.DoubleJumpInterface;
import morgan.lesbois.interfaces.GrappleInterface;
import net.fabricmc.loader.impl.lib.sat4j.core.Vec;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
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
    public GrappleHookEntity lesbois$getGrappleHook() {
        return grappleHook;
    }

    @Unique
    public void lesbois$setGrappleHook(@Nullable GrappleHookEntity hook) {
        this.grappleHook = hook;
    }


    @Unique
    @Nullable
    public GrappleHookEntity lesbois$grapple(float maxDistance, float minDistance, boolean disableFallDamage, float pullSpeed, float lookAssist, float damping) {
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

        Vec3d offset = new Vec3d(hit.getSide().getUnitVector()).multiply(0.1);

        GrappleHookEntity hook = new GrappleHookEntity(this.getWorld(), (PlayerEntity) (Object) this, hit.getPos().add(offset), this.getYaw(), this.getPitch(), disableFallDamage, minDistance, pullSpeed, lookAssist, damping);
        this.getWorld().spawnEntity(hook);
        this.grappleHook = hook;

        DoubleJumpInterface doubleJumps = (DoubleJumpInterface) (Object) this;
        doubleJumps.lesbois$setDoubleJumps(doubleJumps.lesbois$getMaxDoubleJumps());

        return hook;
    }

    @Unique
    public boolean lesbois$unGrapple() {
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
        return player.isOnGround() || ((GrappleInterface) player).lesbois$getGrappleHook() != null;
    }
}