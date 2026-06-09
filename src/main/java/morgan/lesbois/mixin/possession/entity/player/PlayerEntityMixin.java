package morgan.lesbois.mixin.possession.entity.player;

import morgan.lesbois.components.LesboisEntityComponents;
import morgan.lesbois.interfaces.PossessionInterface;
import morgan.lesbois.interfaces.PossessorInterface;
import morgan.lesbois.network.packet.PossessionS2CPacket;
import morgan.lesbois.network.packet.UnPossessionS2CPacket;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
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
    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    public boolean lesbois$isPossessing() {
        return LesboisEntityComponents.POSSESSION.get(this).isPossessing();
    }

    @Nullable
    public MobEntity lesbois$getPossessedEntity() {
        return LesboisEntityComponents.POSSESSION.get(this).getPossessedEntity();
    }

    public void lesbois$setPossessedEntity(@Nullable MobEntity entity) {
        LesboisEntityComponents.POSSESSION.get(this).setPossessedEntity(entity);
    }

    @Unique
    private static final Set<EntityType<?>> UNPOSSESSABLE_TYPES = Set.of(
            EntityType.ENDER_DRAGON,
            EntityType.WITHER,
            EntityType.WARDEN,
            EntityType.SHULKER
    );

    public boolean lesbois$canPossess(MobEntity entity) {
        PlayerEntity possessor = ((PossessorInterface) entity).lesbois$getPossessor();
        if (possessor != null && possessor != (PlayerEntity) (Object) this)
            return false;

        if (UNPOSSESSABLE_TYPES.contains(entity.getType()))
            return false;

        return true;
    }

    public boolean lesbois$possess(MobEntity entity) {
        if (!lesbois$canPossess(entity)) return false;

        if ( this.lesbois$getPossessedEntity() != null && this.lesbois$getPossessedEntity() != entity )
            this.lesbois$unPossess();

        this.lesbois$setPossessedEntity(entity);
        ((PossessorInterface) entity).lesbois$setPossessor((PlayerEntity) (Object) this);

        entity.setNoGravity(true);
        ((PossessorInterface) entity).lesbois$stopTargetSelectorGoals();
        Entity vehicle = entity.getVehicle();
        if (vehicle!=null) {
            entity.dismountVehicle();
            this.startRiding(vehicle);
        }

        this.setPos(entity.getPos().x, entity.getPos().y, entity.getPos().z);
        this.calculateDimensions();

        if ((PlayerEntity) (Object) this instanceof ServerPlayerEntity serverPlayerEntity) {
            ServerPlayNetworking.send(serverPlayerEntity, new PossessionS2CPacket(entity.getId()));
        }

        return true;
    }

    public void lesbois$unPossess() {
        MobEntity entity = this.lesbois$getPossessedEntity();

        if (entity != null) {
            entity.setNoGravity(false);

            ((PossessorInterface) entity).lesbois$setPossessor(null);
        }

        this.lesbois$setPossessedEntity(null);
        this.setHealth(this.getMaxHealth());
        this.calculateDimensions();

        if ((PlayerEntity) (Object) this instanceof ServerPlayerEntity serverPlayerEntity) {
            ServerPlayNetworking.send(serverPlayerEntity, new UnPossessionS2CPacket());
        }
    }



    @Inject(method = "tick", at = @At("TAIL"))
    public void tick(CallbackInfo ci) {
        MobEntity entity = this.lesbois$getPossessedEntity();

        if (entity == null || entity.isRemoved()) {
            return;
        }

        if (entity.isDead()) {
            this.lesbois$unPossess();
            return;
        }

        if (((PossessorInterface) entity).lesbois$getPossessor() != (PlayerEntity) (Object) this) {
            ((PossessorInterface) entity).lesbois$setPossessor((PlayerEntity) (Object) this);
        }

        Box entityBoundingBox = entity.getBoundingBox();
        Box playerBoundingBox = this.getBoundingBox();

        if ( entityBoundingBox.getLengthX() != playerBoundingBox.getLengthX() ||
             entityBoundingBox.getLengthY() != playerBoundingBox.getLengthY() ||
             entityBoundingBox.getLengthZ() != playerBoundingBox.getLengthZ() ) {

            this.calculateDimensions();
        }
    }

    @Inject(method = "applyDamage", at= @At("HEAD"), cancellable = true)
    public void redirectApplyDamage(DamageSource source, float amount, CallbackInfo ci) {
        MobEntity entity = this.lesbois$getPossessedEntity();

        if ( entity != null ) {
            ci.cancel();
        }
    }
}
