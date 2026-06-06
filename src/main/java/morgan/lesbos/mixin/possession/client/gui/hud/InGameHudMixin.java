package morgan.lesbos.mixin.possession.client.gui.hud;

import morgan.lesbos.interfaces.PossessionInterface;
import morgan.lesbos.interfaces.PossessorInterface;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.registry.tag.TagKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(InGameHud.class)
public class InGameHudMixin {

    @Redirect(
            method = "renderStatusBars",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/PlayerEntity;isSubmergedIn(Lnet/minecraft/registry/tag/TagKey;)Z"
            )
    )
    private boolean redirectIsSubmergedIn(PlayerEntity player, TagKey<Fluid> fluidTag) {
        MobEntity entity = ((PossessionInterface) player).lesbos$getPossessedEntity();

        if ( entity != null ) {
            if (entity.canBreatheInWater()) {
                return false;
            }
        }

        return player.isSubmergedIn(fluidTag);
    }
}