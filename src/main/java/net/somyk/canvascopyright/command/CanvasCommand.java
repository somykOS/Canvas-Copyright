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
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.regex.Pattern;

import static net.minecraft.server.command.CommandManager.RegistrationEnvironment;
import static net.somyk.canvascopyright.CanvasCopyright.MOD_ID;
import static net.somyk.canvascopyright.util.AuthorMethods.*;

public class CanvasCommand {
    //private static final Style STYLE_SUCCESS = Style.EMPTY.withColor(Formatting.DARK_GREEN);
    private static final Style STYLE_FAIL = Style.EMPTY.withColor(Formatting.RED);
    private static final Pattern VALID_NAME_PATTERN = Pattern.compile("^[a-z0-9_]{3,}$", Pattern.CASE_INSENSITIVE);

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, RegistrationEnvironment environment) {
        LiteralCommandNode<ServerCommandSource> canvasNode = CommandManager.literal("canvas").build();

        canvasNode.addChild(buildSubCommand("add", CanvasCommand::add));
        canvasNode.addChild(buildSubCommand("remove", CanvasCommand::remove));

        dispatcher.getRoot().addChild(canvasNode);
    }

    private static LiteralCommandNode<ServerCommandSource> buildSubCommand(String name, CommandExecutor executor) {
        return CommandManager.literal(name)
                .then(CommandManager.argument("player", StringArgumentType.greedyString())
                        .executes(context -> executor.execute(context, StringArgumentType.getString(context, "player"))))
                .build();
    }

    @FunctionalInterface
    private interface CommandExecutor {
        int execute(CommandContext<ServerCommandSource> context, String playerName);
    }

    private static int add(CommandContext<ServerCommandSource> context, String playerName) {
        return modifyCanvas(context, playerName, true);
    }

    private static int remove(CommandContext<ServerCommandSource> context, String playerName) {
        return modifyCanvas(context, playerName, false);
    }

    private static int modifyCanvas(CommandContext<ServerCommandSource> context, String playerName, boolean isAdding) {
        PlayerEntity player = context.getSource().getPlayer();
        if (player == null) return -1;

        ItemStack itemStack = player.getMainHandStack();

        if (!isValidCanvas(itemStack)) {
            sendFeedback(context, "You should have canvas in main hand", STYLE_FAIL);
            return -1;
        }

        if (!canModifyCanvas(player, itemStack, isAdding)) {
            notAllowedModify(context);
            return -1;
        }

        if (!VALID_NAME_PATTERN.matcher(playerName).matches()) {
            sendFeedback(context, "Player name must be at least 3 characters long and contain no special symbols", STYLE_FAIL);
            return -1;
        }

        boolean success = modifyAuthorNBT(itemStack, playerName, isAdding ? 1 : 0);
        if (!success) {
            String message = isAdding ? "There is already the author" : "This author is not found";
            sendFeedback(context, message, STYLE_FAIL);
            return -1;
        }


        String message = "§6" + playerName + "§a successfully " + (isAdding ? "added to " : "removed from ") + "canvas authors";
        sendFeedback(context, message, Style.EMPTY);

        return 1;
    }

    private static boolean isValidCanvas(ItemStack itemStack) {
        return itemStack.isOf(DecorationsItems.CANVAS);
    }

    private static boolean canModifyCanvas(PlayerEntity player, ItemStack itemStack, boolean isAdding) {
        String permission = isAdding ? "add-author" : "remove-author";
        return isMainAuthor(itemStack, player) || Permissions.check(player, MOD_ID + "." + permission);
    }

    private static void sendFeedback(CommandContext<ServerCommandSource> context, String message, Style style) {
        context.getSource().sendFeedback(() -> Text.literal(message).setStyle(style), false);
    }

    private static void notAllowedModify(CommandContext<ServerCommandSource> context) {
        sendFeedback(context, "You're not allowed to modify this map", STYLE_FAIL);
    }
}