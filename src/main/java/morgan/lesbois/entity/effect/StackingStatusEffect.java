package morgan.lesbois.entity.effect;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.util.math.MathHelper;

public class StackingStatusEffect extends StatusEffect {
    private final int maxStack;
    protected StackingStatusEffect(StatusEffectCategory category, int color, int maxStack) {
        super(category, color);
        this.maxStack = MathHelper.clamp(maxStack, 0, 255);
    }

    public int getMaxStack() {
        return this.maxStack;
    }
}
