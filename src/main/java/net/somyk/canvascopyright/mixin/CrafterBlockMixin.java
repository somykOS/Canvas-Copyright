package net.somyk.canvascopyright.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import eu.pb4.polydecorations.item.CanvasItem;
import eu.pb4.polydecorations.item.DecorationsItems;
import net.minecraft.block.CrafterBlock;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import static net.somyk.canvascopyright.util.AuthorMethods.*;

@Mixin(CrafterBlock.class)
public class CrafterBlockMixin {

    @WrapOperation(method = "craft", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isEmpty()Z"))
    private boolean cancelCraftIfNotPublic(ItemStack instance, Operation<Boolean> original){
        if (original.call(instance)) {
            return true;
        }

        if (instance.isOf(DecorationsItems.CANVAS)) {
            if(instance.getOrDefault(CanvasItem.DATA_TYPE, CanvasItem.Data.DEFAULT).image().isPresent()) return !isPublic(instance);
        }

        return false;
    }
}
