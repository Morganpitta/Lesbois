package morgan.lesbois.mixin.common.client.world;

import morgan.lesbois.entity.CoinEntity;
import morgan.lesbois.entity.effect.LesboisStatusEffects;
import morgan.lesbois.powers.ActionOnCoinPowerType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.WindowEventHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.thread.ReentrantThreadExecutor;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin extends ReentrantThreadExecutor<Runnable> implements WindowEventHandler {
    @Shadow
    @Nullable
    public ClientPlayerEntity player;

    public MinecraftClientMixin(String string) {
        super(string);
    }

    @Inject(method = "doAttack", at = @At("HEAD"), cancellable = true)
    private void cancelAttack(CallbackInfoReturnable<Boolean> cir) {
        if (this.player != null) {
            List<CoinEntity> coins = this.player.getWorld().getEntitiesByType(
                    TypeFilter.instanceOf(CoinEntity.class),
                    Box.from(this.player.getPos()).expand(64),
                    entity -> {
                        if(entity.getOwner() != this.player) return false;
                        Vec3d entityVec = entity.getPos().subtract(this.player.getPos()).normalize();
                        Vec3d rotationVec = this.player.getRotationVector().normalize();

                        return rotationVec.dotProduct(entityVec) >= Math.cos(Math.toRadians(5));
                    }
            );

            for (CoinEntity coin : coins) {
                ActionOnCoinPowerType.triggerCoinActions(this.player);
                coin.discard();
            }

            if (!coins.isEmpty()) {
                cir.setReturnValue(false);
                return;
            }

            if (this.player.hasStatusEffect(LesboisStatusEffects.FALTERED)) {
                cir.setReturnValue(false);
                return;
            }
        }
    }
}
