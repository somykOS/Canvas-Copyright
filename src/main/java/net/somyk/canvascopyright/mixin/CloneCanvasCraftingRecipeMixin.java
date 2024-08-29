package net.somyk.canvascopyright.mixin;

import eu.pb4.polydecorations.recipe.CloneCanvasCraftingRecipe;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

import static net.somyk.canvascopyright.util.AuthorMethods.AUTHORS_KEY;

@Mixin(CloneCanvasCraftingRecipe.class)
public class CloneCanvasCraftingRecipeMixin {

    @Shadow @Final private Item input;

    @Inject(method = "craft(Lnet/minecraft/recipe/input/CraftingRecipeInput;Lnet/minecraft/registry/RegistryWrapper$WrapperLookup;)Lnet/minecraft/item/ItemStack;", at = @At("RETURN"), cancellable = true)
    private void craft(CraftingRecipeInput inventory, RegistryWrapper.WrapperLookup wrapperLookup, CallbackInfoReturnable<ItemStack> cir) {
        NbtCompound tag;
        for (var stack : inventory.getStacks()) {
            if (stack.isOf(this.input)) {
                if (stack.contains(DataComponentTypes.CUSTOM_DATA)) {
                    tag = Objects.requireNonNull(stack.get(DataComponentTypes.CUSTOM_DATA)).copyNbt();
                    if(tag.contains(AUTHORS_KEY)){
                        ItemStack returnValue = cir.getReturnValue();
                        returnValue.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(tag));
                        cir.setReturnValue(returnValue);
                    }
                }
            }
        }
    }
}
