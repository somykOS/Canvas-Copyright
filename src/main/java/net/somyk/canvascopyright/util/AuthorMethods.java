package net.somyk.canvascopyright.util;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Optional;
import java.util.function.Consumer;

import static net.somyk.canvascopyright.util.ModConfig.*;

public class AuthorMethods {
    public static final String AUTHORS_KEY = "authors";
    public static final String PUBLIC_KEY = "public";
    private static final Style TOOLTIP_STYLE = Style.EMPTY.withColor(Formatting.GRAY).withItalic(false);

    public static boolean isAuthor(ItemStack itemStack, PlayerEntity playerEntity) {
        return getAuthors(itemStack)
                .map(authors -> authors.contains(NbtString.of(playerEntity.getName().getString())))
                .orElse(false);
    }

    public static boolean isMainAuthor(ItemStack itemStack, PlayerEntity playerEntity) {
        return getAuthors(itemStack)
                .map(authors -> !authors.isEmpty() && authors.getString(0).orElse("").equals(playerEntity.getName().getString()))
                .orElse(false);
    }

    public static boolean canCopy(ItemStack itemStack, PlayerEntity playerEntity) {
        if (!getBooleanValue(disableCopy)) return true;

        return getBooleanValue(authorsCopy) && isAuthor(itemStack, playerEntity) || isPublic(itemStack);
    }

    public static boolean isPublic(ItemStack itemStack) {
        NbtCompound tag = getCustomData(itemStack);
        return tag.getBoolean(PUBLIC_KEY, false);
    }

    public static boolean changeAccessibility(ItemStack itemStack) {
        NbtCompound tag = getCustomData(itemStack);
        tag.putBoolean(PUBLIC_KEY, !isPublic(itemStack));
        itemStack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(tag));
        return isPublic(itemStack);
    }

    public static boolean modifyAuthorNBT(ItemStack itemStack, String playerName, int operation) {
        NbtCompound tag = getCustomData(itemStack);
        NbtList authors = tag.getList(AUTHORS_KEY).orElse(new NbtList());

        int index = findAuthorIndex(authors, playerName);

        boolean modified = false;
        if (operation == 1 && index == -1) {
            authors.add(NbtString.of(playerName));
            modified = true;
        } else if (operation == 0 && index != -1) {
            authors.remove(index);
            modified = true;
        }

        if (modified) {
            updateAuthorsTag(itemStack, tag, authors);
        }

        return modified;
    }

    public static void addToolTip(ItemStack itemStack, Consumer<Text> tooltip) {
        if (!getBooleanValue(displayLore)) return;

        getAuthors(itemStack).ifPresent(authors -> {
            if (!authors.isEmpty()) {
                addAuthorsToTooltip(authors, tooltip);
            }
        });

        NbtCompound tag = getCustomData(itemStack);
        if (tag.getBoolean(PUBLIC_KEY, false)) {
            tooltip.accept(Text.translatable("item.canvas-copyright.tooltip.public").setStyle(Style.EMPTY.withColor(Formatting.DARK_GRAY)));
        }
        tooltip.accept(Text.empty());
    }

    private static Optional<NbtList> getAuthors(ItemStack itemStack) {
        NbtCompound tag = getCustomData(itemStack);
        return tag.getList(AUTHORS_KEY);
    }

    private static void addAuthorsToTooltip(NbtList authors, Consumer<Text> tooltip) {
        int maxPlayers = getIntValue(maxPlayerLore);
        int authorCount = Math.min(authors.size(), maxPlayers);

        tooltip.accept(Text.translatable("book.byAuthor", authors.getString(0).orElse("") + (authorCount > 1 ? "," : "")).setStyle(TOOLTIP_STYLE));
        StringBuilder line;

        for (int i = 1; i < authorCount; i += 2) {
            line = new StringBuilder(authors.getString(i).orElse(""));

            if (i + 1 < authorCount && i + 1 < authors.size()) {
                line.append(", ").append(authors.getString(i + 1).orElse(""));
                if (i + 2 < authorCount && i + 2 < authors.size()) line.append(",");
                else if (i + 2 >= authorCount && i + 2 < authors.size()) line.append("...");
            } else if (i + 1 >= authorCount && i + 1 < authors.size()) line.append("...");

            tooltip.accept(Text.literal(line.toString()).setStyle(TOOLTIP_STYLE));
        }
    }

    public static NbtCompound getCustomData(ItemStack itemStack) {
        return itemStack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT).copyNbt();
    }

    private static int findAuthorIndex(NbtList authors, String playerName) {
        for (int i = 0; i < authors.size(); i++) {
            if (authors.getString(i).orElse("").equalsIgnoreCase(playerName)) {
                return i;
            }
        }
        return -1;
    }

    private static void updateAuthorsTag(ItemStack itemStack, NbtCompound tag, NbtList authors) {
        if (authors.isEmpty()) {
            tag.remove(AUTHORS_KEY);
        } else {
            tag.put(AUTHORS_KEY, authors);
        }
        itemStack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(tag));
    }
}