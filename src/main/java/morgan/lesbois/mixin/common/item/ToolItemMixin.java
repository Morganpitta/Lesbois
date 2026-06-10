package morgan.lesbois.mixin.common.item;

import morgan.lesbois.interfaces.FalteredInterface;
import morgan.lesbois.interfaces.ParryInterface;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ToolItem.class)
public abstract class ToolItemMixin extends Item implements FalteredInterface {
    public ToolItemMixin(Settings settings) {
        super(settings);
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BLOCK;
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return 20;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (((ParryInterface)user).lesbois$canParry()) {
            ItemStack itemStack = user.getStackInHand(hand);

            if ( itemStack.getItem() instanceof SwordItem || itemStack.getItem() instanceof AxeItem) {
                user.setCurrentHand(hand);

                return TypedActionResult.consume(itemStack);
            }
        }

        return TypedActionResult.pass(user.getStackInHand(hand));
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (!world.isClient() && user instanceof PlayerEntity player) {
            player.getItemCooldownManager().set(this, 40);
            this.lesbois$setFaltered(stack, true);

            world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ITEM_SHIELD_BREAK, SoundCategory.PLAYERS, 0.8F, 0.8F);
        }
        return stack;
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (!world.isClient() && user instanceof PlayerEntity player) {
            player.getItemCooldownManager().set(this, 20);
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (entity instanceof PlayerEntity player) {
            if (this.lesbois$isFaltered(stack) && !player.getItemCooldownManager().isCoolingDown(this)) {
                this.lesbois$setFaltered(stack, false);
            }
        }
    }
}
