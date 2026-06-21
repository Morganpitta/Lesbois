package morgan.lesbois.mixin.common.client.gui.hud;

import morgan.lesbois.Lesbois;
import morgan.lesbois.powers.DisableHungerPowerType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {
    @Shadow
    public abstract TextRenderer getTextRenderer();

    @Shadow
    @Final
    private MinecraftClient client;
    @Unique
    private static final Identifier FOOD_DISABLED_TEXTURE = Lesbois.id("hud/food_disabled");

    @Redirect(
            method = "renderFood",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lnet/minecraft/util/Identifier;IIII)V")
    )
    private void disableFood(DrawContext instance, Identifier texture, int x, int y, int width, int height, DrawContext context, PlayerEntity player, int top, int right) {
        if (DisableHungerPowerType.shouldDisableHunger(player)) {
            instance.drawGuiTexture(FOOD_DISABLED_TEXTURE, x, y, width, height);
        } else {
            instance.drawGuiTexture(texture, x, y, width, height);
        }
    }

    @Inject(method = "renderMainHud", at = @At("TAIL"))
    private void renderMainHud(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        float speed = 0;
        if (this.client.player != null) {
            speed = (float) this.client.player.getVelocity().length();
        }
        String string = "" + speed;
        int x = (context.getScaledWindowWidth() - this.getTextRenderer().getWidth(string)) / 2;
        int y = context.getScaledWindowHeight() / 2 - 4;
        context.drawText(this.getTextRenderer(), string, x + 1, y, 0, false);
    }
}
