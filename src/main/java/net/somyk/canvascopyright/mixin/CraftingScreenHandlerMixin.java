package net.somyk.canvascopyright.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import eu.pb4.polydecorations.item.CanvasItem;
import eu.pb4.polydecorations.item.DecorationsItems;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.world.World;
import net.somyk.canvascopyright.util.AuthorMethods;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(CraftingScreenHandler.class)
public class CraftingScreenHandlerMixin {

    // Checking if a player can copy a map
    @ModifyExpressionValue(method = "updateResult", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/recipe/CraftingRecipe;craft(Lnet/minecraft/recipe/input/RecipeInput;Lnet/minecraft/registry/RegistryWrapper$WrapperLookup;)Lnet/minecraft/item/ItemStack;"))
    private static ItemStack playerCanCopyCheck(ItemStack original, ScreenHandler handler, World world, PlayerEntity player, RecipeInputInventory craftingInventory, CraftingResultInventory resultInventory, @Nullable RecipeEntry<CraftingRecipe> recipe){
        if(original.isOf(DecorationsItems.CANVAS) && original.contains(CanvasItem.DATA_TYPE)) {
            if(!AuthorMethods.canCopy(original, player)) return ItemStack.EMPTY;
        }
        return original;
    }
}
