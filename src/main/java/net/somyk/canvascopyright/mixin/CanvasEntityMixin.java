package net.somyk.canvascopyright.mixin;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.UnboundedMapCodec;
import eu.pb4.mapcanvas.api.utils.VirtualDisplay;
import eu.pb4.polydecorations.entity.CanvasEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.storage.NbtWriteView;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.ClickType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Optional;

import static net.somyk.canvascopyright.util.AuthorMethods.AUTHORS_KEY;
import static net.somyk.canvascopyright.util.AuthorMethods.PUBLIC_KEY;

@Mixin(CanvasEntity.class)
public class CanvasEntityMixin {

	@Unique	private NbtList authors = new NbtList();
	@Unique	private boolean open = false;

	@Inject(method = "onUsed", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;swingHand(Lnet/minecraft/util/Hand;Z)V", shift = At.Shift.AFTER))
	private void onUsed(ServerPlayerEntity serverPlayerEntity, VirtualDisplay.ClickType clickType, int x, int y, CallbackInfo ci) {
		NbtString playerName = NbtString.of(serverPlayerEntity.getName().getString());
		if(!authors.contains(playerName)) authors.add(playerName);
	}

	@Inject(method = "writeCustomData", at = @At("TAIL"))
	private void writeAuthor(WriteView view, CallbackInfo ci){
		NbtCompound nbt = ((NbtWriteView) view).getNbt();

		if(!authors.isEmpty()) {
			nbt.put(AUTHORS_KEY, authors);
		}
		nbt.putBoolean(PUBLIC_KEY, open);

	}

	@Inject(method = "readCustomData", at = @At("TAIL"))
	private void readAuthor(ReadView view, CallbackInfo ci){
		Codec<List<String>> listCodec = Codec.list(UnboundedMapCodec.STRING);

		Optional<List<String>> authorsOptional = view.read(AUTHORS_KEY, listCodec);
		if (authorsOptional.isPresent()) {
			authors = new NbtList();
			authorsOptional.get().forEach(s -> authors.add(NbtString.of(s)));
		} else {
			authors = new NbtList();
		}

		open = view.getBoolean(PUBLIC_KEY, false);
	}

	@Inject(method = "toStack", at = @At("RETURN"), cancellable = true)
	private void setAuthor(CallbackInfoReturnable<ItemStack> cir){
		if(authors.isEmpty()) return;

		ItemStack stack = cir.getReturnValue();
		NbtCompound nbt = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT).copyNbt();
		nbt.put(AUTHORS_KEY, authors);
		nbt.putBoolean(PUBLIC_KEY, open);
		stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
		cir.setReturnValue(stack);
	}

	@Inject(method = "loadFromStack", at = @At("TAIL"))
	private void getAuthor(ItemStack stack, CallbackInfo ci){
		NbtCompound nbt = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT).copyNbt();
		if(nbt.getList(AUTHORS_KEY).isPresent()) authors = nbt.getList(AUTHORS_KEY).get();
		open = nbt.getBoolean(PUBLIC_KEY, false);
	}

}