package morgan.lesbois.mixin.common.entity;

import morgan.lesbois.powers.DragModifierPowerType;
import net.minecraft.block.Block;
import net.minecraft.entity.*;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Redirect(method = "travel", at= @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;getSlipperiness()F"))
    public float travelIgnoreBlockFriction(Block instance) {
        boolean ignoreBlockFriction = DragModifierPowerType.shouldIgnoreBlockFriction((LivingEntity) (Object) this);

        if (ignoreBlockFriction)
            return 1;

        return instance.getSlipperiness();
    }

    @ModifyConstant(
            method = "travel",
            constant = @Constant(floatValue = 0.91F, ordinal = 0)
    )
    public float travelModifyFriction(float constant) {
        return DragModifierPowerType.getFriction((LivingEntity) (Object) this);
    }

    @ModifyConstant(
            method = "travel",
            constant = @Constant(floatValue = 0.91F, ordinal = 1)
    )
    public float travelModifyAirDrag(float constant) {
        return DragModifierPowerType.getAirDrag((LivingEntity) (Object) this);
    }
}
