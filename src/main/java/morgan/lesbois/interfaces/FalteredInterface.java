package morgan.lesbois.interfaces;

import morgan.lesbois.component.LesboisComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Unit;

public interface FalteredInterface {
    public default boolean lesbois$isFaltered(ItemStack stack) {
        return stack.contains(LesboisComponentTypes.FALTERED);
    }

    public default void lesbois$setFaltered(ItemStack stack, boolean faltered) {
        if (faltered) {
            stack.set(LesboisComponentTypes.FALTERED, Unit.INSTANCE);
        } else {
            stack.remove(LesboisComponentTypes.FALTERED);
        }
    }
}
