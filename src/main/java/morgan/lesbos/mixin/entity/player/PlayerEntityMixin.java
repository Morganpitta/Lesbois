package morgan.lesbos.mixin.entity.player;

import morgan.lesbos.Lesbos;
import morgan.lesbos.entity.GrappleHookEntity;
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
    public GrappleHookEntity lesbos$grapple(double maxDistance) {
        if (this.grappleHook != null) {
            this.grappleHook.discard();
            this.grappleHook = null;
        }

        RaycastContext raycastContext = new RaycastContext(
            this.getEyePos(),
            this.getEyePos().add(this.getRotationVec(1.0F).multiply(maxDistance)),
            RaycastContext.ShapeType.OUTLINE,
            RaycastContext.FluidHandling.NONE,
            this
        );

        BlockHitResult hit = this.getWorld().raycast(raycastContext);

        if (hit.getType() == HitResult.Type.MISS) return null;

        GrappleHookEntity hook = new GrappleHookEntity(this.getWorld(), (PlayerEntity) (Object) this, hit.getPos());
        this.getWorld().spawnEntity(hook);
        this.grappleHook = hook;

        return hook;
    }
}
