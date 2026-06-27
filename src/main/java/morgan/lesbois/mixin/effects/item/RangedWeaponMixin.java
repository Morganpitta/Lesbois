package morgan.lesbois.mixin.effects.item;

import morgan.lesbois.entity.effect.LesboisStatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({BowItem.class, CrossbowItem.class})
public abstract class RangedWeaponMixin {
    @Inject(method = "use", at=@At("HEAD"), cancellable = true)
    public void preventUse(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
        ItemStack itemStack = user.getStackInHand(hand);

        if (user.hasStatusEffect(LesboisStatusEffects.FALTERED)) {
            cir.setReturnValue(TypedActionResult.fail(itemStack));
        }
    }
}
