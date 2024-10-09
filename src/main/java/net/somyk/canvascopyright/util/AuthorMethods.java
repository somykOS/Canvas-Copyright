package net.somyk.canvascopyright.util;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;
import java.util.Optional;

import static net.somyk.canvascopyright.util.ModConfig.getBooleanValue;

public class AuthorMethods {
    public static final String AUTHORS_KEY = "authors";
    public static final String PUBLIC_KEY = "public";
    private static final Style TOOLTIP_STYLE = Style.EMPTY.withColor(Formatting.GRAY).withItalic(false);
    private static final int MAX_AUTHORS_DISPLAYED = 5;

    public static boolean isAuthor(ItemStack itemStack, PlayerEntity playerEntity) {
        return getAuthors(itemStack)
                .map(authors -> authors.contains(NbtString.of(playerEntity.getName().getString())))
                .orElse(false);
    }

    public static boolean isMainAuthor(ItemStack itemStack, PlayerEntity playerEntity) {
        return getAuthors(itemStack)
                .map(authors -> !authors.isEmpty() && authors.getString(0).equals(playerEntity.getName().getString()))
                .orElse(false);
    }

    public static boolean canCopy(ItemStack itemStack, PlayerEntity playerEntity) {
        if (!getBooleanValue("disableCopy")) return true;

        NbtCompound tag = getCustomData(itemStack);
        if (tag.getString(PUBLIC_KEY).equals("true")) return true;

        return getBooleanValue("authorsCanCopy") && isAuthor(itemStack, playerEntity);
    }

    public static boolean freeCopy(ItemStack itemStack) {
        NbtCompound tag = getCustomData(itemStack);
        if(tag.contains(PUBLIC_KEY)) return false;
        tag.putString(PUBLIC_KEY, "true");
        itemStack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(tag));
        return true;
    }

    public static boolean modifyAuthorNBT(ItemStack itemStack, String playerName, int operation) {
        NbtCompound tag = getCustomData(itemStack);
        NbtList authors = tag.getList(AUTHORS_KEY, NbtElement.STRING_TYPE);

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

    public static void addToolTip(ItemStack itemStack, List<Text> tooltip) {
        if (!getBooleanValue("displayAuthorsLore")) return;

        getAuthors(itemStack).ifPresent(authors -> {
            if (!authors.isEmpty()) {
                addAuthorsToTooltip(authors, tooltip);
            }
        });

        NbtCompound tag = getCustomData(itemStack);
        if (tag.getString(PUBLIC_KEY).equals("true")) {
            tooltip.add(Text.translatable("item.canvas-copyright.tooltip.public").setStyle(Style.EMPTY.withColor(Formatting.DARK_GRAY)));
        }
        tooltip.add(Text.empty());
    }

    private static Optional<NbtList> getAuthors(ItemStack itemStack) {
        NbtCompound tag = getCustomData(itemStack);
        return Optional.ofNullable(tag.getList(AUTHORS_KEY, NbtElement.STRING_TYPE));
    }

    private static void addAuthorsToTooltip(NbtList authors, List<Text> tooltip) {
        tooltip.add(Text.translatable("book.byAuthor", authors.getString(0) + (authors.size() > 1 ? "," : "")).setStyle(TOOLTIP_STYLE));

        for (int i = 1; i < Math.min(authors.size(), MAX_AUTHORS_DISPLAYED); i += 2) {
            StringBuilder line = new StringBuilder(authors.getString(i));
            if (i + 1 < authors.size()) {
                line.append(", ").append(authors.getString(i + 1));
                if (i + 2 < authors.size() && i + 2 < MAX_AUTHORS_DISPLAYED) {
                    line.append(",");
                }
            }
            if (i + 2 == MAX_AUTHORS_DISPLAYED && i + 2 < authors.size()) {
                line.append("...");
            }
            tooltip.add(Text.literal(line.toString()).setStyle(TOOLTIP_STYLE));
        }
    }

    public static NbtCompound getCustomData(ItemStack itemStack) {
        return itemStack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT).copyNbt();
    }

    private static int findAuthorIndex(NbtList authors, String playerName) {
        for (int i = 0; i < authors.size(); i++) {
            if (authors.getString(i).equalsIgnoreCase(playerName)) {
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