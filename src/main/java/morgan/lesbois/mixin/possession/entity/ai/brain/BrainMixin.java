package morgan.lesbois.mixin.possession.entity.ai.brain;

import morgan.lesbois.interfaces.PossessorInterface;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(Brain.class)
public abstract class BrainMixin<E extends LivingEntity> {
    @Shadow
    public abstract void stopAllTasks(ServerWorld world, E entity);

    @Shadow
    @Final
    private Set<Activity> possibleActivities;

    @Inject(method = "tick", at=@At("HEAD"), cancellable = true)
    public void tick(ServerWorld world, E entity, CallbackInfo ci) {
        if (entity instanceof MobEntity mobEntity) {
            PlayerEntity player = ((PossessorInterface) mobEntity).lesbois$getPossessor();

            if (player != null) {
                this.stopAllTasks(world, entity);
                mobEntity.getNavigation().stop();

                this.possibleActivities.clear();

                ci.cancel();
            }
        }
    }
}
