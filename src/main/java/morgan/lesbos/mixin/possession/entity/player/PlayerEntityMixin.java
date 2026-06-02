package morgan.lesbos.mixin.possession.entity.player;

import morgan.lesbos.components.LesbosComponents;
import morgan.lesbos.interfaces.PossessionInterface;
import morgan.lesbos.interfaces.PossessorInterface;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
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

import java.util.Set;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements PossessionInterface {
    @Unique
    public boolean wasPossessing;

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
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

    private static final Set<EntityType<?>> UNPOSSESSABLE_TYPES = Set.of(
            EntityType.ENDER_DRAGON,
            EntityType.WITHER,
            EntityType.WARDEN
    );

    public boolean lesbos$canPossess(MobEntity entity) {
        PlayerEntity possessor = ((PossessorInterface) entity).lesbos$getPossessor();
        if (possessor != null && possessor != (PlayerEntity) (Object) this)
            return false;

        if (UNPOSSESSABLE_TYPES.contains(entity.getType()))
            return false;

        return true;
    }

    public boolean lesbos$possess(MobEntity entity) {
        if (!lesbos$canPossess(entity)) return false;

        if ( this.lesbos$getPossessedEntity() != null && this.lesbos$getPossessedEntity() != entity )
            this.lesbos$unPossess();

        this.lesbos$setPossessedEntity(entity);
        ((PossessorInterface) entity).lesbos$setPossessor((PlayerEntity) (Object) this);

        entity.setNoGravity(true);
        ((PossessorInterface) entity).lesbos$stopTargetSelectorGoals();

        this.calculateDimensions();

        return true;
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
        else if ( !wasPossessing ) {
            this.lesbos$possess(entity);
            wasPossessing = true;
        }

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
