package morgan.lesbos.mixin.entity.player;

import morgan.lesbos.Lesbos;
import morgan.lesbos.components.LesbosComponents;
import morgan.lesbos.interfaces.PossessionInterface;
import morgan.lesbos.interfaces.PossessorInterface;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityPossessionMixin extends LivingEntity implements PossessionInterface {
    @Unique
    public boolean wasPossessing;

    protected PlayerEntityPossessionMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }



    public boolean lesbos$isPossessing() {
        return LesbosComponents.POSSESSION.get(this).isPossessing();
    }

    @Nullable
    public MobEntity lesbos$getPossessedEntity() {
        return LesbosComponents.POSSESSION.get(this).getPossessedEntity();
    }

    public void lesbos$setPossessedEntity(@Nullable MobEntity entity) {
        LesbosComponents.POSSESSION.get(this).setPossessedEntity(entity);
    }



    public void lesbos$possess(MobEntity entity) {
        if (((PossessorInterface) entity).lesbos$getPossessor() != null) return;

        if ( this.lesbos$getPossessedEntity() != null )
            this.lesbos$unPossess();

        this.lesbos$setPossessedEntity(entity);
        ((PossessorInterface) entity).lesbos$setPossessor((PlayerEntity) (Object) this);

        entity.setNoGravity(true);
        ((PossessorInterface) entity).lesbos$stopTargetSelectorGoals();

        this.calculateDimensions();
    }

    public void lesbos$unPossess() {
        MobEntity entity = this.lesbos$getPossessedEntity();

        if (entity != null) {
            entity.setNoGravity(false);

            ((PossessorInterface) entity).lesbos$setPossessor(null);
        }

        this.lesbos$setPossessedEntity(null);

        this.calculateDimensions();
    }



    @Inject(method = "tick", at = @At("TAIL"))
    public void tick(CallbackInfo ci) {
        MobEntity entity = this.lesbos$getPossessedEntity();

        if (entity == null) {
            if ( wasPossessing ) {
                this.lesbos$unPossess();
                wasPossessing = false;
            }
            return;
        }

        wasPossessing = true;

        if (entity.isDead() || entity.isRemoved()) {
            this.lesbos$unPossess();
            return;
        }

        if (((PossessorInterface) entity).lesbos$getPossessor() != (PlayerEntity) (Object) this) {
            ((PossessorInterface) entity).lesbos$setPossessor((PlayerEntity) (Object) this);
        }

        Box entityBoundingBox = entity.getBoundingBox();
        Box playerBoundingBox = this.getBoundingBox();

        if ( entityBoundingBox.getLengthX() != playerBoundingBox.getLengthX() ||
             entityBoundingBox.getLengthY() != playerBoundingBox.getLengthY() ||
             entityBoundingBox.getLengthZ() != playerBoundingBox.getLengthZ() ) {

            this.calculateDimensions();
        }
    }
    // Health, hasStatusEffect, attributes
}
