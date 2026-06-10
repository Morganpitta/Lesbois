package morgan.lesbois.mixin.common.client.world;

import morgan.lesbois.interfaces.FalteredInterface;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.WindowEventHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolItem;
import net.minecraft.util.thread.ReentrantThreadExecutor;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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
            ItemStack heldItem = this.player.getMainHandStack();

            if (heldItem.getItem() instanceof ToolItem tool && ((FalteredInterface) tool).lesbois$isFaltered(heldItem)) {
                cir.setReturnValue(false);
            }
        }
    }
}
