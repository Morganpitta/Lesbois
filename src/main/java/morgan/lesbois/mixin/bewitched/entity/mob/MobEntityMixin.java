package morgan.lesbois.mixin.bewitched.entity.mob;

import morgan.lesbois.Lesbois;
import morgan.lesbois.interfaces.Bewitchable;
import morgan.lesbois.entity.ai.goal.BewitchedFollowOwnerGoal;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;
import java.util.UUID;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin extends LivingEntity implements Bewitchable {
    @Shadow
    @Final
    protected GoalSelector targetSelector;
    @Shadow
    @Final
    protected GoalSelector goalSelector;
    @Unique
    private static final TrackedData<Optional<UUID>> OWNER_UUID = DataTracker.registerData(MobEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);

    protected MobEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void addBewitchedAi(EntityType<? extends LivingEntity> type, World world, CallbackInfo ci) {
        this.goalSelector.add(1, new BewitchedFollowOwnerGoal((MobEntity) (Object) this, 2.0F, 7.0F, 5.0F));
    }

    @Nullable
    public UUID lesbois$getOwnerUuid() {
        return this.dataTracker.get(OWNER_UUID).orElse(null);
    }

    @Override
    public void lesbois$setOwnerUuid(@Nullable UUID uuid) {
        this.dataTracker.set(OWNER_UUID, Optional.ofNullable(uuid));
    }

    public void lesbois$setBewitched(@Nullable PlayerEntity owner) {
        this.lesbois$setOwnerUuid(owner != null ? owner.getUuid() : null);
    }

    @Inject(method = "initDataTracker", at=@At("TAIL"))
    public void initDataTracker(DataTracker.Builder builder, CallbackInfo ci) {
        builder.add(OWNER_UUID, Optional.empty());
    }

    @Inject(method = "writeCustomDataToNbt", at=@At("TAIL"))
    public void writeCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
        if (this.lesbois$getOwnerUuid() != null) {
            nbt.putUuid(Lesbois.stringId("owner"), this.lesbois$getOwnerUuid());
        }
    }

    @Inject(method = "readCustomDataFromNbt", at=@At("TAIL"))
    public void readCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
        UUID uuid = null;
        if (nbt.containsUuid(Lesbois.stringId("owner"))) {
            uuid = nbt.getUuid(Lesbois.stringId("owner"));
        }

        this.lesbois$setOwnerUuid(uuid);
    }
}
