package net.somyk.canvascopyright.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import eu.pb4.polydecorations.item.CanvasItem;
import eu.pb4.polydecorations.item.DecorationsItems;
import net.minecraft.block.CrafterBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import static net.somyk.canvascopyright.util.AuthorMethods.PUBLIC_KEY;
import static net.somyk.canvascopyright.util.AuthorMethods.getCustomData;

@Mixin(CrafterBlock.class)
public class CrafterBlockMixin {

//    @WrapOperation(method = "craft", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isEmpty()Z"))
//    private boolean craft(ItemStack instance, Operation<Boolean> original){
//        NbtCompound tag = getCustomData(instance);
//        return original.call(instance) || (instance.isOf(DecorationsItems.CANVAS) && (instance.getOrDefault(CanvasItem.DATA_TYPE, CanvasItem.Data.DEFAULT).image().isPresent() || tag.contains(PUBLIC_KEY)));
//    }

    @WrapOperation(method = "craft", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isEmpty()Z"))
    private boolean craft(ItemStack instance, Operation<Boolean> original){
        if (original.call(instance)) {
            return true;
        }

        if (instance.isOf(DecorationsItems.CANVAS)) {
            NbtCompound tag = getCustomData(instance);
            if(instance.getOrDefault(CanvasItem.DATA_TYPE, CanvasItem.Data.DEFAULT).image().isPresent()) return !tag.contains(PUBLIC_KEY);
        }

        return false;
    }
}
