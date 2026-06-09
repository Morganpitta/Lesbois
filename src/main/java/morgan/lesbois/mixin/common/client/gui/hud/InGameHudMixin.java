package morgan.lesbois.mixin.common.client.gui.hud;

import morgan.lesbois.Lesbois;
import morgan.lesbois.powers.DisableHungerPowerType;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Unique
    private static final Identifier FOOD_DISABLED_TEXTURE = Lesbois.id("hud/food_disabled");

    @Redirect(
            method = "renderFood",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lnet/minecraft/util/Identifier;IIII)V")
    )
    private void disableFood(DrawContext instance, Identifier texture, int x, int y, int width, int height, DrawContext context, PlayerEntity player, int top, int right) {
        if (DisableHungerPowerType.shouldDisableHunger(player)) {
            instance.drawGuiTexture(FOOD_DISABLED_TEXTURE, x, y, width, height);
        }
        else {
            instance.drawGuiTexture(texture, x, y, width, height);
        }
    }
}
