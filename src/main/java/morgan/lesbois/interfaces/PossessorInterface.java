package morgan.lesbois.interfaces;

import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.Nullable;

public interface PossessorInterface {
    public void lesbois$setPossessor(@Nullable PlayerEntity player);
    public @Nullable PlayerEntity lesbois$getPossessor();

    public void lesbois$stopTargetSelectorGoals();
}
