package morgan.lesbois.mixin.common.client.gui.hud;

import morgan.lesbois.Lesbois;
import morgan.lesbois.powers.DisableHungerPowerType;
import morgan.lesbois.powers.SpeedometerPowerType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
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
        ClientPlayerEntity player = this.client.player;
        if (this.client.player != null && SpeedometerPowerType.shouldShow(player)) {
            float speed = (float) player.getVelocity().length();

            // No rounding options... grrr
            String string = String.format("%.1f", Math.floor(speed*10.f)/10.f);
            int x = (context.getScaledWindowWidth()) / 2 + 91 + 6 + 18 + 6;
            int y = context.getScaledWindowHeight() - 20 + 9 - this.getTextRenderer().fontHeight/2;

            int colour = Colors.WHITE;
            if (speed > 2.5)
                colour = Colors.RED;
            else if (speed > 1.5)
                colour = Colors.YELLOW;

            context.drawText(this.getTextRenderer(), string, x + 1, y, colour, false);
        }
    }
}
