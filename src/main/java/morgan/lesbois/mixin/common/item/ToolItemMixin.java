package morgan.lesbois.mixin.common.item;

import morgan.lesbois.component.LesboisComponentTypes;
import morgan.lesbois.interfaces.FalteredInterface;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.util.Unit;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ToolItem.class)
public abstract class ToolItemMixin extends Item implements FalteredInterface {
    public ToolItemMixin(Settings settings) {
        super(settings);
    }
}
