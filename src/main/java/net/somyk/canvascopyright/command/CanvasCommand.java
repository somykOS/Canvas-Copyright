package net.somyk.canvascopyright.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import eu.pb4.polydecorations.item.DecorationsItems;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.somyk.canvascopyright.util.ModConfig;

import java.util.regex.Pattern;

import static net.minecraft.server.command.CommandManager.RegistrationEnvironment;
import static net.somyk.canvascopyright.CanvasCopyright.MOD_ID;
import static net.somyk.canvascopyright.util.AuthorMethods.*;
import static net.somyk.canvascopyright.util.ModConfig.*;

public class CanvasCommand {
    private static final Style STYLE_FAIL = Style.EMPTY.withColor(Formatting.RED);
    private static final Pattern VALID_NAME_PATTERN = Pattern.compile("^[a-z0-9_]{3,}$", Pattern.CASE_INSENSITIVE);

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, RegistrationEnvironment environment) {
        LiteralCommandNode<ServerCommandSource> canvasNode = CommandManager.literal("canvas")
                .then(CommandManager.literal("add")
                        .then(CommandManager.argument("player", StringArgumentType.greedyString())
                                .executes(context -> modifyCanvas(context, StringArgumentType.getString(context, "player"), true))))
                .then(CommandManager.literal("remove")
                        .then(CommandManager.argument("player", StringArgumentType.greedyString())
                                .executes(context -> modifyCanvas(context, StringArgumentType.getString(context, "player"), false))))
                .then(CommandManager.literal("to-public")
                        .requires(source -> ModConfig.getBooleanValue(publicDomain) && ModConfig.getBooleanValue(disableCopy))
                        .executes(CanvasCommand::publicDomain))
                .build();

        dispatcher.getRoot().addChild(canvasNode);
    }

    private static int publicDomain(CommandContext<ServerCommandSource> context) {
        return executeWithPlayerAndCanvas(context, (player, itemStack) -> {
//            if (!getBooleanValue(disableCopy)) {
//                return sendFeedback(context, "command.canvas.error.free_copy", STYLE_FAIL);
//            }
            if (!isMainAuthor(itemStack, player)) {
                return sendFeedback(context, "command.canvas.error.not_allowed", STYLE_FAIL);
            }
            if (freeCopy(itemStack)) {
                return sendFeedback(context, "command.canvas.success.allow_copy", Style.EMPTY.withColor(Formatting.GREEN));
            } else {
                return sendFeedback(context, "command.canvas.error.already_public", STYLE_FAIL);
            }
        });
    }

    private static int modifyCanvas(CommandContext<ServerCommandSource> context, String playerName, boolean isAdding) {
        return executeWithPlayerAndCanvas(context, (player, itemStack) -> {
            if (!canModifyCanvas(player, itemStack, isAdding)) {
                return sendFeedback(context, "command.canvas.error.not_allowed", STYLE_FAIL);
            }
            if (!VALID_NAME_PATTERN.matcher(playerName).matches()) {
                return sendFeedback(context, "command.canvas.error.invalid_name", STYLE_FAIL);
            }
            boolean success = modifyAuthorNBT(itemStack, playerName, isAdding ? 1 : 0);
            if (!success) {
                String key = isAdding ? "command.canvas.error.author_exists" : "command.canvas.error.author_not_found";
                return sendFeedback(context, key, STYLE_FAIL);
            }
            String key = isAdding ? "command.canvas.success.author_added" : "command.canvas.success.author_removed";
            return sendFeedback(context, key, Style.EMPTY.withColor(Formatting.GREEN), (isAdding ? "§6" : "§c") + playerName + "§а");
        });
    }

    private static int executeWithPlayerAndCanvas(CommandContext<ServerCommandSource> context, PlayerCanvasExecutor executor) {
        PlayerEntity player = context.getSource().getPlayer();
        if (player == null) return -1;

        ItemStack itemStack = player.getMainHandStack();
        if (!itemStack.isOf(DecorationsItems.CANVAS)) {
            return sendFeedback(context, "command.canvas.error.no_canvas", STYLE_FAIL);
        }

        return executor.execute(player, itemStack);
    }

    private static boolean canModifyCanvas(PlayerEntity player, ItemStack itemStack, boolean isAdding) {
        String permission = isAdding ? "add-author" : "remove-author";
        return isMainAuthor(itemStack, player) || Permissions.check(player, MOD_ID + "." + permission);
    }

    private static int sendFeedback(CommandContext<ServerCommandSource> context, String translationKey, Style style, Object... args) {
        MutableText message = Text.translatable(translationKey, args);
        if (style != null) {
            message.setStyle(style);
        }
        context.getSource().sendFeedback(() -> message, false);
        return (style == STYLE_FAIL) ? -1 : 1;
    }

    @FunctionalInterface
    private interface PlayerCanvasExecutor {
        int execute(PlayerEntity player, ItemStack itemStack);
    }
}