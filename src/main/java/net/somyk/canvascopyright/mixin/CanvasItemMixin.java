package net.somyk.canvascopyright.mixin;

import eu.pb4.polydecorations.item.CanvasItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.somyk.canvascopyright.util.AuthorMethods;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(CanvasItem.class)
public class CanvasItemMixin {

    @Inject(method = "method_7851", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getOrDefault(Lnet/minecraft/component/ComponentType;Ljava/lang/Object;)Ljava/lang/Object;",
            ordinal = 1, shift = At.Shift.AFTER))
    public void addAuthor(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type, CallbackInfo ci){
        AuthorMethods.addToolTip(stack, tooltip);
    }

}
