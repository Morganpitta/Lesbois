package morgan.lesbois.mixin.double_jump.client.network;

import com.mojang.authlib.GameProfile;
import morgan.lesbois.interfaces.DoubleJumpInterface;
import morgan.lesbois.network.packet.DoubleJumpC2SPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends AbstractClientPlayerEntity {
    @Shadow
    public Input input;
    @Unique
    private boolean wasJumping = false;

    public ClientPlayerEntityMixin(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    @Inject(
            method = "tickMovement",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;tickMovement()V", shift = At.Shift.BEFORE)
    )
    public void tickMovementDoubleJump(CallbackInfo ci) {
        if (this.input.jumping && !wasJumping) {
            if (((DoubleJumpInterface) this).lesbois$canDoubleJump() && !(this.isInLava() || this.isTouchingWater())) {
                ((DoubleJumpInterface) this).lesbois$doubleJump();
                ClientPlayNetworking.send(new DoubleJumpC2SPacket());
            }
        }

        wasJumping = this.input.jumping;
    }
}
