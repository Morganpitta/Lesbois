package morgan.lesbos.interfaces;

import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.Nullable;

public interface PossessorInterface {
    public void lesbos$setPossessor(@Nullable PlayerEntity player);
    public @Nullable PlayerEntity lesbos$getPossessor();

    public void lesbos$stopTargetSelectorGoals();
}
