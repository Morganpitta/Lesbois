package morgan.lesbois.item;

import morgan.lesbois.Lesbois;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

public class LesboisItems {
    public static final Item COIN = register("coin", new Item(new Item.Settings()));

    public static void register() {
    }

    public static Item register(String path, Item item) {
        return Registry.register(Registries.ITEM, RegistryKey.of(Registries.ITEM.getKey(), Lesbois.id(path)), item);
    }
}
