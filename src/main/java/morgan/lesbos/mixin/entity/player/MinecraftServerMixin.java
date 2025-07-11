package morgan.lesbos.mixin.entity.player;


import morgan.lesbos.Lesbos;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.EnumMap;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
    @Inject(at = @At("HEAD"), method = "loadWorld")
    private void loadWorld(CallbackInfo info) {
        Lesbos.globalInventoryMain = DefaultedList.ofSize(36, ItemStack.EMPTY);
        Lesbos.globalInventoryMap = new EnumMap<EquipmentSlot, ItemStack>(EquipmentSlot.class);
    }
}


