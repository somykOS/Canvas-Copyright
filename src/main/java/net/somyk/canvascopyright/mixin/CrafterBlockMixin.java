package net.somyk.canvascopyright.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import eu.pb4.polydecorations.item.CanvasItem;
import eu.pb4.polydecorations.item.DecorationsItems;
import net.minecraft.block.CrafterBlock;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(CrafterBlock.class)
public class CrafterBlockMixin {

    @WrapOperation(method = "craft", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isEmpty()Z"))
    private boolean craft(ItemStack instance, Operation<Boolean> original){
        return original.call(instance) || (instance.isOf(DecorationsItems.CANVAS) && instance.getOrDefault(CanvasItem.DATA_TYPE, CanvasItem.Data.DEFAULT).image().isPresent());
    }
}
