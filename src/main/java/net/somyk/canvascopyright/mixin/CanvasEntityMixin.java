package net.somyk.canvascopyright.mixin;

import eu.pb4.polydecorations.entity.CanvasEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ClickType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.somyk.canvascopyright.util.AuthorMethods.AUTHORS_KEY;
import static net.somyk.canvascopyright.util.AuthorMethods.PUBLIC_KEY;

@Mixin(CanvasEntity.class)
public class CanvasEntityMixin {

	@Unique	private NbtList authors = new NbtList();
	@Unique	private String open = "";

	@Inject(method = "onUsed", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;swingHand(Lnet/minecraft/util/Hand;Z)V", shift = At.Shift.AFTER))
	private void onUsed(ServerPlayerEntity serverPlayerEntity, ClickType clickType, int x, int y, CallbackInfo ci) {
		NbtString playerName = NbtString.of(serverPlayerEntity.getName().getString());
		if(!authors.contains(playerName)) authors.add(playerName);
	}

	@Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
	private void writeAuthor(NbtCompound nbt, CallbackInfo ci){
		if(!authors.isEmpty()) {
			nbt.put(AUTHORS_KEY, authors);
		}
		if (!open.isEmpty()){
			nbt.put(PUBLIC_KEY, NbtString.of("true"));
		}
	}

	@Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
	private void readAuthor(NbtCompound nbt, CallbackInfo ci){
		if(nbt.contains(AUTHORS_KEY)) authors = nbt.getList(AUTHORS_KEY, NbtElement.STRING_TYPE);
		if(nbt.contains(PUBLIC_KEY)) open = nbt.getString(PUBLIC_KEY);
	}

	@Inject(method = "toStack", at = @At("RETURN"), cancellable = true)
	private void setAuthor(CallbackInfoReturnable<ItemStack> cir){
		if(authors.isEmpty()) return;

		ItemStack stack = cir.getReturnValue();
		NbtCompound nbt = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT).copyNbt();
		nbt.put(AUTHORS_KEY, authors);
		if (!open.isEmpty()) nbt.put(PUBLIC_KEY, NbtString.of("true"));
		stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
		cir.setReturnValue(stack);
	}

	@Inject(method = "loadFromStack", at = @At("TAIL"))
	private void getAuthor(ItemStack stack, CallbackInfo ci){
		NbtCompound nbt = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT).copyNbt();
		if(nbt.contains(AUTHORS_KEY)) authors = nbt.getList(AUTHORS_KEY, NbtElement.STRING_TYPE);
		if(nbt.contains(PUBLIC_KEY)) open = nbt.getString(PUBLIC_KEY);
	}

}