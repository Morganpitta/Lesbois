package morgan.lesbois.mixin.possession.entity.mob;

import morgan.lesbois.interfaces.PossessionInterface;
import morgan.lesbois.interfaces.PossessorInterface;
import morgan.lesbois.mixin.common.entity.mob.MobEntityAccessor;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.ai.goal.PrioritizedGoal;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin extends LivingEntity implements PossessorInterface {
    @Unique
    @Nullable
    private PlayerEntity possessor;

    @Final
    @Shadow
    protected GoalSelector targetSelector;

    @Shadow
    public abstract EntityNavigation getNavigation();

    @Shadow
    private ItemStack bodyArmor;

    @Shadow
    @Final
    protected GoalSelector goalSelector;

    @Shadow
    public abstract void setTarget(@Nullable LivingEntity target);

    @Unique
    private static final TrackedData<Integer> POSSESSOR_ID = DataTracker.registerData(
            MobEntity.class, TrackedDataHandlerRegistry.INTEGER
    );

    protected MobEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Nullable
    public PlayerEntity lesbois$getPossessor() {
        return this.possessor;
    }

    public void lesbois$setPossessor(@Nullable PlayerEntity player) {
        this.possessor = player;
        this.dataTracker.set(POSSESSOR_ID, player != null ? player.getId() : -1);
    }

    @Inject(method = "initDataTracker", at = @At("TAIL"))
    private void initDataTrackerAddPossessor(DataTracker.Builder builder, CallbackInfo ci) {
        builder.add(POSSESSOR_ID, -1);
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        if (POSSESSOR_ID.equals(data)) {
            int id = this.dataTracker.get(POSSESSOR_ID);
            this.possessor = id != -1 ? (PlayerEntity) this.getWorld().getEntityById(id) : null;
            if ( this.possessor != null && ((PossessionInterface) this.possessor).lesbois$getPossessedEntity() != (MobEntity) (Object) this) {
                this.possessor = null;
            }
        }
        super.onTrackedDataSet(data);
    }

    public void lesbois$stopTargetSelectorGoals() {
        GoalSelector targetSelector = this.targetSelector;

        for (PrioritizedGoal goal : targetSelector.getGoals()) {
            if (goal.isRunning()) {
                goal.stop();
            }
        }

        for (PrioritizedGoal goal : this.goalSelector.getGoals()) {
            if (goal.isRunning()) {
                goal.stop();
            }
        }

        this.setTarget(null);
        this.getBrain().forget(MemoryModuleType.ATTACK_TARGET);
        this.getBrain().forget(MemoryModuleType.ANGRY_AT);
        this.getBrain().forget(MemoryModuleType.UNIVERSAL_ANGER);

        if (this.getNavigation() != null) {
            this.getNavigation().stop();
        }
    }

    @Inject(
            method = "setTarget",
            at = @At("HEAD"),
            cancellable = true
    )
    private void possessedCancelSetTarget(LivingEntity target, CallbackInfo ci) {
        if (this.lesbois$getPossessor() != null && target != null) {
            ci.cancel();
        }
    }

    @Unique
    private GoalSelector emptyGoalSelector = null;

    @Redirect(
            method = "tickNewAi",
            at = @At(value = "FIELD", target = "Lnet/minecraft/entity/mob/MobEntity;targetSelector:Lnet/minecraft/entity/ai/goal/GoalSelector;", opcode = Opcodes.GETFIELD)
    )
    private GoalSelector redirectTargetSelector(MobEntity instance) {
        if (this.lesbois$getPossessor() != null) {
            if (this.emptyGoalSelector == null) {
                this.emptyGoalSelector = new GoalSelector(instance.getWorld().getProfilerSupplier());
            }

            return this.emptyGoalSelector;
        }

        // Target selector is protected and MobEntityMixin isn't a subclass and thus cant access, so I had to do this :(
        return ((MobEntityAccessor) instance).lesbois$getTargetSelector();
    }



    @Redirect(
            method = "tickNewAi",
            at = @At(value = "FIELD", target = "Lnet/minecraft/entity/mob/MobEntity;goalSelector:Lnet/minecraft/entity/ai/goal/GoalSelector;", opcode = Opcodes.GETFIELD)
    )
    private GoalSelector redirectGoalSelector(MobEntity instance) {
        // Dolphins have a goal that can spit out items... Man screw dolphins

        if (this.lesbois$getPossessor() != null) {
            GoalSelector goalSelector = new GoalSelector(instance.getWorld().getProfilerSupplier());

            for (PrioritizedGoal prioritizedGoal : ((MobEntityAccessor) instance).lesbois$getGoalSelector().getGoals()) {
                if (!prioritizedGoal.getGoal().getClass().getSimpleName().equals("PlayWithItemsGoal")) {
                    goalSelector.add(prioritizedGoal.getPriority(), prioritizedGoal);
                }
            }

            return goalSelector;
        }

        return ((MobEntityAccessor) instance).lesbois$getGoalSelector();
    }

    @Inject(method = "getEquippedStack", at=@At("HEAD"), cancellable = true)
    public void redirectEquippedStack(EquipmentSlot slot, CallbackInfoReturnable<ItemStack> cir) {
        if (this.dead) return;
        PlayerEntity player = this.lesbois$getPossessor();
        if (player != null) {
            if (slot.getType() == EquipmentSlot.Type.ANIMAL_ARMOR) {
                cir.setReturnValue(this.bodyArmor);
            }
            else {
                cir.setReturnValue(player.getEquippedStack(slot));
            }
        }
    }
}
