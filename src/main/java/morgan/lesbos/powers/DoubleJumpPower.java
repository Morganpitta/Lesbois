package morgan.lesbos.powers;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import net.minecraft.entity.LivingEntity;

public class DoubleJumpPower extends Power {
    private final int doubleJumps;

    public DoubleJumpPower(PowerType<?> type, LivingEntity entity, int doubleJumps) {
        super(type, entity);
        this.doubleJumps = doubleJumps;
    }

    public int getDoubleJumps() {
        return doubleJumps;
    }

    public static boolean canDoubleJump(LivingEntity entity)
    {
        for (DoubleJumpPower doubleJumpPower : PowerHolderComponent.getPowers(entity, DoubleJumpPower.class)) {
            return true;
        }
        return false;
    }

    public static int getMaxDoubleJumps(LivingEntity entity)
    {
        int maxDoubleJumps = 0;
        for (DoubleJumpPower doubleJumpPower : PowerHolderComponent.getPowers(entity, DoubleJumpPower.class)) {
            maxDoubleJumps = Math.max(maxDoubleJumps,doubleJumpPower.getDoubleJumps());
        }
        return maxDoubleJumps;
    }
}
