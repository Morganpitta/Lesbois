package morgan.lesbois.mixin.effects.entity.mob;

import morgan.lesbois.entity.effect.LesboisStatusEffects;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin extends LivingEntity {
    protected MobEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "mobTick", at = @At("HEAD"))
    private void preventAttack(CallbackInfo ci) {
        if (this.hasStatusEffect(LesboisStatusEffects.FALTERED)) {
            this.getBrain().remember(MemoryModuleType.ATTACK_COOLING_DOWN, true, 20L);
        }
    }
}
